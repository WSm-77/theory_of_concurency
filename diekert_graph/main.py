import os
from typing import Dict, List
from task.task import Task
from input_parser.input_parser import parse_input
from collections import deque
from visualization.plot_graph import plot_graph

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

if __name__ == "__main__":
    example_path = os.path.join(os.path.dirname(__file__), "examples", "example1.json")
    try:
        alphabet, tasks, word = parse_input(example_path)
        print(f"alphabet: {alphabet}")
        print(f"tasks: {tasks}")
        print(f"word: {word}")
    except Exception as e:
        print("Error parsing input:", e)

    dependency_graph = create_dependency_graph(alphabet, tasks)
    print(dependency_graph)
    independency_graph = create_independency_graph(dependency_graph)
    print(independency_graph)

    word_graph = create_word_graph(word, alphabet, tasks)

    print(word_graph)
    diekert_graph = create_diekert_graph(word, alphabet, tasks)
    print(diekert_graph)

    plot_graph(diekert_graph, word)
