package model;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

public class Vertex extends StackPane
{
    private final Text vertexNumberText;
    private final double[] centre;
    private int vertexNumberInt;

    // GETTERS

    public double[] getCentre() { return centre; }
    public int intValue() { return vertexNumberInt; }

    // CONSTRUCTOR

    public Vertex(double radius, int vertexNumber, double centreX, double centreY)
    {
        centre = new double[2];
        centre[0] = centreX;
        centre[1] = centreY;

        vertexNumberInt = vertexNumber;

        // Configure vertex circle
        Circle circle = new Circle(radius);
        circle.getStyleClass().add("circle");

        // Configure vertex number
        this.vertexNumberText = new Text(String.valueOf(vertexNumber));
        this.vertexNumberText.getStyleClass().add("number");

        // Configure the vertex
        this.getChildren().addAll(circle, this.vertexNumberText);

        this.setLayoutX(centreX-radius);
        this.setLayoutY(centreY-radius);
    }

    // METHODS

    public void decrementVertexNumber()
    {
        vertexNumberInt--;
        vertexNumberText.setText(String.valueOf(vertexNumberInt));
    }
}
