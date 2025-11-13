from dataclasses import dataclass

@dataclass
class Task:
    task_id: str
    variable: str
    new_value: str
