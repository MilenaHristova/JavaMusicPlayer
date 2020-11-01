package bg.sofia.uni.fmi.mjt.spotify;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Song {
	public String title;
	public String artist;
	public Path path;
	public int timesPlayed;
	
	public Song (String title, String artist, String filename) {
		this.title = title;
		this.artist = artist;
		Path basepath = Paths.get(".");
		this.path = Paths.get(basepath.toAbsolutePath().toString(), 
				"\\resources\\bg\\sofia\\uni\\fmi\\mjt\\spotify\\resources\\songs\\" + filename);
		this.timesPlayed = 0;
	}
	
	public Path getPath() {
		return this.path;
	}
	
	public String toString() {
		return this.title + "#" + this.artist;
	}
	
	public void incrementTimesPlayed() {
		timesPlayed++;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof Song) {
			Song other = (Song) obj;
			return this.artist.equals(other.artist) 
					&& this.title.equals(other.title);
		}
		return false;
	}

}
