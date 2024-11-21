package models.challenges;

import java.util.Scanner;

// Represents a puzzle challenge
public class Puzzle extends Challenge {
    private String question;   // The puzzle question
    private String answer;     // The correct answer

    // Constructor to initialize the puzzle with a question and answer
    public Puzzle(String description, String question, String answer) {
        super(description);
        this.question = question;
        this.answer = answer;
    }

    // Override execute method to handle the puzzle challenge
    @Override
    public boolean execute() {
        System.out.println(getDescription());
        System.out.println("Puzzle: " + question);
        Scanner scanner = new Scanner(System.in);
        System.out.print("Your Answer: ");
        String playerAnswer = scanner.nextLine();
        return playerAnswer.equalsIgnoreCase(answer); // Case-insensitive comparison
    }
}
