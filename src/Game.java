import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game {

    // Constants for the game
    private static final int INITIAL_HEALTH = 50; // Starting health points
    private static final int HEALTH_LOSS = 10; // Health points lost for a wrong answer
    private static final int POINTS_PER_WIN = 20; // Points gained for a correct answer
    private static final int POINTS_PER_LOSS = -10; // Points deducted for a wrong answer
    private static final int MAX_LEVELS = 5; // Maximum levels in the game

    // Game state variables
    private int health = INITIAL_HEALTH; // Current health of the player
    private int level = 1; // Current level the player is on
    private int score = 0; // Player's score
    private final Map<String, String[]> questions = new HashMap<>(); // Stores questions and answers
    private final Set<String> usedQuestions = new HashSet<>(); // Tracks questions that have already been asked
    private String playerName; // Player's name

    public Game() {
        populateQuestions(); // Initialize the questions
    }

    public static void main(String[] args) {
        Game game = new Game(); // Create a game instance
        game.start(); // Start the game
    }

    // Method to start the game
    private void start() {
        // Create the main frame for the game
        JFrame frame = new JFrame("Text-Based Adventure Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);

        // Main panel with BorderLayout
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(getLevelColor(level)); // Set initial background color

        // Ask the player for their name
        playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Adventurer"; // Default name if none is provided
        }

        // Save the player's session in a file
        savePlayerSession();

        // Top panel for player name and health
        JLabel nameLabel = new JLabel("Ahoy, traveler " + playerName + "!");
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel healthLabel = new JLabel(getHealthIcons(), SwingConstants.RIGHT);
        healthLabel.setFont(new Font("Georgia", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.WEST); // Player name on the left
        topPanel.add(healthLabel, BorderLayout.EAST); // Health status on the right
        topPanel.setBackground(getLevelColor(level));

        // Center message for welcoming the player
        JLabel messageLabel = new JLabel("Welcome, brave adventurer!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Georgia", Font.PLAIN, 18));

        // Start button to begin the adventure
        JButton startButton = new JButton("Start Adventure");
        startButton.addActionListener(_ -> gameLoop(frame, panel, healthLabel, messageLabel, topPanel));

        // Add components to the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        // Add the panel to the frame and display
        frame.add(panel);
        frame.setVisible(true);
    }

    // Method to populate the game's questions
    private void populateQuestions() {
        questions.put("What has keys but cannot open locks?", new String[]{"keyboard", "a keyboard"});
        questions.put("I speak without a mouth and hear without ears. What am I?", new String[]{"echo", "an echo"});
        questions.put("What is so fragile that saying its name breaks it?", new String[]{"silence", "quiet"});
        questions.put("The more you take, the more you leave behind. What am I?", new String[]{"footsteps", "steps"});
        questions.put("What can travel around the world while staying in the corner?", new String[]{"stamp", "a stamp"});
    }

    // Get a unique question that hasn't been asked yet
    private String getUniqueQuestion() {
        if (usedQuestions.size() == questions.size()) {
            usedQuestions.clear(); // Reset used questions if all have been asked
        }
        List<String> questionList = new ArrayList<>(questions.keySet());
        String question;
        do {
            question = questionList.get(new Random().nextInt(questionList.size()));
        } while (usedQuestions.contains(question)); // Ensure the question is unique
        usedQuestions.add(question); // Mark the question as used
        return question;
    }

    // Check if the player's answer is correct
    private boolean validateAnswer(String[] validAnswers, String answer) {
        for (String validAnswer : validAnswers) {
            if (validAnswer.equalsIgnoreCase(answer.trim())) {
                return true; // Return true if the answer matches any valid answer
            }
        }
        return false; // Return false otherwise
    }

    // Get the background color based on the level
    private Color getLevelColor(int level) {
        return switch (level % 5) {
            case 1 -> Color.LIGHT_GRAY;
            case 2 -> Color.CYAN;
            case 3 -> Color.PINK;
            case 4 -> Color.ORANGE;
            default -> Color.GREEN;
        };
    }

    // Get a string of health icons based on the player's health
    private String getHealthIcons() {
        int starCount = health / 10; // Each 10 health points is represented by one star
        return "★ ".repeat(Math.max(0, starCount)) // Append stars to the health bar
                .trim();
    }

    // Save the player's session data in a CSV file
    private void savePlayerSession() {
        File file = new File("player_sessions.csv");
        try {
            Map<String, Integer> players = readPlayerSessions();
            players.put(playerName, players.getOrDefault(playerName, 0) + 1); // Increment session count

            // Write the updated session data to the file
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Name,Sessions");
                for (Map.Entry<String, Integer> entry : players.entrySet()) {
                    writer.println(entry.getKey() + "," + entry.getValue());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving player session: " + e.getMessage());
        }
    }

    // Read player session data from a CSV file
    private Map<String, Integer> readPlayerSessions() {
        Map<String, Integer> players = new HashMap<>();
        File file = new File("player_sessions.csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine(); // Skip the header row
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    players.put(parts[0], Integer.parseInt(parts[1]));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error reading player sessions: " + e.getMessage());
            }
        }
        return players;
    }

    // Save the leaderboard data in a CSV file
    private void saveLeaderboard() {
        File file = new File("leaderboard.csv");
        try {
            Map<String, Integer> leaderboard = readLeaderboard();
            leaderboard.put(playerName, leaderboard.getOrDefault(playerName, 0) + score); // Update the player's score

            // Write the updated leaderboard data to the file
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("Name,Score");
                leaderboard.entrySet().stream()
                        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Sort by score
                        .forEach(entry -> writer.println(entry.getKey() + "," + entry.getValue()));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving leaderboard: " + e.getMessage());
        }
    }

    // Read leaderboard data from a CSV file
    private Map<String, Integer> readLeaderboard() {
        Map<String, Integer> leaderboard = new HashMap<>();
        File file = new File("leaderboard.csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine(); // Skip the header row
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    leaderboard.put(parts[0], Integer.parseInt(parts[1]));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error reading leaderboard: " + e.getMessage());
            }
        }
        return leaderboard;
    }

    // Display the leaderboard after the game ends
    private void showLeaderboard(JFrame frame) {
        Map<String, Integer> leaderboard = readLeaderboard();
        StringBuilder leaderboardText = new StringBuilder("<html><h2>Leaderboard</h2>");
        leaderboard.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()) // Sort by score
                .forEach(entry -> leaderboardText.append("<p>").append(entry.getKey()).append(": ").append(entry.getValue()).append(" points</p>"));
        leaderboardText.append("</html>");

        JLabel leaderboardLabel = new JLabel(leaderboardText.toString());
        leaderboardLabel.setFont(new Font("Georgia", Font.PLAIN, 16));

        JOptionPane.showMessageDialog(frame, leaderboardLabel, "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    // Main game loop
    private void gameLoop(JFrame frame, JPanel panel, JLabel healthLabel, JLabel ignoredMessageLabel, JPanel topPanel) {
        // Game loop for 5 levels
        for (int i = 1; i <= MAX_LEVELS; i++) {
            String question = getUniqueQuestion();
            String[] validAnswers = questions.get(question);

            // Ask the player the current level's question
            String answer = JOptionPane.showInputDialog(frame, "Level " + level + ": " + question);
            if (answer == null) {
                JOptionPane.showMessageDialog(frame, "Game Over! You abandoned the quest.");
                saveLeaderboard();
                showLeaderboard(frame);
                return; // Exit the game if the player cancels
            }

            // Check the answer and update the score and health accordingly
            if (validateAnswer(validAnswers, answer)) {
                JOptionPane.showMessageDialog(frame, "Correct answer! Level won.");
                score += POINTS_PER_WIN; // Add points for a correct answer
            } else {
                JOptionPane.showMessageDialog(frame, "Wrong answer. Level lost.");
                score += POINTS_PER_LOSS; // Deduct points for a wrong answer
                health -= HEALTH_LOSS; // Deduct health for a wrong answer
                if (health <= 0) {
                    JOptionPane.showMessageDialog(frame, "Game Over! You ran out of health.");
                    saveLeaderboard();
                    showLeaderboard(frame);
                    return; // Exit the game if health reaches 0
                }
            }

            // Update level, health, and UI components
            level++;
            healthLabel.setText(getHealthIcons());
            panel.setBackground(getLevelColor(level));
            topPanel.setBackground(getLevelColor(level));
        }

        // Game ends after 5 levels
        JOptionPane.showMessageDialog(frame, "Adventure complete! Final score: " + score + " points.");
        saveLeaderboard(); // Save the player's score to the leaderboard
        showLeaderboard(frame); // Display the leaderboard
    }
}
