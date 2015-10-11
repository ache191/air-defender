package com.goodgamestudios.exercise.oche.logic;

/**
 * Created by a.chekanskiy@gmail.com on 09.10.15.
 */

import com.goodgamestudios.exercise.oche.Game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * "Mediator" for key input logic
 * A class to handle keyboard input from the user. The class
 * handles both dynamic input during game play, i.e. left/right/up/down/shoot/pause
 * and more static type input (i.e. press any key to
 * continue)
 */
public class KeyInputLogicMediator extends KeyAdapter {
    private static final int ESC_CODE = 27;

    private static volatile KeyInputLogicMediator INSTANCE = null;

    public static KeyInputLogicMediator getInstance() {
        if (INSTANCE == null) {
            synchronized (EntityLogicMediator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new KeyInputLogicMediator();
                }
            }
        }
        return INSTANCE;
    }

    private KeyInputLogicMediator() {
        this.pressCount = 1;
        this.waitingForKeyPress = true;
        this.upPressed = false;
        this.downPressed = false;
        this.leftPressed = false;
        this.rightPressed = false;
        this.firePressed = false;
        this.pausePressed = false;
    }

    //Current game entity exists in
    private Game game;

    //true if we're holding up game play until a key has been pressed
    private boolean waitingForKeyPress;
    //true if the up cursor key is currently pressed
    private boolean upPressed;
    //true if the down cursor key is currently pressed
    private boolean downPressed;
    //true if the left cursor key is currently pressed
    private boolean leftPressed;
    //true if the right cursor key is currently pressed
    private boolean rightPressed;
    //true if we are firing
    private boolean firePressed;
    //true if we are on pause
    private boolean pausePressed;

    //The number of key presses we've had while waiting for an "any key" press
    private int pressCount;

    public void init(Game game) {
        this.game = game;
    }

    /**
     * Notification from AWT that a key has been pressed. Note that
     * a key being pressed is equal to being pushed down but *NOT*
     * released. Thats where keyTyped() comes in.
     *
     * @param e The details of the key that was pressed
     */
    public void keyPressed(KeyEvent e) {
        // if we're waiting for an "any key" typed then we don't
        // want to do anything with just a "press"
        if (this.waitingForKeyPress) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            firePressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_P) {
            pausePressed = true;
        }
    }

    /**
     * Notification from AWT that a key has been released.
     *
     * @param e The details of the key that was released
     */
    public void keyReleased(KeyEvent e) {
        // if we're waiting for an "any key" typed then we don't
        // want to do anything with just a "released"
        if (waitingForKeyPress) {
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            leftPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            rightPressed = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            firePressed = false;
        }
    }

    /**
     * Notification from AWT that a key has been typed. Note that
     * typing a key means to both press and then release it.
     *
     * @param e The details of the key that was typed.
     */
    public void keyTyped(KeyEvent e) {
        // if we're waiting for a "any key" type then
        // check if we've recieved any recently. We may
        // have had a keyType() event from the user releasing
        // the shoot or move keys, hence the use of the "pressCount"
        // counter.
        if (waitingForKeyPress) {
            //release pause is another case
            if (pausePressed) {
                pausePressed = false;
                waitingForKeyPress = false;
                EntityLogicMediator.getInstance().releasePause();
                return;
            }

            if (pressCount == 1) {
                // since we've now recieved our key typed
                // event we can mark it as such and start
                // our new game
                waitingForKeyPress = false;
                setStartState();
                pressCount = 0;
            } else {
                pressCount++;
            }
        }

        // if we hit escape, then quit the game
        if (e.getKeyChar() == ESC_CODE) {
            System.exit(0);
        }
    }

    /**
     * If we want to start new game, we should reset state for all controls
     */
    public void setStartState() {
        if (this.game == null) {
            throw new IllegalStateException("Object is not initialised, please call init(Game game) method before use!");
        }

        EntityLogicMediator entityMediator = EntityLogicMediator.getInstance();
        entityMediator.clearAllGameEntities();
        entityMediator.initEntities(this.game);
        StateLogicMediator.getInstance().resetScore();
        // blank out any keyboard settings we might currently have
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
        firePressed = false;
        pausePressed = false;
    }

    public boolean isMoveLeft() {
        return leftPressed
                && !rightPressed
                && !upPressed
                && !downPressed;
    }

    public boolean isMoveRight() {
        return rightPressed
                && !leftPressed
                && !upPressed
                && !downPressed;
    }

    public boolean isMoveUp() {
        return upPressed
                && !downPressed
                && !leftPressed
                && !rightPressed;
    }

    public boolean isMoveDown() {
        return downPressed
                && !upPressed
                && !leftPressed
                && !rightPressed;
    }

    public boolean isWaitingForKeyPress() {
        return waitingForKeyPress;
    }

    public void setWaitingForKeyPress(boolean waitingForKeyPress) {
        this.waitingForKeyPress = waitingForKeyPress;
    }

    public boolean isFirePressed() {
        return firePressed;
    }

    public boolean isPausePressed() {
        return pausePressed;
    }

}
