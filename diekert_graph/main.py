import os
import sys
from typing import Dict, List
from task.task import Task
from input_parser.input_parser import parse_input
from collections import deque
from visualization.plot_graph import plot_graph
from utils.topo_sort import topological_sort

def parse_args():
    if len(sys.argv) == 1:
        example_path = os.path.join(os.path.dirname(__file__), "examples", "example1.json")
        return example_path
    if len(sys.argv) != 2:
        print("Usage: python main.py <input_file_path>")
        sys.exit(1)
    return sys.argv[1]

def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    dependency_graph = {symbol: set() for symbol in alphabet}

    for vertex in tasks:
        for neighbour in tasks:
            if neighbour.variable in vertex.new_value:
                dependency_graph[vertex.task_id].add(neighbour.task_id)
                dependency_graph[neighbour.task_id].add(vertex.task_id)

    return dependency_graph

def create_independency_graph(dependency_graph: Dict[str, set]):
    return {vertex:
                {neighbour for neighbour in dependency_graph if vertex not in dependency_graph[neighbour]}
                for vertex in dependency_graph
            }

def create_word_graph(word: str, alphabet: List[str], tasks: List[Task]):
    dependency_graph = create_dependency_graph(alphabet, tasks)

    n = len(word)

    word_graph = {i : set() for i in range(n)}

    for i in range(n - 1):
        for j in range(i + 1, n):
            vertex = word[i]
            neighbour = word[j]

            if neighbour in dependency_graph[vertex]:
                word_graph[i].add(j)

    return word_graph

def create_diekert_graph(word: str, alphabet: List[str], tasks: List[Task]) -> Dict[int, set]:
    word_graph = create_word_graph(word, alphabet, tasks)

    diekert_graph = {}

    for vertex in word_graph:
        visited = {v : False for v in word_graph}

        queue = deque(word_graph[vertex])

        while queue:
            curr = queue.popleft()

            for neighbour in word_graph[curr]:
                visited[neighbour] = True
                queue.append(neighbour)

        diekert_graph[vertex] = {neigh for neigh in word_graph[vertex] if not visited[neigh]}

    return diekert_graph

def foata_normal_form(word: str, alphabet: List[str], tasks: List[Task]) -> List[set]:
    diekert_graph = create_diekert_graph(word, alphabet, tasks)
    dependency_graph = create_dependency_graph(alphabet, tasks)

    topo_sorted = topological_sort(diekert_graph)

    foata_forms = []

    current_form: set = {topo_sorted[0]}

    i = 1

    while i < len(word):
        curr_elem = topo_sorted[i]

        # check if curr_elem is independent with all elements in current_form
        if not {elem for elem in current_form if word[curr_elem] in dependency_graph[word[elem]]}:
            current_form.add(curr_elem)
        else:
            foata_forms.append(current_form)
            current_form = {curr_elem}

        i += 1

    foata_forms.append(current_form)

    return foata_forms

def print_relation_graph(graph: Dict[str, set], relation: str) -> None:
    output = f"{relation} = "
    relations = []

    for vertex in graph:
        for neighbour in graph[vertex]:
            relations.append(f"({vertex}, {neighbour})")

    output += f"{{{', '.join(relations)}}}"

    print(output)

def print_foata_forms(foata_forms: List[set], word: str) -> None:
    output = f"FNF([{word}]) = "

    single_forms = list(map(lambda form: f"({''.join([word[idx] for idx in form])})", foata_forms))

    output += "".join(single_forms)

    print(output)

if __name__ == "__main__":
    input_file = parse_args()

    alphabet, tasks, word = parse_input(input_file)

    dependency_graph = create_dependency_graph(alphabet, tasks)
    independency_graph = create_independency_graph(dependency_graph)

    word_graph = create_word_graph(word, alphabet, tasks)
    diekert_graph = create_diekert_graph(word, alphabet, tasks)

    plot_graph(diekert_graph, word)

    foata_forms = foata_normal_form(word, alphabet, tasks)
    print_relation_graph(dependency_graph, "D")
    print_relation_graph(independency_graph, "I")
    print_foata_forms(foata_forms, word)
