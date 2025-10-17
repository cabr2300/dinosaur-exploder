package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.Entity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Pure logic tests for BombComponent (no JavaFX or FXGL).
 * - updateBombUI() and spawnBombBullets() are neutralized for test stability.
 * - Focus on logical behavior and boundary conditions.
 */
class BombComponentTest2 {

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

    // ---------- useBomb tests ----------

    @Test
    @DisplayName("useBomb: from 3 → decrements to 2 and spawns once")
    void useBomb_decrements_and_spawns_from3() {
        assertEquals(3, comp.getBombCount());
        comp.useBomb(new Entity());
        assertEquals(2, comp.getBombCount());
        assertEquals(1, comp.spawnCalls);
    }

    @Test
    @DisplayName("useBomb: boundary 1 → 0, spawns once more")
    void useBomb_boundary_1_to_0() {
        comp.drainTo(1);
        comp.useBomb(new Entity());
        assertEquals(0, comp.getBombCount());
        assertEquals(3, comp.spawnCalls);
    }

    @Test
    @DisplayName("useBomb: at 0 → no decrement and no spawn")
    void useBomb_at_zero_no_effect() {
        comp.drainTo(0);
        int beforeCount = comp.getBombCount();
        int beforeSpawns = comp.spawnCalls;
        comp.useBomb(new Entity());
        assertEquals(beforeCount, comp.getBombCount());
        assertEquals(beforeSpawns, comp.spawnCalls);
    }

    // ---------- Level regeneration tests ----------

    @Test
    @DisplayName("checkLevel: same level → no regeneration")
    void checkLevel_sameLevel_noRegen() {
        comp.drainTo(2);
        comp.checkLevelForBombRegeneration(1);
        assertEquals(2, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→2 → regenerates 1 bomb and clamps to max")
    void checkLevel_levelUp_regenAndClamp() {
        comp.drainTo(2);
        comp.checkLevelForBombRegeneration(2);
        assertEquals(3, comp.getBombCount());
        comp.checkLevelForBombRegeneration(3);
        assertEquals(3, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: jump multiple levels → still only 1 regeneration")
    void checkLevel_jumpMultipleLevels_regenOnce() {
        comp.drainTo(2);
        comp.checkLevelForBombRegeneration(5);
        assertEquals(3, comp.getBombCount());
        comp.checkLevelForBombRegeneration(6);
        assertEquals(3, comp.getBombCount());
    }

    // ---------- Coin regeneration tests ----------

    @Test
    @DisplayName("trackCoin: 14 coins → no regeneration, counter = 14")
    void trackCoin_14_noRegen() {
        comp.drainTo(2);
        for (int i = 0; i < 14; i++) comp.trackCoinForBombRegeneration();
        assertEquals(2, comp.getBombCount());
        assertEquals(14, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 15 coins → regenerates once and resets counter")
    void trackCoin_15_regenOnce() {
        comp.drainTo(2);
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 30 coins → two regens, clamped to max")
    void trackCoin_30_twoRegensClamped() {
        comp.drainTo(1);
        for (int i = 0; i < 30; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("trackCoin: 15 coins while already max → stays max")
    void trackCoin_15_atMax_staysMax() {
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    // ---------- Clamp & boundary combo ----------

    @Test
    @DisplayName("Clamp test: from 2 → regen to 3, then stay at 3 even with more regen")
    void clamp_from2_to3_thenStay() {
        comp.drainTo(2);
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        comp.checkLevelForBombRegeneration(2);
        assertEquals(3, comp.getBombCount());
    }

    // ---------- Initial state tests ----------

    @Test
    @DisplayName("Initial values in production class are correct")
    void initialValues_correct() {
        BombComponent prod = new BombComponent();
        assertEquals(3, prod.getBombCount());
        assertEquals(0, prod.getCoinCounter());
    }

    @Test
    @DisplayName("Logic stress: multiple level ups and coin gains combined")
    void stress_multipleMixedRegeneration() {
        comp.drainTo(1);
        comp.checkLevelForBombRegeneration(2);
        assertEquals(2, comp.getBombCount());
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        comp.checkLevelForBombRegeneration(3);
        assertEquals(3, comp.getBombCount());
    }
    @Test
    void useBomb_calls_spawn_when_count_positive() {
        var spy = spy(new BombComponent());
        // prepare state: set internal count to 1 (via public path if possible)
        doNothing().when(spy).updateBombUI();
        doNothing().when(spy).spawnBombBullets(any());
        spy.checkLevelForBombRegeneration(999); // or your chosen public path to reach 1
        spy.useBomb(new Entity());
        verify(spy, times(1)).spawnBombBullets(any());
    }
 
    @Test
    void useBomb_does_not_spawn_at_zero() {
        var spy = spy(new BombComponent());
 
        // neutralize side-effects
        doNothing().when(spy).updateBombUI();
        doNothing().when(spy).spawnBombBullets(any());
 
        // drain to 0 via public API (this WILL call spawnBombBullets while >0)
        while (spy.getBombCount() > 0) {
            spy.useBomb(new Entity());
        }
 
        // clear the recorded history so we only check the *next* call
        clearInvocations(spy);
 
        // call at zero
        spy.useBomb(new Entity());
 
        // assert: no new spawn at zero
        verify(spy, never()).spawnBombBullets(any());
    }
}
