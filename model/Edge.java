package model;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

import static java.lang.Math.PI;

public class Edge extends Group
{
    // edge start and edge end is NOT the centre of startVertex and endVertex of the edge respectively
    final double[] start, end;
    // the edge angle lies between 0 to PI
    final double edgeAngle;
    final Vertex startVertex, endVertex;
    boolean isDirected;
    // An edge is bi-directional if its undirected OR (directed AND it connects both the vertices in both directions)
    boolean isBidirectional;

    // GETTER

    public Vertex getStartVertex() { return startVertex; }
    public Vertex getEndVertex() { return endVertex; }

    // CONSTRUCTOR

    private Edge(double[] start, double[] end, Vertex startVertex, Vertex endVertex, double edgeAngle, boolean isDirected)
    {
        this.start = start;
        this.end = end;
        this.edgeAngle = edgeAngle;
        this.startVertex = startVertex;
        this.endVertex = endVertex;
        this.isDirected = isDirected;
        // if the edge is undirected, then set isBidirectional to true
        // if its directed, then set it to false since if the edge is created for the first time then it will be uni-directional
        this.isBidirectional = !isDirected;

        Line edge = new Line(start[0], start[1], end[0], end[1]);
        edge.getStyleClass().add("line");

        this.getChildren().add(edge);

        if (isDirected) this.getChildren().add(getEdgeArrow());
    }

    // METHODS

    /* the method returns the coordinates of the rectangular region around the edge of fixed semi-length
       the rectangular region is used in deletion the edge. any click inside this region, will lead to
       deletion of the edge

       Rectangle labelling:

       There is no fixed rectangle labelling. It depends upon the edge orientation (whether it makes an acute angle or obtuse)
       and relative position of start and end points.

       CASE I: Edge makes obtuse angle. Start is left of End.

       A            D
        ____________
       |            |
       |            |
        ------------
       B            C

       CASE II: Edge makes obtuse angle. End is left of Start.

       D            A
        ____________
       |            |
       |            |
        ------------
       C            B

       CASE I: Edge makes acute angle. Start is left of End.

       B            C
        ____________
       |            |
       |            |
        ------------
       A            D

       CASE IV: Edge makes acute angle. End is left of Start.

       C            B
        ____________
       |            |
       |            |
        ------------
       D            A
    */

    public double[][] getEdgeRectangleCorners()
    {
        // semi-length of the rectangle
        final int semiLength = 4;

        // corners A, B, D and D of the rectangle
        double[] A = new double[2];
        double[] B = new double[2];
        double[] C = new double[2];
        double[] D = new double[2];

        // perpendicularEdgeAngle lies between 0 to PI
        double perpendicularEdgeAngle;
        if (edgeAngle < 0) perpendicularEdgeAngle = edgeAngle - PI/2;
        else perpendicularEdgeAngle = edgeAngle + PI/2;

        A[0] = start[0] + semiLength * Math.cos(perpendicularEdgeAngle);
        A[1] = start[1] + semiLength * Math.sin(perpendicularEdgeAngle);

        B[0] = start[0] - semiLength * Math.cos(perpendicularEdgeAngle);
        B[1] = start[1] - semiLength * Math.sin(perpendicularEdgeAngle);

        C[0] = end[0] - semiLength * Math.cos(perpendicularEdgeAngle);
        C[1] = end[1] - semiLength * Math.sin(perpendicularEdgeAngle);

        D[0] = end[0] + semiLength * Math.cos(perpendicularEdgeAngle);
        D[1] = end[1] + semiLength * Math.sin(perpendicularEdgeAngle);

        return new double[][]{A, B, C, D};
    }

    // the method is used to add an arrow to the given edge which is useful in case of directed graphs

    private Polygon getEdgeArrow()
    {
        // declare the needed variables
        final double arrowLength = 15;
        final double arrowAngle = Math.toRadians(45);
        int signFactor = end[1] < start[1] ? 1 : -1;

        // coordinate (x0, y0)
        double x0 = end[0] - signFactor*Math.cos(edgeAngle);
        double y0 = end[1] - signFactor*Math.sin(edgeAngle);

        // coordinate (x1, y1)
        double x1 = end[0] + signFactor*arrowLength*Math.cos(edgeAngle+arrowAngle);
        double y1 = end[1] + signFactor*arrowLength*Math.sin(edgeAngle+arrowAngle);

        // coordinate (x2, y2)
        double x2 = end[0] + signFactor*arrowLength*Math.cos(edgeAngle-arrowAngle);
        double y2 = end[1] + signFactor*arrowLength*Math.sin(edgeAngle-arrowAngle);

        // coordinate (x3, y3)
        double x3 = end[0] + signFactor*(arrowLength/3) * Math.cos(edgeAngle);
        double y3 = end[1] + signFactor*(arrowLength/3) * Math.sin(edgeAngle);

        // configure arrow head
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(x0, y0, x1, y1, x3, y3, x2, y2);
        arrowHead.getStyleClass().add("arrow-head");

        return arrowHead;
    }

    // STATIC METHOD

    public static Edge getEdge(Vertex startVertex, Vertex endVertex, double vertexRadius, boolean isDirected)
    {
        double[] startVertexCentre = startVertex.getCentre();
        double[] endVertexCentre = endVertex.getCentre();

        // offset makes sure that edge does not enter inside the vertex. it has been determined experimentally
        final double offset = 0.7;
        vertexRadius = vertexRadius + offset;
        // sign factor is necessary to move from vertex centre to its outer boundary
        int signFactor = startVertexCentre[1] < endVertexCentre[1] ? 1 : -1;

        // Math.atan(...) returns angle in radians between -PI/2 to PI/2. hence a check is made to keep the angle
        // between 0 to PI
        double edgeAngle = Math.atan((endVertexCentre[1]-startVertexCentre[1])/(endVertexCentre[0]-startVertexCentre[0]));
        if (edgeAngle < 0) edgeAngle += PI;

        // line end points : first row stores the start point and second row stores the end point
        double[][] endPoints = new double[2][2];

        // fill the start point
        endPoints[0][0] = startVertexCentre[0] + signFactor*vertexRadius*Math.cos(edgeAngle);
        endPoints[0][1] = startVertexCentre[1] + signFactor*vertexRadius*Math.sin(edgeAngle);

        // fill the end point
        endPoints[1][0] = endVertexCentre[0] - signFactor*vertexRadius*Math.cos(edgeAngle);
        endPoints[1][1] = endVertexCentre[1] - signFactor*vertexRadius*Math.sin(edgeAngle);

        return new Edge(endPoints[0], endPoints[1], startVertex, endVertex, edgeAngle, isDirected);
    }
}
