package utilities;

import models.Room;
import models.challenges.EnemyChallenge;
import models.challenges.Puzzle;

// Interface for creating rooms and linking them
public interface RoomFactory {

    // Method to create and link rooms with difficulty levels
    static Room createGameMap(String roomSelection) {
        // Create rooms with different difficulties
        Room easyRoom = new Room("You are in an easy room with simple challenges.");
        Room intermediateRoom = new Room("You are in an intermediate room with moderate challenges.");
        Room hardRoom = new Room("You are in a hard room with tough challenges.");

        // Add challenges to rooms based on difficulty
        easyRoom.addChallenge(new Puzzle("Solve the riddle to proceed.",
                "What has keys but can't open locks?", "keyboard") {
            @Override
            public boolean isCorrectAnswer(int answerNumber) {
                return false;
            }
        });
        intermediateRoom.addChallenge(new EnemyChallenge("A wild skeleton attacks!", "Skeleton Warrior", 30) {
            @Override
            public boolean isCorrectAnswer(int answerNumber) {
                return false;
            }
        });
        hardRoom.addChallenge(new Puzzle("Answer the guardian's question to pass.",
                "What is the capital of France?", "Paris") {
            @Override
            public boolean isCorrectAnswer(int answerNumber) {
                return false;
            }
        });
        hardRoom.addChallenge(new EnemyChallenge("An ogre challenges you to battle!", "Ogre", 50) {
            @Override
            public boolean isCorrectAnswer(int answerNumber) {
                return false;
            }
        });

        // Link rooms together
        easyRoom.setForward(intermediateRoom);
        intermediateRoom.setBack(easyRoom);
        intermediateRoom.setForward(hardRoom);
        hardRoom.setBack(intermediateRoom);

        return easyRoom; // Return the starting room, which is the easy room
    }
}
