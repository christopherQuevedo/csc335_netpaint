package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;
/**
 * PaintObject
 * 
 * <p>A PaintObject is an abstract class defining a shape that
 * can be drawn with two points <p>
 * 
 * @author Chris Quevedo
 *
 */
public abstract class PaintObject implements Serializable{
	
	protected Color color;
	protected Point from;
	protected Point to;

	/**
	 * Create a general paint object of Color color, with corner points at
	 * Point to and Point from
	 * @param color - color of PaintObject
	 * @param from - corner point
	 * @param to - corner point
	 */
	public PaintObject(Color color, Point from, Point to) {
		this.color = color;
		this.from = from;
		this.to= to;
	}
	
	//Setters
	public void setColor(Color newColor) {
		this.color = newColor;
	}
	public void setFrom(double x, double y) {
		this.from.setLocation(x, y);
	}
	public void setTo(double x, double y) {
		this.to.setLocation(x, y);
	}
	
	/**
	 * abstract draw method to draw the objects.
	 * @param gc - the graphicscontext of the main canvas
	 */
	public abstract void draw(GraphicsContext gc);

}