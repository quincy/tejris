package com.quakbo.tejris;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class Block {
    private Image sprite = null;

    /**
     * The constructor for a Block.
     *
     * @param sprites the Slick SpriteSheet object which holds all of the block images.
     */
    Block(Image sprite) {
        this.sprite = sprite;
    }

    /**
     * Draws this Block on the given Graphics context.
     *
     * @param x the x-coordinate to draw the image to.
     * @param y the y-coordinate to draw the image to.
     * @param g the graphics context to draw on.
     */
    public void draw(int x, int y, Graphics g) {
        sprite.draw(x,y);
    }
}
