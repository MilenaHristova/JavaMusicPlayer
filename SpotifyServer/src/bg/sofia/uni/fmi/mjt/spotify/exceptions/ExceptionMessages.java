package bg.sofia.uni.fmi.mjt.spotify.exceptions;

public class ExceptionMessages {
	public static final String USER_ALREADY_REGISTERED_MSG =
			"A user with this username already exists";
	public static final String WRITING_USER_IN_FILE_EXCEPTION_MSG = 
			"Unsuccessful writing user in database";
	public static final String UNSUCCESSFUL_LOGIN_MSG =
			"Incorrect username or password.";
	public static final String CANNOT_CREATE_FILE_MSG = "There was a problem creating the file.";
	public static final String CANNOT_WRITE_TO_FILE_MSG = "A problem occured while writing to the file.";
	public static final String SONG_NOT_FOUND_MSG = "There is no song with this name";
	public static final String PLAYLIST_NOT_FOUND_MSG = "There is no playlist with this name.";
	public static final String CANNOT_CREATE_PLAYLIST_MSG = "Cannot create playlist.";
	public static final String INVALID_PLAYLIST_NAME_MSG = "Playlist names cannot contain spaces.";
	public static final String USER_NOT_LOGGED_IN_MSG = "You should log in first.";
	public static final String USER_ALREADY_LOGGED_IN_MSG = "You are already logged in.";

}

