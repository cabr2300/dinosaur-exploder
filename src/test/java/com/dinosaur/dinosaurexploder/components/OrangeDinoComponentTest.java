package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;

import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;
import com.dinosaur.dinosaurexploder.components.OrangeDinoComponent;
import com.dinosaur.dinosaurexploder.utils.GameTimer;

public class OrangeDinoComponentTest {

    // unit tester
    @Test
    void dinoLivesIsTen() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        assertEquals(10, dino.getLives());

    }

    @Test
    void dinoLivesIs20AfterSet() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        dino.setLives(20);

        assertEquals(20, dino.getLives());

    }

    @Test
    void dinoLivesIs7AfterDamage() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        dino.damage(3);
        assertEquals(7, dino.getLives());

    }

    @Test
    void dinoLivesIsMinusOneAfterDamage() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        dino.damage(11);
        assertEquals(-1, dino.getLives());

    }

    @Test
    void dinoSpeedISOnePointFive() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        assertEquals(1.5, dino.getMovementSpeed());

    }

    @Test
    void dinoSpeedIsFiveAfterSet() {
        // need to mock to be able to initialise orangeDino
        GameTimer mockTimer = mock(GameTimer.class);
        PlayerComponent mockPlayer = new PlayerComponent();
        OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

        dino.setMovementSpeed(5);

        assertEquals(5, dino.getMovementSpeed());

    }

    // @Test
    // void dinoMovedUp()
    // throws IllegalArgumentException, IllegalAccessException,
    // NoSuchFieldException, SecurityException {
    // // need to mock to be able to initialise orangeDino
    // GameTimer mockTimer = mock(GameTimer.class);
    // PlayerComponent mockPlayer = new PlayerComponent();
    // OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

    // Entity mockEntity = mock(Entity.class);
    // when(mockEntity.getY()).thenReturn(50.0);

    // Field entityField = Component.class.getDeclaredField("entity");
    // entityField.setAccessible(true);
    // entityField.set(dino, mockEntity);

    // dino.moveUp();

    // verify(mockEntity).translateY(-1.5);

    // }

    // @Test
    // void dinoIsPausedTrueAfterSet() {
    // // need to mock to be able to initialise orangeDino
    // GameTimer mockTimer = mock(GameTimer.class);
    // PlayerComponent mockPlayer = new PlayerComponent();
    // OrangeDinoComponent dino = new OrangeDinoComponent(mockTimer, mockPlayer);

    // dino.setPaused(true);

    // assertEquals(null, dino.onUpdate(3));

    // }

}
