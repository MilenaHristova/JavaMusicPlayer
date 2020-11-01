package bg.sofia.uni.fmi.mjt.spotify;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class WriteThread implements Runnable {
	private static final String DISCONNECT_COMMAND = "disconnect";
	private static final String STOP_COMMAND = "stop";

	private DataOutputStream dataOutStream;
    private Scanner scanner;
    ReadThread readThread;
	
	public WriteThread(DataOutputStream dataOutStream, ReadThread readThread) {
		this.dataOutStream = dataOutStream;
		scanner = new Scanner(System.in);
		this.readThread = readThread;
	}
	
	@Override
    public void run() { 
    	String msg = "";
        while (!msg.equals(DISCONNECT_COMMAND)) {  
            msg = scanner.nextLine(); 
            try { 
            	if(msg.equals(STOP_COMMAND)) {
            		dataOutStream.writeUTF(msg); 
                    readThread.stopped = true;
            	}
            	else dataOutStream.writeUTF(msg); 
            } catch (IOException e) { 
                e.printStackTrace(SpotifyClient.exceptionsWriter); 
            }
        } 
        try {
            dataOutStream.close();
		} catch (IOException e) {
			e.printStackTrace(SpotifyClient.exceptionsWriter);
		}
    } 

}
