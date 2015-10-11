package com.goodgamestudios.exercise.oche.logic;

import com.goodgamestudios.exercise.oche.Game;
import com.goodgamestudios.exercise.oche.entities.AlienEntity;
import com.goodgamestudios.exercise.oche.entities.AlienShotEntity;
import com.goodgamestudios.exercise.oche.entities.Entity;
import com.goodgamestudios.exercise.oche.entities.ShipEntity;

import java.awt.*;
import java.util.LinkedList;
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
        this.allEntities = new LinkedList<Entity>();
        this.disposedShotsAndEnemies = new LinkedList<Entity>();
    }

    private static final double MOVEMENT_SPEEDUP_COEFFICIENT = 1.02;
    private static final int SHIP_START_X_POSITION = 370;
    private static final int SHIP_START_Y_POSITION = 550;
    private static final int ALIEN_ROW_COUNT = 5;
    private static final int ALIENS_PER_ROW_COUNT = 12;

    private static final int ALIEN_COL_CORRECTIVE = 50;
    private static final int ALIEN_ROW_CORRECTIVE = 30;
    private static final int ALIEN_ROW_DISTANCE = 50;
    private static final int ALIEN_COL_DISTANCE = 100;


    private Game game;

    /**
     * The list of all the entities that exist in our game
     */
    private List<Entity> allEntities;
    /**
     * The list of entities that need to be removed from the game this loop
     */
    private List<Entity> disposedShotsAndEnemies;
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
        this.ship = new ShipEntity(this.game, SHIP_START_X_POSITION, SHIP_START_Y_POSITION);
        this.allEntities.add(this.ship);

        // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
        this.alienCount = 0;
        for (int row = 0; row < ALIEN_ROW_COUNT; row++) {
            for (int col = 0; col < ALIENS_PER_ROW_COUNT; col++) {
                Entity alien = new AlienEntity(this.game,
                        ALIEN_COL_DISTANCE + (col * ALIEN_COL_CORRECTIVE),
                        (ALIEN_ROW_DISTANCE) + row * ALIEN_ROW_CORRECTIVE);
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
                entity.setHorizontalMovement(entity.getHorizontalMovement() * MOVEMENT_SPEEDUP_COEFFICIENT);
            }
        }
    }

    public void calculateCollisionsAndRemoveCollidedEntities() {
        for (int i = 0; i < this.allEntities.size(); i++) {
            for (int j = i + 1; j < this.allEntities.size(); j++) {
                Entity me = this.allEntities.get(i);
                Entity him = this.allEntities.get(j);

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

    public void processAlienShot() {
        List<AlienShotEntity> shotList = new LinkedList<AlienShotEntity>();
        for (Entity entity : this.allEntities) {
            if (entity instanceof AlienEntity) {
                AlienShotEntity alienShot = ((AlienEntity) entity).tryToFireAndReturnShot();
                if (alienShot != null) {
                    shotList.add(alienShot);
                }
            }
        }
        this.allEntities.addAll(shotList);
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

    public void addShot(Entity shot) {
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

    public void disposeEntity(Entity entity) {
        this.disposedShotsAndEnemies.add(entity);
    }

}


