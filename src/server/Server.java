package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import model.PaintObject;

/**
 * A Server that can handle multiple connects.  When started, this
 * code sends the Vector<PaintObect> to all connected clients
 * 
 * @author Chris Quevedo
 *
 */
public class Server {
	
	private static ServerSocket serverSocket;
	private static List<ObjectOutputStream> outputStreams = new Vector<>();
	private static List<PaintObject> allPaintObjects;
	private static ObjectOutputStream outputToClient;

	public static int PORT_NUMBER = 4011;

	public static void main(String args[]) throws IOException {
		//new Server();
		serverSocket = new ServerSocket(PORT_NUMBER);
	    allPaintObjects = new Vector<PaintObject>();
	    
	    while (true) {
	        Socket socket = serverSocket.accept();
	        ObjectInputStream inputFromClient = new ObjectInputStream(socket.getInputStream());
	        outputToClient = new ObjectOutputStream(socket.getOutputStream());

	        // Maintain a list of output streams so this server
	        // can write to clients
	        outputStreams.add(outputToClient);

	        // Give the entire list of PAINTOBJECTS to each new client 
	        outputToClient.writeObject(allPaintObjects);

	        // Start the loop that reads a client's writeObject in the background in a 
	        // different Thread so this server can also wait for new client connections in this
	        // loop. This new thread allows the Server to wait for each client's PAINTOBJECT send.
	        ClientHandler clientHandler = new ClientHandler(inputFromClient);
	        clientHandler.start();
	        //Thread thread = new Thread(clientHandler);
	        //thread.start();
	    }
	}
	
	// This thread is waiting for the client's writeObject. When read here
	// the new PAINTOBJECT is added to vector allPaintObjects. Then that
	// modified List<PaintObject> is written to all client's ObjectOutput Stream 
	  
	// This needs to be Runnable so the Thread can call the run method.
	//private static class ClientHandler implements Runnable {
	private static class ClientHandler extends Thread {

		private ObjectInputStream input;

	    public ClientHandler(ObjectInputStream inputFromClient) {
	    	this.input = inputFromClient;
	    }

	    @Override
	    public void run() {
	    	// This while(true) loop reads the new PAINTOBJECT from the client. When one
	    	// client sends a PAINTOBJECT, write the vector allPaintObejcts to all Clients.
	    	PaintObject pObj = null;
	    	while (true) {
	    		try {
	    			pObj = (PaintObject) input.readObject();
	    			allPaintObjects.add(pObj);
	    			// Send the updated vector to all Clients.
	    			// It is assumed the Client will know how to display them.
	    			for (ObjectOutputStream outputToClient : outputStreams) {
	    					outputToClient.reset();  
	    					outputToClient.writeObject(allPaintObjects);
	    			}
	    		} 
	    		catch (IOException e) {
	    		}
	    		catch (ClassNotFoundException e) {
	        	}
	    	}
	    }
	}
	
}
