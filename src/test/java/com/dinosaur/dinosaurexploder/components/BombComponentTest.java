package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Pure logic tests for BombComponent (no JavaFX or FXGL).
 * Uses Arrange / Act / Assert style for clarity and consistency.
 */
class BombComponentTest {

    /** Test double disables UI and real FXGL spawn logic. */
    static class TestableBombComponent extends BombComponent {
        int spawnCalls = 0;

        @Override
        protected void updateBombUI() { /* no-op to avoid JavaFX */ }

        @Override
        protected void spawnBombBullets(Entity player) {
            spawnCalls++;
        }

        /** Helper: consume bombs until reaching a specific count. */
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
    @DisplayName("useBomb: boundary 1 → 0, spawns once more")
    void useBomb_boundary_1_to_0() {
        // Arrange
        comp.drainTo(1); // now bombCount = 1

        // Act
        comp.useBomb(new Entity());

        // Assert
        assertEquals(0, comp.getBombCount());
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
        comp.useBomb(new Entity());

        // Assert
        assertEquals(beforeCount, comp.getBombCount());
        assertEquals(beforeSpawns, comp.spawnCalls);
    }

    // ---------- Level regeneration (EP/BVA) ----------

    @Test
    @DisplayName("checkLevel: same level → no regeneration")
    void checkLevel_sameLevel_noRegen() {
        // Arrange
        comp.drainTo(2);

        // Act
        comp.checkLevelForBombRegeneration(1);

        // Assert
        assertEquals(2, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→2 → regenerates 1 bomb and clamps to max")
    void checkLevel_levelUp_regenAndClamp() {
        // Arrange
        comp.drainTo(2);

        // Act
        comp.checkLevelForBombRegeneration(2);

        // Assert
        assertEquals(3, comp.getBombCount());

        // Act 2: attempt another level up
        comp.checkLevelForBombRegeneration(3);

        // Assert 2: still max
        assertEquals(3, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: jump multiple levels → only one regeneration occurs")
    void checkLevel_jumpMultipleLevels_regenOnce() {
        // Arrange
        comp.drainTo(2);

        // Act
        comp.checkLevelForBombRegeneration(5);

        // Assert
        assertEquals(3, comp.getBombCount());

        // Act 2
        comp.checkLevelForBombRegeneration(6);

        // Assert 2
        assertEquals(3, comp.getBombCount());
    }

    // ---------- Coin regeneration (EP/BVA) ----------

    @Test
    @DisplayName("trackCoin: 14 coins → no regeneration, counter = 14")
    void trackCoin_14_noRegen() {
        // Arrange
        comp.drainTo(2);

        // Act
        for (int i = 0; i < 14; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(2, comp.getBombCount());
        assertEquals(14, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 15 coins → regenerates once and resets counter")
    void trackCoin_15_regenOnce() {
        // Arrange
        comp.drainTo(2);

        // Act
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 30 coins → two regens, clamped to max")
    void trackCoin_30_twoRegensClamped() {
        // Arrange
        comp.drainTo(1);

        // Act
        for (int i = 0; i < 30; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 15 coins while already max → stays at max")
    void trackCoin_15_atMax_staysMax() {
        // Arrange
        assertEquals(3, comp.getBombCount());

        // Act
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    // ---------- Clamp & boundary combination ----------

    @Test
    @DisplayName("Clamp: from 2 → regen to 3, then stay at 3 even with more regen")
    void clamp_from2_to3_thenStay() {
        // Arrange
        comp.drainTo(2);

        // Act
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();

        // Assert
        assertEquals(3, comp.getBombCount());

        // Act 2
        comp.checkLevelForBombRegeneration(2);

        // Assert 2
        assertEquals(3, comp.getBombCount());
    }

    // ---------- Initial state ----------

    @Test
    @DisplayName("Initial values in production class are correct")
    void initialValues_correct() {
        // Arrange + Act
        BombComponent prod = new BombComponent();

        // Assert
        assertEquals(3, prod.getBombCount());
        assertEquals(0, prod.getCoinCounter());
    }

    // ---------- Mixed scenario ----------

    @Test
    @DisplayName("Stress: multiple level ups and coin gains combined")
    void stress_multipleMixedRegeneration() {
        // Arrange
        comp.drainTo(1);

        // Act
        comp.checkLevelForBombRegeneration(2);
        for (int i = 0; i < 15; i++)
            comp.trackCoinForBombRegeneration();
        comp.checkLevelForBombRegeneration(3);

        // Assert
        assertEquals(3, comp.getBombCount());
    }

    // ---------- Mockito-based verification ----------

    @Test
    @DisplayName("useBomb: calls spawnBombBullets() once when bombCount > 0")
    void useBomb_calls_spawn_when_count_positive() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // Drain to 1 bomb to simulate ready-to-use state
        while (spyComponent.getBombCount() > 1)
            spyComponent.useBomb(new Entity());

        clearInvocations(spyComponent);

        // Act
        spyComponent.useBomb(new Entity());

        // Assert
        verify(spyComponent, times(1)).spawnBombBullets(any());
        verify(spyComponent, times(1)).updateBombUI();
        assertTrue(spyComponent.getBombCount() >= 0);
    }

        // ---------- Defensive behavior & UI verification (Mockito) ----------

    @Test
    @DisplayName("useBomb: never goes negative and never spawns after reaching zero")
    void useBomb_never_goes_negative_and_never_spawns_after_zero() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // Drain all bombs to zero (these will still spawn while > 0)
        while (spyComponent.getBombCount() > 0)
            spyComponent.useBomb(new Entity());

        clearInvocations(spyComponent); // reset invocation history

        // Act
        for (int i = 0; i < 10; i++)
            spyComponent.useBomb(new Entity()); // repeatedly press bomb key when already empty

        // Assert
        verify(spyComponent, never()).spawnBombBullets(any());
        assertEquals(0, spyComponent.getBombCount(), "Bomb count must never go below 0");
    }

    @Test
    @DisplayName("updateBombUI: triggered on both useBomb() and regeneration")
    void ui_updates_on_useBomb_and_on_regen() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // Act 1: useBomb (3 → 2)
        spyComponent.useBomb(new Entity());

        // Assert 1: UI updated
        verify(spyComponent, atLeastOnce()).updateBombUI();

        // Act 2: regeneration via level-up
        clearInvocations(spyComponent);
        spyComponent.checkLevelForBombRegeneration(2); // 2 → 3

        // Assert 2: UI updated again
        verify(spyComponent, atLeastOnce()).updateBombUI();
    }

    @Test
    @DisplayName("updateBombUI: not called when state remains unchanged")
    void ui_does_not_update_when_nothing_changes() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // --- Case 1: useBomb at zero (no bombs left) ---
        while (spyComponent.getBombCount() > 0)
            spyComponent.useBomb(new Entity());
        clearInvocations(spyComponent);

        // Act 1
        spyComponent.useBomb(new Entity());

        // Assert 1
        verify(spyComponent, never()).updateBombUI();

        // --- Case 2: coins below threshold (14 < 15) ---
        clearInvocations(spyComponent);
        for (int i = 0; i < 14; i++)
            spyComponent.trackCoinForBombRegeneration();

        // Assert 2
        verify(spyComponent, never()).updateBombUI();

        // --- Case 3: same level (no advancement) ---
        clearInvocations(spyComponent);
        spyComponent.checkLevelForBombRegeneration(1);

        // Assert 3
        verify(spyComponent, never()).updateBombUI();
    }

    // @Test
    // @DisplayName("checkLevelForBombRegeneration: regenerates only on exact +1 level increase")
    // void level_advances_only_on_increment_by_one() {
    //     // Arrange
    //     var spyComponent = spy(new BombComponent());
    //     doNothing().when(spyComponent).updateBombUI();
    //     doNothing().when(spyComponent).spawnBombBullets(any());

    //     // Drain from 3 → 2 so we can observe later regeneration
    //     while (spyComponent.getBombCount() > 2)
    //         spyComponent.useBomb(new Entity());

    //     clearInvocations(spyComponent);

    //     // Act 1: jump multiple levels (1 → 5)
    //     spyComponent.checkLevelForBombRegeneration(5);

    //     // Assert 1: no regeneration occurs
    //     assertEquals(2, spyComponent.getBombCount(), "Jumping several levels should not regenerate");

    //     // Act 2: exact +1 level increment (5 → 6)
    //     clearInvocations(spyComponent);
    //     spyComponent.checkLevelForBombRegeneration(6);

    //     // Assert 2: one regeneration occurs and UI refreshed
    //     assertEquals(3, spyComponent.getBombCount(), "Increment by one level should regenerate");
    //     verify(spyComponent, atLeastOnce()).updateBombUI();
    // }

}
