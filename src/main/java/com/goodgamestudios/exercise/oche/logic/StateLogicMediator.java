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

/**
 * Created by a.chekanskiy@gmail.com on 10.10.15.
 */
public class StateLogicMediator {
    // Singleton with double check
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

    private int score;
    private List<GameAttempt> bestTenAttempts;
    private GameAttempt lastAttempt;
    private File attemptsFile;

    public void makeNewAttempt() {
        GameAttempt gameAttempt = new GameAttempt();
        gameAttempt.setLifeCount(EntityLogicMediator.getInstance().getShip().lifeLeft());
        gameAttempt.setScore(this.score);
        this.lastAttempt = gameAttempt;
        mergeGameAttemptsToGetTopTen();
    }

    public void printAllAttempts(Graphics2D g) {
        g.drawString("TOP TEN", (800 - g.getFontMetrics().stringWidth("TOP TEN")) / 2, 335);
        int corrective = 350;
        for (String s : getAllAttemptsAsStrings()) {
            g.drawString(s, (800 - g.getFontMetrics().stringWidth(s)) / 2, corrective);
            corrective = corrective + 15;
        }
    }



    private void mergeGameAttemptsToGetTopTen() {
        //List<GameAttempt> mergedAttempts = getPreviousAttempts();
        //this.gameAttempts.addAll();
        this.bestTenAttempts.add(this.lastAttempt);
        Collections.sort(this.bestTenAttempts);
        if(this.bestTenAttempts.size() > 10) {
            this.bestTenAttempts = this.bestTenAttempts.subList(0, 10);
        }
        //this.bestTenAttempts = mergedAttempts;
        List<String> attempts = new LinkedList();
        for (GameAttempt attempt : this.bestTenAttempts) {
            attempts.add(attempt.toCSVLikeString());
        }

        try {
            FileUtils.write(attemptsFile, "");
            FileUtils.writeLines(attemptsFile, attempts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
        return result;
    }

    private GameAttempt valueOfCSVLikeString(String str) {
        String[] val = str.split(";");
        GameAttempt gameAttempt = new GameAttempt();
        gameAttempt.setScore(Integer.valueOf(val[0]));
        gameAttempt.setLifeCount(Integer.valueOf(val[1]));
        gameAttempt.setDate(new Date(Long.valueOf(val[2])));
        return gameAttempt;
    }

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

    public int getScore() {
        return score;
    }

    public void resetScore() {
        this.score = 0;
    }

    public void incrementScore() {
        this.score++;
    }


    public class GameAttempt implements Comparable<GameAttempt> {
        private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        private int score;
        private int lifeCount;
        private Date date;

        public GameAttempt() {
            this.date = new Date();
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

        public String toCSVLikeString(){
            String result = this.score + ";" + this.getLifeCount() + ";" + this.date.getTime();
            return result;
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
