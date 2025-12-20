import graphviz as gv
from typing import Dict

from task.task import Task

def plot_graph(graph: Dict[Task, set], graph_name: str) -> None:
    dot = gv.Digraph(format='png')

    for vertex in graph:
        dot.node(vertex.task_id, label=vertex.task_id)

    for vertex in graph:
        for neighbour in graph[vertex]:
            dot.edge(vertex.task_id, neighbour.task_id)
    dot.render(f"graph_{graph_name}")
