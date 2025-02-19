package gr.unipi.TestTiviaAPI;

public class TriviaGameSet {
	private int numQuestions;
	private String category;
	private String difficulty;
	private String type;

	public TriviaGameSet(String numQuestionsStr, String category, String difficulty, String type) {
		this.numQuestions = parseNumberOfQuestions(numQuestionsStr);
		this.category = parseCategory(category);
		this.difficulty = parseDifficulty(difficulty);
		this.type = parseQuestionType(type);
	}

	private int parseNumberOfQuestions(String numQuestionsStr) {

		return Integer.parseInt(numQuestionsStr.trim());
	}

	private String parseCategory(String category) {
		{
			switch (category) {
			case "Any Category":
				return null;
			case "General Knowledge":
				return "9";
			case "Entertainment: Books":
				return "10";
			case "Entertainment: Film":
				return "11";
			case "Entertainment: Music":
				return "12";
			case "Entertainment: Musicals & Theatres":
				return "13";
			case "Entertainment: Television":
				return "14";
			case "Entertainment: Video Games":
				return "15";
			case "Entertainment: Board Games":
				return "16";
			case "Science & Nature":
				return "17";
			case "Science: Computers":
				return "18";
			case "Science: Mathematics":
				return "19";
			case "Mythology":
				return "20";
			case "Sports":
				return "21";
			case "Geography":
				return "22";
			case "History":
				return "23";
			case "Politics":
				return "24";
			case "Art":
				return "25";
			case "Celebrities":
				return "26";
			case "Animals":
				return "27";
			case "Vehicles":
				return "28";
			case "Entertainment: Comics":
				return "29";
			case "Science: Gadgets":
				return "30";
			case "Entertainment: Japanese Anime & Manga":
				return "31";
			case "Entertainment: Cartoon & Animations":
				return "32";
			}
			return null;
		}
	}

	private String parseDifficulty(String difficulty) {
		if (difficulty.equals("Easy")) {
			return "easy";
		} else if (difficulty.equals("Medium")) {
			return "medium";
		} else if (difficulty.equals("Hard")) {
			return "hard";
		} else {
			return null;
		}

	}

	private String parseQuestionType(String type) {
		if (type.equals("Any type")) {
			return null;
		} else if (type.equals("Multiple Choice")) {
			return "multiple";
		} else {
			return "boolean";
		}
	}

	public int getNumQuestions() {
		return numQuestions;
	}

	public String getCategory() {
		return category;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getType() {
		return type;
	}
}
