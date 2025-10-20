package com.dinosaur.dinosaurexploder.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.dinosaur.dinosaurexploder.components.GreenDinoComponent;

import javafx.geometry.Point2D;

public class GreenDinoProjectileSpeedTest {


    private static double EnemyProjectileSpeed() {
        Entity projectil = new Entity();
        projectil.addComponent(new ProjectileComponent(new Point2D(0, 1), 300));

        double tpf = 0.016;
        double y0 = projectil.getY();
        projectil.getComponent(ProjectileComponent.class).onUpdate(tpf);
        return (projectil.getY() - y0) / tpf; // pixlar per sek.
    }

    private static double DinoSpeed(LevelManager lm) {
        try (MockedStatic<FXGL> mocked = Mockito.mockStatic(FXGL.class)) {
            mocked.when(() -> FXGL.geto("levelManager")).thenReturn(lm);

            LocalTimer fakeTimer = Mockito.mock(LocalTimer.class);
            Mockito.when(fakeTimer.elapsed(Mockito.any(Duration.class))).thenReturn(false);
            mocked.when(FXGL::newLocalTimer).thenReturn(fakeTimer);

            Entity dino = new Entity();
            GreenDinoComponent comp = new GreenDinoComponent();
            dino.addComponent(comp);
            comp.onAdded();

            double y0 = dino.getY();
            comp.onUpdate(0.016);
            return dino.getY() - y0; //px/frame
        }
    }

    @Test
    void DinoSpeedVsProjektilSpeedLevel1() {
        LevelManager lm = new LevelManager();
        double projectileSpeed = EnemyProjectileSpeed();
        assertEquals(300.0, projectileSpeed);
        double projectilePxPerFrame = projectileSpeed / 60.0;  //(gör om pixlar/sek till pixlar/frame)

        // Level 1 
        double dinoPxPerFrame = DinoSpeed(lm);
        assertTrue(projectilePxPerFrame > dinoPxPerFrame); //dino snabbare än projectil

    }

    @Test
    void DinoSpeedVsProjektilSpeedLevel1to10() {
        LevelManager lm = new LevelManager();
        double projectileSpeed = EnemyProjectileSpeed();
        double projectilePxPerFrame = projectileSpeed / 60.0;  //(gör om pixlar/sek till pixlar/frame)

        // Level 1-10
        for (int i = 0; i < 10; i++) lm.nextLevel(); // dino snabbare än projectil första 10 levels
        double dinoPxPerFrame = DinoSpeed(lm);
        assertTrue(projectilePxPerFrame > dinoPxPerFrame);

    }

        @Test
        void DinoSpeedVsProjektilSpeedLevel1To100() {
        LevelManager lm = new LevelManager();
        double projectileSpeed = EnemyProjectileSpeed();
        double projectilePxPerFrame = projectileSpeed / 60.0;  //(gör om pixlar/sek till pixlar/frame)

            for (int level = 1; level <= 100; level++) {
                double dinoPxPerFrame = DinoSpeed(lm);

                boolean ok = projectilePxPerFrame > dinoPxPerFrame;

                System.out.printf("Level %2d: projectile=%.2f, dino=%.2f -> %s%n",
                        level, projectilePxPerFrame, dinoPxPerFrame, ok ? "OK" : "FAIL");
                assertTrue(projectilePxPerFrame > dinoPxPerFrame);
                lm.nextLevel();
            }

    }

}
