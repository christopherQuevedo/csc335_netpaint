package model;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
/**
 * Picture
 * 
 * A Picture is a class extending PaintObject defining a picture that
 * can be drawn with two corner points and a String filename
 * 
 * @author Chris Quevedo
 *
 */
public class Picture extends PaintObject implements Serializable{

	private String filename;

	/**
	 * Create a picture with corner points from and to and filename str
	 * @param from
	 * @param to
	 * @param str
	 */
	public Picture(Point from, Point to, String str) {
		super(Color.BLACK, from, to);
		
		filename = str;
		filename = "file:images/" + filename;
		
	}

	/**
	 * draw(GraphicsContext gc)
	 * get the topleft corner coordinates from the corner points with some math.
	 * get the width and length of object with some maths.
	 * create image with these parameters and draw it
	 */
	@Override
	public void draw(GraphicsContext gc) {
		double topLeftX = Math.min(from.getX(), to.getX());
		double topLeftY = Math.min(from.getY(), to.getY());
		double w = Math.abs(to.getX() - from.getX());
		double l = Math.abs(to.getY() - from.getY());
		
		Image img = new Image(filename, w, l, false, false);
		gc.drawImage(img , topLeftX, topLeftY);
	}


}
