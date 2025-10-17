package com.dinosaur.dinosaurexploder.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import com.almasb.fxgl.entity.Entity;


public class HeartTest {

    @Test
    void checkHeartMovesDownWithSpeed() {
        Heart heart = new Heart();
        Entity e = new Entity();
        e.addComponent(heart);

        double startY = e.getY();
        double tpf = 0.016; 
        heart.onUpdate(tpf);

        assertTrue(e.getY() > startY);
        assertEquals(startY + 100.0 * tpf, e.getY(), 0.0001);
    }
}
