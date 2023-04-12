// Maliha Tarafdar and Pranav Tripuraneni

package model;

public class Song implements Comparable<Song> {
	private String title;
	private String artist;
	private String album;
	private String year;

	public Song(String title, String artist, String album, String year) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
	}

	public Song(String title, String artist) {
		this.title = title;
		this.artist = artist;
		this.album = "";
		this.year = "";
	}

	public String getTitle() {
		return this.title;
	}

	public String getArtist() {
		return this.artist;
	}

	public String getAlbum() {
		return this.album;
	}

	public String getYear() {
		return this.year;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public void setYear(String year) {
		this.year = year;
	}

	@Override
	public String toString() {
		return this.title + " | " + this.artist;
	}

	@Override
	public boolean equals(Object song) {
		// 2 songs are equal if they have the same title and artist
		if (song == this) {
			return true;
		}
		if (song == null || !(song instanceof Song)) {
			return false;
		}
		return title.equals(((Song) song).getTitle()) && artist.equals(((Song) song).getArtist());
	}

	@Override
	public int compareTo(Song otherSong) {
		String title = this.title.toLowerCase();
		String artist = this.artist.toLowerCase();
		String otherTitle = otherSong.getTitle().toLowerCase();
		String otherArtist = otherSong.getArtist().toLowerCase();

		// compare: title -> artist
		int compare = title.compareTo(otherTitle);
		if (compare != 0) {
			return compare;
		}
		return artist.compareTo(otherArtist);
	}

}
