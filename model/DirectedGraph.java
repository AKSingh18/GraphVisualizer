package model;

public class DirectedGraph extends Graph
{
    @Override
    public void addEdge(int source, int destination)
    {
        adjacencyList.get(source).add(destination);
    }
}
