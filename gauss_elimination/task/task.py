from dataclasses import dataclass
from typing import Set, Callable, FrozenSet
from torch import Tensor

@dataclass(eq=True, frozen=True)
class Task:
    task_id: str
    variable: str
    uses: FrozenSet[str]
    # matrix: Tensor
    # b: Tensor
    func: Callable
