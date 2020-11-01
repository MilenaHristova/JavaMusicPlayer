package bg.sofia.uni.fmi.mjt.spotify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import bg.sofia.uni.fmi.mjt.spotify.exceptions.ExceptionMessages;

public class MusicDatabase {
	private Map<String, Song> songsByName;
	private Set<Song> songs; 
	private Map<String, Playlist> playlists;
	
	public MusicDatabase() {
		playlists = new HashMap<String, Playlist>();
		songsByName = new HashMap<String, Song>();
		
		songs = new TreeSet<Song>(new Comparator<Song>() {
            @Override 
            public int compare(Song first, Song second) {
            	if(first.timesPlayed >= second.timesPlayed) return 1;
            	else return -1;
            	}
            });
		
		addSongs();
	}
	
	public List<Song> searchSongs(String substr) {
		List<Song> res = songs.stream()
				.filter(s -> s.title.contains(substr) || s.artist.contains(substr))
				.collect(Collectors.toList());
		return res;
	}
	
	public String searchSongsToString(String substr) {
		List<Song> songs = searchSongs(substr);
		String res = songs.stream()
				.map(s -> s.title + " by " + s.artist)
				.collect(Collectors.joining("\n"));
		return res;
	}
	
	public String mostPlayed(int num) {
		String res = songs.stream().limit(num)
				.map(x -> x.title + " by " + x.artist)
				.collect(Collectors.joining("\n"));
		return res;
	}
	
	public void addSong(String title, String author, String pathToFile) {
		Song song = new Song(title, author, pathToFile);
		songsByName.put(title, song);
		songs.add(song);
	}
	
	public void addSongs() {
		addSong("scattered mind", "bettogh", "bettogh-scattered-mind.wav");
		addSong("on my way", "ghostrifter", "ghostrifter-on-my-way.wav");
		addSong("folker hero theme", "hayden", "hayden-folker-hero-theme.wav");
		addSong("modern love", "jay someday", "jay-someday-modern-love.wav");
		addSong("a magical journey through space", "leonell cassio", "leonell-cassio-a-magical-journey-through-space.wav");
		addSong("sirius", "maittre", "maittre-sirius.wav");
	}
	
	public Playlist createPlaylist(String name) {
		if(name.contains(" ")) {
			throw new RuntimeException(ExceptionMessages.INVALID_PLAYLIST_NAME_MSG);
		}
		try {
			Playlist playlist = new Playlist(name);
			this.playlists.put(name, playlist);
			return playlist;
		} catch (RuntimeException e) {
			throw new RuntimeException(ExceptionMessages.CANNOT_CREATE_PLAYLIST_MSG);
		}
	}
	
	public void addSongToPlaylist(String playlistName, String songName) {
		Playlist playlist = playlists.get(playlistName);
		if(playlist == null) {
			throw new RuntimeException(ExceptionMessages.PLAYLIST_NOT_FOUND_MSG);
		}
		Song song = songsByName.get(songName);
		if(song == null) {
			throw new RuntimeException(ExceptionMessages.SONG_NOT_FOUND_MSG);
		}
		playlist.addSong(song);
	}
	
	public String showPlaylist(String playlistName) {
		Playlist playlist = playlists.get(playlistName);
		if(playlist == null) {
			throw new RuntimeException(ExceptionMessages.PLAYLIST_NOT_FOUND_MSG);
		}
		return playlist.toString();
	}
	
	public Song getSong(String name) {
		Song song = songsByName.get(name);
		if(song == null) {
			throw new RuntimeException(ExceptionMessages.SONG_NOT_FOUND_MSG);
		}
		return song;
	}
}
