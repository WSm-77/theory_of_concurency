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

def create_tasks(n: int) -> Tuple[List[Task], List[Task], List[Task]]:
    # m_k,i = M_k,i / M_i,i
    a_tasks = []

    # n_k,i = M_i,j * m_k,i
    b_tasks = []

    # M_k,j = M_k,j - n_k,i
    c_tasks = []
    tasks = []
    for i in range(n - 1):
        for k in range(i + 1, n):
            task_id = f"a_{k},{i}"
            variable = f"m_{k},{i}"
            uses = {f"M_{k},{i}", f"M_{i},{i}"}
            a_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            for j in range(i, n):
                task_id = f"b_{i},{j},{k}"
                variable = f"n_{i},{j},{k}"
                uses = {f"M_{i},{j}", f"m_{k},{i}"}
                b_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
                tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
            for j in range(i, n):
                task_id = f"c_{i},{j},{k}"
                variable = f"M_{k},{j}"
                uses = {f"M_{k},{j}", f"n_{i},{j},{k}"}
                c_tasks.append(Task(task_id=task_id, variable=variable, uses=uses))
                tasks.append(Task(task_id=task_id, variable=variable, uses=uses))


    return a_tasks, b_tasks, c_tasks, tasks


# def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
#     dependency_graph = {symbol: set() for symbol in alphabet}

#     for i, vertex in enumerate(tasks):
#         for j in range(i - 1, -1, -1):
#             neighbour = tasks[j]
#             if neighbour.variable in vertex.uses:
#                 # dependency_graph[vertex.task_id].add(neighbour.task_id)
#                 dependency_graph[neighbour.task_id].add(vertex.task_id)
#                 break

#     return dependency_graph

def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    dependency_graph = {symbol: set() for symbol in alphabet}

    for i, vertex in enumerate(tasks):
        for j in range(i - 1, -1, -1):
            neighbour = tasks[j]
            if neighbour.variable in vertex.uses:
                # dependency_graph[vertex.task_id].add(neighbour.task_id)
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

def create_diekert_graph(alphabet: List[str], tasks: List[Task]) -> Dict[int, set]:
    graph = create_dependency_graph(alphabet, tasks)
    diekert_graph = {}

    for vertex in graph:
        print(f"Processing vertex: {vertex}")
        visited = {v : False for v in graph}

        queue = deque(graph[vertex])

        while queue:
            curr = queue.popleft()

            for neighbour in graph[curr]:
                visited[neighbour] = True
                queue.append(neighbour)

        diekert_graph[vertex] = {neigh for neigh in graph[vertex] if not visited[neigh]}

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

    A, b = parse_input(input_file)
    print("Matrix A:")
    print(A)
    print("Vector b:")
    print(b)

    a_tasks, b_tasks, c_tasks, tasks = create_tasks(A.size(0))
    # tasks = a_tasks + b_tasks + c_tasks
    alphabet = [task.task_id for task in tasks]

    dependency_graph = create_dependency_graph(alphabet, tasks)
    # independency_graph = create_independency_graph(dependency_graph)

    # word_graph = create_word_graph(word, alphabet, tasks)
    # diekert_graph = create_diekert_graph(alphabet, tasks)

    plot_graph(dependency_graph, input_file.split("/")[-1].split(".")[0])

    # foata_forms = foata_normal_form(word, alphabet, tasks)
    print_relation_graph(dependency_graph, "D")
    # print_relation_graph(independency_graph, "I")
    # print_foata_forms(foata_forms, word)
