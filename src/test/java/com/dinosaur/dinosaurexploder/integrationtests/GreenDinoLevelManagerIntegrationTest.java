// package com.dinosaur.dinosaurexploder.integrationtests;

// import com.almasb.fxgl.dsl.FXGL;
// import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
// import com.dinosaur.dinosaurexploder.utils.LevelManager;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedStatic;

// import java.lang.reflect.Field;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;

// public class GreenDinoLevelManagerIntegrationTest {

// //     @Test
// //     void testOnAddedSetsVerticalSpeed() throws Exception {
// //         // 1. Mocka LevelManager
// //         LevelManager mockLevelManager = mock(LevelManager.class);
// //         when(mockLevelManager.getEnemySpeed()).thenReturn(3.5);

// //         // 2. Mocka FXGL.geto() statiskt för "levelManager"
// //         try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class)) {
// //             fxglMock.when(() -> FXGL.geto("levelManager")).thenReturn(mockLevelManager);

// //             // 3. Skapa komponent och kör onAdded
// //             GreenDinoComponent dino = new GreenDinoComponent();
// //             dino.onAdded();

// //             // 4. Läs private field verticalSpeed via reflektion
// //             Field field = GreenDinoComponent.class.getDeclaredField("verticalSpeed");
// //             field.setAccessible(true);
// //             double verticalSpeed = (double) field.get(dino);

// //             // 5. Kontrollera att verticalSpeed sattes korrekt
// //             assertEquals(3.5, verticalSpeed);
// //         }
// //     }
// // }

package com.dinosaur.dinosaurexploder.integrationtests;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.LocalTimer;
import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;
import com.dinosaur.dinosaurexploder.utils.LevelManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class GreenDinoLevelManagerIntegrationTest {

    private GreenDinoComponent greenDino;
    private LevelManager levelManager;
    private LocalTimer mockTimer;

    @BeforeEach
    public void setUp() {
        greenDino = new GreenDinoComponent();
        levelManager = new LevelManager();

        // Mocka FXGL.newLocalTimer() så vi slipper engine
        mockTimer = mock(LocalTimer.class);
    }

    @Test
    public void testVerticalSpeedIsSetFromLevelManager() throws Exception {
        try (MockedStatic<FXGL> fxglMock = mockStatic(FXGL.class)) {
            // Mocka FXGL.geto() så vi får levelManager
            fxglMock.when(() -> FXGL.geto("levelManager")).thenReturn(levelManager);
            // Mocka FXGL.newLocalTimer() så vi slipper starta FXGL engine
            fxglMock.when(FXGL::newLocalTimer).thenReturn(mockTimer);

            // Kör onAdded
            greenDino.onAdded();

            // Hämta verticalSpeed med reflection
            Field verticalSpeedField = GreenDinoComponent.class.getDeclaredField("verticalSpeed");
            verticalSpeedField.setAccessible(true);
            double verticalSpeedValue = (double) verticalSpeedField.get(greenDino);

            // Kontrollera att verticalSpeed är samma som i LevelManager
            assertEquals(levelManager.getEnemySpeed(), verticalSpeedValue,
                    "GreenDinoComponent should take verticalSpeed from LevelManager");
        }
    }
}
