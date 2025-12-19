from torch import Tensor, tensor

def parse_input(file_path: str) -> Tensor:
    """
    Parse the text input file and return a matrix A and vector b for Gauss Elimination.

    Raises:
        FileNotFoundError: if file_path does not exist
    """
    with open(file_path, 'r') as file:
        n = int(file.readline().strip())

        A = [list(map(float, file.readline().strip().split(' '))) for _ in range(n)]
        b = list(map(float, file.readline().strip().split(' ')))

        A = tensor(A)
        b = tensor(b).unsqueeze(1)  # Convert to column vector

        return A, b
