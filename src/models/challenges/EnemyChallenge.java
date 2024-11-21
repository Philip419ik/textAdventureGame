package models.challenges;

import java.util.Random;

// Represents an enemy combat challenge
public class EnemyChallenge extends Challenge {
    private String enemyName;  // Name of the enemy
    private int enemyHealth;   // Enemy's health

    // Constructor to initialize the enemy challenge
    public EnemyChallenge(String description, String enemyName, int enemyHealth) {
        super(description);
        this.enemyName = enemyName;
        this.enemyHealth = enemyHealth;
    }

    // Override execute method to handle the combat challenge
    @Override
    public boolean execute() {
        System.out.println(getDescription());
        System.out.println("Enemy: " + enemyName + " (Health: " + enemyHealth + ")");
        Random random = new Random();

        while (enemyHealth > 0) {
            int playerAttack = random.nextInt(10) + 1; // Player attacks with 1-10 damage
            enemyHealth -= playerAttack;
            System.out.println("You hit " + enemyName + " for " + playerAttack + " damage!");
            if (enemyHealth > 0) {
                System.out.println(enemyName + " has " + enemyHealth + " health left.");
            } else {
                System.out.println("You defeated " + enemyName + "!");
            }
        }

        return true; // Combat won
    }
}
