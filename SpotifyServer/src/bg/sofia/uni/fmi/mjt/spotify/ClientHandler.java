package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.ExceptionMessages;
import bg.sofia.uni.fmi.mjt.spotify.users.Users;

public class ClientHandler implements Runnable{
	private Socket socket;
	private String username;
	private boolean isLoggedIn;
	private DataInputStream input; 
	private DataOutputStream output;
	private MusicDatabase musicDatabase;
	private Users users;
	
	private PlayThread playThread;
	
	private static final String REGISTER_COMMAND = "register";
	private static final String LOGIN_COMMAND = "login";
	private static final String SEARCH_COMMAND = "search";
	private static final String TOP_SONGS_COMMAND = "top";
	private static final String CREATE_PLAYLIST_COMMAND = "create-playlist";
	private static final String ADD_SONG_TO_PLAYLIST_COMMAND = "add-song-to";
	private static final String SHOW_PLAYLIST_COMMAND = "show-playlist";
	private static final String PLAY_SONG_COMMAND = "play";
	private static final String STOP_COMMAND = "stop";
	private static final String DISCONNECT_COMMAND = "disconnect";
	
	private static final String SUCCESSFUL_REGISTER_MSG = "Registered user ";
	private static final String SUCCESSFUL_LOGIN_MSG = "You are logged in.";
	private static final String CREATE_PLAYLIST_MSG = "Playlist created";
	private static final String ADD_SONG_MSG = "The song is added to the playlist";
	
	public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output, MusicDatabase music) {
		this.socket = socket;
		this.input = input;
		this.output = output;
		this.users = new Users();
		this.musicDatabase = music; 
	}
	
	public void run() {   
        String received; 
        while (true)  
        { 
            try
            { 
                received = input.readUTF();    
                if(received.equals(DISCONNECT_COMMAND)){ 
                    break; 
                } 
                proccessCommand(received);
                
            } catch (IOException e) {
            	e.printStackTrace(SpotifyServer.exceptionsWriter);
            }
        } 
        try
        { 
            this.input.close(); 
            this.output.close(); 
            this.socket.close();
              
        } catch(IOException e) { 
            e.printStackTrace(SpotifyServer.exceptionsWriter); 
        } 
	}
	
	public synchronized void sendMessage(String message) {
		try {
			output.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
	}
	
	private void register(String[] tokens) {
		if(this.isLoggedIn) {
			sendMessage(ExceptionMessages.USER_ALREADY_LOGGED_IN_MSG);
			return;
		}
		String email = tokens[1];
		String password = tokens[2];
		users.registerUser(email, password);
		sendMessage(SUCCESSFUL_REGISTER_MSG + email);
	}
	
	private void login(String[] tokens) {
		if(this.isLoggedIn) {
			sendMessage(ExceptionMessages.USER_ALREADY_LOGGED_IN_MSG);
			return;
		}
		String username = tokens[1];
		String password = tokens[2];
		boolean loggedIn = users.logIn(username, password);
	    if(!loggedIn) {
	    	sendMessage(ExceptionMessages.UNSUCCESSFUL_LOGIN_MSG);
	    }
	    this.isLoggedIn = loggedIn;
	    sendMessage(SUCCESSFUL_LOGIN_MSG);
	}
	
	private void createPlaylist(String[] tokens) {
		if(!this.isLoggedIn) {
			sendMessage(ExceptionMessages.USER_NOT_LOGGED_IN_MSG);
			return;
		}
		String name = tokens[1];
		musicDatabase.createPlaylist(name);
		sendMessage(CREATE_PLAYLIST_MSG);
	}
	
	private void addSongToPlaylist(String[] tokens) {
		if(!this.isLoggedIn) {
			sendMessage(ExceptionMessages.USER_NOT_LOGGED_IN_MSG);
			return;
		}
		String nameOfPlaylist = tokens[1];
		String nameOfSong = "";
		for(int i = 2; i < tokens.length; i++) {
			nameOfSong += tokens[i] + " ";
		}
		musicDatabase.addSongToPlaylist(nameOfPlaylist, nameOfSong.trim());
	    sendMessage(ADD_SONG_MSG);
	}
	
	private void search(String[] tokens) {
		String searchTerm = "";
		for(int i = 1; i < tokens.length; i++) {
			searchTerm += tokens[i] + " ";
		}
		String songs = musicDatabase.searchSongsToString(searchTerm.trim());
	    sendMessage(songs);
	}
	
	private void topSongs(String[] tokens) {
		int num = Integer.parseInt(tokens[1].trim());
		String res = musicDatabase.mostPlayed(num);
		sendMessage(res);
	}
	
	private void playSong(String[] tokens) {
		String nameOfSong = "";
		for(int i = 1; i < tokens.length; i++) {
			nameOfSong += tokens[i] + " ";
		}
		Song song = musicDatabase.getSong(nameOfSong.trim());
		playThread = new PlayThread(musicDatabase, output, song);
		Thread play = new Thread(playThread);
		play.start();
	}
	
	private void showPlaylist(String[] tokens) {
		String name = tokens[1];
		String playlist = musicDatabase.showPlaylist(name);
		sendMessage(playlist);
	}
	
	public synchronized void proccessCommand(String received) {
		String[] tokens = received.split("\\s+");
    	String command = tokens[0];
    	try {
    		if(command.equals(REGISTER_COMMAND)) {
    			register(tokens);
        	} else if(command.equals(LOGIN_COMMAND)) {
        		login(tokens);
        	} else if (command.equals(CREATE_PLAYLIST_COMMAND)) {
        		createPlaylist(tokens);
        	} else if (command.equals(ADD_SONG_TO_PLAYLIST_COMMAND)) {
        		addSongToPlaylist(tokens);
        	} else if (command.equals(SHOW_PLAYLIST_COMMAND)) {
        		showPlaylist(tokens);
        	} else if (command.equals(SEARCH_COMMAND)) {
        		search(tokens);
        	} else if (command.equals(TOP_SONGS_COMMAND)) {
        		topSongs(tokens);
        	} else if (command.equals(PLAY_SONG_COMMAND)) {
        		playSong(tokens);
        	} else if (command.equals(STOP_COMMAND)) {
        		playThread.stopped = true;
        	}
    	} catch (RuntimeException e) {
    		sendMessage(e.getMessage());
    	}
    	
    }

}
