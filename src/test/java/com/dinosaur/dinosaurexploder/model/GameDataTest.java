// package com.dinosaur.dinosaurexploder.model;

// import com.dinosaur.dinosaurexploder.exception.LockedShipException;
// import com.dinosaur.dinosaurexploder.utils.FileDataProvider;

// import com.dinosaur.dinosaurexploder.utils.ShipUnlockChecker;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedConstruction;
// import org.mockito.Mockito;

// import static org.mockito.Mockito.*;
// import static org.junit.jupiter.api.Assertions.*;

// import java.util.List;

// public class GameDataTest {

//     @Test
//     @DisplayName("Test basic getters")
//     void testIntitialStats() {
//         assertEquals(1, GameData.getSelectedShip(), "Test initial ship");
//         assertEquals(1, GameData.getSelectedWeapon(), "Test initial weapon");
//     }

//     @Test
//     @DisplayName("Test getters that require FileDataProvider by mocking it")
//     void testGettersWithFileDataprovider() {

//         // mock creation inside method for static testing
//         HighScore mockHighScore = Mockito.mock(HighScore.class);
//         TotalCoins mockTotalCoins = Mockito.mock(TotalCoins.class);

//         // setting up mock return values
//         when(mockHighScore.getHigh()).thenReturn(150);
//         when(mockTotalCoins.getTotal()).thenReturn(200);

//         // static mocking has auto-close and only active within a try-block
//         try (MockedConstruction<FileDataProvider> mockedConstruction =
//                 // mock: the mock of FileDataProvider. context: each of the crated instances of the mock
//                      Mockito.mockConstruction(FileDataProvider.class, (mock, context) -> {
//                          when(mock.getHighScore()).thenReturn(mockHighScore);
//                          when(mock.getTotalCoins()).thenReturn(mockTotalCoins);
//                      })) {

//             assertEquals(150, GameData.getHighScore(), "getHighScore() works");
//             assertEquals(200, GameData.getTotalCoins(), "getTotalCoins() works");

//             // list of mocks constructed in the test
//             List<FileDataProvider> constructed = mockedConstruction.constructed();
//             // the class creates FileDataProvider instances 4 times. While only two are called in the test, all 4 are created
//             assertEquals(4, constructed.size());
//             // the two first mocks are created by ShipUnlockChecker and WeaponUnlockChecker and never used
//             verify(constructed.get(2)).getHighScore();
//             verify(constructed.get(3)).getTotalCoins();
//         }
//     }

//     @Test
//     @DisplayName("checkUnlockedShip()")
//     void testGetUnlocked() {

//         try (MockedConstruction<ShipUnlockChecker> mockedConstruction =
//                      Mockito.mockConstruction(ShipUnlockChecker.class, (mock, context) -> {
//                          when(mock.check(20)).thenReturn(1);
//                      })) {
//             assertDoesNotThrow(() -> GameData.checkUnlockedShip(-1));
//             assertTrue(GameData.checkUnlockedShip(20));

//             // list of mocks constructed in the test
//             List<ShipUnlockChecker> constructed = mockedConstruction.constructed();
//             assertEquals(1, constructed.size());
//             verify(constructed.getFirst()).check(20);
//             verify(constructed.getFirst()).check(-1);
//         }
//     }
// }
