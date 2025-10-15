package com.dinosaur.dinosaurexploder.integrationtests;

import com.dinosaur.dinosaurexploder.components.LevelProgressBarComponent;
import com.dinosaur.dinosaurexploder.components.ScoreComponent;
import com.dinosaur.dinosaurexploder.model.CollisionHandler;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelIntegrationTest {

    private LevelManager levelManager;
    private CollisionHandler collisionHandler;
    private ScoreComponent scoreComponent;
    private LevelProgressBarComponent progressBar;

    @BeforeEach
    void setUp() {
        levelManager = new LevelManager();
        collisionHandler = new CollisionHandler(levelManager);
        scoreComponent = new ScoreComponent();
        progressBar = new LevelProgressBarComponent(new Rectangle(0, 10, Color.GREEN), levelManager) {
            @Override
            public void updateProgress() {
                /* skip UI */ }
        };
    }

    @Test
    @DisplayName("Integration: defeating enough enemies increases level and updates score")
    void levelUpIntegration() {
        // Arrange
        int startLevel = levelManager.getCurrentLevel();
        int initialScore = scoreComponent.getScore();

        // Act — loopa tills levelManager byter nivå
        int iterations = 0;
        while (levelManager.getCurrentLevel() == startLevel && iterations < 50) {
            collisionHandler.isLevelUpAfterHitDino(scoreComponent, progressBar);
            iterations++;
        }

        // Assert
        assertEquals(startLevel + 1, levelManager.getCurrentLevel(), "Should level up after enough kills");
        assertTrue(scoreComponent.getScore() > initialScore, "Score should increase after hits");
        System.out.println("Enemies defeated before level-up: " + iterations);
    }

    @Test
    @DisplayName("Integration: single enemy defeat updates both score and progress bar")
    void singleEnemyDefeatUpdatesScoreAndProgress() {
        // Arrange
        int initialScore = scoreComponent.getScore();

        // Act
        collisionHandler.isLevelUpAfterHitDino(scoreComponent, progressBar);

        // Assert
        assertTrue(scoreComponent.getScore() > initialScore, "Score should increase after hit");
    }

    @Test
    @DisplayName("Integration: defeating boss increases score by current level and advances level")
    void bossDefeatIntegration() {
        // Arrange
        levelManager.nextLevel(); // Level 2
        int startLevel = levelManager.getCurrentLevel();
        int startScore = scoreComponent.getScore();

        // Act
        collisionHandler.handleBossDefeat(scoreComponent);

        // Assert
        assertEquals(startLevel + 1, levelManager.getCurrentLevel(), "Boss defeat should trigger next level");
        assertEquals(startScore + startLevel, scoreComponent.getScore(),
                "Boss defeat should award score equal to current level");
    }

    @Test
    @DisplayName("Integration: losing life does not affect score or level")
    void losingLifeDoesNotAffectScoreOrLevel() {
        var startScore = scoreComponent.getScore();
        var startLevel = levelManager.getCurrentLevel();

        collisionHandler.getDamagedPlayerLife(new com.dinosaur.dinosaurexploder.components.LifeComponent());

        assertEquals(startScore, scoreComponent.getScore(), "Score should remain unchanged");
        assertEquals(startLevel, levelManager.getCurrentLevel(), "Level should remain unchanged");
    }

}
