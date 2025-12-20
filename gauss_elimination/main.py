import os
import sys
from typing import Dict, List, Tuple
import torch
from task.task import Task
from input_parser.input_parser import parse_input
from collections import deque
from visualization.plot_graph import plot_graph
import concurrent.futures

def parse_args():
    if len(sys.argv) == 1:
        example_path = os.path.join(os.path.dirname(__file__), "examples", "example2.txt")
        return example_path
    if len(sys.argv) != 2:
        print("Usage: python main.py <input_file_path>")
        sys.exit(1)
    return sys.argv[1]

def create_a_function(A, n_tensor, m_tensor, i, k):
    def task_function():
        # print("Executing a_task_function for i:", i, "k:", k)
        # print(f"A[{i}, {i}]:", A.__getitem__((i, i)))
        # print(f"A[{k}, {i}]:", A.__getitem__((k, i)))
        m_tensor.__setitem__((k, i), A.__getitem__((k, i)) / A.__getitem__((i, i)))
    return task_function

def create_b_function(A, n_tensor, m_tensor, i, j, k):
    def task_function():
        # print("Executing b_task_function for i:", i, "j:", j, "k:", k)
        # print(f"A[{i}, {j}]:", A.__getitem__((i, j)))
        # print(f"m_tensor[{k}, {i}]:", m_tensor.__getitem__((k, i)))
        n_tensor.__setitem__((i, j, k), A.__getitem__((i, j)) * m_tensor.__getitem__((k, i)))
    return task_function

def create_c_function(A, n_tensor, m_tensor, i, j, k):
    def task_function():
        # print("Executing c_task_function for i:", i, "j:", j, "k:", k)
        # print(f"A[{k}, {j}]:", A.__getitem__((k, j)))
        # print(f"n_tensor[{i}, {j}, {k}]:", n_tensor.__getitem__((i, j, k)))
        A.__setitem__((k, j), A.__getitem__((k, j)) - n_tensor.__getitem__((i, j, k)))
    return task_function

def create_tasks(A: torch.Tensor, b: torch.Tensor, m_tensor: torch.Tensor, n_tensor: torch.Tensor) -> Tuple[List[Task], List[Task], List[Task]]:
    n, m = A.size()

    # m_k,i = M_k,i / M_i,i
    a_tasks = []

    # n_k,i = M_i,j * m_k,i
    b_tasks = []

    # M_k,j = M_k,j - n_k,i
    c_tasks = []
    for i in range(n - 1):
        for k in range(i + 1, n):
            task_id = f"a_{i},{k}"
            variable = f"m_{i},{k}"
            uses = frozenset({f"M_{i},{k},{i-1}", f"M_{i},{i},{i-1}"})
            a_task = Task(
                task_id=task_id,
                variable=variable,
                uses=uses,
                # func=lambda: m_tensor.__setitem__((k, i), A.__getitem__((k, i)) / A.__getitem__((i, i))),
                func=create_a_function(A, n_tensor, m_tensor, i, k),
            )
            a_tasks.append(a_task)
            for j in range(i, m):
                task_id = f"b_{i},{j},{k}"
                variable = f"n_{i},{j},{k}"
                uses = frozenset({f"M_{i},{j},{i-1}", f"m_{i},{k}"})
                b_task = Task(
                    task_id=task_id,
                    variable=variable,
                    uses=uses,
                    # func=lambda: n_tensor.__setitem__((i, j, k), A.__getitem__((i, j)) * m_tensor.__getitem__((k, i)))
                    func=create_b_function(A, n_tensor, m_tensor, i, j, k)
                )
                b_tasks.append(b_task)
            for j in range(i, m):
                task_id = f"c_{i},{j},{k}"
                variable = f"M_{k},{j},{i}"
                uses = frozenset({f"M_{k},{j},{i-1}", f"n_{i},{j},{k}"})
                c_task = Task(
                    task_id=task_id,
                    variable=variable,
                    uses=uses,
                    # func=lambda: A.__setitem__((k, j), A.__getitem__((k, j)) - n_tensor.__getitem__((i, j, k)))
                    func=create_c_function(A, n_tensor, m_tensor, i, j, k)
                )
                c_tasks.append(c_task)

    return a_tasks, b_tasks, c_tasks

def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    dependency_graph = {task: set() for task in tasks}

    for vertex in tasks:
        for neighbour in tasks:
            if neighbour.variable in vertex.uses:
                dependency_graph[neighbour].add(vertex)

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

def foata_normal_form(alphabet: List[str], tasks: List[Task]) -> List[set[Task]]:
    diekert_graph = create_diekert_graph(alphabet, tasks)

    in_edges = {vertex: 0 for vertex in diekert_graph}

    for vertex in diekert_graph:
        for neighbour in diekert_graph[vertex]:
            in_edges[neighbour] += 1

    foata_forms = []

    to_check = set(diekert_graph.keys())

    while to_check:
        current_form = set()

        for vertex in to_check:
            if in_edges[vertex] == 0:
                current_form.add(vertex)

        foata_forms.append(current_form)
        to_check -= current_form

        for vertex in current_form:
            for neighbour in diekert_graph[vertex]:
                in_edges[neighbour] -= 1

    return foata_forms

def gauss_elimination(A: torch.Tensor, b: torch.Tensor) -> None:
    n = A.size(0)
    # A_cp, b_cp = A.clone(), b.clone()
    A_aug = torch.hstack((A, b))
    n, m = A_aug.size()

    m_tensor = torch.zeros((n, n)).float()
    n_tensor = torch.zeros((n, m, n)).float()
    a_tasks, b_tasks, c_tasks = create_tasks(A_aug, A_aug[:, -1], m_tensor, n_tensor)
    tasks = a_tasks + b_tasks + c_tasks
    alphabet = [task.task_id for task in tasks]

    foata_forms: List[set[Task]] = foata_normal_form(alphabet, tasks)

    for form in foata_forms:
        # futures = [executor.submit(task.func) for task in form]
        # concurrent.futures.wait(futures)
        print(form)
        for task in form:
            print( "Executing task:", task.task_id)
            task.func()
            print("A_aug after task", task.task_id)
            print(A_aug)
            print("------")
            # print("m_tensor after task", task.task_id)
            # print(m_tensor)
            # print("------")
            # print("n_tensor after task", task.task_id)
            # print(n_tensor)
            # print("------")
            # executor.map(lambda task: task.func(), form)
    # with concurrent.futures.ThreadPoolExecutor(max_workers=8) as executor:
    #     for form in foata_forms:
    #         # futures = [executor.submit(task.func) for task in form]
    #         # concurrent.futures.wait(futures)
    #         print(form)
    #         for task in form:
    #             print( "Executing task:", task.task_id)
    #             task.func()
    #             print("A_aug after task", task.task_id)
    #             print(A_aug)
    #             print("------")
    #             print("m_tensor after task", task.task_id)
    #             print(m_tensor)
    #             print("------")
    #             print("n_tensor after task", task.task_id)
    #             print(n_tensor)
    #             print("------")
    #         # executor.map(lambda task: task.func(), form)

    return A_aug[:, :-1], A_aug[:, -1]

def print_relation_graph(graph: Dict[Task, set[Task]], relation: str) -> None:
    output = f"{relation} = "
    relations = []

    for vertex in graph:
        for neighbour in graph[vertex]:
            relations.append(f"({vertex.task_id}, {neighbour.task_id})")

    output += f"{{{', '.join(relations)}}}"

    print(output)

def print_foata_forms(foata_forms: List[set[Task]], word: str) -> None:
    output = f"FNF([{word}]) = "
    print(foata_forms)

    single_forms = list(map(lambda form: f"[{' | '.join(task.task_id for task in form)}]", foata_forms))
    output += "".join(single_forms)

    print(output)

if __name__ == "__main__":
    input_file = parse_args()

    A, b = parse_input(input_file)
    print("Matrix A:")
    print(A)
    print("Vector b:")
    print(b)

    A_cp, b_cp = gauss_elimination(A, b)

    print(A_cp, b_cp)

    # a_tasks, b_tasks, c_tasks = create_tasks(torch.hstack((A, b)), b, None, None)
    # tasks = a_tasks + b_tasks + c_tasks
    # alphabet = [task.task_id for task in tasks]
    # print(alphabet)

    # dependency_graph = create_dependency_graph(alphabet, tasks)
    # diekert_graph = create_diekert_graph(alphabet, tasks)

    # plot_graph(diekert_graph, input_file.split("/")[-1].split(".")[0] + "_diekert")

    # foata_forms = foata_normal_form(alphabet, tasks)
    # print_relation_graph(dependency_graph, "D")
    # print_foata_forms(foata_forms, "; ".join(alphabet))
