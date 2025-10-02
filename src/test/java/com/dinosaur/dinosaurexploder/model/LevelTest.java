package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    void levelUp() {
        LevelManager levelManager = new LevelManager();

        incrementDefeatedEnemiesManyTimes(levelManager, levelManager.getEnemiesToDefeat());

        assertTrue(levelManager.shouldAdvanceLevel());
    }

    @Test
    void levelUpManyTimes() {
        LevelManager levelManager = new LevelManager();

        incrementDefeatedEnemiesManyTimes(levelManager, levelManager.getEnemiesToDefeat());
        levelManager.nextLevel();
        incrementDefeatedEnemiesManyTimes(levelManager, 2 * levelManager.getEnemiesToDefeat());

        assertTrue(levelManager.shouldAdvanceLevel());
    }
    
    private void incrementDefeatedEnemiesManyTimes(LevelManager levelManager, int count) {
        for (int i = 0; i < count; i++) {
            levelManager.incrementDefeatedEnemies();
        }
    }

    @Test
    void checkInitialStats() {
        LevelManager levelManager = new LevelManager();
        assertEquals(1, levelManager.getCurrentLevel(), "Current level correct");
        assertEquals(0.75, levelManager.getEnemySpawnRate(), "Enemy spawn rate correct");
        assertEquals(1.5, levelManager.getEnemySpeed(), "Enemy speed correct");
        assertEquals(0, levelManager.getLevelProgress(), "Level progress correct");
        assertEquals(5, levelManager.getEnemiesToDefeat(), "Enemies to defeat correct");
    }
}