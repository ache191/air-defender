package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;
import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;
import com.goodgamestudios.exercise.oche.logic.KeyInputLogicMediator;

/**
 * The entity that represents the players ship
 */
public class ShipEntity extends Entity {
    private static String SPRITE_PATH = "sprites/ship.gif";

    private static final double MOVE_SPEED = 300;
    private static final long FIRING_INTERVAL = 500;
    private static final int SPEED_BOUNDARY = 0;
    private static final int LEFT_BOUNDARY = 10;
    private static final int RIGHT_BOUNDARY = 750;
    private static final int UP_BOUNDARY = 10;
    private static final int BOTTOM_BOUNDARY = 550;
    private static final int SHOT_X_CORRECTIVE = 10;
    private static final int SHOT_Y_CORRECTIVE = 30;
    private static final int LIFE_COUNT = 5;

    //Time elapsed from last fire
    private long lastFire = 0;
    //Number of lifes left
    private int lifeCount;
    //Current game entity exists in
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
        this.lifeCount = LIFE_COUNT;
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
        if ((dx < SPEED_BOUNDARY) && (x < LEFT_BOUNDARY)) {
            return;
        }

        //the same for up up boundary
        if ((dy < SPEED_BOUNDARY) && (y < UP_BOUNDARY)) {
            return;
        }

        // if we're moving right and have reached the right hand side
        // of the screen, don't move
        if ((dx > SPEED_BOUNDARY) && (x > RIGHT_BOUNDARY)) {
            return;
        }

        //the same for bottom boundary
        if ((dy > SPEED_BOUNDARY) && (y > BOTTOM_BOUNDARY)) {
            return;
        }

        super.move(delta);
    }

    /**
     * Read what control was pressed and make correspondent move
     */
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

    /**
     * Make a fire attempt, in the case of success add shot entity to correspondent logic mediator
     */
    public void tryToFire() {
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = System.currentTimeMillis();
        ShotEntity shot = new ShotEntity(
                this.game, this.getX() + SHOT_X_CORRECTIVE, this.getY() - SHOT_Y_CORRECTIVE);
        EntityLogicMediator.getInstance().addShot(shot);
    }

    /**
     * Notification that the player's ship has collided with something
     *
     * @param other The entity with which the ship has collided
     */
    public void collidedWith(Entity other) {
        // if its an alien, notify the game that the player is dead
        if (other instanceof AlienEntity) {
            this.lifeCount = 0;
            EntityLogicMediator.getInstance().clearAllGameEntities();
            game.notifyDeath();
        }
    }

    public int lifeLeft() {
        return this.lifeCount;
    }

    public void decreaseLifeCount() {
        this.lifeCount--;
    }
}