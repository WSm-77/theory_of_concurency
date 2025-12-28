from typing import Dict

def topological_sort(G: Dict[int, set]) -> list[int]:
    def dfs_visit(vertex):
        nonlocal G, V, sortIdx

        visited[vertex] = True

        for neighbour in G[vertex]:
            if not visited[neighbour]:
                dfs_visit(neighbour)

        topologicalSort[sortIdx] = vertex
        sortIdx -= 1
    #end def

    V = len(G)
    visited = {v: False for v in G}
    topologicalSort = [None]*V
    sortIdx = V - 1

    for vertex in G:
        if not visited[vertex]:
            dfs_visit(vertex)

    return topologicalSort
