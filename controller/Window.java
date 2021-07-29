package controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

public class Window
{
    // VARIABLES

    // tools consists of addVertex, addEdge, deleteVertex and deleteEdge
    @FXML private ToggleGroup tools;

    @FXML private ToggleButton addVertex;
    @FXML private ToggleButton addEdge;
    @FXML private ToggleButton deleteVertex;
    @FXML private ToggleButton deleteEdge;

    // reference to the nested workspaceController object
    @FXML private Workspace workspaceController;

    // INITIALIZER

    // set the windowController of the workspace
    public void initialize()
    {
        workspaceController.setWindowController(this);
    }

    // GETTERS

    // it returns the ID of the current selected tool
    public String getSelectedTool()
    {
        if (tools.getSelectedToggle() != null) return ((ToggleButton)tools.getSelectedToggle()).getId();

        return null;
    }

    // To add hover effect onMouseEntered()
    @FXML
    private void onMouseEntered(MouseEvent mouseEvent)
    {
        Node source = (Node)mouseEvent.getSource();
        if (!source.equals(tools.getSelectedToggle())) source.setStyle("-fx-background-color: #DCDCDC");
    }

    // To remove the hover effect onMouseExited()
    @FXML
    private void onMouseExited(MouseEvent mouseEvent)
    {
        Node source = (Node)mouseEvent.getSource();
        if (!source.equals(tools.getSelectedToggle())) source.setStyle("-fx-background-color: transparent");
    }

    // to reset the screen
    @FXML
    private void onReset()
    {
        workspaceController.resetWorkspace();
    }

    // To add gradient on the selected ToggleButton and remove gradient from any other ToggleButton
    @FXML
    private void onToolsSelect()
    {
        addVertex.setStyle("-fx-background-color: transparent");
        addEdge.setStyle("-fx-background-color: transparent");
        deleteVertex.setStyle("-fx-background-color: transparent");
        deleteEdge.setStyle("-fx-background-color: transparent");

        ToggleButton selectedToggleButton = (ToggleButton) tools.getSelectedToggle();
        if (selectedToggleButton != null) selectedToggleButton.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, rgba(183, 222, 235, 0.50) 0%, rgba(147, 112, 219, 0.40) 100%)");
    }
}
