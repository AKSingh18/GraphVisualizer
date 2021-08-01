package model;

import javafx.scene.Node;

import java.util.ArrayList;

public abstract class Graph
{
    final ArrayList<Vertex> vertices;
    final ArrayList<Edge> edges;
    final double vertexRadius;
    int vertexCount;

    ArrayList<ArrayList<Integer>> adjacencyList;

    // CONSTRUCTOR

    public Graph()
    {
        vertexRadius = 24;
        vertexCount = 0;

        adjacencyList = new ArrayList<>();
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
    }

    // GETTER

    public double getVertexRadius() { return vertexRadius; }

    // METHODS

    public abstract boolean isGraphDirected();

    // method to add vertex from the adjacency list
    void addVertex()
    {
        adjacencyList.add(new ArrayList<>());
    }

    // method to be used by controller class to create a vertex
    public Node addVertex(double[] centre)
    {
        // add the to the adjacency list
        addVertex();

        Vertex vertex = new Vertex(vertexRadius, vertexCount++, centre[0], centre[1]);
        vertices.add(vertex);
        return vertex;
    }

    // method to add edge from the adjacency list
    abstract public void addEdge(int source, int destination);

    // method to be used by controller class to create an edge
    public Node addEdge(double[] previousClick, double[] currentClick, boolean isDirected)
    {
        Edge edge = null;

        int startVertexIndex = findVertexIndex(previousClick[0], previousClick[1]);
        int endVertexIndex = findVertexIndex(currentClick[0], currentClick[1]);

        // only try to add the edge if the user has clicked on vertices
        if (startVertexIndex != -1 && endVertexIndex != -1 && startVertexIndex != endVertexIndex)
        {
            Vertex startVertex = vertices.get(startVertexIndex);
            Vertex endVertex = vertices.get(endVertexIndex);

            final int source = startVertex.intValue();
            final int destination = endVertex.intValue();

            // check if there already exists an edge from start to end vertex, only add the edge if there does not
            if (noEdge(source, destination))
            {
                /*  new edge will be created only if two cases follow:
                        1: edge to be added is undirected
                        2: edge to be added is directed and no edge already exists from destination to source
                */
                if (!isDirected || noEdge(destination, source))
                {
                    addEdge(source, destination);
                    edge = Edge.getEdge(startVertex, endVertex, vertexRadius, isDirected);
                    edges.add(edge);
                }
                // if case 2 follows, then an reverse edge already exists. Find that reverse edge and set it as
                // bi-directional
                else
                {
                    edge = getEdge(destination, source);
                    if (edge != null)
                    {
                        addEdge(source, destination);
                        edge.setBidirectional(true);
                    }

                    // reset the edge to avoid duplicate entry
                    edge = null;
                }
            }
        }

        return edge;
    }

    // method to delete vertex from the adjacency list and update the adjacency list
    private void deleteVertex(int vertex)
    {
        // remove all the edges starting from that vertex
        adjacencyList.remove(vertex);

        /* remove all the edges ending at that vertex AND decrement the vertex number of all the vertices having vertex
           number than that of the vertex to be deleted
        */
        for (ArrayList<Integer> neighbours : adjacencyList)
        {
            int index = 0;

            while (index < neighbours.size())
            {
                int neighbour = neighbours.get(index);

                if (neighbour == vertex)
                {
                    neighbours.remove(index);
                    continue;
                }

                if (neighbours.get(index) > vertex) neighbours.set(index, neighbour-1);
                index++;
            }
        }
    }

    /* method to be used by controller class to delete a vertex. it returns a List of nodes to be deleted by the
       controller class. deletion of vertex will also lead to the deletion of any edge connected to the vertex.
       hence, a list of Node is returned.
    */
    public ArrayList<Node> deleteVertex(double[] coordinates)
    {
        ArrayList<Node> nodesToDelete = new ArrayList<>();
        int vertexIndex = findVertexIndex(coordinates[0], coordinates[1]);

        // if index >= 0 then the point of click is inside a vertex
        if (vertexIndex >= 0)
        {
            Vertex foundVertex = vertices.get(vertexIndex);

            // delete any edge which has this vertex has its starting or ending point
            int edgeIndex = 0;
            while (edgeIndex < edges.size())
            {
                Edge edge = edges.get(edgeIndex);

                if (edge.getStartVertex() == foundVertex || edge.getEndVertex() == foundVertex)
                {
                    edges.remove(edge);
                    nodesToDelete.add(edge);
                }
                else edgeIndex++;
            }

            // decrement the vertex number of all the vertices ahead of found vertex
            for (int i = vertexIndex+1;i < vertices.size();i++) vertices.get(i).decrementVertexNumber();

            deleteVertex(foundVertex.intValue());

            // delete the found vertex and update the vertexCount
            nodesToDelete.add(foundVertex);
            vertices.remove(vertexIndex);
            vertexCount--;
        }

        return nodesToDelete;
    }

    // method to delete edge from the adjacency list
    abstract void deleteEdge(int source, int destination);

    // method to be used by controller class to delete an edge
    public Edge deleteEdge(double[] coordinates)
    {
        int edgeIndex = edges.size() - 1;
        Edge edge = null;

        while (edgeIndex >= 0)
        {
            edge = edges.get(edgeIndex);

            if (pointLiesInsideRectangle(edge.getEdgeRectangleCorners(), coordinates)) break;

            edgeIndex--;
        }

        if (edgeIndex != -1)
        {
            /* edge will be deleted only if two cases follow:
               1: edge is undirected
               2: edge is directed and unidirectional
            */
            if (!edge.isDirected() || !edge.isBidirectional())
            {
                deleteEdge(edge.getStartVertex().intValue(), edge.getEndVertex().intValue());
                edges.remove(edge);
            }
            // if case 2 follows, then the edge is directed and bi-directional. In that case, make the edge as
            // unidirectional
            else
            {
                deleteEdge(edge.getEndVertex().intValue(), edge.getStartVertex().intValue());
                edge.setBidirectional(false);
                // reset the edge to avoid deletion
                edge = null;
            }

            return edge;
        }

        return null;
    }

    // method to be used by controller class to reset the graph
    public void reset()
    {
        vertexCount = 0;

        vertices.clear();
        edges.clear();

        adjacencyList = new ArrayList<>();
    }

    /* the method checks if the user clicked point lies inside the rectangle or not. help has been taken from stackoverflow.
       LINK: https://stackoverflow.com/questions/2752725/finding-whether-a-point-lies-inside-a-rectangle-or-not
    */

    private boolean pointLiesInsideRectangle(double[][] corners, double[] point)
    {
        double[] A = corners[0];
        double[] B = corners[1];
        double[] C = corners[2];
        double[] D = corners[3];

        return (isClockwise(A, B, point) && isClockwise(B, C, point) && isClockwise(C, D, point) && isClockwise(D, A, point)) ||
                (!isClockwise(A, B, point) && !isClockwise(B, C, point) && !isClockwise(C, D, point) && !isClockwise(D, A, point));
    }

    /* the method determines whether a directed segment p1-p2 is closer to a directed segment p1-p0
       in a clockwise direction or in a counterclockwise direction with respect to their common endpoint p1
    */

    private boolean isClockwise(double[] p2, double[] p1, double[] p0)
    {
        return ((p0[0]-p1[0])*(p2[1]-p1[1]) - (p0[1]-p1[1])*(p2[0]-p1[0])) > 0;
    }

    /* This method locates the vertex located closet to (x, y) coordinates of the workspace.
       WARNING: Current implementation of vertex uses StackPane to hold the vertex.
       getLayoutX() and getLayoutY() return the top-left most point of the StackPane. They DO NOT return the centre of the vertex.

       The vertex can be considered as enclosed inside a square of side equal to the vertex diameter. Any click event
       inside that square will lead to the deletion of that vertex or addition of any edge.
    */
    private int findVertexIndex(double x, double y)
    {
        int index = vertices.size()-1;
        Vertex vertex;
        double vertexX, vertexY;

        while (index >= 0)
        {
            vertex = vertices.get(index);
            vertexX = vertex.getLayoutX();
            vertexY = vertex.getLayoutY();

            // check if the point of click is inside the vertex
            if ((x-vertexX >= 0 && x-vertexX <= 2*vertexRadius) && (y-vertexY >= 0 && y-vertexY <= 2*vertexRadius)) break;

            index--;
        }

        return index;
    }

    /* This method checks if there is any edge present between source and destination
       It returns true if no edge is present between source and destination or else false.
    */
    private boolean noEdge(int source, int destination)
    {
        ArrayList<Integer> sourceNeighbours = adjacencyList.get(source);

        for (Integer sourceNeighbour : sourceNeighbours) if (sourceNeighbour == destination) return false;

        return true;
    }

    /* this method returns an edge if has the same source and destination as provided in the method
       parameters.
       it returns null if such edge is found.

       It is advised that noEdge(int, int) is used before calling this method.
    */
    private Edge getEdge(int source, int destination)
    {
        for (Edge edge: edges)
        {
            if (edge.getStartVertex().intValue()==source && edge.getEndVertex().intValue()==destination) return edge;
        }

        return null;
    }
}
