package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;

/**
 * An entity which represents one of our space invader aliens.
 *
 * @author Kevin Glass
 */
public class AlienEntity extends Entity {
    private static String SPRITE_PATH = "sprites/alien.gif";
    /**
     * The speed at which the alien moves horizontally
     */
    private static double MOVE_SPEED = 75;
    private static long FIRING_INTERVAL = 100;
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
        if ((dx < 0) && (x < 10)) {
            game.updateLogic();
        }
        // and vice vesa, if we have reached the right hand side of
        // the screen and are moving right, request a logic update
        if ((dx > 0) && (x > 750)) {
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
        y += 10;

        // if we've reached the bottom of the screen then the player
        // dies
        if (y > 570) {
            game.notifyDeath();
        }
    }

    public AlienShotEntity tryToFireAndReturnShot() {
        double chance = Math.random();
        if(chance > 0.001) {
            return null;
        }
        // check that we have waiting long enough to fire
        if (System.currentTimeMillis() - lastFire < FIRING_INTERVAL) {
            return null;
        }

        // if we waited long enough, create the shot entity, and record the time.
        lastFire = System.currentTimeMillis();
        return new AlienShotEntity(
                this.game, this.getX() - 10, this.getY() + 30);
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