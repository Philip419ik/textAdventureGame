package utilities;

import java.util.*;

// Utility class for managing the leaderboard
public class Leaderboard {
    private static final String FILE_PATH = "leaderboard.txt"; // File to store leaderboard data

    // Method to add a new score to the leaderboard
    public static void addScore(String playerName, int score) {
        List<String> scores = FileHandler.readFile(FILE_PATH);
        scores.add(playerName + " - " + score); // Add new score to the list
        FileHandler.writeFile(FILE_PATH, scores); // Save updated list to file
    }

    // Method to display the leaderboard
    public static void displayLeaderboard() {
        List<String> scores = FileHandler.readFile(FILE_PATH);
        if (scores.isEmpty()) {
            System.out.println("No scores yet. Be the first to play!");
        } else {
            System.out.println("Leaderboard:");
            scores.forEach(System.out::println); // Print each score
        }
    }
}
