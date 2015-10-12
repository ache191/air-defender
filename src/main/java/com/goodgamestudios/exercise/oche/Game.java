package com.goodgamestudios.exercise.oche;

import com.goodgamestudios.exercise.oche.entities.ShipEntity;
import com.goodgamestudios.exercise.oche.logic.EntityLogicMediator;
import com.goodgamestudios.exercise.oche.logic.KeyInputLogicMediator;
import com.goodgamestudios.exercise.oche.logic.StateLogicMediator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic.
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 */
public class Game extends Canvas {
    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    private static final String THREAD_KAPUT = "Unexpected thread error {}";

    private static final int GAME_X_RESOLUTION = 800;
    private static final int GAME_Y_RESOLUTION = 600;
    private static final int GAME_BUFFER_STRATEGY = 2;

    private static final String PRESS_ANY_KEY_MSG = "Press any key";
    private static final int PRINT_SCREEN_WIDTH = 800;
    private static final int PRINT_SCREEN_HEIGHT_MSG = 250;
    private static final int PRINT_SCREEN_HEIGHT_ANY_KEY_MSG = 300;
    private static final int PRINT_WIDTH_CORRECTIVE = 2;
    private static final int PRINT_HUD_X = 10;
    private static final int PRINT_HUD_Y = 20;
    private static final int SLEEP_PERIOD = 10;

    //The strategy that allows us to use accelerate page flipping
    private BufferStrategy strategy;
    //True if the game is currently "running", i.e. the game loop is looping
    private boolean gameRunning = true;
    //All Entity related logic
    private EntityLogicMediator entityMediator;
    //All Key Input based related logic
    private KeyInputLogicMediator keyInputLogicMediator;
    //All State related logic
    private StateLogicMediator stateLogicMediator;


    //The message to display which waiting for a key press
    private String message;

    //True if game logic needs to be applied this loop, normally as a result of a game event
    private boolean logicRequiredThisLoop;

    /**
     * Construct our game and set it running.
     */
    public Game() {
        // create a frame to contain our game
        JFrame container = new JFrame("Space Invaders");

        // get hold the content of the frame and set up the resolution of the game
        JPanel panel = (JPanel) container.getContentPane();
        panel.setPreferredSize(new Dimension(GAME_X_RESOLUTION, GAME_Y_RESOLUTION));
        panel.setLayout(null);

        // setup our canvas size and put it into the content of the frame
        setBounds(0, 0, GAME_X_RESOLUTION, GAME_Y_RESOLUTION);
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
        createBufferStrategy(GAME_BUFFER_STRATEGY);
        strategy = getBufferStrategy();
        this.message = "";
        this.logicRequiredThisLoop = false;
    }

    /**
     * Initialise the entities in our game so there's something
     * to see at startup
     */
    public void initGame() {
        this.entityMediator = EntityLogicMediator.getInstance();
        this.entityMediator.initEntities(this);
        this.keyInputLogicMediator.init(this);
        this.stateLogicMediator = StateLogicMediator.getInstance();
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
        this.stateLogicMediator.makeNewAttempt();
        message = "Oh no! They got you, try again?";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    /**
     * Notification that the player has won since all the aliens
     * are dead.
     */
    public void notifyWin() {
        this.stateLogicMediator.makeNewAttempt();
        message = "Well done! You Win!";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    /**
     * Notification that game is paused
     */
    public void notifyPause() {
        message = "Paused";
        this.keyInputLogicMediator.setWaitingForKeyPress(true);
    }

    /**
     * Notification that an alien has been killed
     */
    public void notifyAlienKilled() {
        // reduce the alient count, if there are none left, the player has won!
        this.stateLogicMediator.incrementScore();
        this.entityMediator.notifyAlienKilled();
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
            g.fillRect(0, 0, GAME_X_RESOLUTION, GAME_Y_RESOLUTION);


            // cycle round asking each entity to move itself
            if (!this.keyInputLogicMediator.isWaitingForKeyPress()) {
                this.entityMediator.processAlienShot();
                this.entityMediator.moveAllEntities(delta);
            }

            // cycle round drawing all the entities we have in the game
            this.entityMediator.drawAllEntities(g);
            //check collisions
            this.entityMediator.calculateCollisionsAndRemoveCollidedEntities();

            // if a game event has indicated that game logic should
            // be resolved, cycle round every entity requesting that
            // their personal logic should be considered.
            if (logicRequiredThisLoop) {
                this.entityMediator.doLogic();
                logicRequiredThisLoop = false;
            }

            //HUD score logic
            g.setColor(Color.white);
            g.drawString(String.valueOf(
                    "Score: " +
                            this.stateLogicMediator.getScore() +
                            " Life left: " +
                            this.entityMediator.getShip().lifeLeft()), PRINT_HUD_X, PRINT_HUD_Y);

            // if we're waiting for an "any key" press then draw the
            // current message
            if (this.keyInputLogicMediator.isWaitingForKeyPress()) {
                g.setColor(Color.white);
                g.drawString(message,
                            (PRINT_SCREEN_WIDTH - g.getFontMetrics().stringWidth(message)) / PRINT_WIDTH_CORRECTIVE,
                            PRINT_SCREEN_HEIGHT_MSG);
                g.drawString(PRESS_ANY_KEY_MSG,
                            (PRINT_SCREEN_WIDTH - g.getFontMetrics().stringWidth(PRESS_ANY_KEY_MSG)) / PRINT_WIDTH_CORRECTIVE,
                            PRINT_SCREEN_HEIGHT_ANY_KEY_MSG);
                this.stateLogicMediator.printAllAttempts(g);
            }

            // finally, we've completed drawing so clear up the graphics
            // and flip the buffer over
            g.dispose();
            strategy.show();

            // resolve the movement of the ship. First assume the ship
            // isn't moving. If either cursor key is pressed then
            // update the movement appropraitely
            ShipEntity ship = this.entityMediator.getShip();
            ship.processKeyBasedMovement();

            // if we're pressing fire, attempt to fire
            if (this.keyInputLogicMediator.isFirePressed()) {
                ship.tryToFire();
            }
            //if pause was pressed, make pause
            if (this.keyInputLogicMediator.isPausePressed()) {
                this.entityMediator.makePause();
            }

            // finally pause for a bit. Note: this should run us at about
            // 100 fps but on windows this might vary each loop due to
            // a bad implementation of timer
            try {
                Thread.sleep(SLEEP_PERIOD);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, THREAD_KAPUT, e.getMessage());
            }
        }
    }

    public EntityLogicMediator getEntityMediator() {
        return this.entityMediator;
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
