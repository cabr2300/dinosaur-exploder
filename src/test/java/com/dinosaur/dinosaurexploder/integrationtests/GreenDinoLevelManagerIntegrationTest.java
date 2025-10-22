package com.dinosaur.dinosaurexploder.integrationtests;

import com.almasb.fxgl.dsl.FXGL;
import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;

public class GreenDinoLevelManagerIntegrationTest {

    @Test
    void verticalSpeedIsSetFromLevelManager()
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        LevelManager levelManager = new LevelManager();
        levelManager.nextLevel();

        // mockar en dino
        try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class)) {
            fxglMock.when(() -> FXGL.geto("levelManager")).thenReturn(levelManager);

            GreenDinoComponent dino = new GreenDinoComponent();

            dino.onAdded();

            // reflektion för att försöka läsa ut verticalSpeed även fast det ligger som
            // skyddat i sin paket deklaration
            Field speedField = GreenDinoComponent.class.getDeclaredField("verticalSpeed");
            speedField.setAccessible(true);
            double speed = (double) speedField.get(dino);

            assertEquals(levelManager.getEnemySpeed(), speed,
                    "GreenDinoComponent should use LevelManager's enemySpeed");
        }
    }

}
