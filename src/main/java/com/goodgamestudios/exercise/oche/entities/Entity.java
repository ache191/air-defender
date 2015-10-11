package com.goodgamestudios.exercise.oche.entities;

import com.goodgamestudios.exercise.oche.sprites.Sprite;
import com.goodgamestudios.exercise.oche.sprites.SpriteStore;

import java.awt.*;

/**
 * An entity represents any element that appears in the game. The
 * entity is responsible for resolving collisions and movement
 * based on a set of properties defined either by subclass or externally.
 */
public abstract class Entity {

    private static final int MILLISECONDS_CORRECTIVE = 1000;
    // true if entity is paused
    protected boolean isPaused;
    // The current x location of this entity
    protected double x;
    // The current y location of this entity
    protected double y;
    // The sprite that represents this entity
    protected Sprite sprite;
    // The current speed of this entity horizontally (pixels/sec)
    protected double dx;
    // The current speed of this entity vertically (pixels/sec)
    protected double dy;
    // The rectangle used for this entity during collisions  resolution
    private Rectangle me;
    // The rectangle used for other entities during collision resolution
    private Rectangle him;

    /**
     * Construct a entity based on a sprite image and a location.
     *
     * @param ref The reference to the image to be displayed for this entity
     * @param x   The initial x location of this entity
     * @param y   The initial y location of this entity
     */
    public Entity(String ref, int x, int y) {
        this.sprite = SpriteStore.get().getSprite(ref);
        this.x = x;
        this.y = y;
        this.me = new Rectangle();
        this.him = new Rectangle();
    }

    /**
     * Request that this entity move itself based on a certain ammount
     * of time passing.
     *
     * @param delta The amount of time that has passed in milliseconds
     */
    public void move(long delta) {
        //we can not move within "pause"
        if (isPaused) {
            return;
        }
        // update the location of the entity based on move speeds
        x += (delta * dx) / MILLISECONDS_CORRECTIVE;
        y += (delta * dy) / MILLISECONDS_CORRECTIVE;
    }

    /**
     * Check if this entity collised with another.
     *
     * @param other The other entity to check collision against
     * @return True if the entities collide with each other
     */
    public boolean collidesWith(Entity other) {
        me.setBounds((int) x, (int) y, sprite.getWidth(), sprite.getHeight());
        him.setBounds((int) other.x, (int) other.y, other.sprite.getWidth(), other.sprite.getHeight());

        return me.intersects(him);
    }

    /**
     * Do the logic associated with this entity. This method
     * will be called periodically based on game events
     */
    public void doLogic() {
    }

    public void setPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public void setHorizontalMovement(double dx) {
        this.dx = dx;
    }

    public void setVerticalMovement(double dy) {
        this.dy = dy;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public double getHorizontalMovement() {
        return dx;
    }

    public double getVerticalMovement() {
        return dy;
    }

    /**
     * Draw this entity to the graphics context provided
     *
     * @param g The graphics context on which to draw
     */
    public void draw(Graphics g) {
        sprite.draw(g, (int) x, (int) y);
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    /**
     * Notification that this entity collided with another.
     *
     * @param other The entity with which this entity collided.
     */
    public abstract void collidedWith(Entity other);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;

        Entity entity = (Entity) o;

        if (Double.compare(entity.dx, dx) != 0) return false;
        if (Double.compare(entity.dy, dy) != 0) return false;
        if (isPaused != entity.isPaused) return false;
        if (Double.compare(entity.x, x) != 0) return false;
        if (Double.compare(entity.y, y) != 0) return false;
        if (!him.equals(entity.him)) return false;
        if (!me.equals(entity.me)) return false;
        if (!sprite.equals(entity.sprite)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (isPaused ? 1 : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + sprite.hashCode();
        temp = Double.doubleToLongBits(dx);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(dy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + me.hashCode();
        result = 31 * result + him.hashCode();
        return result;
    }
}