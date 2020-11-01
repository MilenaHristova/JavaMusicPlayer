package bg.sofia.uni.fmi.mjt.spotify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.spotify.users.Users;
import junit.framework.Assert;

public class SpotifyServerTests {
	
	@Test
	public void searchSongsShouldFindCorrectSongs() {
		MusicDatabase music = new MusicDatabase();
		List<Song> result = music.searchSongs("si");
		
		assertEquals(2, result.size());
		
		long countFirst = result.stream()
		.filter(p -> p.title.toLowerCase() == "a magical journey through space")
		.count();
		assertEquals(countFirst, 1);
		
		long countSecond = result.stream()
				.filter(p -> p.title.toLowerCase() == "sirius")
				.count();
				assertEquals(countSecond, 1);
	}
	
	@Test
	public void createPlaylistShouldCreateNewFile() {
		MusicDatabase music = new MusicDatabase();
		Playlist playlist = music.createPlaylist("test");
		Path path = playlist.getPath();
		
		assertTrue(path.toFile().exists());
		try {
			Scanner reader = new Scanner(Files.newBufferedReader(path, StandardCharsets.UTF_8));
			assertTrue(reader.hasNext());
			assertEquals(reader.nextLine(), "test");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test(expected = RuntimeException.class)
	public void createPlaylistShouldThrowException() {
		MusicDatabase music = new MusicDatabase();
		Playlist playlist = music.createPlaylist("test 12");
	}
	
	@Test 
	public void addSongShouldWriteSongToFile() {
		MusicDatabase music = new MusicDatabase();
		Playlist playlist = music.createPlaylist("test");
		Song song = music.getSong("sirius");
		playlist.addSong(song);
		Path path = playlist.getPath();
		
		try {
			Scanner reader = new Scanner(Files.newBufferedReader(path, StandardCharsets.UTF_8));
			String name = reader.nextLine();
			assertTrue(reader.hasNext());
			assertEquals(reader.nextLine(), song.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void registeredUserShouldBeWrittenToTheFile() {
		Users users = new Users();
		users.registerUser("test", "pass");
		users.registerUser("test2", "pass");
		
		Path pathToFile = Paths.get("src\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\users\\users.txt");
		
		boolean firstExists = false;
		boolean secondExists = false;
		Scanner scanner = null;
		try {
			scanner = new Scanner(Files.newBufferedReader(pathToFile, StandardCharsets.UTF_8));
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (line.equals("test pass")) {
					firstExists = true;
				}
				if (line.equals("test2 pass")) {
					secondExists = true;
				}
			}
			assertEquals(firstExists, true);
			assertEquals(secondExists, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void registeredUserShouldLoginSuccessfuly() {
		Users users = new Users();
		users.registerUser("test", "pass");
		
		boolean login = users.logIn("test", "pass");
		assertEquals(login, true);
		
		boolean loginWrongPassword = users.logIn("test", "pass11");
		assertEquals(loginWrongPassword, false);
		
		boolean loginWrongName = users.logIn("test12", "pass11");
		assertEquals(loginWrongPassword, false);
	}
	
	

}
