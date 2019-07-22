package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;
/**
 * Line
 * 
 * A Line is a class extending PaintObject defining a line that
 * can be drawn with two endpoints and a color
 * 
 * @author Chris Quevedo
 *
 */

public class Line extends PaintObject implements Serializable{

	/**
	 * Create a line with Color color, endpoints from and to
	 * @param color
	 * @param from
	 * @param to
	 */
	public Line(Color color, Point from, Point to) {
		super(color, from, to);
	}

	/**
	 * draw(GraphicsContext gc)
	 * sets the gc fill/stroke to the line color then simply strokes
	 * a line between the endpoints 
	 */
	@Override
	public void draw(GraphicsContext gc) {
	
		gc.setFill(ColorTypeConverter.Awt2Fx(color));
		gc.setStroke(ColorTypeConverter.Awt2Fx(color));
		gc.strokeLine(from.getX(), from.getY(), to.getX(), to.getY());
		
	}

}
