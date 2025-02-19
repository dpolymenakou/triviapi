package gr.unipi.TestTiviaAPI;

import java.util.List;

import exception.TriviaAPIException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.triviagame.Results;
import service.TriviaAPIService;

public class GameWindowManager extends Application {

	private Stage primaryStage;

	@Override
	public void start(Stage stage) {
		this.primaryStage = stage;
		showStartWindow();
		primaryStage.setWidth(400);
		primaryStage.setHeight(300);
	}

	
	//Δημιουργία μενού επιλογών
	private void showStartWindow() {
		Label titleLabel = new Label("Welcome to Trivia Game!");
		titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		Label numQuestionsLabel = new Label("Number of Questions:");

		TextField numQuestionsField = new TextField("10");
		numQuestionsField.setPromptText(null);

		Label guidanceLabel = new Label("Please enter a number between 10 and 50");
		guidanceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

		Label categoryLabel = new Label("Category:");
		ChoiceBox<String> categoryBox = new ChoiceBox<>();
		categoryBox.getItems().addAll("Any Category", "General Knowledge", "Entertainment: Books",
				"Entertainment: Film", "Entertainment: Music", "Entertainment: Musicals & Theatres",
				"Entertainment: Television", "Entertainment: Video Games", "Entertainment: Board Games",
				"Science & Nature", "Science: Computers", "Science: Mathematics", "Mythology", "Sports", "Geography",
				"History", "Politics", "Art", "Celebrities", "Animals", "Vehicles", "Entertainment: Comics",
				"Science: Gadgets", "Entertainment: Japanese Anime & Manga", "Entertainment: Cartoon & Animations");
		categoryBox.setValue("Any Category");

		Label difficultyLabel = new Label("Difficulty:");
		ChoiceBox<String> difficultyBox = new ChoiceBox<>();
		difficultyBox.getItems().addAll("Any Difficulty", "Easy", "Medium", "Hard");
		difficultyBox.setValue("Any Difficulty");

		Label typeLabel = new Label("Question Type:");
		ChoiceBox<String> typeBox = new ChoiceBox<>();
		typeBox.getItems().addAll("Any Type", "Multiple Choice", "True/False");
		typeBox.setValue("Any Type");

		Button startButton = new Button("Start Game");
		startButton.setOnAction(event -> {

			try {
				if (numQuestionsField.getText().trim().isEmpty()) {
					throw new IllegalArgumentException("You must enter an amount.");
				}

				int numQuestions = Integer.parseInt(numQuestionsField.getText().trim());

				if (numQuestions < 1 || (numQuestions > 50)) {
					throw new IllegalArgumentException("Number of questions must be between 1 and 50.");
				}

				// Κλήση της TriviaGameSettings ωστε να γίνει mapping και να επιστραφούν οι επιλογές σε σωστή μορφή για κλήση του URI
				TriviaGameSet settings = new TriviaGameSet(numQuestionsField.getText(), categoryBox.getValue(),
						difficultyBox.getValue(), typeBox.getValue());

				System.out.println("Selected Category: " + categoryBox.getValue());
				System.out.println(settings.getCategory());

				// Κλήση του TriviaAPIService
				TriviaAPIService tas = new TriviaAPIService("https://opentdb.com/api.php", settings.getNumQuestions(),
						settings.getCategory(), settings.getDifficulty(), settings.getType());

				List<Results> questions = tas.getQuestionData();

				// Λήψη των επιλογών πληροφοριών σε μεταβλητές για χρήση επιλογών σε περίπτωση
				// play again
				String category = settings.getCategory();
				String difficulty = settings.getDifficulty();
				String type = settings.getType();
				Stage gameStage = new Stage();

				// Κλήση του νέου παραθύρου GameWindow
				new MainTriviaGame(gameStage, questions, numQuestions, category, difficulty, type);
				gameStage.show();

				primaryStage.close();

			} catch (NumberFormatException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setTitle("Wrong Input");
				a.setContentText("You must give an integer");
				a.showAndWait();

			} catch (IllegalArgumentException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setTitle("Wrong input");
				a.setContentText(e.getMessage());
				a.showAndWait();

			} catch (IndexOutOfBoundsException e) {
				Alert a = new Alert(AlertType.ERROR);
				a.setTitle("Wrong input");
				a.setContentText("Code 1: No Results\nThe API doesn't have enough questions for your query.Please choose lower amount of questions for this category \nError"+e.getMessage());
				a.showAndWait();
				

			} catch (TriviaAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
	//Δημιουργία τη διάταξη της αρχικής οθόνης του παιχνιδιού, τοποθετώντας τα στοιχεία εισόδου του χρήστη
		
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);
		gridPane.setPadding(new Insets(20));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.add(numQuestionsLabel, 0, 0);
		gridPane.add(numQuestionsField, 1, 0);
		gridPane.add(guidanceLabel, 1, 1);
		gridPane.add(categoryLabel, 0, 2);
		gridPane.add(categoryBox, 1, 2);
		gridPane.add(difficultyLabel, 0, 3);
		gridPane.add(difficultyBox, 1, 3);
		gridPane.add(typeLabel, 0, 4);
		gridPane.add(typeBox, 1, 4);
		gridPane.add(startButton, 0, 5, 2, 1);

		FlowPane root = new FlowPane();
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.setVgap(10);
		root.getChildren().addAll(titleLabel, gridPane);

		Scene scene = new Scene(root, 400, 400);
		primaryStage.setTitle("Trivia Game - Start Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
