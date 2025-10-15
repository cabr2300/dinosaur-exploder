package com.dinosaur.dinosaurexploder.components;

import com.dinosaur.dinosaurexploder.model.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.dinosaur.dinosaurexploder.utils.LevelManager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended unit tests for ScoreComponent (EP/BVA + robustness).
 * Covers highscore persistence, UI text updates, and edge cases.
 */
class ScoreComponentTest {

    // ---------- Basic get/set ----------

    @Test
    @DisplayName("get/set score: sets and retrieves correct value")
    void setAndGetScore_returnsCorrectValue() {
        // Arrange
        var comp = new ScoreComponent();

        // Act
        comp.setScore(10);

        // Assert
        assertEquals(10, comp.getScore());
    }

    // ---------- Increment logic ----------

    @Test
    @DisplayName("incrementScore: increases score by given value")
    void incrementScore_increasesCorrectly() {
        // Arrange
        var comp = new ScoreComponent();
        comp.setScore(5);

        // Act
        comp.incrementScore(7);

        // Assert
        assertEquals(12, comp.getScore());
    }

    @Test
    @DisplayName("incrementScore: handles negative increments safely")
    void incrementScore_negativeValue_decreasesScore() {
        // Arrange
        var comp = new ScoreComponent();
        comp.setScore(10);

        // Act
        comp.incrementScore(-3);

        // Assert
        assertEquals(7, comp.getScore());
    }

    @Test
    @DisplayName("incrementScore: triggers saveHighScore when new highscore reached")
    void incrementScore_triggersSaveOnNewHigh() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        comp.setScore(8);

        // Replace static highscore via reflection
        Field field = ScoreComponent.class.getDeclaredField("highScore");
        field.setAccessible(true);
        field.set(null, new HighScore(5));

        // Act
        comp.incrementScore(10);

        // Assert
        HighScore hs = (HighScore) field.get(null);
        assertTrue(hs.getHigh() >= 18);
    }

    @Test
    @DisplayName("incrementScore: does not trigger saveHighScore when below highscore")
    void incrementScore_doesNotTriggerSaveWhenBelowHigh() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        comp.setScore(10);

        Field field = ScoreComponent.class.getDeclaredField("highScore");
        field.setAccessible(true);
        field.set(null, new HighScore(50));

        // Act
        comp.incrementScore(5);

        // Assert
        assertEquals(15, comp.getScore());
        HighScore hs = (HighScore) field.get(null);
        assertEquals(50, hs.getHigh());
    }

    // ---------- Persistence ----------

    @Test
    @DisplayName("saveHighScore: writes HighScore object without throwing")
    void saveHighScore_writesFileSafely() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        Field field = ScoreComponent.class.getDeclaredField("highScore");
        field.setAccessible(true);
        field.set(null, new HighScore(123));

        Method saveMethod = ScoreComponent.class.getDeclaredMethod("saveHighScore");
        saveMethod.setAccessible(true);

        // Act + Assert
        assertDoesNotThrow(() -> saveMethod.invoke(comp));

        File file = new File(com.dinosaur.dinosaurexploder.constants.GameConstants.HIGH_SCORE_FILE);
        assertTrue(file.exists(), "High score file should be created");
    }

    @Test
    @DisplayName("loadHighScore: does not throw when file is missing")
    void loadHighScore_safeOnMissingFile() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        Method loadMethod = ScoreComponent.class.getDeclaredMethod("loadHighScore");
        loadMethod.setAccessible(true);

        // Act + Assert
        assertDoesNotThrow(() -> loadMethod.invoke(comp));
    }

    // ---------- UI text updates ----------

    @Test
    @DisplayName("updateTexts: updates text fields with score and highscore values")
    void updateTexts_setsCorrectLabels() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        Field scoreTextField = ScoreComponent.class.getDeclaredField("scoreText");
        Field highTextField = ScoreComponent.class.getDeclaredField("highScoreText");
        Field highScoreField = ScoreComponent.class.getDeclaredField("highScore");

        scoreTextField.setAccessible(true);
        highTextField.setAccessible(true);
        highScoreField.setAccessible(true);

        var scoreText = new javafx.scene.text.Text();
        var highText = new javafx.scene.text.Text();
        scoreTextField.set(comp, scoreText);
        highTextField.set(comp, highText);

        comp.setScore(42);
        highScoreField.set(null, new HighScore(99));

        Method updateMethod = ScoreComponent.class.getDeclaredMethod("updateTexts");
        updateMethod.setAccessible(true);

        // Act
        updateMethod.invoke(comp);

        // Assert
        assertTrue(scoreText.getText().contains("42"), "Score text should show current score");
        assertTrue(highText.getText().contains("99"), "High score text should show current high");
    }

    @Test
    @DisplayName("Coin collision gives +2 score")
    void coinCollision_increasesScoreByTwo() {
        // Arrange
        var levelManager = new LevelManager();
        var handler = new CollisionHandler(levelManager);
        var score = new ScoreComponent();
        var coins = new CollectedCoinsComponent() {
            @Override
            protected void updateText() {
            } // disable UI
        };

        int before = score.getScore();

        // Act
        handler.onPlayerGetCoin(coins, score, null);

        // Assert
        assertEquals(before + 2, score.getScore());
    }

    @Test
    @DisplayName("Defeating boss gives score equal to current level")
    void bossDefeat_givesScoreEqualToLevel() {
        // Arrange
        var levelManager = new LevelManager();
        var handler = new CollisionHandler(levelManager);
        var score = new ScoreComponent();

        levelManager.nextLevel(); // set to level 2
        levelManager.nextLevel(); // now level 3

        int before = score.getScore();

        // Act
        handler.handleBossDefeat(score);

        // Assert
        assertEquals(before + 3, score.getScore(), "Boss reward should match level value");
    }

    @Test
    @DisplayName("incrementScore: equal to current highscore should not trigger save")
    void incrementScore_equalToHigh_doesNotSave() throws Exception {
        // Arrange
        var comp = new ScoreComponent();
        comp.setScore(10);

        Field field = ScoreComponent.class.getDeclaredField("highScore");
        field.setAccessible(true);
        field.set(null, new HighScore(20));

        // Act
        comp.incrementScore(10); // new score = 20, equal to current highscore

        // Assert
        HighScore hs = (HighScore) field.get(null);
        assertEquals(20, hs.getHigh(), "Equal highscore should not overwrite");
    }

}
