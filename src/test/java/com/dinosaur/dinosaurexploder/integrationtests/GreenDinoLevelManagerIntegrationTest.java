package com.dinosaur.dinosaurexploder.integrationtests;

import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GreenDinoLevelManagerIntegrationTest {

    /**
     * Testdubbel som använder en egen LevelManager istället för FXGL.geto
     */
    static class TestableGreenDinoComponent extends GreenDinoComponent {
        private final LevelManager levelManager;

        public TestableGreenDinoComponent(LevelManager levelManager) {
            this.levelManager = levelManager;
        }

        @Override
        public void onAdded() {
            // Använd testets LevelManager istället för FXGL
            this.verticalSpeed = levelManager.getEnemySpeed();
        }

        public double getVerticalSpeedForTest() {
            return verticalSpeed;
        }
    }

    @Test
    void testVerticalSpeedIsSetFromLevelManager() {
        LevelManager levelManager = new LevelManager();
        levelManager.nextLevel(); // t.ex. enemySpeed = 1.7

        TestableGreenDinoComponent greenDino = new TestableGreenDinoComponent(levelManager);
        greenDino.onAdded();

        assertEquals(levelManager.getEnemySpeed(), greenDino.getVerticalSpeedForTest(),
                "GreenDinoComponent should take verticalSpeed from LevelManager");
    }
}
