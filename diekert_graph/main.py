import os
from typing import Dict, Any, List
from task.task import Task
from input_parser.input_parser import parse_input


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
