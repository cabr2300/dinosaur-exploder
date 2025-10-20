package com.dinosaur.dinosaurexploder.components;

import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GreenDinoComponentTest {

    /**
     * Test-dubbel som låter oss styra LevelManager utan FXGL.
     */
    static class TestableGreenDinoComponent extends GreenDinoComponent {

        private final LevelManager levelManager;
        boolean shootCalled = false;

        public TestableGreenDinoComponent(LevelManager levelManager) {
            this.levelManager = levelManager;
        }

        @Override
        public void onAdded() {
            // Hoppa över FXGL.geto och använd testets levelManager
            this.verticalSpeed = levelManager.getEnemySpeed();
        }

        @Override
        public void shoot() {
            // Undvik FXGL.spawn och AudioManager
            shootCalled = true;
        }

        public double getVerticalSpeedForTest() {
            return verticalSpeed;
        }
    }

    @Test
    void testVerticalSpeedSetFromLevelManager() {
        LevelManager levelManager = new LevelManager();
        levelManager.nextLevel(); // Öka enemySpeed t.ex. till 1.7

        TestableGreenDinoComponent dino = new TestableGreenDinoComponent(levelManager);
        dino.onAdded();

        assertEquals(levelManager.getEnemySpeed(), dino.getVerticalSpeedForTest(),
                "verticalSpeed should match LevelManager enemy speed");
    }

    @Test
    void testShootCalledDuringUpdate() {
        LevelManager levelManager = new LevelManager();
        TestableGreenDinoComponent dino = new TestableGreenDinoComponent(levelManager);

        // Vi sätter timer och y-position manuellt så villkoret i onUpdate() triggar
        dino.verticalSpeed = 1.5;
        dino.entity = new DummyEntity(10, 10); // fake entity så getPosition funkar
        dino.timer.capture(); // Simulera att timer redan tickat

        // Nu kör vi update
        dino.onUpdate(0.016);

        // Verifiera att shoot() anropades
        assertTrue(dino.shootCalled, "shoot() should be called when enough time has passed");
    }

    /**
     * Enkel fake Entity för att undvika FXGL:s riktiga Entity.
     */
    static class DummyEntity extends com.almasb.fxgl.entity.Entity {
        private final javafx.geometry.Point2D pos;

        DummyEntity(double x, double y) {
            this.pos = new javafx.geometry.Point2D(x, y);
        }

        @Override
        public javafx.geometry.Point2D getPosition() {
            return pos;
        }

        @Override
        public void translateY(double value) {
            // no-op
        }
    }
}
