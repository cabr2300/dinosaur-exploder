package com.dinosaur.dinosaurexploder.model;

import com.dinosaur.dinosaurexploder.components.BombComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BombComponentTest {

    private BombComponent bombComponent;

    @BeforeEach
    void setUp() {
        bombComponent = new BombComponent();
    }

    @Test
    void testInitialValues() {
        assertEquals(3, bombComponent.getBombCount(), "check bomb count");
        assertEquals(0, bombComponent.getCoinCounter(), "Check initial coins");
    }

    @Test
    void testCoinAddition() {
        // testing bomb regeneration at 15 coins would require bombComponent.onAdded()
        // which can't be run without initializing internal graphics
        for(int i = 0; i < 14; i++) {
            assertDoesNotThrow(() -> bombComponent.trackCoinForBombRegeneration(), "Check coin addition");
        }
        assertEquals(3, bombComponent.getBombCount(), "Check no bomb generation at 14 coins)");
    }

    @Test
    void testCheckLevel() {
        assertDoesNotThrow(() -> bombComponent.checkLevelForBombRegeneration(1));
        // cannot check level up because it requires bomb creation which requires graphics
    }
}
