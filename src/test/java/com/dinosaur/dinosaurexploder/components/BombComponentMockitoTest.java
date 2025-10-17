package com.dinosaur.dinosaurexploder.components;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito + Reflection test for BombComponent.
 * No changes to production code required.
 */
class BombComponentMockitoTest {

    /** Subclass disables FXGL and UI side effects. */
    static class MockableBombComponent extends BombComponent {
        @Override protected void updateBombUI() {}
        @Override protected void spawnBombBullets(Entity player) {}
        protected Node createBombUI() { return mock(Node.class); }
        protected void updateTexts() {}
    }

    MockableBombComponent bomb;

    @BeforeEach
    void setup() {
        bomb = Mockito.spy(new MockableBombComponent());
        bomb.bomb1 = mock(ImageView.class);
        bomb.bomb2 = mock(ImageView.class);
        bomb.bomb3 = mock(ImageView.class);
        bomb.bombText = mock(Text.class);

        doNothing().when(bomb).updateBombUI();
        doNothing().when(bomb).spawnBombBullets(any());
    }

    // ---------- Reflection helpers ----------
    private int getPrivateBombCount(BombComponent bomb) {
        try {
            Field f = BombComponent.class.getDeclaredField("bombCount");
            f.setAccessible(true);
            return (int) f.get(bomb);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void setPrivateBombCount(BombComponent bomb, int value) {
        try {
            Field f = BombComponent.class.getDeclaredField("bombCount");
            f.setAccessible(true);
            f.set(bomb, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // ---------- Tests ----------

    @Test
    @DisplayName("useBomb decrements and calls UI/spawn once")
    void useBomb_decrements_and_updates() {
        int before = getPrivateBombCount(bomb);
        bomb.useBomb(new Entity());
        assertEquals(before - 1, getPrivateBombCount(bomb));
        verify(bomb, times(1)).updateBombUI();
        verify(bomb, times(1)).spawnBombBullets(any());
    }

    @Test
    @DisplayName("useBomb does nothing when bombCount = 0")
    void useBomb_noEffect_whenEmpty() {
        setPrivateBombCount(bomb, 0);
        bomb.useBomb(new Entity());
        assertEquals(0, getPrivateBombCount(bomb));
        verify(bomb, never()).spawnBombBullets(any());
    }

    @Test
    @DisplayName("trackCoinForBombRegeneration regenerates at 15 coins")
    void trackCoin_regenerates_after15() {
        setPrivateBombCount(bomb, 2);
        for (int i = 0; i < 15; i++) bomb.trackCoinForBombRegeneration();
        assertEquals(3, getPrivateBombCount(bomb));
        verify(bomb, atLeastOnce()).updateBombUI();
    }

    @Test
    @DisplayName("Multiple coin thresholds clamp at max bombCount")
    void trackCoin_clampsAtMax() {
        setPrivateBombCount(bomb, 1);
        for (int i = 0; i < 60; i++) bomb.trackCoinForBombRegeneration();
        assertEquals(3, getPrivateBombCount(bomb));
    }

    @Test
    @DisplayName("checkLevelForBombRegeneration adds bomb when level increases")
    void levelUp_addsBomb_whenIncreased() {
        setPrivateBombCount(bomb, 2);
        bomb.checkLevelForBombRegeneration(2);
        assertEquals(3, getPrivateBombCount(bomb));
        verify(bomb, atLeastOnce()).updateBombUI();
    }

    @Test
    @DisplayName("checkLevelForBombRegeneration ignores same level")
    void levelUp_ignored_whenSameLevel() {
        setPrivateBombCount(bomb, 2);
        bomb.checkLevelForBombRegeneration(1);
        assertEquals(2, getPrivateBombCount(bomb));
    }

    // @Test
    // @DisplayName("updateBombUI: verifies correct visibility values via captors")
    // void updateBombUI_visibility() {
    //     // enable real logic for this test only
    //     doCallRealMethod().when(bomb).updateBombUI();

    //     ArgumentCaptor<Boolean> visibleCaptor = ArgumentCaptor.forClass(Boolean.class);
    //     setPrivateBombCount(bomb, 2);
    //     bomb.updateBombUI();

    //     verify(bomb.bombText).setText(contains("2"));
    //     // we don't rely on actual FXGL rendering, just logic behavior
    //     verify(bomb.bomb1, atLeastOnce()).setVisible(true);
    //     verify(bomb.bomb2, atLeastOnce()).setVisible(true);
    //     verify(bomb.bomb3, atLeastOnce()).setVisible(false);
    // }

    @Test
    @DisplayName("Initial bombCount should be 3")
    void initialValues_reflectionCheck() {
        assertEquals(3, getPrivateBombCount(bomb));
    }
}
