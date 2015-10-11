package com.goodgamestudios.exercise.oche.logic;

import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * "Mediator" for state logic
 * Holds all the "state" for the game and processes all the logic needed for it
 * Score, best 10 attempts, provides saving score table in file
 */
public class StateLogicMediator {
    private static Logger LOGGER = Logger.getLogger(StateLogicMediator.class.getName());

    private static final String CAN_NOT_FETCH_HIGH_SCORES = "Error to fetch high score table from file! {}";
    private static final String CAN_NOT_WRITE_TO_HIGH_SCORES = "Error to write to highscore file! {}";

    private static final int PRINT_SCREEN_WIDTH = 800;
    private static final int PRINT_SCREEN_HEIGHT = 335;
    private static final int PRINT_WIDTH_CORRECTIVE = 2;
    private static final int PRINT_START_CORRECTIVE = 350;
    private static final int PRINT_CORRECTIVE_SHIFT = 15;

    private static volatile StateLogicMediator INSTANCE = null;

    public static StateLogicMediator getInstance() {
        if (INSTANCE == null) {
            synchronized (EntityLogicMediator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StateLogicMediator();
                }
            }
        }
        return INSTANCE;
    }

    private StateLogicMediator() {
        URL url = this.getClass().getClassLoader().getResource("attempts/attempts");
        attemptsFile = new File(url.getPath());
        this.bestTenAttempts = getPreviousAttempts();
    }

    //Current game score
    private int score;
    //Best 10 game attempts
    private List<GameAttempt> bestTenAttempts;
    //Last game attempt performed by the player
    private GameAttempt lastAttempt;
    //File object with "highscore table"
    private File attemptsFile;

    /**
     * Save new game attempt and merge it with highscore table to get most recent highscores
     */
    public void makeNewAttempt() {
        GameAttempt gameAttempt = new GameAttempt();
        gameAttempt.setLifeCount(EntityLogicMediator.getInstance().getShip().lifeLeft());
        gameAttempt.setScore(this.score);
        this.lastAttempt = gameAttempt;
        mergeGameAttemptsToGetTopTen();
    }

    /**
     *Print highscore table in the game
     * @param g Window where game is drawn
     */
    public void printAllAttempts(Graphics2D g) {
        g.drawString("TOP TEN",
                    (PRINT_SCREEN_WIDTH - g.getFontMetrics().stringWidth("TOP TEN")) / PRINT_WIDTH_CORRECTIVE,
                    PRINT_SCREEN_HEIGHT);
        int corrective = PRINT_START_CORRECTIVE;
        for (String s : getAllAttemptsAsStrings()) {
            g.drawString(s,
                        (PRINT_SCREEN_WIDTH - g.getFontMetrics().stringWidth(s)) / PRINT_WIDTH_CORRECTIVE,
                        corrective);
            corrective = corrective + PRINT_CORRECTIVE_SHIFT;
        }
    }

    /**
     * Merge last game attempt with highscore table and get best 10 attempts
     */
    private void mergeGameAttemptsToGetTopTen() {
        this.bestTenAttempts.add(this.lastAttempt);
        Collections.sort(this.bestTenAttempts);
        if(this.bestTenAttempts.size() > 10) {
            this.bestTenAttempts = this.bestTenAttempts.subList(0, 10);
        }
        List<String> attempts = new LinkedList();
        for (GameAttempt attempt : this.bestTenAttempts) {
            attempts.add(attempt.toCSVLikeString());
        }

        try {
            FileUtils.write(attemptsFile, "");
            FileUtils.writeLines(attemptsFile, attempts);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, CAN_NOT_WRITE_TO_HIGH_SCORES, e.getMessage());
        }
    }

    /**
     * Get list of all attempts from file
     * @return List of all previous attempts
     */
    private List<GameAttempt> getPreviousAttempts() {
        List<GameAttempt> result = new LinkedList();
        try {
            List<String> strings = FileUtils.readLines(attemptsFile);
            if(!strings.isEmpty()) {
                for (String string : strings) {
                    result.add(valueOfCSVLikeString(string));
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, CAN_NOT_FETCH_HIGH_SCORES, e.getMessage());
        }
        return result;
    }

    /**
     * Get GameAttempt object from serialized string
     *
     * @param str string representation of attempt from file
     * @return object deserialized from saved string
     */
    private GameAttempt valueOfCSVLikeString(String str) {
        String[] val = str.split(";");
        GameAttempt gameAttempt = new GameAttempt();
        //As we've serialized this string by self we know for sure the order of serialization.
        gameAttempt.setScore(Integer.valueOf(val[0]));
        gameAttempt.setLifeCount(Integer.valueOf(val[1]));
        gameAttempt.setDate(new Date(Long.valueOf(val[2])));
        return gameAttempt;
    }

    /**
     * Serialize best 10 GameAttempt object to strings for print
     * @return best 10 attempts as strings
     */
    private List<String> getAllAttemptsAsStrings() {
        if(this.bestTenAttempts == null || this.bestTenAttempts.isEmpty()){
            return Collections.emptyList();
        }
        List<String> result = new LinkedList();
        for (GameAttempt gameAttempt : this.bestTenAttempts) {
            result.add(gameAttempt.toString());
        }
        return result;
    }

    /**
     * Reset current score to start value
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Add +1 to current score
     */
    public void incrementScore() {
        this.score++;
    }

    public int getScore() {
        return score;
    }

    /**
     * Class representing GameAttempt
     */
    public class GameAttempt implements Comparable<GameAttempt> {
        //Date Format for readable representation
        private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        //Score reached
        private int score;
        //Life number left
        private int lifeCount;
        //Date of attempt
        private Date date;

        public GameAttempt() {
            this.date = new Date();
        }

        /**
         *
         * @return Serialize GameAttempt object as CSV like string with ";" delimiter
         */
        public String toCSVLikeString(){
            String result = this.score + ";" + this.getLifeCount() + ";" + this.date.getTime();
            return result;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getLifeCount() {
            return lifeCount;
        }

        public void setLifeCount(int lifeCount) {
            this.lifeCount = lifeCount;
        }

        public Date getDate() {
            return this.date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getStringDate() {
            return sdf.format(date);
        }

        @Override
        public String toString() {
            return "Score: "
                    + getScore()
                    + " lifes: "
                    + getLifeCount()
                    + " "
                    + getStringDate();
        }

        @Override
        public int compareTo(GameAttempt that) {
            if(this == that) {
                return 0;
            }

            if(this.getScore() < that.getScore()) {
                return 1;
            } else  if (this.getScore() > that.getScore()) {
                return -1;
            }

            if(this.getLifeCount() < that.getLifeCount()) {
                return 1;
            } else  if (this.getLifeCount() > that.getLifeCount()) {
                return -1;
            }

            return this.getDate().compareTo(that.getDate());
        }
    }
}
