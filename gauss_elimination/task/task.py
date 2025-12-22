from dataclasses import dataclass
from typing import Callable, FrozenSet

@dataclass(eq=True, frozen=True)
class Task:
    task_id: str
    variable: str
    uses: FrozenSet[str]
    func: Callable
