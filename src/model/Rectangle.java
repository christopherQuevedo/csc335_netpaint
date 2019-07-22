package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;
/**
 * Rectangle
 * 
 * A Rectangle is a class extending PaintObject defining a rectangle that
 * can be drawn with two corner points and a color
 * 
 * @author Chris Quevedo
 *
 */

public class Rectangle extends PaintObject implements Serializable{

	/**
	 * Create a rectangle with Color color, corner points from and to
	 * @param color
	 * @param from
	 * @param to
	 */
	public Rectangle(Color color, Point from, Point to) {
		super(color, from, to);
	}

	/**
	 * draw(GraphicsContext gc)
	 * get the topleft corner coordinates from the corner points with some math.
	 * get the width and length of object with some maths.
	 * set color and fillRect
	 */
	@Override
	public void draw(GraphicsContext gc) {
		double topLeftX = Math.min(from.getX(), to.getX());
		double topLeftY = Math.min(from.getY(), to.getY());
		double w = Math.abs(to.getX() - from.getX());
		double l = Math.abs(to.getY() - from.getY());
		gc.setFill(ColorTypeConverter.Awt2Fx(color));
		gc.fillRect(topLeftX, topLeftY, w, l);
	}


}
