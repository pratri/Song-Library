// Maliha Tarafdar and Pranav Tripuraneni

package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import model.Song;

public class MainController {
	@FXML
	private AnchorPane songPane;
	@FXML
	private ListView<Song> songListView;
	@FXML
	private Pane songAddPane, songDetailsPane, songEditPane;
	@FXML
	private Button addButton, editButton, deleteButton, addSaveButton, addCancelButton, editSaveButton,
			editCancelButton;
	@FXML
	private Label titleLabel, artistLabel, albumLabel, yearLabel;
	@FXML
	private Text emptyText;
	@FXML
	private TextField addTitleField, addArtistField, addAlbumField, addYearField, editTitleField, editArtistField,
			editAlbumField, editYearField;

	public ObservableList<Song> songList;

	public void shutdown(Stage stage) {
		// save songs to file on program close
		stage.setOnCloseRequest(event -> {
			System.out.println("saving songs");
			try {
				File file = new File("./data/songs.txt");
				file.getParentFile().mkdirs();
				file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for (Song song : songList) {
					writer.write(song.getTitle() + "\t");
					writer.write(song.getArtist() + "\t");
					writer.write(song.getAlbum() + "\t");
					writer.write(song.getYear() + "\n");
				}
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void initialize() {
		songList = FXCollections.observableArrayList();

		// use data file to fill songList
		try (BufferedReader reader = new BufferedReader(new FileReader("./data/songs.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("\t");
				if (fields.length == 2) {
					songList.add(new Song(fields[0], fields[1]));
				} else if (fields.length == 4) {
					songList.add(new Song(fields[0], fields[1], fields[2], fields[3]));
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("file does not exist");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// sort songList and set ListView
		FXCollections.sort(songList);
		songListView.setItems(songList);
		songListView.getSelectionModel().selectFirst();

		// show songAddPane if songList is empty
		showSongDetails();
		if (songList.isEmpty()) {
			showSongAdd();
		}

		// add listener for ListView select
		songListView.getSelectionModel().selectedIndexProperty()
				.addListener((obs, oldVal, newVal) -> showSongDetails());

		// add button listeners
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showSongAdd();
			}
		});
		editButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showSongEdit();
			}
		});
		deleteButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				deleteSong();
			}
		});

	}

	private void showSongDetails() {
		songPane.getChildren().setAll(songDetailsPane); // switch pane

		int index = songListView.getSelectionModel().getSelectedIndex();

		if (songList.isEmpty()) {
			emptyText.setVisible(true);
			titleLabel.setText("");
			artistLabel.setText("");
			albumLabel.setText("");
			yearLabel.setText("");
		} else if (index >= 0) {
			emptyText.setVisible(false);
			// update labels
			titleLabel.setText("Title: " + songList.get(index).getTitle());
			artistLabel.setText("Artist: " + songList.get(index).getArtist());
			albumLabel.setText("Album: " + songList.get(index).getAlbum());
			yearLabel.setText("Year: " + songList.get(index).getYear());
		}
	}

	private void showSongAdd() {
		songPane.getChildren().setAll(songAddPane); // switch pane
	}

	private void showSongEdit() {
		songPane.getChildren().setAll(songEditPane); // switch pane

		// cannot edit if no song selected
		if (songListView.getSelectionModel().getSelectedIndex() < 0) {
			Alert editError = new Alert(Alert.AlertType.ERROR);
			editError.setTitle("Edit Failed");
			editError.setHeaderText("Cannot edit when no song is selected.");
			editError.showAndWait();
			showSongDetails();
		}
	}

	public void saveAddedSong(ActionEvent e) {
		String title = addTitleField.getText().trim();
		String artist = addArtistField.getText().trim();
		String album = addAlbumField.getText().trim();
		String year = addYearField.getText().trim();

		if (validateSong(title, artist, album, year)) {
			// add confirmation alert
			Alert addConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
			addConfirmation.setTitle("Add Song");
			addConfirmation.setHeaderText("Are you sure you want to add the following song?");
			addConfirmation.setContentText(
					"Title: " + title + "\nArtist: " + artist + "\nAlbum: " + album + "\nYear: " + year);

			Optional<ButtonType> result = addConfirmation.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// add song
				Song song = new Song(title, artist, album, year);
				if (!songList.contains(song)) {
					songList.add(song);
					songListView.getSelectionModel().selectLast();
					FXCollections.sort(songList);
					showSongDetails();

					// clear text fields
					addTitleField.clear();
					addArtistField.clear();
					addAlbumField.clear();
					addYearField.clear();
				} else {
					// show alert if song already exists
					Alert songExistsError = new Alert(Alert.AlertType.ERROR);
					songExistsError.setTitle("Add Failed");
					songExistsError.setHeaderText("Cannot add duplicate song. Try again.");
					songExistsError.showAndWait();
				}
			}
		}
	}

	public void cancelAddedSong(ActionEvent e) {
		showSongDetails();
	}

	public void saveEditedSong(ActionEvent e) {
		String title = editTitleField.getText().trim();
		String artist = editArtistField.getText().trim();
		String album = editAlbumField.getText().trim();
		String year = editYearField.getText().trim();

		if (validateSong(title, artist, album, year)) {
			int index = songListView.getSelectionModel().getSelectedIndex();

			// edit confirmation alert
			Alert editConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
			editConfirmation.setTitle("Edit Song");
			editConfirmation.setHeaderText("Are you sure you want to edit the following song?");
			editConfirmation.setContentText("Previous Title: " + songList.get(index).getTitle() + "\n->New Title: "
					+ title + "\nPrevious Artist: " + songList.get(index).getArtist() + "\n->New Artist: " + artist
					+ "\nPrevious Album: " + songList.get(index).getAlbum() + "\n->New Album: " + album
					+ "\nPrevious Year: " + songList.get(index).getYear() + "\n->New Year: " + year);

			Optional<ButtonType> result = editConfirmation.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// remove previous song
				songList.remove(index);

				// add edited song
				Song song = new Song(title, artist, album, year);
				if (!songList.contains(song)) {
					songList.add(song);
					songListView.getSelectionModel().selectLast();
					FXCollections.sort(songList);
					showSongDetails();

					// clear text fields
					editTitleField.clear();
					editArtistField.clear();
					editAlbumField.clear();
					editYearField.clear();
				} else {
					// show alert if song already exists
					Alert songExistsError = new Alert(Alert.AlertType.ERROR);
					songExistsError.setTitle("Edit Failed");
					songExistsError.setHeaderText("Cannot save duplicate song. Try again.");
					songExistsError.showAndWait();
				}
			}
		}
	}

	public void cancelEditedSong(ActionEvent e) {
		showSongDetails();
	}

	private void deleteSong() {
		if (songList.isEmpty()) {
			// show alert if songList is empty
			Alert deleteError = new Alert(Alert.AlertType.ERROR);
			deleteError.setTitle("Delete Failed");
			deleteError.setHeaderText("Cannot delete when song library is empty.");
			deleteError.showAndWait();
			return;
		}

		int index = songListView.getSelectionModel().getSelectedIndex();

		// delete confirmation alert
		Alert deleteConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
		deleteConfirmation.setTitle("Delete Song");
		deleteConfirmation.setHeaderText("Are you sure you want to delete the following song?");
		deleteConfirmation.setContentText(songList.get(index).getTitle() + " by " + songList.get(index).getArtist());

		Optional<ButtonType> result = deleteConfirmation.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// delete song and select next if possible, otherwise select previous
			songList.remove(index);
			songListView.getSelectionModel().select(index < songList.size() ? index : index - 1);
			showSongDetails();
		}
	}

	// a valid song has title and artist that is non-empty, no fields contain '|',
	// and the year is a positive integer
	private boolean validateSong(String title, String artist, String album, String year) {
		if (title.isEmpty() || artist.isEmpty() || title.contains("|") || artist.contains("|") || album.contains("|")
				|| (!year.isEmpty() && !isPositiveInteger(year))) {
			Alert addError = new Alert(Alert.AlertType.ERROR);
			addError.setTitle("Invalid Input");
			addError.setHeaderText("Input is invalid.");
			addError.setContentText(
					"Title and artist must be non-empty.\nNo fields can include '|'.\nYear must be a positive integer.");
			addError.showAndWait();
			return false;
		}
		return true;
	}

	private boolean isPositiveInteger(String s) {
		int x = 0;
		try {
			x = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return x > 0;
	}

}
