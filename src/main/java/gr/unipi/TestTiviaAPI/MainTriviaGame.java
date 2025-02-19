package gr.unipi.TestTiviaAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;

import exception.TriviaAPIException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.triviagame.Results;
import service.TriviaAPIService;

public class MainTriviaGame {

	private Stage primaryStage;
	private List<Results> questionData;
	private int numQuestions;
	private String category;
	private String difficulty;
	private String type;
	private int currentQuestionCnt = 0; 
	private int score = 0; 
	private int correctAnswers = 0;
	private int maxScore = 0;

	
	//Υλοποίηση contractor για την περιπτώση που κληθεί απο το κουμπί Play again με παράμετρο το maxScore του προηγούμενου παιχνιδιού
	
	public MainTriviaGame(Stage primaryStage, List<Results> questions, int numQuestions, String category,
			String difficulty, String type, int maxScore) {
		this.primaryStage = primaryStage;
		this.questionData = questions;
		this.numQuestions = numQuestions;
		this.category = category;
		this.difficulty = difficulty;
		this.type = type;
		this.maxScore = maxScore;

		showNextQuestion();

	}
	
	//Υλοποίηση contractor για την περιπτώση που κληθεί από την main κλάση GameWindowManager
	
	public MainTriviaGame(Stage primaryStage, List<Results> questionData, int numQuestions, String category,
			String difficulty, String type) {
		super();
		this.primaryStage = primaryStage;
		this.questionData = questionData;
		this.numQuestions = numQuestions;
		this.category = category;
		this.difficulty = difficulty;
		this.type = type;

		showNextQuestion();
	}

	//
	// Δημιουργεί και εμφανίζει μια νέα σκηνή με την επόμενη ερώτηση.
	// Αν έχουν απαντηθεί όλες οι ερωτήσεις, εμφανίζει τη σύνοψη του παιχνιδιού.
	
	private void showNextQuestion() {
		if (currentQuestionCnt >= numQuestions) {
			showQuizSummary(); 
			return;
		}

		Results qstData = questionData.get(currentQuestionCnt);

		// Δημιουργούν και διαμορφώνουν το περιβάλλον της ερώτησης, συμπεριλαμβανομένου του κειμένου και των απαντήσεων.  
		// Ορίζουν τη διάταξη των στοιχείων με `GridPane` και `Label`, ενώ κεντράρει τα κουμπιά απαντήσεων.

		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.setHgap(10);
		root.setVgap(10);
		root.getChildren().clear();

		String cleanQuestion = StringEscapeUtils.unescapeHtml4(qstData.getQuestion());

		
		Label questionLabel = new Label("Question " + (currentQuestionCnt + 1) + ": " + cleanQuestion);
		questionLabel.setWrapText(true);
		questionLabel.setMaxWidth(450);
		questionLabel.setStyle("-fx-font-size: 15px;");

		
		Label scoreLabel = new Label("Score: " + score);
		root.add(questionLabel, 0, 0, 2, 1);
		root.add(scoreLabel, 0, 1);
		scoreLabel.setStyle("-fx-font-size: 14px;");

		// Μηνυμα ενημερωσης μετα από κάθε απάντηση
		Label messageLabel = new Label();
		messageLabel.setAlignment(Pos.CENTER);
		messageLabel.setMaxWidth(500);
		root.add(messageLabel, 0, 2, 2, 1);

		// FlowPane για στοίχιση κουμπιών στο κέντρο
		GridPane answersPane = new GridPane();
		answersPane.setAlignment(Pos.CENTER); 
		answersPane.setHgap(10); 
		answersPane.setVgap(10);
		answersPane.setStyle("-fx-font-size: 14px;");
		List<Button> answerButtons = new ArrayList<>();

		//Έλεγχος ερωτήσεων για κάθε ερώτηση και εμφάνιση των αντίστοιχων επιλογών για απαντήσεις
		
		if (qstData.getType().equals("multiple")) {
			List<String> allAnswers = new ArrayList<>(qstData.getIncorrectAnswers());
			allAnswers.add(qstData.getCorrectAnswer());
			Collections.shuffle(allAnswers);

			int row = 0;
			int col = 0;

			//Δημιουργία κουμπιων επιλογής απαντήσεων και αποκωδικοποίηση HTML οντοτήτων σε κανονικό κείμενο
			
			for (String answer : allAnswers) {
				String cleanAnswer = StringEscapeUtils.unescapeHtml4(answer);
				Button answerButton = new Button(cleanAnswer);

				answerButton.setOnAction(e -> checkAnswer(qstData, answerButton.getText(), messageLabel));
				answersPane.add(answerButton, col, row); 
				col++;
				if (col == 2) {
					col = 0;
					row++;
				}
			}

		} else {

			Button trueButton = new Button("True");
			Button falseButton = new Button("False");

			trueButton.setOnAction(e -> checkAnswer(qstData, "True", messageLabel));
			falseButton.setOnAction(e -> checkAnswer(qstData, "False", messageLabel));

			answersPane.add(trueButton, 0, 0);
			answersPane.add(falseButton, 1, 0);
		}

		root.add(answersPane, 0, 3, 2, 1); 

		// Ενημέρωση σκηνής
		Scene scene = new Scene(root, 600, 300);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Trivia Game");
	}

	
	
//Έλεγχος για εμφάνιση μηνυματος και σκορ μετά από κάθε ερώτηση
	
	private void checkAnswer(Results question, String selectedAnswer, Label messageLabel) {
		Platform.runLater(() -> {
			String cleanAnswer = StringEscapeUtils.unescapeHtml4(question.getCorrectAnswer());
			if (selectedAnswer.equals(question.getCorrectAnswer())) {
				score += 10;
				correctAnswers++;
				messageLabel.setText("Correct! Score increaced 10 points.");
				messageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

			} else if (score > 0) {
				score -= 5;
				messageLabel.setText("Incorrect! Score decreased 5 points. The correct answer was: " + cleanAnswer);
				messageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
			} else {
				messageLabel.setText("Incorrect! Score decreased 5 points. The correct answer was: " + cleanAnswer);
				messageLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
			}

			
			messageLabel.setWrapText(true); 
			messageLabel.setMaxWidth(500); 
			messageLabel.setAlignment(Pos.CENTER); 

			PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
			pause.setOnFinished(e -> {
				currentQuestionCnt++;
				showNextQuestion();
			});
			pause.play();

		});
	}
	
		//Εμφάνιση τελικού σκορ και ποσοστών
	
	private void showQuizSummary() {
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));
		root.setHgap(10);
		root.setVgap(10);

		double successRate = ((double) correctAnswers / numQuestions) * 100; 
		Label summaryLabel = new Label("Game Over! Your final score: " + score + "\nThe: "
				+ String.format("%.1f", successRate) + "% of questions was correct.");
		summaryLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
		summaryLabel.setAlignment(Pos.CENTER);
		summaryLabel.setWrapText(true);
		summaryLabel.setMaxWidth(400);
		root.add(summaryLabel, 0, 0, 2, 1); 

		Label highScoreLabel = new Label();
		highScoreLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;"); 
		
		//Έλεγχος για το μέγιστο σκορ
		
		if (score > maxScore) {
			maxScore = score;
			highScoreLabel.setText(" New High Score: " + maxScore + " points! ");

			
			root.add(highScoreLabel, 0, 1, 2, 1);

		}

	
		GridPane buttonPane = new GridPane();
		buttonPane.setAlignment(Pos.CENTER);
		buttonPane.setHgap(10); 
		buttonPane.setVgap(10);

	
		Button menuButton = new Button("Back to Menu");
		menuButton.setOnAction(e -> {
			maxScore = 0;
			new GameWindowManager().start(primaryStage);
		});

		
		Button restartButton = new Button("Play Again");
		restartButton.setOnAction(e -> {
			try {
				
				TriviaAPIService tas = new TriviaAPIService("https://opentdb.com/api.php", numQuestions, category,
						difficulty, type);
				List<Results> newQuestions = tas.getQuestionData(); 

				//Κλήση κατασκευαστή που δέχεται παράμετρο το μέγιστο σκορ
				
				new MainTriviaGame(primaryStage, newQuestions, numQuestions, category, difficulty, type, maxScore);
			} catch (TriviaAPIException ex) {
				Alert a = new Alert(AlertType.ERROR);
				a.setTitle("Wrong input");
				a.setHeaderText("An error occurred from API server, please try again!");
				a.setContentText(ex.getMessage()); 
				a.showAndWait();
				ex.printStackTrace();
			} catch (IndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		});

		
		buttonPane.add(restartButton, 0, 0);
		buttonPane.add(menuButton, 1, 0);

		
		root.add(buttonPane, 0, 2, 2, 1);

		Scene scene = new Scene(root, 400, 400);
		primaryStage.setScene(scene);
	}

}