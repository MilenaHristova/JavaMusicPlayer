package bg.sofia.uni.fmi.mjt.spotify;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ReadThread implements Runnable {
	private static final String DISCONNECTED_MESSAGE = "Disconnected from server";
	private static final String PLAY_MESSAGE = "#AudioFormat#";
	
    private DataInputStream dataInStream;
    public boolean stopped;
    SourceDataLine dataLine;

	public ReadThread(DataInputStream dataInStream) {
		this.dataInStream = dataInStream;
		stopped = false;
	}
	
	public AudioFormat getAudioFormat() {
		try {
			AudioFormat.Encoding encoding = new AudioFormat.Encoding(dataInStream.readUTF());
		    float sampleRate = dataInStream.readFloat();
		    int sampleSizeInBits = dataInStream.readInt();
		    int channels = dataInStream.readInt();
		    int frameSize = dataInStream.readInt();
		    float frameRate = dataInStream.readFloat();
		    boolean bigEndian = dataInStream.readBoolean();
		    
		    AudioFormat audioFormat = 
		    		new AudioFormat(encoding, sampleRate, sampleSizeInBits,
		    				channels, frameSize, frameRate, bigEndian);
		    return audioFormat;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void play() {
		AudioFormat audioFormat = getAudioFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open();
			dataLine.start();
			
			int BUFFER_SIZE = 4096;
			 
			byte[] bytesBuffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			 
			while ((bytesRead = dataInStream.read(bytesBuffer)) != -1) {
			   if(stopped) {
			    	dataLine.stop();
			    	dataLine.flush();
			    	dataLine.close();
			    	while(dataInStream.available() > 0) {
			    		dataInStream.read(bytesBuffer);
			    	}
			    	return;
			    }
				dataLine.write(bytesBuffer, 0, bytesRead);
			}

			dataLine.drain();
			dataLine.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void run() { 
    	String msg = "";
    	while(true) {
            try {  
            	msg = dataInStream.readUTF();
            	if(msg.equals(PLAY_MESSAGE)) {
            		play();
            	} else {
            		System.out.println(msg); 	
            	}
            } catch (IOException e) { 
            	e.printStackTrace(SpotifyClient.exceptionsWriter);
            	SpotifyClient.exceptionsWriter.flush();
            	System.out.println(DISCONNECTED_MESSAGE); 	
                break;
            }  
        } 
    }

}
