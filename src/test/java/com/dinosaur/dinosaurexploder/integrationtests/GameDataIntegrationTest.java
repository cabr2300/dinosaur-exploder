package com.dinosaur.dinosaurexploder.integrationtests;

import com.dinosaur.dinosaurexploder.exception.LockedShipException;
import com.dinosaur.dinosaurexploder.exception.LockedWeaponException;
import com.dinosaur.dinosaurexploder.model.GameData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataIntegrationTest {

    @Test
    @DisplayName("Test getters that involve HighScore, TotalCoins, and FileDataProvider classes")
    void testFileDataProviderIntegration() {
        assertDoesNotThrow(GameData::getHighScore);
        assertDoesNotThrow(GameData::getTotalCoins);
    }

    @Test
    @DisplayName("Test checks that involve WeaponUnlockChecker and ShipUnlockChecker")
    void testCheckUnlocked() {
        assertDoesNotThrow(() -> {
            assertTrue(GameData.checkUnlockedShip(1));
        });
        assertDoesNotThrow(() -> {
            assertTrue(GameData.checkUnlockedWeapon(1));
        });
    }

    @Test
    @DisplayName("Test setting a new ship and then getting it")
    void testSetShip() {
        assertDoesNotThrow(() -> GameData.setSelectedShip(2));
        assertEquals(2, GameData.getSelectedShip());
    }

    @Test
    @DisplayName("Test setter exceptions that involve WeaponUnlockChecker and ShipUnlockChecker")
    void testSetShipAndWeaponWithTooFewCoinsOrScore() {
        assertThrows(LockedShipException.class, () -> GameData.setSelectedShip(3));
        assertThrows(LockedWeaponException.class, () -> GameData.setSelectedWeapon(3));
    }
}
