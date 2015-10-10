package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;

/**
 * An entity which represents one of our space invader aliens.
 *
 * @author oleksandr.chekanskyi
 */
public class AlienEntity extends Entity {
    private static String SPRITE_PATH = "sprites/alien.gif";

    private static final double SHOT_PROBABILITY = 0.001;
    /**
     * The speed at which the alien moves horizontally
     */
    private static final double MOVE_SPEED = 75;
    private static final long FIRING_INTERVAL = 100;
    private static final int RIGHT_BOUNDARY = 750;
    private static final int LEFT_BOUNDARY = 10;
    private static final int BOTTOM_BOUNDARY = 570;
    private static final int SPEED_BOUNDARY = 0;
    private static final int SHOT_X_CORRECTIVE = 10;
    private static final int SHOT_Y_CORRECTIVE = 30;
    private static final int MOVE_SCREEN_CORRECTIVE = 10;

    private long lastFire = 0;
    /**
     * The game in which the entity exists
     */
    private Game game;

    /**
     * Create a new alien entity
     *
     * @param game The game in which this entity is being created
     * @param x    The initial x location of this alien
     * @param y    The initial y location of this alien
     */
    public AlienEntity(Game game, int x, int y) {
        super(SPRITE_PATH, x, y);

        this.game = game;
        dx = -MOVE_SPEED;
    }

    /**
     * Request that this alien moved based on time elapsed
     *
     * @param delta The time that has elapsed since last move
     */
    public void move(long delta) {
        // if we have reached the left hand side of the screen and
        // are moving left then request a logic update
        if ((dx < SPEED_BOUNDARY) && (x < LEFT_BOUNDARY)) {
            game.updateLogic();
        }
        // and vice vesa, if we have reached the right hand side of
        // the screen and are moving right, request a logic update
        if ((dx > SPEED_BOUNDARY) && (x > RIGHT_BOUNDARY)) {
            game.updateLogic();
        }

        // proceed with normal move
        super.move(delta);
    }

    /**
     * Update the game logic related to aliens
     */
    public void doLogic() {
        // swap over horizontal movement and move down the
        // screen a bit
        dx = -dx;
        y += MOVE_SCREEN_CORRECTIVE;

        // if we've reached the bottom of the screen then the player
        // dies
        if (y > BOTTOM_BOUNDARY) {
            game.notifyDeath();
        }
    }

    public AlienShotEntity tryToFireAndReturnShot() {
        double chance = Math.random();
        if(chance > SHOT_PROBABILITY) {
            return null;
        }
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return null;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = System.currentTimeMillis();
        return new AlienShotEntity(
                this.game, this.getX() - SHOT_X_CORRECTIVE, this.getY() + SHOT_Y_CORRECTIVE);
    }

    /**
     * Notification that this alien has collided with another entity
     *
     * @param other The other entity
     */
    public void collidedWith(Entity other) {
        // collisions with aliens are handled elsewhere
    }
}