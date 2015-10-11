package com.goodgamestudios.exercise.oche;

import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This is unit test for EntityLogicMediator
 */
@RunWith(MockitoJUnitRunner.class)
public class EntityLogicMediatorTest {

    @InjectMocks
    private EntityLogicMediator entityLogicMediator;

    @Mock
    private Game game;

    @Test
    public void initMediatorTest() {
        entityLogicMediator.initEntities(game);
        assertNotNull("Ship and Aliens should be initialised!", entityLogicMediator.getShip());
    }

    @Test
    public void killAllAliensShouldNotifyWinTest() {
        entityLogicMediator.initEntities(game);
        for (int i = 0; i<60; i++) {
            entityLogicMediator.notifyAlienKilled();

        }
        verify(game, times(1)).notifyWin();
    }

    @Test(expected = IllegalStateException.class)
    public void tryToInitialiseWithNullGameTest(){
        entityLogicMediator.initEntities(null);
    }
}
