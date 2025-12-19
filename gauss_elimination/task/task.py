from dataclasses import dataclass
from typing import Set

@dataclass
class Task:
    task_id: str
    variable: str
    uses: Set[str]
