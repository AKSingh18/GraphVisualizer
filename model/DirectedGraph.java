package model;

import java.util.ArrayList;

public class DirectedGraph extends Graph {
    @Override
    public void addEdge(int source, int destination) {
        adjacencyList.get(source).add(destination);
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
    }
}
