package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Optimized BombComponent test suite.
 * Combines logic, boundary, and Mockito verification for full coverage
 * without duplicate tests. Uses Arrange / Act / Assert style consistently.
 */
class BombComponentTest {

    // ---------- Testable subclass (for pure logic) ----------

    static class TestableBombComponent extends BombComponent {
        int spawnCalls = 0;

        @Override
        protected void updateBombUI() { /* no-op to avoid JavaFX */ }

        @Override
        protected void spawnBombBullets(Entity player) { spawnCalls++; }

        void drainTo(int targetCount) {
            while (getBombCount() > targetCount)
                useBomb(new Entity());
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
        comp.drainTo(1);

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

    // ---------- Level regeneration ----------

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

        // Act 2
        comp.checkLevelForBombRegeneration(3);

        // Assert 2
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

    // ---------- Coin regeneration ----------

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

    // ---------- Clamp & boundaries ----------

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

    // ---------- Mockito-based verification ----------

    @Test
    @DisplayName("useBomb: calls spawnBombBullets() once when bombCount > 0")
    void useBomb_calls_spawn_when_count_positive() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        while (spyComponent.getBombCount() > 1)
            spyComponent.useBomb(new Entity());
        clearInvocations(spyComponent);

        // Act
        spyComponent.useBomb(new Entity());

        // Assert
        verify(spyComponent, times(1)).spawnBombBullets(any());
        verify(spyComponent, atLeastOnce()).updateBombUI();
    }

    @Test
    @DisplayName("useBomb: never goes negative and never spawns after reaching zero")
    void useBomb_never_goes_negative_and_never_spawns_after_zero() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        while (spyComponent.getBombCount() > 0)
            spyComponent.useBomb(new Entity());
        clearInvocations(spyComponent);

        // Act
        for (int i = 0; i < 5; i++)
            spyComponent.useBomb(new Entity());

        // Assert
        verify(spyComponent, never()).spawnBombBullets(any());
        assertEquals(0, spyComponent.getBombCount());
    }

    @Test
    @DisplayName("updateBombUI: triggered on both useBomb() and regeneration")
    void ui_updates_on_useBomb_and_on_regen() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // Act 1
        spyComponent.useBomb(new Entity());

        // Assert 1
        verify(spyComponent, atLeastOnce()).updateBombUI();

        // Act 2
        clearInvocations(spyComponent);
        spyComponent.checkLevelForBombRegeneration(2);

        // Assert 2
        verify(spyComponent, atLeastOnce()).updateBombUI();
    }

    @Test
    @DisplayName("updateBombUI: not called when state remains unchanged")
    void ui_does_not_update_when_nothing_changes() {
        // Arrange
        var spyComponent = spy(new BombComponent());
        doNothing().when(spyComponent).updateBombUI();
        doNothing().when(spyComponent).spawnBombBullets(any());

        // Case 1: useBomb at zero
        while (spyComponent.getBombCount() > 0)
            spyComponent.useBomb(new Entity());
        clearInvocations(spyComponent);
        spyComponent.useBomb(new Entity());
        verify(spyComponent, never()).updateBombUI();

        // Case 2: coins below threshold
        clearInvocations(spyComponent);
        for (int i = 0; i < 14; i++)
            spyComponent.trackCoinForBombRegeneration();
        verify(spyComponent, never()).updateBombUI();

        // Case 3: same level
        clearInvocations(spyComponent);
        spyComponent.checkLevelForBombRegeneration(1);
        verify(spyComponent, never()).updateBombUI();
    }

    // ---------- Reflection (only for internal verification) ----------

    private int getPrivateBombCount(BombComponent bomb) {
        try {
            Field f = BombComponent.class.getDeclaredField("bombCount");
            f.setAccessible(true);
            return (int) f.get(bomb);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @DisplayName("Reflection: verify initial bombCount = 3")
    void reflection_initialValues_check() {
        // Arrange
        BombComponent bomb = new BombComponent();

        // Act + Assert
        assertEquals(3, getPrivateBombCount(bomb));
    }
}
