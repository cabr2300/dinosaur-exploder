package com.dinosaur.dinosaurexploder.components;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import com.almasb.fxgl.entity.Entity;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;

import com.almasb.fxgl.entity.component.Component;

public class CoinComponentTest {

    @Test
    void testCoinMovesDownward() throws Exception {
        // Arrange
        Entity mockEntity = mock(Entity.class);
        CoinComponent coin = new CoinComponent();

        // sätt mockEntity som coin.entity via reflection
        Field entityField = Component.class.getDeclaredField("entity");
        entityField.setAccessible(true);
        entityField.set(coin, mockEntity);

        double tpf = 0.016; // t.ex. 16 ms per frame
        double expectedMovement = 100.0 * tpf;

        // Act
        coin.onUpdate(tpf);

        // Assert
        verify(mockEntity).translateY(expectedMovement);
    }
}
