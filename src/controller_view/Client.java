package controller_view;

import java.awt.Color;
import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Line;
import model.Oval;
import model.PaintObject;
import model.Picture;
import model.Rectangle;
import model.ColorTypeConverter;

/**
  * A JPanel GUI for Netpaint that has all paint objects drawn on it.
  * This file also represents the controller as it controls how paint objects
  * are drawn and sends new paint objects to the server. All Client objects
  * also listen to the server to read the Vector of PaintObjects and
  * repaint every time any client adds a new one. 
  * 
  * @author Chris Quevedo
  * 
 */
public class Client extends Application {
	
	private BorderPane all;
	private Canvas canvas;
	private GraphicsContext gc;
	private Color color;
	private ColorPicker colorPicker;
	private PaintObject curPaintObject;
	private RadioButton rb0;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private boolean drawing;
	private Vector<PaintObject> pObjects;
	private Socket socket;
	private ObjectOutputStream outputToServer;
	private ObjectInputStream inputFromServer;
	public static final String Address = "localhost";
	public static final int port = 4011;

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Initialize everything in start method
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Yeet");
		all = new BorderPane();
		curPaintObject = new Line(Color.BLUE, new Point(0,0), new Point(0,0));
		
		//set canvas
		setCanvas();
		//set radiobuttons
		setRadioButtons();
		//set color picker
		setColorPicker();
		
		//get our vector ready
		pObjects = new Vector<>();
		
		//initialize drawing to false
		drawing = false;
		
		// Set up this app to connect to a server
	    openConnection();
	    
		//make scene and show
		Scene scene = new Scene(all, 800, 650);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	// Connect to the server, assuming the Server is up and running.
	// Draw all the things that the server says to in its Vector
	@SuppressWarnings("unchecked")
	private void openConnection() {
	    // Our server is on our computer, but make sure to use the same port.
		try {
			socket = new Socket(Address, port);
			outputToServer = new ObjectOutputStream(socket.getOutputStream());
			inputFromServer = new ObjectInputStream(socket.getInputStream());

			// Start a new Thread that reads from the server
			pObjects = (Vector<PaintObject>) inputFromServer.readObject();
			drawVector(pObjects);
			ServerReader listener = new ServerReader();
			// SeverListener has a while(true) loop, so an instance must
			// be started in a new Thread to avoid freezing the GUI
			Thread thread = new Thread(listener);
			thread.start();
	    } 
		catch (IOException | ClassNotFoundException e) {
	    }
	}
	
	// Block for the Server to write a modified Vector<PaintObject> to client.
	// Because JavaFX runs in it own Thread. This must be started in a 
	// new Thread to avoid freezing this GUI.
	private class ServerReader implements Runnable {

	    @SuppressWarnings("unchecked")
	    @Override
	    public void run() {
	    	// Wait for writes from the server where readObject blocks.
	    	try {
	    		while (true) {
	    			pObjects = (Vector<PaintObject>) inputFromServer.readObject();
	    			drawVector(pObjects);
	    		}
	    	} 
	    	catch (IOException e) {
	    	} 
	    	catch (ClassNotFoundException e) {
	    	}
	    } 
	} 
	

	/**
	 * set up the canvas and fill it with white
	 * set up the mouse event handlers and write the private class
	 * immediately under.
	 */
	private void setCanvas() {
		
		canvas = new Canvas(800, 600);
		all.setTop(canvas);
		gc = canvas.getGraphicsContext2D();
		gc.setFill(ColorTypeConverter.Awt2Fx(Color.WHITE));
		gc.fillRect(0, 0, 800, 600);
		
		EventHandler<MouseEvent> mHandler = new MouseHandler();
		canvas.setOnMouseClicked(mHandler);
		canvas.setOnMouseMoved(mHandler);
		
	}
	private class MouseHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if(event.getEventType() == MouseEvent.MOUSE_CLICKED) {
				drawing = !drawing;
				if(drawing == true) {
					curPaintObject.setFrom(event.getX(), event.getY());
				}
				if(drawing == false) {
					try {
					pObjects.add(curPaintObject);
					outputToServer.writeObject(curPaintObject);
					curPaintObject = makeNewPaintObject();
					}
					catch(IOException ioe) {
					}
				}
			}
			else if(event.getEventType() == MouseEvent.MOUSE_MOVED) {
				if(drawing) {
					drawVector(pObjects);
					curPaintObject.setTo(event.getX(), event.getY());
					curPaintObject.draw(gc);
					
				}
			}
			
		}
	}

	/**
	 * Set up the radiobuttons and make it so they toggle together
	 * put them in a gridpane then that in the main borderpane
	 * set up the radiobutton handlers and make the private class
	 * just below this method
	 */
	private void setRadioButtons() {
	    //create the buttons
		rb0 = new RadioButton("Line");
		rb1 = new RadioButton("Rectangle");
		rb2 = new RadioButton("Oval");
		rb3 = new RadioButton("Picture");
		
		//create the toggle group and set rb0 to true
		ToggleGroup radioGroup = new ToggleGroup();
	    rb0.setToggleGroup(radioGroup);
	    rb1.setToggleGroup(radioGroup);
	    rb2.setToggleGroup(radioGroup);
	    rb3.setToggleGroup(radioGroup);
	    rb0.setSelected(true);
	    
	    //put them in the GUI
	    GridPane gPane = new GridPane();
	    gPane.setHgap(20);
	    gPane.add(rb0, 1, 0);
	    gPane.add(rb1, 2, 0);
	    gPane.add(rb2, 3, 0);
	    gPane.add(rb3, 4, 0);
	    all.setLeft(gPane);
	    
	    //setOnAction
	    EventHandler<ActionEvent> rHandler = new RadioHandler(); 
	    rb0.setOnAction(rHandler);
	    rb1.setOnAction(rHandler);
	    rb2.setOnAction(rHandler);
	    rb3.setOnAction(rHandler);
    }
	private class RadioHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			RadioButton clicked = (RadioButton) event.getSource();
			Point newFrom = new Point(0, 0);
			Point newTo = new Point(0, 0);
			if(clicked == rb0) {
				//make a line
				curPaintObject = new Line(color, newFrom, newTo);
			}
			else if(clicked == rb1) {
				//make a rectangle
				curPaintObject = new Rectangle(color, newFrom, newTo);
			}
			else if(clicked == rb2) {
				//make an oval
				curPaintObject = new Oval(color, newFrom, newTo);
			}
			else if(clicked == rb3) {
				//make a picture
				curPaintObject = new Picture(newFrom, newTo, "doge.jpeg");
			}
		}
		
	}
	
	/**
	 * Set up the colorpicker and put it in gui
	 * initialize to blue and add handler
	 * Set up private class right below
	 */
	private void setColorPicker() {
		//create the color picker and initialize blue
		colorPicker = new ColorPicker();
		colorPicker.setValue(ColorTypeConverter.Awt2Fx(Color.BLUE));
		color = ColorTypeConverter.Fx2Awt(colorPicker.getValue());
		
		//put in GUI
		all.setRight(colorPicker);
		
		//setOnAction
		colorPicker.setOnAction(new ColorChanger());

	}
	private class ColorChanger implements EventHandler<ActionEvent> {

	    @Override
	    public void handle(ActionEvent event) {
	    	
	        color = ColorTypeConverter.Fx2Awt(colorPicker.getValue());
	        curPaintObject.setColor(color);
	        
	    }
	}
	
	/**
	 * method to draw all of the saved PaintObjects back onto the canvas
	 * @param pObjects - the vector with all of the objects that have been drawn
	 */
	public void drawVector(Vector<PaintObject> pObjects) {
		gc.setFill(ColorTypeConverter.Awt2Fx(Color.WHITE));
		gc.fillRect(0, 0, 800, 600);
		for (PaintObject po : pObjects) {
			po.draw(gc);
		}
	}

	/**
	 * Once a mouse click is made to stop drawing, the curPaintObject must be put in 
	 * the vector and we need a new curPaintObject that is not the same one we just 
	 * put in the vector.  So we make a new PaintObject based upon what the radio
	 * buttons say to make.
	 * @return returns a newPaintObject back to curPaintObject
	 */
	public PaintObject makeNewPaintObject() {
		PaintObject newPaintObject;
		Point newFrom = new Point(0, 0);
		Point newTo = new Point(0, 0);
		if(rb0.isSelected()) {
			newPaintObject = new Line(color, newFrom, newTo);
		}
		else if(rb1.isSelected()) {
			newPaintObject = new Rectangle(color, newFrom, newTo);
		}
		else if(rb2.isSelected()) {
			newPaintObject = new Oval(color, newFrom, newTo);
		}
		else if(rb3.isSelected()) {
			newPaintObject = new Picture(newFrom, newTo, "doge.jpeg");
		}
		else {
			return null;
		}
		return newPaintObject;
		
	}

	
	
}