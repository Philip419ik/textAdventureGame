package models;

import models.challenges.Challenge;

import java.util.ArrayList;
import java.util.List;

// Represents a room in the game
public class Room {
    private String description; // Description of the room
    private List<Challenge> challenges; // Challenges in the room
    private Room forward;  // Link to the forward room
    private Room back;     // Link to the back room
    private Room left;     // Link to the left room
    private Room right;    // Link to the right room

    // Constructor to initialize a room with a description
    public Room(String description) {
        this.description = description;
        this.challenges = new ArrayList<>();
    }

    // Getter for room description
    public String getDescription() {
        return description;
    }

    // Method to add a challenge to the room
    public void addChallenge(Challenge challenge) {
        challenges.add(challenge);
    }

    // Getter for the list of challenges
    public List<Challenge> getChallenges() {
        return challenges;
    }

    // Methods to set links to adjacent rooms
    public void setForward(Room forward) {
        this.forward = forward;
    }

    public void setBack(Room back) {
        this.back = back;
    }

    public void setLeft(Room left) {
        this.left = left;
    }

    public void setRight(Room right) {
        this.right = right;
    }

    // Methods to get links to adjacent rooms
    public Room getForward() {
        return forward;
    }

    public Room getBack() {
        return back;
    }

    public Room getLeft() {
        return left;
    }

    public Room getRight() {
        return right;
    }

    public Challenge getChallenge() {
        return null;
    }

    public String getDifficulty() {
        return "";
    }
}
