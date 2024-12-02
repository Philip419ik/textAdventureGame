import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game {

    private static final int INITIAL_HEALTH = 50;
    private static final int HEALTH_LOSS = 10;
    private static final int POINTS_PER_WIN = 20;
    private static final int POINTS_PER_LOSS = -10;
    private static final int MAX_LEVELS = 5;

    private int health = INITIAL_HEALTH;
    private int level = 1;
    private int score = 0;
    private final Map<String, String[]> easyQuestions = new HashMap<>();
    private final Map<String, String[]> intermediateQuestions = new HashMap<>();
    private final Map<String, String[]> hardQuestions = new HashMap<>();
    private final Set<String> usedQuestions = new HashSet<>();
    private String playerName;
    private Map<String, String[]> selectedQuestions;

    public Game() {
        populateQuestions();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    private void start() {
        JFrame frame = new JFrame("Riddle Adventure Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setResizable(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(getLevelColor(level));

        playerName = JOptionPane.showInputDialog(frame, "Enter your name:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Adventurer";
        }

        String[] roomOptions = {"Easy", "Intermediate", "Hard"};
        String roomChoice = (String) JOptionPane.showInputDialog(frame, "Select a room:",
                "Room Selection", JOptionPane.QUESTION_MESSAGE, null, roomOptions, roomOptions[0]);

        if (roomChoice != null) {
            switch (roomChoice) {
                case "Easy" -> selectedQuestions = easyQuestions;
                case "Intermediate" -> selectedQuestions = intermediateQuestions;
                case "Hard" -> selectedQuestions = hardQuestions;
            }
        }

        savePlayerSession();

        JLabel nameLabel = new JLabel("Ahoy, traveler " + playerName + "!");
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 16));
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel healthLabel = new JLabel(getHealthIcons(), SwingConstants.RIGHT);
        healthLabel.setFont(new Font("Georgia", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(nameLabel, BorderLayout.WEST);
        topPanel.add(healthLabel, BorderLayout.EAST);
        topPanel.setBackground(getLevelColor(level));

        JLabel messageLabel = new JLabel("Welcome, brave adventurer!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Georgia", Font.PLAIN, 18));

        JButton startButton = new JButton("Start Adventure");
        startButton.addActionListener(_ -> gameLoop(frame, panel, healthLabel, messageLabel, topPanel));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(startButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void populateQuestions() {
        easyQuestions.put("What has keys but cannot open locks?", new String[]{"keyboard", "a keyboard"});
        easyQuestions.put("What is so fragile that saying its name breaks it?", new String[]{"silence", "quiet"});

        intermediateQuestions.put("I speak without a mouth and hear without ears. What am I?", new String[]{"echo", "an echo"});
        intermediateQuestions.put("The more you take, the more you leave behind. What am I?", new String[]{"footsteps", "steps"});

        hardQuestions.put("What can travel around the world while staying in the corner?", new String[]{"stamp", "a stamp"});
        hardQuestions.put("What runs but never walks, has a bed but never sleeps?", new String[]{"river", "a river"});
    }

    private String getUniqueQuestion() {
        if (usedQuestions.size() == selectedQuestions.size()) {
            usedQuestions.clear();
        }
        List<String> questionList = new ArrayList<>(selectedQuestions.keySet());
        String question;
        do {
            question = questionList.get(new Random().nextInt(questionList.size()));
        } while (usedQuestions.contains(question));
        usedQuestions.add(question);
        return question;
    }

    private boolean validateAnswer(String[] validAnswers, String answer) {
        for (String validAnswer : validAnswers) {
            if (validAnswer.equalsIgnoreCase(answer.trim())) {
                return true;
            }
        }
        return false;
    }

    private Color getLevelColor(int level) {
        return switch (level % 5) {
            case 1 -> Color.LIGHT_GRAY;
            case 2 -> Color.CYAN;
            case 3 -> Color.PINK;
            case 4 -> Color.ORANGE;
            default -> Color.GREEN;
        };
    }

    private String getHealthIcons() {
        int starCount = health / 10;
        return "★ ".repeat(Math.max(0, starCount)).trim();
    }

    private void savePlayerSession() {
        File file = new File("player_sessions.csv");
        try {
            Map<String, Integer> players = readPlayerSessions();
            players.put(playerName, players.getOrDefault(playerName, 0) + 1);

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

    private Map<String, Integer> readPlayerSessions() {
        Map<String, Integer> players = new HashMap<>();
        File file = new File("player_sessions.csv");
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file)) {
                scanner.nextLine();
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

    private void displayLeaderboard(JFrame frame) {
        Map<String, Integer> players = readPlayerSessions();
        List<String> leaderboard = new ArrayList<>();
        players.forEach((name, score) -> leaderboard.add(name + ": " + score));

        leaderboard.sort((entry1, entry2) -> Integer.compare(
                Integer.parseInt(entry2.split(":")[1].trim()),
                Integer.parseInt(entry1.split(":")[1].trim())));

        StringBuilder leaderboardMessage = new StringBuilder("Leaderboard:\n\n");
        for (String entry : leaderboard) {
            leaderboardMessage.append(entry).append("\n");
        }

        JOptionPane.showMessageDialog(frame, leaderboardMessage.toString());
    }

    private void gameLoop(JFrame frame, JPanel panel, JLabel healthLabel, JLabel messageLabel, JPanel topPanel) {
        panel.remove(panel.getComponentCount() - 1);
        panel.revalidate();
        panel.repaint();

        while (health > 0 && level <= MAX_LEVELS) {
            String question = getUniqueQuestion();
            String[] validAnswers = selectedQuestions.get(question);

            String answer = JOptionPane.showInputDialog(frame, question);
            if (answer == null) break;

            if (validateAnswer(validAnswers, answer)) {
                score += POINTS_PER_WIN;
                JOptionPane.showMessageDialog(frame, "Correct! Well done, brave adventurer.");
            } else {
                score += POINTS_PER_LOSS;
                health -= HEALTH_LOSS;
                JOptionPane.showMessageDialog(frame, "Wrong answer. Oh no! The enemies are winning!");
            }

            healthLabel.setText(getHealthIcons());
            level++;
            topPanel.setBackground(getLevelColor(level));

            if (health <= 0) {
                JOptionPane.showMessageDialog(frame, "Game Over! You have lost all your health.");
                return;
            }
        }

        if (level > MAX_LEVELS) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You have completed all levels!");
            displayLeaderboard(frame);
        }
    }
}
