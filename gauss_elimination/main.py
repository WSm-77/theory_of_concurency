import os
import sys
from typing import Dict, List, Tuple
from task.task import Task
from input_parser.input_parser import parse_input
from collections import deque
from visualization.plot_graph import plot_graph
from utils.topo_sort import topological_sort

def parse_args():
    if len(sys.argv) == 1:
        example_path = os.path.join(os.path.dirname(__file__), "examples", "example1.txt")
        return example_path
    if len(sys.argv) != 2:
        print("Usage: python main.py <input_file_path>")
        sys.exit(1)
    return sys.argv[1]

# def create_tasks(n: int) -> Tuple[List[Task], List[Task], List[Task]]:
#     # m_k,i = M_k,i / M_i,i
#     a_tasks = []
#     for i in range(n - 1):
#         for k in range(i + 1, n):
#             task_id = f"a_{k},{i}"
#             variable = f"m_{k},{i}"
#             uses = {f"M_{k},{i}", f"M_{i},{i}"}
#             a_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))

#     # n_k,i = M_i,j * m_k,i
#     b_tasks = []
#     for i in range(n - 1):
#         for k in range(i + 1, n):
#             for j in range(i, n):
#                 task_id = f"b_{i},{j},{k}"
#                 variable = f"n_{k},{i}"
#                 uses = {f"M_{i},{j}", f"m_{k},{i}"}
#                 b_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))

#     # M_k,j = M_k,j * n_k,i
#     c_tasks = []
#     for i in range(n - 1):
#         for k in range(i + 1, n):
#             for j in range(i, n):
#                 task_id = f"c_{i},{j},{k}"
#                 variable = f"M_{k},{j}"
#                 uses = {f"M_{k},{j}", f"n_{k},{i}"}
#                 c_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))

#     return a_tasks, b_tasks, c_tasks

def create_tasks(n: int) -> Tuple[List[Task], List[Task], List[Task], List[Task]]:
    # m_k,i = M_k,i / M_i,i
    a_tasks = []

    # n_k,i = M_i,j * m_k,i
    b_tasks = []

    # M_k,j = M_k,j - n_k,i
    c_tasks = []
    tasks = []
    for i in range(n - 1):
        for k in range(i + 1, n):
            task_id = f"a_{i},{k}"
            variable = f"m_{i},{k}"
            uses = {f"M_{i},{k},{i-1}", f"M_{i},{i},{i-1}"}
            a_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            for j in range(i, n):
                task_id = f"b_{i},{j},{k}"
                variable = f"n_{i},{j},{k}"
                uses = {f"M_{i},{j},{i-1}", f"m_{i},{k}"}
                b_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
                tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            for j in range(i, n):
                task_id = f"c_{i},{j},{k}"
                variable = f"M_{k},{j},{i}"
                uses = {f"M_{k},{j},{i-1}", f"n_{i},{j},{k}"}
                c_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
                tasks.append(Task(task_id=task_id, variable=variable, uses=uses))


    return a_tasks, b_tasks, c_tasks, tasks

def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    dependency_graph = {symbol: set() for symbol in alphabet}

    for vertex in tasks:
        for neighbour in tasks:
            if neighbour.variable in vertex.uses:
                dependency_graph[neighbour.task_id].add(vertex.task_id)

    return dependency_graph

def create_diekert_graph(alphabet: List[str], tasks: List[Task]) -> Dict[str, set]:
    graph = create_dependency_graph(alphabet, tasks)
    diekert_graph = {}

    for vertex in graph:
        visited = {v : False for v in graph}

        queue = deque(graph[vertex])

        while queue:
            curr = queue.popleft()

            for neighbour in graph[curr]:
                visited[neighbour] = True
                queue.append(neighbour)

        diekert_graph[vertex] = {neigh for neigh in graph[vertex] if not visited[neigh]}

    return diekert_graph

def foata_normal_form(alphabet: List[str], tasks: List[Task]) -> List[set]:
    dependency_graph = create_dependency_graph(alphabet, tasks)

    in_edges = {vertex: 0 for vertex in dependency_graph}

    for vertex in dependency_graph:
        for neighbour in dependency_graph[vertex]:
            in_edges[neighbour] += 1

    foata_forms = []

    to_check = set(dependency_graph.keys())

    while to_check:
        current_form = set()

        for vertex in to_check:
            if in_edges[vertex] == 0:
                current_form.add(vertex)

        foata_forms.append(current_form)
        to_check -= current_form

        for vertex in current_form:
            for neighbour in dependency_graph[vertex]:
                in_edges[neighbour] -= 1

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
    print(foata_forms)

    single_forms = list(map(lambda form: f"[{' | '.join(form)}]", foata_forms))

    output += "".join(single_forms)

    print(output)

if __name__ == "__main__":
    input_file = parse_args()

    A, b = parse_input(input_file)
    print("Matrix A:")
    print(A)
    print("Vector b:")
    print(b)

    a_tasks, b_tasks, c_tasks, tasks = create_tasks(A.size(0))
    tasks = a_tasks + b_tasks + c_tasks
    alphabet = [task.task_id for task in tasks]
    print(alphabet)

    dependency_graph = create_dependency_graph(alphabet, tasks)
    diekert_graph = create_diekert_graph(alphabet, tasks)

    plot_graph(diekert_graph, input_file.split("/")[-1].split(".")[0] + "_diekert")

    foata_forms = foata_normal_form(alphabet, tasks)
    print_relation_graph(dependency_graph, "D")
    print_foata_forms(foata_forms, "; ".join(alphabet))
