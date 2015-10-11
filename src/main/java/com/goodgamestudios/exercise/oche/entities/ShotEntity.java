package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;

/**
 * An entity representing a shot fired by the player's ship

 */
public class ShotEntity extends Entity {
    private static String SPRITE_PATH = "sprites/shot.gif";

    private static final double MOVE_SPEED = -300;
    private static final double SHOT_BOUNDARY = -100;

    //Current game entity exists in
    private Game game;
    //Prevent double kills flag
    private boolean used;

    /**
     * Create a new shot from the player
     *
     * @param game   The game in which the shot has been created
     * @param x      The initial x location of the shot
     * @param y      The initial y location of the shot
     */
    public ShotEntity(Game game, int x, int y) {
        super(SPRITE_PATH, x, y);

        this.game = game;
        dy = MOVE_SPEED;
        this.used = false;
    }

    /**
     * Request that this shot moved based on time elapsed
     *
     * @param delta The time that has elapsed since last move
     */
    public void move(long delta) {
        // proceed with normal move
        super.move(delta);

        // if we shot off the screen, remove ourselfs
        if (y < SHOT_BOUNDARY) {
            //this.game.entremoveEntity(this);
            this.game.getEntityMediator().disposeEntity(this);
        }
    }

    /**
     * Notification that this shot has collided with another
     * entity
     *
     * @parma other The other entity with which we've collided
     */
    public void collidedWith(Entity other) {
        // prevents double kills, if we've already hit something, don't collide
        if (used) {
            return;
        }

        // if we've hit an alien, kill it!
        if (other instanceof AlienEntity) {
            // remove the affected entities
            this.game.getEntityMediator().disposeEntity(this);
            this.game.getEntityMediator().disposeEntity(other);
            // notify the game that the alien has been killed
            game.notifyAlienKilled();
            used = true;
        }
    }
}