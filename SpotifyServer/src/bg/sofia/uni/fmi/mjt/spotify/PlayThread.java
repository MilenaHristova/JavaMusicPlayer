package bg.sofia.uni.fmi.mjt.spotify;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlayThread implements Runnable{
	MusicDatabase musicDatabase;
	private DataOutputStream output;
	public boolean stopped;
	private Song song;
	
	public PlayThread(MusicDatabase database, DataOutputStream output, Song song) {
		this.musicDatabase = database;
		this.output = output;
		this.song = song;
		this.stopped = false;
	}
	
	public AudioFormat getSongFormat(Song song) {
		Path path = song.getPath();
		try {
			AudioFormat audioFormat = AudioSystem
					.getAudioInputStream(path.toFile()).getFormat();
		    return audioFormat;
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
			return null;
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
			return null;
		}
	}
	
	public AudioInputStream getSongStream(Song song) {
		AudioInputStream stream = null;
		try {
			stream = AudioSystem.getAudioInputStream(song.getPath().toFile());
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
		return stream;
	}
	
	public void sendAudioFormat(AudioFormat audioFormat) {
		try {
			output.writeUTF("#AudioFormat#");
			output.writeUTF(audioFormat.getEncoding().toString());
			output.writeFloat(audioFormat.getSampleRate());
			output.writeInt(audioFormat.getSampleSizeInBits());
			output.writeInt(audioFormat.getChannels());
			output.writeInt(audioFormat.getFrameSize());
			output.writeFloat(audioFormat.getFrameRate());
			output.writeBoolean(audioFormat.isBigEndian());
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
	}

	@Override
	public void run() {
		AudioFormat format = getSongFormat(song);
		sendAudioFormat(format);
		AudioInputStream audioStream = getSongStream(song);
		int read;
		byte[] buff = new byte[4096];
		try {
			while(!stopped && (read = audioStream.read(buff)) > 0) {
				if(stopped) {
					return;
				}
				output.write(buff, 0, read);
			}
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}
	}
}
