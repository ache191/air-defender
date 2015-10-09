package com.goodgamestudios.exercise.oche.logic;

import com.goodgamestudios.exercise.oche.Game;
import com.goodgamestudios.exercise.oche.entities.AlienEntity;
import com.goodgamestudios.exercise.oche.entities.Entity;
import com.goodgamestudios.exercise.oche.entities.ShipEntity;
import com.goodgamestudios.exercise.oche.entities.ShotEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by a.chekanskiy@gmail.com on 09.10.15.
 */
public class EntityLogicMediator {
    // Singleton with double check
    private static volatile EntityLogicMediator INSTANCE = null;

    public static EntityLogicMediator getInstance() {
        if (INSTANCE == null) {
            synchronized (EntityLogicMediator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EntityLogicMediator();
                }
            }
        }
        return INSTANCE;
    }

    private EntityLogicMediator() {
    }

    private Game game;

    /**
     * The list of all the entities that exist in our game
     */
    private List<Entity> allEntities = new ArrayList();
    /**
     * The list of entities that need to be removed from the game this loop
     */
    private List<Entity> disposedShotsAndEnemies = new ArrayList();
    /**
     * The entity representing the player
     */
    private ShipEntity ship;

    private int alienCount;

    public void clearAllGameEntities() {
        this.allEntities.clear();
    }

    /**
     * Initialise the starting state of the entities (ship and aliens). Each
     * entitiy will be added to the overall list of entities in the game.
     */
    public void initEntities(Game game) {
        this.game = game;
        // create the player ship and place it roughly in the center of the screen
        this.ship = new ShipEntity(this.game, 370, 550);
        this.allEntities.add(this.ship);

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        this.alienCount = 0;
        for (int row = 0; row < 5; row++) {
            for (int x = 0; x < 12; x++) {
                Entity alien = new AlienEntity(this.game, 100 + (x * 50), (50) + row * 30);
                this.allEntities.add(alien);
                this.alienCount++;
            }
        }
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        this.alienCount--;


        if (alienCount == 0) {
            this.game.notifyWin();
        }

        // if there are still some aliens left then they all need to get faster, so
        // speed up all the existing aliens
        for (Entity entity : this.allEntities) {
            if (entity instanceof AlienEntity) {
                // speed up by 2%
                entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
            }
        }
    }

    public void calculateCollisionsAndRemoveCollidedEntities() {
        for (int p = 0; p < this.allEntities.size(); p++) {
            for (int s = p + 1; s < this.allEntities.size(); s++) {
                Entity me = this.allEntities.get(p);
                Entity him = this.allEntities.get(s);

                if (me.collidesWith(him)) {
                    me.collidedWith(him);
                    him.collidedWith(me);
                }
            }
        }

        // remove any entity that has been marked for clear up
        this.allEntities.removeAll(this.disposedShotsAndEnemies);
        this.disposedShotsAndEnemies.clear();
    }

    public void moveAllEntities(long delta) {
        for (Entity entity : this.allEntities) {
            entity.move(delta);
        }
    }

    public void doLogic() {
        for (Entity entity : this.allEntities) {
            entity.doLogic();
        }
    }

    public void drawAllEntities(Graphics2D window) {
        for (Entity entity : this.allEntities) {
            entity.draw(window);
        }
    }

    public void addShot(ShotEntity shot) {
        this.allEntities.add(shot);
    }

    public void makePause() {
        for (Entity entity : this.allEntities) {
            entity.setPaused(true);
        }
        this.game.notifyPause();
    }

    public void releasePause() {
        for (Entity entity : this.allEntities) {
            entity.setPaused(false);
        }
    }

    public ShipEntity getShip() {
        return this.ship;
    }

    public int getAlienCount() {
        return alienCount;
    }

    public int entitiesSize() {
        return allEntities.size();
    }

    public void disposeEntity(Entity entity) {
        this.disposedShotsAndEnemies.add(entity);
    }

    public void setGame(Game game) {
        this.game = game;
    }

}


