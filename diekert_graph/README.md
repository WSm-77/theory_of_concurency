# Diekert Graphs and the Foata Normal Form

This project focuses on algorithms for identifying independent tasks that can be executed in parallel without conflict. It implements Diekert graphs and computes the Foata Normal Form (FNF) for a given word.

This README explains how to use the program: the expected input format, how to run it, and a step-by-step description of the algorithms implemented in `main.py` (matching the included code).

## Project structure

The main implementation can be found in `main.py`.

Other important files:

- `task/task.py` — dataclass `Task` (fields: `task_id`, `variable`, `new_value`).
- `utils/topo_sort.py` — topological sort (DFS) used when computing FNF.
- `visualization/plot_graph.py` — uses the Python `graphviz` package to render the Diekert graph (output PNG).

## Requirements

- Python 3.9+ (the code uses modern typing annotations such as `list[int]`)
- the `uv` tool (used here for environment/dependency management)

## Environment setup

Install Python dependencies with:

```bash
uv sync
```

## Input format

The program expects a JSON file with the following keys:

- `alphabet`: an array/list of symbols (strings), e.g. `["a", "b", "c"]`
- `tasks`: an object mapping symbols to task descriptions. Each task entry should contain:
  - `task_id` — a unique identifier for the task (string)
  - `variable` — the variable the task writes/defines (string)
  - `new_value` — the new expression/value produced by the task (string)
- `word`: a string made of symbols from `alphabet` representing the sequence of tasks to execute, e.g. "abac"

Example JSON:

```json
{
    "alphabet": ["a", "b", "c", "d"],
    "tasks": {
        "a": {
            "variable": "x",
            "new_value": "x + y"
        },
        "b": {
            "variable": "y",
            "new_value": "y + 2*z"
        },
        "c": {
            "variable": "x",
            "new_value": "3*x + z"
        },
        "d": {
            "variable": "z",
            "new_value": "y - z"
        }
    },
    "word": "baadcb"
}
```

## How to run

Run the program from the same directory as `main.py`:

```bash
# run with a custom input file
uv run main.py path/to/input.json
```

or

```bash
# by default it uses examples/example1.json
uv run main.py
```

The program will:

- parse the input
- build dependency and independence relations
- construct the word graph and compute the Diekert graph
- generate a Diekert graph image named `graph_<word>.png`
- print the relation sets and the Foata Normal Form to standard output

## Algorithms — description and intent

Below are descriptions of the algorithms implemented in `main.py`. They explain the idea and the steps taken by the code.

### 1) Dependency relation (D)

Idea:

- The dependency relation is a symmetric binary relation between symbols (or tasks) that indicates two symbols cannot be swapped because one depends on the other (read/write conflict).

Implementation (as in `main.py`):

- For every pair of tasks (a, b):
  - If one task reads a variable that the other writes (i.e. `b.variable` appears in `a.new_value`), then the two tasks are dependent and we add an edge between them in the dependency relation.

Pseudocode:

```
for each task a in tasks:
    for each task b in tasks:
        if b.variable appears in a.new_value:
            add dependency between a and b
```

Complexity: $O(n^2 \cdot m)$

where $n$ is the number of tasks and $m$ is the maximum length of `new_value` expressions (for checking variable occurrence).

### 2) Independence relation (I)

Idea:

- Independence I is the complement of the dependency relation on the same set of vertices (excluding reflexive pairs): a pair (x,y) is independent if they are not dependent.

Implementation (in `main.py`):

- For each task, collect all tasks that are not dependent with it and add independence edges between them.

Pseudocode:

```
I = { (a,b) | a in tasks, b in tasks, (a,b) not in D }
```

Complexity: $O(n^2)$ — there are $O(n^2)$ checks and each check is an $O(1)$ set lookup.

### 3) Word graph

Idea:

- The word graph models ordering constraints among positions of the input word. Vertices are indices (positions) in the `word` ($0 \ldots n-1$). There is a directed edge $i \to j$ ($i < j$) if the symbol at position $j$ is dependent on the symbol at position $i$.

Implementation (in `main.py`):

- Build a dependency graph between symbols (tasks). For all $i < j$, if `word[j]` is in dependency_graph[`word[i]`], add edge $i \to j$ in the word graph.

Pseudocode:

```
for i in 0..n-1:
  for j in i+1..n-1:
    if word[j] in D[word[i]]:
      add edge i -> j
```

Interpretation: later occurrences that depend on earlier ones must come after them in any linearization.

Complexity: $O(n^2)$ — there are $O(n^2)$ checks and each is an $O(1)$ set lookup.

### 4) Diekert graph (transitive reduction of the word graph)

Idea:

- The Diekert graph is the transitive reduction of the word graph: it keeps only direct dependencies (cover relations) and removes edges implied by transitivity.

Implementation (in `main.py`):

- For each vertex $v$ in the word graph:
  - Run BFS/DFS from $v$'s direct neighbors to mark all reachable nodes. If a neighbor $u$ of $v$ is reachable via another path from $v$, then the edge $v \to u$ is redundant and can be removed.

Pseudocode:

```
for v in all vertices:
  visited = {}
  queue = all direct neighbors of v
  while queue not empty:
    x = queue.pop()
    for y in neighbors(x):
      if y not visited:
        mark visited
        queue.push(y)
  neighbors(v) = { u in neighbors(v) | u not visited }
```

Complexity: running BFS for each vertex over its reachable subgraph — worst-case $O(n \cdot (n + m))$, where $n$ is the word length and $m$ is the number of edges.

### 5) Foata Normal Form (FNF)

Idea:

- FNF groups word positions into a sequence (list) of sets (layers) so that positions inside each layer are pairwise independent, while all dependencies between layers are preserved. Each class corresponds to a step where tasks can be executed in parallel.

Implementation (used in `main.py`):

- Compute the Diekert graph for the word.
- Perform a topological sort of the Diekert graph.
- Iterate over positions in topological order and greedily place each position into the current Foata class if it is independent of every element already in that class; otherwise start a new class. Topological sorting guarantees dependencies are respected.

Implementation detail (from `main.py`):

- When adding position `p` to the current class `C`, the code checks whether there exists `q` in `C` such that `p` is dependent on `q`. If none exists, `p` is independent of all elements of `C` and is appended; otherwise a new class is started.

Pseudocode:

```
topo_sorted = topological_sort(diekert_graph)
foata_classes = []
current = { topo_sorted[0] }
for p in topo_sorted[1:]:
  if for all q in current: (p,q) not in D:
    add p to current
  else:
    foata_classes.append(current)
    current = {p}
foata_classes.append(current)
```

The result is a list of index-sets; when printing, code maps indices back to letters from the input word.

Complexity: dominated by topological sorting and independence checks; in practice $O(n^2)$.

## Topological sort

- `utils/topo_sort.py` implements a standard DFS-based topological sort. It visits each node, recursively visits neighbors, and appends nodes on return — yielding a valid topological order for acyclic directed graphs (such as the Diekert graph).

Complexity: $O(V + E)$

## Output

- The program prints two relation sets: `D` (dependency) and `I` (independence). Each is printed as a set of ordered pairs.
- It prints the Foata Normal Form in the format `FNF([word]) = (class1)(class2)...` where each class contains the letters from the input word belonging to that class.
- The Diekert graph is rendered using Graphviz. The output image file is named `graph_<word>.png`.

### Example output for `examples/example1.json`

1. Dependency relation set:

```plaintext
D = {(a, c), (a, b), (a, a), (b, b), (b, d), (b, a), (c, c), (c, d), (c, a), (d, c), (d, b), (d, d)}
```

2. Independence relation set:

```plaintext
I = {(a, d), (b, c), (c, b), (d, a)}
```

3. Foata Normal Form:

```plaintext
FNF([baadcb]) = (b)(ad)(a)(cb)
```

4. Diekert graph in DOT format:

```plaintext
digraph {
	0 [label=b]
	1 [label=a]
	2 [label=a]
	3 [label=d]
	4 [label=c]
	5 [label=b]
	0 -> 1
	0 -> 3
	1 -> 2
	2 -> 4
	2 -> 5
	3 -> 4
	3 -> 5
}
```

5. The generated image file `graph_baadcb.png` contains a visualization of the Diekert graph.

![Example Diekert graph](resources/graph_baadcb.png)
