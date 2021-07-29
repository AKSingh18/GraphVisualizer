package controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import src.Edge;
import src.Vertex;

import java.util.ArrayList;

public class Workspace
{
    // VARIABLES

    private Window windowController;
    private final ArrayList<Vertex> vertices;
    private final ArrayList<Edge> edges;
    private final double vertexRadius;
    private int vertexCount;
    private double previousClickX, previousClickY;

    @FXML private AnchorPane workspace;

    // CONSTRUCTOR

    public Workspace()
    {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        vertexRadius = 24;
        vertexCount = 0;
        previousClickX = previousClickY = -1;
    }

    // SETTER

    public void setWindowController(Window windowController)
    {
        this.windowController = windowController;
    }

    // EVENT HANDLERS

    // following event takes place onMouseClicked(MouseEvent mouseEvent)
    @FXML
    private void updateWorkspace(MouseEvent mouseEvent)
    {
        String selectedTool = windowController.getSelectedTool();
        if (selectedTool != null)
        {
            if (selectedTool.equals("addVertex")) addVertex(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("addEdge")) addEdge(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("deleteVertex")) deleteVertex(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("deleteEdge")) deleteEdge(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    // METHODS

    private void addVertex(double x, double y)
    {
        // add the vertex on the screen only if its fits the workspace
        if (x-vertexRadius <= 0 || y-vertexRadius <= 0 || x+vertexRadius >= workspace.getWidth() || y+vertexRadius >= workspace.getHeight()) return;

        Vertex vertex = new Vertex(vertexRadius, vertexCount++, x, y);
        workspace.getChildren().add(vertex);
        vertices.add(vertex);

        AnchorPane.setTopAnchor(vertex, y-vertexRadius);
        AnchorPane.setLeftAnchor(vertex, x-vertexRadius);
    }

    private void addEdge(double x, double y)
    {
        int startVertexIndex = findVertexIndex(previousClickX, previousClickY);
        int endVertexIndex = findVertexIndex(x, y);

        if (startVertexIndex != -1 && endVertexIndex != -1 && startVertexIndex != endVertexIndex)
        {
            Vertex startVertex = vertices.get(startVertexIndex);
            Vertex endVertex = vertices.get(endVertexIndex);

            Edge edge = Edge.getEdge(startVertex, endVertex, vertexRadius, true);

            edges.add(edge);
            workspace.getChildren().add(edge);
            // reset the previous click
            previousClickX = previousClickY = -1;
        }
        else
        {
            previousClickX = x;
            previousClickY = y;
        }
    }

    private void deleteVertex(double x, double y)
    {
        int vertexIndex = findVertexIndex(x, y);

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
                    workspace.getChildren().remove(edge);
                }
                else edgeIndex++;
            }

            // decrement the vertex number of all the vertices ahead of found vertex
            for (int i = vertexIndex+1;i < vertices.size();i++) vertices.get(i).decrementVertexNumber();

            // delete the found vertex and update the vertexCount
            workspace.getChildren().remove(foundVertex);
            vertices.remove(vertexIndex);
            vertexCount--;
        }
    }

    private void deleteEdge(double x, double y)
    {
        int edgeIndex = edges.size() - 1;
        Edge edge = null;

        while (edgeIndex >= 0)
        {
            edge = edges.get(edgeIndex);

            if (pointLiesInsideRectangle(edge.getEdgeRectangleCorners(), new double[]{x, y})) break;

            edgeIndex--;
        }

        if (edgeIndex != -1)
        {
            edges.remove(edge);
            workspace.getChildren().remove(edge);
        }
    }

    public void resetWorkspace()
    {
        workspace.getChildren().clear();
        vertices.clear();
        edges.clear();

        previousClickX = previousClickY = -1;
        vertexCount = 0;
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
}
