import json
from typing import Dict, Any, List
from task.task import Task

def parse_input(file_path: str) -> tuple[list[str], Dict[str, Any], str]:
    """
    Parse the JSON input file and return a dict with keys: 'alphabet', 'tasks', 'word'.

    Raises:
        FileNotFoundError: if file_path does not exist
        ValueError: if required keys are missing or types are incorrect
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    if not isinstance(data, dict):
        raise ValueError("Input JSON must be an object at top level")

    for key in ("alphabet", "tasks", "word"):
        if key not in data:
            raise ValueError(f"Missing required key in input JSON: {key}")

    alphabet: List[str] = data["alphabet"]
    tasks = [Task(task_id=k, variable=v["variable"], new_value=v["new_value"]) for k, v in data["tasks"].items()]
    word: str = data["word"]

    if not isinstance(alphabet, list) or not all(isinstance(s, str) for s in alphabet):
        raise ValueError("alphabet must be a list of strings")

    if not isinstance(tasks, list) or not all(isinstance(t, Task) for t in tasks):
        raise ValueError("tasks must be a list of Task objects")

    if not isinstance(word, str):
        raise ValueError("word must be a string")

    unknown_symbols = [ch for ch in set(word) if ch not in alphabet]
    if unknown_symbols:
        raise ValueError(f"Symbols not in alphabet found in word: {unknown_symbols}")

    return alphabet, tasks, word
