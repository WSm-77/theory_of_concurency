import graphviz as gv
from typing import Dict

def plot_graph(graph: Dict[int, set], word: str) -> None:
    dot = gv.Digraph(format='png')

    for vertex in graph:
        dot.node(str(vertex), label=word[vertex])

    for vertex in graph:
        for neighbour in graph[vertex]:
            dot.edge(str(vertex), str(neighbour))

    dot.render(f"graph_{word}")
