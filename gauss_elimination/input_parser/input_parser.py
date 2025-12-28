import numpy as np

def parse_input(file_path: str) -> np.ndarray:
    """
    Parse the text input file and return a matrix A and vector b for Gauss Elimination.

    Raises:
        FileNotFoundError: if file_path does not exist
    """
    with open(file_path, 'r') as file:
        n = int(file.readline().strip())

        A = [list(map(float, file.readline().strip().split(' '))) for _ in range(n)]
        b = list(map(float, file.readline().strip().split(' ')))

        A = np.array(A)
        b = np.array(b).reshape(-1, 1)  # Convert to column vector

        return A, b
