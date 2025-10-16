package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void levelProgressIs2av5AfterTwoDefeatedEnemyes() {
        LevelManager levelManager = new LevelManager();
        assertEquals(0f, levelManager.getLevelProgress());

        levelManager.incrementDefeatedEnemies();
        levelManager.incrementDefeatedEnemies();
        assertEquals(0.4f, levelManager.getLevelProgress());
    }

    @Test
    void shouldAdvanceLevelWhenEnemiesDefeated() {
        LevelManager levelManager = new LevelManager();

        assertFalse(levelManager.shouldAdvanceLevel());

        // ser till att alla fiender blir besegrade, oberoende av hur många som behöver
        // besegras
        for (int i = 0; i < levelManager.getEnemiesToDefeat(); i++) {
            levelManager.incrementDefeatedEnemies();
        }

        assertTrue(levelManager.shouldAdvanceLevel());
    }

    @Test
    void nextLevelUpdatesValuesCorrectly() {
        LevelManager levelManager = new LevelManager();

        levelManager.nextLevel();

        assertEquals(2, levelManager.getCurrentLevel());
        assertEquals(10, levelManager.getEnemiesToDefeat());
        // säkerställ att dessa räknas korrekt vid level up
        assertEquals(1.7, levelManager.getEnemySpeed());
        assertEquals(0.675, levelManager.getEnemySpawnRate());
    }

}
