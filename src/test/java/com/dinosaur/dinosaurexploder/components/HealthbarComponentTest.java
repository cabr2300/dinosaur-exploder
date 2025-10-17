package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.dinosaur.dinosaurexploder.interfaces.Dinosaur;

import com.almasb.fxgl.entity.Entity;


public class HealthbarComponentTest {



    @Test
    void checkHealthBarWidthScalesWithLives() {
        Dinosaur dino = Mockito.mock(Dinosaur.class);
        Mockito.when(dino.getLives()).thenReturn(4);

        HealthbarComponent comp = new HealthbarComponent();
        comp.setDinoComponent(dino);
        comp.getDinoComponent();

        Entity e = new Entity();
        e.addComponent(comp);
        comp.onAdded();
        comp.updateBar();


    }
}
