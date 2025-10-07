package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.almasb.fxgl.entity.Entity;

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
            // BombComponent starts with 3 bombs
            while (getBombCount() > targetCount) {
                useBomb(new Entity());
            }
            // We never increase directly here; regen is tested via level/coins
        }
    }

    // ---------- useBomb (EP/BVA) ----------

    @Test
    @DisplayName("useBomb: from 3 → decrements to 2 and spawns once")
    void useBomb_decrements_and_spawns_from3() {
        var comp = new TestableBombComponent();
        assertEquals(3, comp.getBombCount());
        comp.useBomb(new Entity());
        assertEquals(2, comp.getBombCount());
        assertEquals(1, comp.spawnCalls);
    }

    @Test
    @DisplayName("useBomb: boundary 1 → 0, spawns once")
    void useBomb_boundary_1_to_0() {
        var comp = new TestableBombComponent();
        comp.drainTo(1); // now bombCount = 1
        comp.useBomb(new Entity());
        assertEquals(0, comp.getBombCount());
        assertEquals(3, comp.spawnCalls); // used 2 to drain (3->2, 2->1) + this one = 3
    }

    @Test
    @DisplayName("useBomb: at 0 → no decrement and no spawn")
    void useBomb_at_zero_no_effect() {
        var comp = new TestableBombComponent();
        comp.drainTo(0);
        int before = comp.getBombCount();
        comp.useBomb(new Entity()); // extra call at zero
        assertEquals(before, comp.getBombCount());
        assertEquals(3, comp.spawnCalls); // unchanged from draining
    }

    // ---------- checkLevelForBombRegeneration (EP/BVA) ----------

    @Test
    @DisplayName("checkLevel: same level (1→1) → no regeneration")
    void level_same_no_regen() {
        var comp = new TestableBombComponent();
        comp.drainTo(2); // to detect a regen later if it happens
        comp.checkLevelForBombRegeneration(1); // lastLevel starts at 1
        assertEquals(2, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→2 (boundary) → +1 regen, clamped to max")
    void level_up_by_one_regen_and_clamp() {
        var comp = new TestableBombComponent();
        comp.drainTo(2); // currently 2
        comp.checkLevelForBombRegeneration(2);
        assertEquals(3, comp.getBombCount()); // back to max
        // extra level up while already max -> still 3
        comp.checkLevelForBombRegeneration(3);
        assertEquals(3, comp.getBombCount());
    }

    @Test
    @DisplayName("checkLevel: 1→5 (jump) → one regen for that call, lastLevel updated")
    void level_jump_regen_once_and_update_lastLevel() {
        var comp = new TestableBombComponent();
        comp.drainTo(2); // 2
        comp.checkLevelForBombRegeneration(5);
        assertEquals(3, comp.getBombCount());
        // calling again with 6 still regens only if there is room
        comp.checkLevelForBombRegeneration(6);
        assertEquals(3, comp.getBombCount()); // already at max => clamp
    }

    // ---------- trackCoinForBombRegeneration (EP/BVA) ----------

    @Test
    @DisplayName("coins: 14 (just below threshold) → no regen, counter=14")
    void coins_14_no_regen_counter14() {
        var comp = new TestableBombComponent();
        comp.drainTo(2); // 2 bombs left
        for (int i = 0; i < 14; i++) comp.trackCoinForBombRegeneration();
        assertEquals(2, comp.getBombCount());
        assertEquals(14, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 15 (threshold) → regen once, counter resets to 0")
    void coins_15_regen_once_and_reset() {
        var comp = new TestableBombComponent();
        comp.drainTo(2); // 2 bombs
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount()); // +1 and clamp to max
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 30 (2×threshold) → up to two regens across sequence, clamped to max")
    void coins_30_two_regens_clamped() {
        var comp = new TestableBombComponent();
        comp.drainTo(1); // start at 1 so we can observe two increments
        for (int i = 0; i < 30; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount()); // 1 -> 2 at 15, 2 -> 3 at 30
        assertEquals(0, comp.getCoinCounter());
    }

    @Test
    @DisplayName("coins: 15 at already max → stays at max, counter resets")
    void coins_15_at_max_stays_max() {
        var comp = new TestableBombComponent(); // already at 3
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration();
        assertEquals(3, comp.getBombCount());
        assertEquals(0, comp.getCoinCounter());
    }

    // ---------- Clamp behavior via public paths ----------

    @Test
    @DisplayName("Clamp: from max-1 then regen → hits exactly max; further regen attempts stay at max")
    void clamp_from_maxMinusOne_hits_max_and_stays() {
        var comp = new TestableBombComponent();
        comp.drainTo(2);
        // Regen via coins to reach max
        for (int i = 0; i < 15; i++) comp.trackCoinForBombRegeneration(); // +1 to 3
        assertEquals(3, comp.getBombCount());
        // Try to regen again (level up) -> remains 3
        comp.checkLevelForBombRegeneration(2);
        assertEquals(3, comp.getBombCount());
    }
}
