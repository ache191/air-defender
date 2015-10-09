package com.goodgamestudios.exercise.oche.sprites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by a.chekanskiy@gmail.com on 08.10.15.
 */
public class BackgroundSprite {
    private BufferedImage image = null;

    private int x;
    private int y;

    public BackgroundSprite() {
        this(0,0);
    }

    public BackgroundSprite(int x, int y) {
        this.x = x;
        this.y = y;

        // Try to open the image file background.png
        try {
            image = ImageIO.read(new File("background2.png"));
        }
        catch (Exception e) { System.out.println(e); }

    }

    /**
     * Method that draws the image onto the Graphics object passed
     * @param window
     */
    public void draw(Graphics window) {

        // create an accelerated image of the right size to store our sprite in
        GraphicsConfiguration gc =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        image = gc.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.TRANSLUCENT);

        // Draw the image onto the Graphics reference
        window.drawImage(image, getX(), getY(), image.getWidth(), image.getHeight(), null);

        // Move the x position left for next time
        this.y -= 5;

        // Check to see if the image has gone off stage left
        if (this.y <= -1 * image.getWidth()) {

            // If it has, line it back up so that its left edge is
            // lined up to the right side of the other background image
            this.y = this.y + image.getWidth() * 2;
        }

    }

    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getImageWidth() {
        return image.getWidth();
    }

}

