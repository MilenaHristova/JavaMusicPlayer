package bg.sofia.uni.fmi.mjt.spotify.users;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import bg.sofia.uni.fmi.mjt.spotify.SpotifyServer;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.ExceptionMessages;

public class Users {
	private Path usersFile;
    private HashMap<String, String> users;
    private BufferedWriter writer;
    private Scanner reader;
    private final String USERS_FILE = "src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\users\\users.txt";
    
    public Users() {
    	this.users = new HashMap<String, String>();
    	//Path basePath = Paths.get(".");
		usersFile = Paths.get(USERS_FILE);
    	
		try {
    		writer = Files.newBufferedWriter(usersFile, StandardCharsets.UTF_8);
    		reader = new Scanner(Files.newBufferedReader(usersFile, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
    }
    
    private boolean isRegistered(String username) {
    	try {
			reader = new Scanner(Files.newBufferedReader(usersFile, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	while(reader.hasNext()) {
			String nextLine = reader.nextLine();
			String[] tokens = nextLine.split(" ");
			if(tokens[0].equals(username)) {
				return true;
			}
		}
    	return false;
    }
	
	public synchronized void registerUser(String username, String password) {
		if(isRegistered(username)) {
			throw new RuntimeException(ExceptionMessages.USER_ALREADY_REGISTERED_MSG);
		}
		users.put(username, password);
		try {
			writer.append(username + " " + password);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
			throw new RuntimeException(ExceptionMessages.WRITING_USER_IN_FILE_EXCEPTION_MSG);
		}
	}
	
	public boolean logIn(String username, String password) {
		try {
			reader = new Scanner(Files.newBufferedReader(usersFile, StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
		while(reader.hasNext()) {
			String nextLine = reader.nextLine();
			String[] tokens = nextLine.split("\\s+");
			if(tokens[0].equals(username)) {
				return tokens[1].equals(password);
			}
		}
		return false;
	}

}
