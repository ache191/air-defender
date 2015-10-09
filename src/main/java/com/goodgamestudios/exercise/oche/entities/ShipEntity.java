package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;
import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;
import com.goodgamestudios.exercise.oche.logic.KeyInputLogicMediator;

/**
 * The entity that represents the players ship
 *
 * @author Kevin Glass
 */
public class ShipEntity extends Entity {
    private static String SPRITE_PATH = "ship.gif";
    /**
     * The speed at which the player's ship should move (pixels/sec)
     */
    private static double MOVE_SPEED = 300;
    private static long FIRING_INTERVAL = 500;
    private long lastFire = 0;

    /**
     * The game in which the ship exists
     */
    private Game game;

    /**
     * Create a new entity to represent the players ship
     *
     * @param game The game in which the ship is being created
     * @param x    The initial x location of the player's ship
     * @param y    The initial y location of the player's ship
     */
    public ShipEntity(Game game, int x, int y) {
        super(SPRITE_PATH, x, y);

        this.game = game;
    }

    /**
     * Request that the ship move itself based on an elapsed amount of
     * time
     *
     * @param delta The time that has elapsed since last move (ms)
     */
    public void move(long delta) {
        // if we're moving left and have reached the left hand side
        // of the screen, don't move
        if ((dx < 0) && (x < 10)) {
            return;
        }

        if ((dy < 0) && (y < 10)) {
            return;
        }

        // if we're moving right and have reached the right hand side
        // of the screen, don't move
        if ((dx > 0) && (x > 750)) {
            return;
        }

        if ((dy > 0) && (y > 550)) {
            return;
        }

        super.move(delta);
    }

    public void processKeyBasedMovement() {
        KeyInputLogicMediator keyInputLogicMediator = KeyInputLogicMediator.getInstance();

        this.setHorizontalMovement(0);
        this.setVerticalMovement(0);

        if (keyInputLogicMediator.isMoveLeft()) {
            this.setHorizontalMovement(-MOVE_SPEED);
            this.setVerticalMovement(0);
        } else if (keyInputLogicMediator.isMoveRight()) {
            this.setHorizontalMovement(MOVE_SPEED);
            this.setVerticalMovement(0);
        } else if (keyInputLogicMediator.isMoveUp()) {
            this.setVerticalMovement(-MOVE_SPEED);
            this.setHorizontalMovement(0);
        } else if (keyInputLogicMediator.isMoveDown()) {
            this.setVerticalMovement(MOVE_SPEED);
            this.setHorizontalMovement(0);
        }
    }

    public void tryToFire() {
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(
                this.game, this.getX() + 10, this.getY() - 30);
        EntityLogicMediator.getInstance().addShot(shot);
    }

    /**
     * Notification that the player's ship has collided with something
     *
     * @param other The entity with which the ship has collided
     */
    public void collidedWith(Entity other) {
        // if its an alien, notify the game that the player
        // is dead
        if (other instanceof AlienEntity) {
            game.notifyDeath();
        }
    }
}