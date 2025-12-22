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
        print(f"Executing A function for i={i}, k={k}")
        print(f"m_tensor before:\n{m_tensor}\n")
        m_tensor.__setitem__((k, i), A.__getitem__((k, i)) / A.__getitem__((i, i)))
        print(f"m_tensor after:\n{m_tensor}\n")
    return task_function

def create_b_function(A, n_tensor, m_tensor, i, j, k):
    def task_function():
        print(f"Executing B function for i={i}, j={j}, k={k}")
        print(f"n_tensor before:\n{n_tensor}\n")
        n_tensor.__setitem__((i, j, k), A.__getitem__((i, j)) * m_tensor.__getitem__((k, i)))
        print(f"n_tensor after:\n{n_tensor}\n")
    return task_function

def create_c_function(A, n_tensor, m_tensor, i, j, k):
    def task_function():
        print(f"Executing C function for i={i}, j={j}, k={k}")
        print(f"A before:\n{A}\n")
        A.__setitem__((k, j), A.__getitem__((k, j)) - n_tensor.__getitem__((i, j, k)))
        print(f"A after:\n{A}\n")
    return task_function

def create_d_function(A, q_tensor, i):
    def task_function():
        print(f"Executing D function for i={i}")
        print(f"q_tensor before:\n{q_tensor}\n")
        q_tensor.__setitem__(i, A.__getitem__((i, i)))
        print(f"q_tensor after:\n{q_tensor}\n")
    return task_function

def create_e_function(A, q_tensor, i, j):
    def task_function():
        print(f"Executing E function for i={i}, j={j}")
        print(f"A before:\n{A}\n")
        A.__setitem__((i,j), A.__getitem__((i, j)) / q_tensor.__getitem__(i))
        print(f"A after:\n{A}\n")
    return task_function

def create_f_function(A, r_tensor, i, k):
    def task_function():
        print(f"Executing F function for i={i}, k={k}")
        print(f"r_tensor before:\n{r_tensor}\n")
        r_tensor.__setitem__((i, k), A.__getitem__((k, i)) / A.__getitem__((i, i)))
        print(f"r_tensor after:\n{r_tensor}\n")
    return task_function

def create_g_function(A, r_tensor, s_tensor, i, j, k):
    def task_function():
        print(f"Executing G function for i={i}, j={j}, k={k}")
        print(f"s_tensor before:\n{s_tensor}\n")
        s_tensor.__setitem__((i, j, k), A.__getitem__((i, j)) * r_tensor.__getitem__((i, k)))
        print(f"s_tensor after:\n{s_tensor}\n")
    return task_function

def create_h_function(A, s_tensor, i, j, k):
    def task_function():
        print(f"Executing H function for i={i}, j={j}, k={k}")
        print(f"A before:\n{A}\n")
        A.__setitem__((k, j), A.__getitem__((k, j)) - s_tensor.__getitem__((i, j, k)))
        print(f"A after:\n{A}\n")
    return task_function

def create_tasks(
        A: torch.Tensor,
        m_tensor: torch.Tensor,
        n_tensor: torch.Tensor,
        q_tensor: torch.Tensor,
        r_tensor: torch.Tensor,
        s_tensor: torch.Tensor,
) -> Tuple[List[Task], List[Task], List[Task]]:
    n, m = A.size()

    # m_k,i = M_k,i / M_i,i
    a_tasks = []

    # n_k,i = M_i,j * m_k,i
    b_tasks = []

    # M_k,j = M_k,j - n_k,i
    c_tasks = []

    # q_i = M_i,i
    d_tasks = []

    # M_i,j = M_i,j / q_i
    e_tasks = []

    tasks = []
    for i in range(n):
        d_task = Task(
            task_id=f"d_{i}",
            variable=f"q_{i}",
            uses=frozenset({f"M_{i},{i}"}),
            func=create_d_function(A, q_tensor, i)
        )
        d_tasks.append(d_task)
        tasks.append(d_task)
        for j in range(i, m):
            e_task = Task(
                task_id=f"e_{i},{j}",
                variable=f"M_{i},{j}",
                uses=frozenset({f"M_{i},{j}", f"q_{i}"}),
                func=create_e_function(A, q_tensor, i, j)
            )
            e_tasks.append(e_task)
            tasks.append(e_task)
        for k in range(i + 1, n):
            task_id = f"a_{i},{k}"
            variable = f"m_{i},{k}"
            uses = frozenset({f"M_{k},{i}", f"M_{i},{i}"})
            a_task = Task(
                task_id=task_id,
                variable=variable,
                uses=uses,
                func=create_a_function(A, n_tensor, m_tensor, i, k),
            )
            a_tasks.append(a_task)
            tasks.append(a_task)
            for j in range(i, m):
                task_id = f"b_{i},{j},{k}"
                variable = f"n_{i},{j},{k}"
                uses = frozenset({f"M_{i},{j}", f"m_{i},{k}"})
                b_task = Task(
                    task_id=task_id,
                    variable=variable,
                    uses=uses,
                    func=create_b_function(A, n_tensor, m_tensor, i, j, k)
                )
                b_tasks.append(b_task)
                tasks.append(b_task)
            for j in range(i, m):
                task_id = f"c_{i},{j},{k}"
                variable = f"M_{k},{j}"
                uses = frozenset({f"M_{k},{j}", f"n_{i},{j},{k}"})
                c_task = Task(
                    task_id=task_id,
                    variable=variable,
                    uses=uses,
                    func=create_c_function(A, n_tensor, m_tensor, i, j, k)
                )
                c_tasks.append(c_task)
                tasks.append(c_task)

    # backward substitution phase

    # r_i,k = M_k,i
    f_tasks = []

    # s_i,j,k = M_i,j * r_i,k
    g_tasks = []

    # M_k,j = M_k,j - s_i,j,k
    h_tasks = []

    for i in range(n-1, -1, -1):
        for k in range(i-1, -1, -1):
            f_task = Task(
                task_id=f"f_{i},{k}",
                variable=f"r_{i},{k}",
                uses=frozenset({f"M_{k},{i}", f"M_{i},{i}"}),
                func=create_f_function(A, r_tensor, i, k)
            )
            f_tasks.append(f_task)
            tasks.append(f_task)
            for j in [i, m-1]:
                g_task = Task(
                    task_id=f"g_{i},{j},{k}",
                    variable=f"s_{i},{j},{k}",
                    uses=frozenset({f"M_{i},{j}", f"r_{i},{k}"}),
                    func=create_g_function(A, r_tensor, s_tensor, i, j, k)
                )
                g_tasks.append(g_task)
                tasks.append(g_task)
            for j in [i, m-1]:
                h_task = Task(
                    task_id=f"h_{i},{j},{k}",
                    variable=f"M_{k},{j}",
                    uses=frozenset({f"M_{k},{j}", f"s_{i},{j},{k}"}),
                    func=create_h_function(A, s_tensor, i, j, k)
                )
                h_tasks.append(h_task)
                tasks.append(h_task)

    # normalization phase

    # # q_i = M_i,i
    # d_tasks = []

    # # M_i,j = M_i,j / q_i
    # e_tasks = []

    # for i in range(n-1, -1, -1):
    #     d_task = Task(
    #         task_id=f"d_{i}",
    #         variable=f"q_{i}",
    #         uses=frozenset({f"M_{i},{i}"}),
    #         func=create_d_function(A, q_tensor, i)
    #     )
    #     d_tasks.append(d_task)
    #     tasks.append(d_task)
    #     for j in range(i, m):
    #         e_task = Task(
    #             task_id=f"e_{i},{j}",
    #             variable=f"M_{i},{j}",
    #             uses=frozenset({f"M_{i},{j}", f"q_{i}"}),
    #             func=create_e_function(A, q_tensor, i, j)
    #         )
    #         e_tasks.append(e_task)
    #         tasks.append(e_task)

    return tasks

def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    dependency_graph = {task: set() for task in tasks}

    for i, vertex in enumerate(tasks):
        for used_variable in vertex.uses:
            for j in range(i-1, -1, -1):
                neighbour = tasks[j]
                if neighbour.variable == used_variable:
                    dependency_graph[neighbour].add(vertex)
                    break

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
    A_aug = torch.hstack((A, b))
    n, m = A_aug.size()

    m_tensor = torch.zeros((n, n)).float()
    n_tensor = torch.zeros((n, m, n)).float()
    q_tensor = torch.zeros(n).float()
    r_tensor = torch.zeros((n,m)).float()
    s_tensor = torch.zeros((n,m,n)).float()
    tasks = create_tasks(A_aug, m_tensor, n_tensor, q_tensor, r_tensor, s_tensor)
    alphabet = [task.task_id for task in tasks]

    foata_forms: List[set[Task]] = foata_normal_form(alphabet, tasks)

    # with concurrent.futures.ThreadPoolExecutor(max_workers=8) as executor:
        # for form in foata_forms:
            # futures = [executor.submit(task.func) for task in form]
            # concurrent.futures.wait(futures)

    for form in foata_forms:
        for task in form:
            # print(f"Executing task: {task.task_id}")
            # print(f"A_aug before:\n{A_aug}\n")
            task.func()
            # print(f"A_aug after:\n{A_aug}\n")
            # print("-" * 40)

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

    single_forms = list(map(lambda form: f"[{' | '.join(task.task_id for task in form)}]", foata_forms))
    output += "\n".join(single_forms)

    print(output)

if __name__ == "__main__":
    input_file = parse_args()

    A, b = parse_input(input_file)
    print("Matrix A:")
    print(A)
    print("Vector b:")
    print(b)

    A_aug = torch.hstack((A, b))
    n, m = A_aug.size()
    m_tensor = torch.zeros((n, n)).float()
    n_tensor = torch.zeros((n, m, n)).float()
    q_tensor = torch.zeros(n).float()
    r_tensor = torch.zeros((n,m)).float()
    s_tensor = torch.zeros((n,m,n)).float()
    tasks = create_tasks(A_aug, m_tensor, n_tensor, q_tensor, r_tensor, s_tensor)
    alphabet = [task.task_id for task in tasks]

    dependency_graph = create_dependency_graph(alphabet, tasks)
    diekert_graph = create_diekert_graph(alphabet, tasks)
    print(dependency_graph)

    plot_graph(dependency_graph, input_file.split("/")[-1].split(".")[0])
    plot_graph(diekert_graph, input_file.split("/")[-1].split(".")[0] + "_diekert")

    foata_forms = foata_normal_form(alphabet, tasks)
    # print_relation_graph(dependency_graph, "D")
    print_foata_forms(foata_forms, "; ".join(alphabet))

    A_cp, b_cp = gauss_elimination(A, b)

    print("Resulting Matrix A after Gauss Elimination:")
    print(A_cp)
    print("Resulting Vector b after Gauss Elimination:")
    print(b_cp)
