package bg.sofia.uni.fmi.mjt.spotify;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.spotify.users.Users;

public class SpotifyServer {
	public static Path exceptionsFilePath = 
			Paths.get("src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\exceptions\\exceptions.txt"); 
	public static PrintWriter exceptionsWriter;
	
	private static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 7777;
    private static final int SLEEP_MILLIS = 200;
    public static Set<ClientHandler> clientHandlers = new HashSet<>();
    ServerSocket serverSocket;
    
    public SpotifyServer() {
    	try {
			exceptionsWriter = new PrintWriter(exceptionsFilePath.toFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public void execute() throws IOException {
    	serverSocket = new ServerSocket(SERVER_PORT); 
        System.out.println("Server running");
        Socket socket;
        
        while (true)  
        { 
            socket = getSocket(); 
 
            DataInputStream dataInStream = 
            		new DataInputStream(socket.getInputStream()); 
            DataOutputStream dataOutStream =
            		new DataOutputStream(socket.getOutputStream()); 
              
            ClientHandler handler = new ClientHandler(socket, dataInStream, dataOutStream, new MusicDatabase()); 
            Thread t = new Thread(handler); 
            clientHandlers.add(handler); 
            t.start(); 
        } 
    }
    
    public Socket getSocket() {
    	Socket socket = null;
    	try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace(exceptionsWriter);
		}
    	return socket;
    }
    
    public static void main(String[] args) throws IOException  
    { 
        SpotifyServer server = new SpotifyServer();
        server.execute();
    } 
}
