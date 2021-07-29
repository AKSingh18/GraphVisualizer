package model;

public class UndirectedGraph extends Graph
{
    @Override
    public void addEdge(int source, int destination)
    {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }
}
