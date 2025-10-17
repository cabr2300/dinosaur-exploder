package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class LifeComponentTest {
    
    @Test
    void checkCurrentLifestatus (){
        LifeComponent lifecomponent = new LifeComponent(); 
        int testLife = lifecomponent.getLife();
        assertEquals(testLife, 3);

    }

    @Test
    void checkLifeUpdates (){
        LifeComponent lifecomponent = new LifeComponent();
        int currentLife = lifecomponent.getLife();

        //start with 3 lifes:
        assertEquals(currentLife, 3);

        //decreaseLife, from 3 to 2 lives:
        currentLife = lifecomponent.decreaseLife(1);
        assertEquals(currentLife, 2);

        //increaseLife, from 2 to 3 lives:
        currentLife=lifecomponent.increaseLife(1);
        assertEquals(currentLife, 3);

        //increase Life when you have maxlife:
        currentLife=lifecomponent.increaseLife(1);
        assertEquals(currentLife, 3);

        //decreaseLife, from 3 to -1
        currentLife=lifecomponent.decreaseLife(4);
        assertEquals(currentLife, -1);


    }





}
