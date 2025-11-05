import os
from typing import Dict, Any, List
from task.task import Task
from input_parser.input_parser import parse_input


def create_dependency_graph(alphabet: List[str], tasks: List[Task]):
    pass

def create_independency_graph():
    pass


if __name__ == "__main__":
    example_path = os.path.join(os.path.dirname(__file__), "examples", "example1.json")
    try:
        parsed = parse_input(example_path)
        print("Parsed input:")
        print(parsed)
    except Exception as e:
        print("Error parsing input:", e)
