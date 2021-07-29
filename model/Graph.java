package model;

import java.util.ArrayList;

public abstract class Graph
{
    final ArrayList<ArrayList<Integer>> adjacencyList;
    int vertices;

    public Graph()
    {
        adjacencyList = new ArrayList<>();
        vertices = 0;
    }

    public void addVertex()
    {
        adjacencyList.add(new ArrayList<>());
    }

    abstract public void addEdge(int source, int destination);
}
