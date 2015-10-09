package com.goodgamestudios.exercise.oche;

import com.goodgamestudios.exercise.oche.entities.ShipEntity;
import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;
import com.goodgamestudios.exercise.oche.logic.KeyInputLogicMediator;
import com.goodgamestudios.exercise.oche.sprites.BackgroundSprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic.
 * <p/>
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * <p/>
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 *
 * @author Kevin Glass
 */
public class Game extends Canvas {
    /**
     * The stragey that allows us to use accelerate page flipping
     */
    private BufferStrategy strategy;
    /**
     * True if the game is currently "running", i.e. the game loop is looping
     */
    private boolean gameRunning = true;

    private EntityLogicMediator entityContainer;

    private KeyInputLogicMediator keyInputLogicMediator;

    /**
     * The message to display which waiting for a key press
     */
    private String message = "";

    /**
     * True if game logic needs to be applied this loop, normally as a result of a game event
     */
    private boolean logicRequiredThisLoop = false;

    /**
     * Construct our game and set it running.
     */
    public Game() {
        // create a frame to contain our game
        JFrame container = new JFrame("Space Invaders 101");

        // get hold the content of the frame and set up the resolution of the game
        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(800, 600));
        panel.setLayout(null);

        // setup our canvas size and put it into the content of the frame
        setBounds(0, 0, 800, 600);
        panel.add(this);

        // Tell AWT not to bother repainting our canvas since we're
        // going to do that our self in accelerated mode
        setIgnoreRepaint(true);

        // finally make the window visible
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // add a listener to respond to the user closing the window. If they
        // do we'd like to exit the game
        container.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        // add a key input system (defined below) to our canvas
        // so we can respond to key pressed
        this.keyInputLogicMediator = KeyInputLogicMediator.getInstance();
        addKeyListener(KeyInputLogicMediator.getInstance());

        // request the focus so key events come to us
        requestFocus();

        // create the buffering strategy which will allow AWT
        // to manage our accelerated graphics
        createBufferStrategy(2);
        strategy = getBufferStrategy();
    }

    public void initGame() {
        // initialise the entities in our game so there's something
        // to see at startup
        this.entityContainer = EntityLogicMediator.getInstance();
        this.entityContainer.initEntities(this);
        this.keyInputLogicMediator.init(this);
    }

    /**
     * Notification from a game entity that the logic of the game
     * should be run at the next opportunity (normally as a result of some
     * game event)
     */
    public void updateLogic() {
        logicRequiredThisLoop = true;
    }

    /**
     * Notification that the player has died.
     */
    public void notifyDeath() {
        message = "Oh no! They got you, try again?";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        message = "Well done! You Win!";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    public void notifyPause() {
        message = "Paused";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        this.keyInputLogicMediator.incrementScore();
        this.entityContainer.notifyAlienKilled();
    }

    /**
     * The main game loop. This loop is running during all game
     * play as is responsible for the following activities:
     * <p/>
     * - Working out the speed of the game loop to update moves
     * - Moving the game entities
     * - Drawing the screen contents (entities, text)
     * - Updating game events
     * - Checking Input
     * <p/>
     */
    public void gameLoop() {
        long lastLoopTime = System.currentTimeMillis();

        // keep looping round til the game ends
        while (gameRunning) {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long delta = System.currentTimeMillis() - lastLoopTime;
            lastLoopTime = System.currentTimeMillis();

            // Get hold of a graphics context for the accelerated
            // surface and blank it out
            Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
            g.setColor(Color.black);
            g.fillRect(0, 0, 800, 600);

            // cycle round asking each entity to move itself
            if (!this.keyInputLogicMediator.isWaitingForKeyPress()) {
                this.entityContainer.moveAllEntities(delta);
            }

            // cycle round drawing all the entities we have in the game
            this.entityContainer.drawAllEntities(g);

            this.entityContainer.calculateCollisionsAndRemoveCollidedEntities();

            // if a game event has indicated that game logic should
            // be resolved, cycle round every entity requesting that
            // their personal logic should be considered.
            if (logicRequiredThisLoop) {
                this.entityContainer.doLogic();
                logicRequiredThisLoop = false;
            }

            //HUD score logic
            g.setColor(Color.white);
            g.drawString(String.valueOf("Score: " + this.keyInputLogicMediator.getScore()), 10, 20);


            // if we're waiting for an "any key" press then draw the
            // current message
            if (this.keyInputLogicMediator.isWaitingForKeyPress()) {
                g.setColor(Color.white);
                g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
                g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
            }

            // finally, we've completed drawing so clear up the graphics
            // and flip the buffer over
            g.dispose();
            strategy.show();

            // resolve the movement of the ship. First assume the ship
            // isn't moving. If either cursor key is pressed then
            // update the movement appropraitely
            ShipEntity ship = this.entityContainer.getShip();
            ship.processKeyBasedMovement();

            // if we're pressing fire, attempt to fire
            if (this.keyInputLogicMediator.isFirePressed()) {
                ship.tryToFire();
            }

            if (this.keyInputLogicMediator.isPausePressed()) {
                this.entityContainer.makePause();
            }

            // finally pause for a bit. Note: this should run us at about
            // 100 fps but on windows this might vary each loop due to
            // a bad implementation of timer
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    public EntityLogicMediator getEntityContainer() {
        return this.entityContainer;
    }

    /**
     * The entry point into the game. We'll simply create an
     * instance of class which will start the display and game
     * loop.
     *
     * @param args The arguments that are passed into our game
     */
    public static void main(String args[]) {
        Game g = new Game();
        g.initGame();
        // Start the main game loop, note: this method will not
        // return until the game has finished running. Hence we are
        // using the actual main thread to run the game.
        g.gameLoop();
    }
}
