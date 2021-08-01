package model;

import java.util.ArrayList;

public class UndirectedGraph extends Graph
{
    @Override
    public void addEdge(int source, int destination)
    {
        adjacencyList.get(source).add(destination);
        adjacencyList.get(destination).add(source);
    }

    @Override
    public boolean isGraphDirected() {
        return false;
    }

    @Override
    void deleteEdge(int source, int destination)
    {
        // delete the edge from source to destination
        ArrayList<Integer> sourceNeighbours = adjacencyList.get(source);

        for (int i = 0; i < sourceNeighbours.size(); i++)
        {
            if (sourceNeighbours.get(i) == destination)
            {
                sourceNeighbours.remove(i);
                break;
            }
        }

        // delete the edge from destination to source
        ArrayList<Integer> destinationNeighbours = adjacencyList.get(destination);

        for (int i = 0; i < destinationNeighbours.size(); i++)
        {
            if (destinationNeighbours.get(i) == source)
            {
                destinationNeighbours.remove(i);
                break;
            }
        }
    }
}
