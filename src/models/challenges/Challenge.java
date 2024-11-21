package models.challenges;

// Abstract class representing a general challenge
public abstract class Challenge {
    private String description; // Description of the challenge

    // Constructor to initialize the challenge with a description
    public Challenge(String description) {
        this.description = description;
    }

    // Getter for the challenge description
    public String getDescription() {
        return description;
    }

    // Abstract method to execute the challenge
    public abstract boolean execute();

    public abstract boolean isCorrectAnswer(int answerNumber);
}
