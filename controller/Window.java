package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.DirectedGraph;
import model.Graph;

import java.util.ArrayList;

public class Window
{
    // VARIABLES

    // tools consists of addVertex, addEdge, deleteVertex and deleteEdge ToggleButton
    @FXML private ToggleGroup tools;
    // graph type consists of directed and undirected ToggleButton
    @FXML private ToggleGroup graphType;

    @FXML private ToggleButton undirected;
    @FXML private ToggleButton directed;

    @FXML private AnchorPane workspace;

    // By default, the graph is undirected
    Graph graph;
    private double previousClickX, previousClickY;

    // CONSTRUCTOR
    public Window()
    {
        graph = new DirectedGraph();
        previousClickX = previousClickY = -1;
    }

    // INITIALIZER

    public void initialize()
    {
        // disable the radio-button style class
        undirected.getStyleClass().remove("radio-button");
        directed.getStyleClass().remove("radio-button");

        // select the graph toggle by default
        graphType.selectToggle(undirected);
    }

    // GETTERS

    // it returns the ID of the current selected tool
    public String getSelectedTool()
    {
        if (tools.getSelectedToggle() != null) return ((ToggleButton)tools.getSelectedToggle()).getId();

        return null;
    }

    // it returns the ID of the current selected graph

    // EVENT HANDLERS

    // following event takes place onMouseClicked(MouseEvent mouseEvent)
    @FXML
    private void updateWorkspace(MouseEvent mouseEvent)
    {
        String selectedTool = getSelectedTool();

        if (selectedTool != null)
        {
            if (selectedTool.equals("addVertex")) addVertex(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("addEdge")) addEdge(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("deleteVertex")) deleteVertex(mouseEvent.getX(), mouseEvent.getY());
            if (selectedTool.equals("deleteEdge")) deleteEdge(mouseEvent.getX(), mouseEvent.getY());
        }
    }

    // to reset the screen
    @FXML
    private void onReset()
    {
        workspace.getChildren().clear();
        graph.reset();
    }

    // METHODS

    private void addVertex(double x, double y)
    {
        // add the vertex on the screen only if its fits the workspace
        double vertexRadius = graph.getVertexRadius();
        if (x-vertexRadius <= 0 || y-vertexRadius <= 0 || x+vertexRadius >= workspace.getWidth() || y+vertexRadius >= workspace.getHeight()) return;

        workspace.getChildren().add(graph.addVertex(new double[]{x, y}));
    }

    private void addEdge(double x, double y)
    {
        Node nodeToAdd = graph.addEdge(new double[]{previousClickX, previousClickY}, new double[]{x, y}, true);

        if (nodeToAdd != null)
        {
            workspace.getChildren().add(nodeToAdd);

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
        ArrayList<Node> nodesToDelete = graph.deleteVertex(new double[]{x, y});

        for (Node node : nodesToDelete) workspace.getChildren().remove(node);
    }

    private void deleteEdge(double x, double y)
    {
        Node nodeToDelete = graph.deleteEdge(new double[]{x, y});

        workspace.getChildren().remove(nodeToDelete);
    }


}
