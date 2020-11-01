package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.ExceptionMessages;

public class Playlist {
	private static int num = 0;
	private String name;
	private Path path;
	private Set<Song> songs;
	private BufferedWriter writer;
	
	public Playlist(String name) {
		Path basePath = Paths.get(".");
		Path pathToFile = Paths.get(basePath.toAbsolutePath().toString(), "resources\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\resources\\playlists\\" + "playlist-" + num + ".txt");
		num++;
		
		try {
			this.writer = Files.newBufferedWriter(pathToFile, StandardCharsets.UTF_8);
			writer.write(name);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
		}

		this.path = pathToFile;
		this.name = name;
		this.songs = new HashSet<Song>();
	}
	
	public void addSong(Song song) {
		this.songs.add(song);
		try {
			writer.append(song.toString());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace(SpotifyServer.exceptionsWriter);
			throw new RuntimeException(ExceptionMessages.CANNOT_WRITE_TO_FILE_MSG);
		}
	}
	
	public String toString() {
		return songs.stream().map(s -> s.toString())
				.collect(Collectors.joining("\n"));
	}
	
	public Path getPath() {
		return this.path;
	}

}
