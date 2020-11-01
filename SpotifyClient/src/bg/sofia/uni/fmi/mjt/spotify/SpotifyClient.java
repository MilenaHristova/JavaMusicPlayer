package bg.sofia.uni.fmi.mjt.spotify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SpotifyClient {
	public static Path exceptionsFilePath;
			
	public static PrintWriter exceptionsWriter;
	
	private static final int SERVER_PORT = 7777;
	private static final String SERVER_HOST = "localhost";
	private String userName;
	
	public SpotifyClient() {
		try {
			Path basePath = Paths.get(System.getProperty("user.dir") + "/..").toRealPath();
			exceptionsFilePath = Paths.get(basePath.toString(), 
					"\\src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\exceptions\\exceptions.txt"); 
			exceptionsWriter = new PrintWriter(exceptionsFilePath.toFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	    
	public void execute() throws IOException {
	    Socket socket = getSocket();
	          
	    DataInputStream dataInStream = new DataInputStream(socket.getInputStream()); 
	    DataOutputStream dataOutStream = new DataOutputStream(socket.getOutputStream()); 
	  
	    ReadThread readThread = new ReadThread(dataInStream);
	    Thread readMessage = new Thread(readThread); 
	    WriteThread writeThread = new WriteThread(dataOutStream, readThread);
	    Thread sendMessage = new Thread(writeThread);      
	  
	    sendMessage.start(); 
	    readMessage.start(); 
	}
	    
	public Socket getSocket() {
	    InetAddress ip = null;
		try {
			ip = InetAddress.getByName(SERVER_HOST);
		} catch (UnknownHostException e) {
			e.printStackTrace(SpotifyClient.exceptionsWriter);
		} 
	    try {
			return new Socket(ip, SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace(SpotifyClient.exceptionsWriter);
			return null;
		}
	}
	    
	public static void main(String args[])
	{ 
	    SpotifyClient client = new SpotifyClient();
	    try {
	        client.execute();
	    } catch (IOException e) {
	        e.printStackTrace(SpotifyClient.exceptionsWriter);
	    }
	        
	} 
}
