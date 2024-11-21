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
    private final Map<String, String[]> easyQuestions = new HashMap<>(); // Stores easy questions and answers
    private final Map<String, String[]> intermediateQuestions = new HashMap<>(); // Stores intermediate questions and answers
    private final Map<String, String[]> hardQuestions = new HashMap<>(); // Stores hard questions and answers
    private final Set<String> usedQuestions = new HashSet<>(); // Tracks questions that have already been asked
    private String playerName; // Player's name
    private Map<String, String[]> selectedQuestions; // Store questions based on selected room

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

        // Ask the player to select a room
        String[] roomOptions = {"Easy", "Intermediate", "Hard"};
        String roomChoice = (String) JOptionPane.showInputDialog(frame, "Select a room:",
                "Room Selection", JOptionPane.QUESTION_MESSAGE, null, roomOptions, roomOptions[0]);

        // Set the selected room's question set based on the player's choice
        if (roomChoice != null) {
            switch (roomChoice) {
                case "Easy":
                    selectedQuestions = easyQuestions;
                    break;
                case "Intermediate":
                    selectedQuestions = intermediateQuestions;
                    break;
                case "Hard":
                    selectedQuestions = hardQuestions;
                    break;
            }
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
        startButton.addActionListener(e -> gameLoop(frame, panel, healthLabel, messageLabel, topPanel));

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
        // Easy questions
        easyQuestions.put("What has keys but cannot open locks?", new String[]{"keyboard", "a keyboard"});
        easyQuestions.put("What is so fragile that saying its name breaks it?", new String[]{"silence", "quiet"});

        // Intermediate questions
        intermediateQuestions.put("I speak without a mouth and hear without ears. What am I?", new String[]{"echo", "an echo"});
        intermediateQuestions.put("The more you take, the more you leave behind. What am I?", new String[]{"footsteps", "steps"});

        // Hard questions
        hardQuestions.put("What can travel around the world while staying in the corner?", new String[]{"stamp", "a stamp"});
        hardQuestions.put("What runs but never walks, has a bed but never sleeps?", new String[]{"river", "a river"});
    }

    // Get a unique question that hasn't been asked yet
    private String getUniqueQuestion() {
        if (usedQuestions.size() == selectedQuestions.size()) {
            usedQuestions.clear(); // Reset used questions if all have been asked
        }
        List<String> questionList = new ArrayList<>(selectedQuestions.keySet());
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
        switch (level % 5) {
            case 1: return Color.LIGHT_GRAY;
            case 2: return Color.CYAN;
            case 3: return Color.PINK;
            case 4: return Color.ORANGE;
            default: return Color.GREEN;
        }
    }

    // Get a string of health icons based on the player's health
    private String getHealthIcons() {
        int starCount = health / 10; // Each 10 health points is represented by one star
        StringBuilder healthBar = new StringBuilder();
        for (int i = 0; i < starCount; i++) {
            healthBar.append("★ "); // Append stars to the health bar
        }
        return healthBar.toString().trim();
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

    // Method to display the leaderboard in a pop-up
    private void displayLeaderboard(JFrame frame) {
        Map<String, Integer> players = readPlayerSessions();
        List<String> leaderboard = new ArrayList<>();
        players.forEach((name, score) -> leaderboard.add(name + ": " + score));

        // Sort the leaderboard by score in descending order
        leaderboard.sort((entry1, entry2) -> Integer.compare(Integer.parseInt(entry2.split(":")[1].trim()), Integer.parseInt(entry1.split(":")[1].trim())));

        // Prepare the leaderboard message
        StringBuilder leaderboardMessage = new StringBuilder("Leaderboard:\n\n");
        for (String entry : leaderboard) {
            leaderboardMessage.append(entry).append("\n");
        }

        // Show the leaderboard in a dialog
        JOptionPane.showMessageDialog(frame, leaderboardMessage.toString());
    }

    // Game loop: Processes player's answers and handles the flow
    private void gameLoop(JFrame frame, JPanel panel, JLabel healthLabel, JLabel messageLabel, JPanel topPanel) {
        // Hide the start button
        panel.remove(panel.getComponentCount() - 1);
        panel.revalidate();
        panel.repaint();

        while (health > 0 && level <= MAX_LEVELS) {
            // Get a new question and ask the player
            String question = getUniqueQuestion();
            String[] validAnswers = selectedQuestions.get(question);

            // Display the question and get the player's answer
            String answer = JOptionPane.showInputDialog(frame, question);
            if (answer == null) break; // Exit if player cancels

            // Check the answer
            if (validateAnswer(validAnswers, answer)) {
                score += POINTS_PER_WIN; // Correct answer, increase score
                messageLabel.setText("Correct! Well done, brave adventurer.");
            } else {
                score += POINTS_PER_LOSS; // Wrong answer, decrease score
                health -= HEALTH_LOSS; // Lose health
                messageLabel.setText("Wrong answer. Your health is decreasing...");
            }

            // Update the health and level
            healthLabel.setText(getHealthIcons());
            level++; // Increase the level
            topPanel.setBackground(getLevelColor(level));

            // Show leaderboard after each question
            displayLeaderboard(frame);

            if (health <= 0) {
                JOptionPane.showMessageDialog(frame, "Game Over! You have lost all your health.");
                break;
            }
        }
    }
}
