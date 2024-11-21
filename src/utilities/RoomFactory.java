package utilities;

import models.Room;
import models.challenges.Challenge;
import models.challenges.EnemyChallenge;
import models.challenges.Puzzle;

// Utility class for creating rooms and linking them
public class RoomFactory {

    // Method to create and link rooms
    public static Room createGameMap() {
        // Create rooms
        Room room1 = new Room("You are in a dimly lit dungeon.");
        Room room2 = new Room("You have entered a room filled with ancient relics.");
        Room room3 = new Room("This room has an eerie glow and strange noises.");

        // Add challenges to rooms
        room1.addChallenge(new Puzzle("Solve the ancient riddle to proceed.",
                "What has keys but can't open locks?", "keyboard"));
        room2.addChallenge(new EnemyChallenge("A wild skeleton attacks!", "Skeleton Warrior", 30));
        room3.addChallenge(new Puzzle("Answer the guardian's question to pass.",
                "What is the capital of France?", "paris"));

        // Link rooms together
        room1.setForward(room2);
        room2.setBack(room1);
        room2.setForward(room3);
        room3.setBack(room2);

        return room1; // Return the starting room
    }
}
