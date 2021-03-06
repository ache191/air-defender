package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.Game;
import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;

/**
 * An entity that represents shot from Alien
 */
public class AlienShotEntity extends Entity {
    private static String SPRITE_PATH = "sprites/alien_shot.gif";

    private static final double MOVE_SPEED = 300;
    private static final int BULLET_BOUNDARY = 700;
    private static final int LIFE_BOUNDARY = 0;

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
    public AlienShotEntity(Game game, int x, int y) {
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

        // if we shot off the screen, remove
        if (y > BULLET_BOUNDARY) {
            EntityLogicMediator.getInstance().disposeEntity(this);
        }
    }

    /**
     * Notification that this shot has collided with another
     * entity
     *
     * @parma other The other entity with which we've collided
     */
    public void collidedWith(Entity other) {
        // prevents double kills, if we've already hit something,
        // don't collide
        if (used) {
            return;
        }

        //other aliens is not vulnerable to alien weapon
        if(other instanceof AlienEntity) {
            return;
        }

        // if we've hit a ship, check if we have enough life attempts, otherwise kill it with fire!
        if (other instanceof ShipEntity) {
            // remove the affected entities
            EntityLogicMediator entityLogicMediator = EntityLogicMediator.getInstance();
            entityLogicMediator.disposeEntity(this);
            if(entityLogicMediator.getShip().lifeLeft() > LIFE_BOUNDARY){
                entityLogicMediator.getShip().decreaseLifeCount();
            } else {
                game.notifyDeath();
            }

            used = true;
        }
    }
}

