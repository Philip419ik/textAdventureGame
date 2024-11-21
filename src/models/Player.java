package models;

import java.util.HashMap;
import java.util.Map;

// Represents the player in the game
public class Player {
    // Player's name
    private final String name;
    private int health;  // Player's health
    private final Map<String, Integer> inventory; // Inventory of items

    // Constructor to initialize the player with a name and health
    public Player(String name, int health) {
        this.name = name;
        this.health = health;
        this.inventory = new HashMap<>(); // Empty inventory at the start
    }

    // Getter for the player's name
    public String getName() {
        return name;
    }

    // Getter for the player's health
    public int getHealth() {
        return health;
    }

    // Method to reduce the player's health
    public void reduceHealth(int amount) {
        health = Math.max(0, health - amount); // Health cannot go below 0
    }

    // Method to increase the player's health
    public void increaseHealth(int amount) {
        health += amount; // Adds health points
    }

    // Method to add an item to the player's inventory
    public void addItem(String item, int quantity) {
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
    }

    // Method to get the player's inventory
    public Map<String, Integer> getInventory() {
        return inventory;
    }

    // Method to display player stats
    public void displayStats() {
        System.out.println("Player: " + name);
        System.out.println("Health: " + health);
        System.out.println("Inventory: " + inventory);
    }
}
