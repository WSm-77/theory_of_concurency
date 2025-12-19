import graphviz as gv
from typing import Dict

def plot_graph(graph: Dict[str, set], graph_name: str) -> None:
    dot = gv.Digraph(format='png')

    for vertex in graph:
        dot.node(vertex, label=vertex)

    for vertex in graph:
        for neighbour in graph[vertex]:
            dot.edge(vertex, neighbour)

    dot.render(f"graph_{graph_name}")
