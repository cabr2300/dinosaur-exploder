package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LevelManagerTest {

    @Test
    void initialValuesAreCorrect() {
        LevelManager levelManager = new LevelManager();

        assertEquals(1, levelManager.getCurrentLevel());
        assertEquals(5, levelManager.getEnemiesToDefeat());
        assertEquals(1.5, levelManager.getEnemySpeed());
        assertEquals(0.75, levelManager.getEnemySpawnRate());
    }

    @Test
    void levelProgressUpdatesCorrectly() {
        LevelManager levelManager = new LevelManager();
        assertEquals(0f, levelManager.getLevelProgress());

        levelManager.incrementDefeatedEnemies();
        assertEquals(0.2f, levelManager.getLevelProgress(), 0.01); // 1/5 = 0.2
    }
}
