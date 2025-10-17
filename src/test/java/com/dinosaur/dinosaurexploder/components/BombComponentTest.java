package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit tests for BombComponent using JUnit 5.
 * We avoid JavaFX/FXGL side-effects by overriding UI/spawn methods.
 */
class BombComponentTest {

    /** Test double that neutralizes UI and records spawn calls. */
    static class TestableBombComponent extends BombComponent {
        int spawnCalls = 0;

        @Override
        protected void updateBombUI() {
            /* no-op in unit tests */
        }

        @Override
        protected void spawnBombBullets(Entity player) {
            spawnCalls++;
        }

        /** Helper to set the bomb count by consuming bombs to reach a target. */
        void drainTo(int targetCount) {
            while (getBombCount() > targetCount) {
                useBomb(new Entity());
            }
        }
    }

    TestableBombComponent comp;

    @BeforeEach
    void setup() {
        comp = new TestableBombComponent();
    }

    // ---------- useBomb (EP/BVA) ----------

    @Test
    @DisplayName("useBomb: from 3 → decrements to 2 and spawns once")
    void useBomb_decrements_and_spawns_from3() {
        // Arrange
        assertEquals(3, comp.getBombCount());

        // Act
        comp.useBomb(new Entity());

        // Assert
        assertEquals(2, comp.getBombCount());
        assertEquals(1, comp.spawnCalls);
    }

    @Test
    @DisplayName("useBomb: boundary 1 → 0, spawns once")
    void useBomb_boundary_1_to_0() {
        // Arrange
        comp.drainTo(1); // now bombCount = 1

        // Act
        comp.useBomb(new Entity());

        // Assert
        assertEquals(0, comp.getBombCount());
        // used 2 to drain (3->2, 2->1) + this one = 3
        assertEquals(3, comp.spawnCalls);
    }

    @Test
    @DisplayName("useBomb: at 0 → no decrement and no spawn")
    void useBomb_at_zero_no_effect() {
        // Arrange
        comp.drainTo(0);
        int beforeCount = comp.getBombCount();
        int beforeSpawns = comp.spawnCalls;

        // Act
        comp.useBomb(new Entity()); // extra call at zero

        // Assert
        assertEquals(beforeCount, comp.getBombCount());
        assertEquals(beforeSpawns, comp.spawnCalls); // unchanged from draining
    }

    // ---------- checkLevelForBombRegeneration (EP/BVA) ----------

    @Test
    @DisplayName("checkLevel: same level (1→1) → no regeneration")
    void level_same_no_regen() {
        // Arrange
        comp.drainTo(2); // detect regen if it happens

        // Act
        comp.checkLevelForBombRegeneration(1); // lastLevel starts at 1

        // Assert
        assertEquals(2, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→2 (boundary) → +1 regen, clamped to max")
    void level_up_by_one_regen_and_clamp() {
        // Arrange
        comp.drainTo(2); // currently 2

        // Act
        comp.checkLevelForBombRegeneration(2);

        // Assert
        assertEquals(3, comp.getBombCount()); // back to max

        // Act 2: extra level up while already max
        comp.checkLevelForBombRegeneration(3);

        // Assert 2: still 3
        assertEquals(3, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→5 (jump) → one regen for that call, lastLevel updated")
    void level_jump_regen_once_and_update_lastLevel() {
        // Arrange
        comp.drainTo(2);

        // Act
        comp.checkLevelForBombRegeneration(5);

        // Assert
        assertEquals(3, comp.getBombCount());

        // Act 2: call again with a higher level
        comp.checkLevelForBombRegeneration(6);

        // Assert 2: already at max → stays clamped
        assertEquals(3, comp.getBombCount());
    }

    // ---------- trackCoinForBombRegeneration (EP/BVA) ----------

    @Test
    @DisplayName("coins: 14 (just below threshold) → no regen, counter=14")
    void coins_14_no_regen_counter14() {
        // Arrange
        comp.drainTo(2); // 2 bombs left

        // Act
        for (int i = 0; i < 14; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(2, comp.getBombCount());
        assertEquals(14, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 15 (threshold) → regen once, counter resets to 0")
    void coins_15_regen_once_and_reset() {
        // Arrange
        comp.drainTo(2);

        // Act
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount()); // +1 and clamp to max
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 30 (2×threshold) → up to two regens across sequence, clamped to max")
    void coins_30_two_regens_clamped() {
        // Arrange
        comp.drainTo(1); // start at 1 so we can observe two increments

        // Act
        for (int i = 0; i < 30; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount()); // 1->2 at 15, 2->3 at 30
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 15 at already max → stays at max, counter resets")
    void coins_15_at_max_stays_max() {

        // already at 3

        // Act
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    // ---------- Clamp behavior via public paths ----------

    @Test
    @DisplayName("Clamp: from max-1 then regen → hits exactly max; further regen attempts stay at max")
    void clamp_from_maxMinusOne_hits_max_and_stays() {
        // Arrange
        comp.drainTo(2);

        // Act: regen via coins to reach max
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert: at max
        assertEquals(3, comp.getBombCount());

        // Act 2: try to regen again (level up)
        comp.checkLevelForBombRegeneration(2);

        // Assert 2: remains 3
        assertEquals(3, comp.getBombCount());
    }

    @Test
    @DisplayName("Intital values tested on production class")
    void testInitialValues() {
        BombComponent bombComponent = new BombComponent();
        assertEquals(3, bombComponent.getBombCount(), "check bomb count");
        assertEquals(0, bombComponent.getCoinCounter(), "Check initial coins");
    }

    @Test
    @DisplayName("Coin addition with bomb regeneration, testable subclass")
    void testCoinAddition() {

        comp.drainTo(2);

        for (int i = 0; i < 14; i++) {
            assertDoesNotThrow(comp::trackCoinForBombRegeneration, "Check coin addition");
        }
        assertEquals(2, comp.getBombCount(), "Check no bomb generation at 14 coins)");

        assertDoesNotThrow(comp::trackCoinForBombRegeneration, "Check coin addition past 14");
        assertEquals(3, comp.getBombCount(), "Check bomb generation at 15 coins)");
    }

    @Test
    @DisplayName("Level up with bomb regeneration, testable subclass")
    void testCheckLevel() {

        comp.drainTo(2);

        assertDoesNotThrow(() -> comp.checkLevelForBombRegeneration(1), "Calling method without changing level");
        assertEquals(2, comp.getBombCount(), "Before bomb regeneration");

        assertDoesNotThrow(() -> comp.checkLevelForBombRegeneration(2), "Calling method for level change");
        assertEquals(3, comp.getBombCount(), "After bomb regeneration");
    }
    

}
