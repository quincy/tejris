package com.quakbo.tejris.pieces;

import com.quakbo.tejris.board.PlayField;
import com.quakbo.tejris.gamestates.GameplayState;
import java.awt.Point;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class Tetrad {
    /** Static values for indicating Tetrad shapes. */
    public static final int SHAPE_I = 0;
    public static final int SHAPE_J = 1;
    public static final int SHAPE_L = 2;
    public static final int SHAPE_S = 3;
    public static final int SHAPE_Z = 4;
    public static final int SHAPE_O = 5;
    public static final int SHAPE_T = 6;

    /** An array of Block objects which comprise this Tetrad. */
    private Block[] blocks = null;

    /**
     * An array of Point objects which hold the positions of each block in the
     * Tetrad.  The x value indicates the row in the PlayField and the y value
     * indicates the column in the PlayField.  This seems backward if you are
     * in terms of x and y.  When dealing with the PlayField coordinates are
     * always addressed as row,col.
     */
    private Point[] coords = null;

    /**
     * The sprite sheet to get the block image from.
     */
    SpriteSheet sprites = null;

    /**
     * An integer representing the shape of this tetrad.  This corresponds with
     * the constants in the Tetrad class.
     */
    private int shape;

    /** An integer indicating the current orientation. */
    private int orientation;

    /**
     * Creates a new Tetrad with the specified shape and orientation.
     *
     * @param row the row on the play field of block 0.
     * @param col the column on the play field of block 0.
     * @param shape the shape of the Tetrad which corresponds to the field variables.
     * @param orientation the orientation as specified in the Game Components section.
     * @param sprites the SpriteSheet to use for the block images.
     */
    public Tetrad(int row, int col, int shape, int orientation, SpriteSheet sprites) {
        blocks = new Block[4];
        coords = new Point[4];

        coords[0]        = new Point(row, col);
        this.shape       = shape;
        this.orientation = orientation;
        this.sprites     = sprites;

        switch (shape) {
            case SHAPE_I:
                setIShape();
                break;
            case SHAPE_J:
                setJShape();
                break;
            case SHAPE_L:
                setLShape();
                break;
            case SHAPE_S:
                setSShape();
                break;
            case SHAPE_Z:
                setZShape();
                break;
            case SHAPE_O:
                setOShape();
                break;
            case SHAPE_T:
                setTShape();
                break;
            default: break;
        }
    }

    /**
     * Attempt to move this tetrad one cell to the left.
     *
     * @param pf the PlayField object.
     */
    public void moveLeft( PlayField pf ) {
        Tetrad newTetrad = new Tetrad(coords[0].x, coords[0].y - 1, shape, orientation, sprites);

        // as long as the new tetrad isn't collided with something we will copy
        // its coordinates to our original tetrad.
        if (!newTetrad.isCollided(pf)) {
            coords = newTetrad.coords;
        }
    }

    /**
     * Attempt to move this tetrad one cell to the right.
     *
     * @param pf the PlayField object.
     */
    public void moveRight(PlayField pf) {
        Tetrad newTetrad = new Tetrad(coords[0].x, coords[0].y + 1, shape, orientation, sprites);

        // as long as the new tetrad isn't collided with something we will copy
        // its coordinates to our original tetrad.
        if (!newTetrad.isCollided(pf)) {
            coords = newTetrad.coords;
        }
    }

    /**
     * Attempt to rotate the Tetrad counterclockwise.  If there are any obstructions
     * the Tetrad will not be rotated.
     *
     * If rotating SHAPE_I we need to find the new position for block 0 before
     * changing the orientation.
     *
     * @param pf the PlayField object which contains this Tetrad.
     */
    public void rotateLeft(PlayField pf) {
        int newOrientation = orientation - 1;
        int row = coords[0].x;
        int col = coords[0].y;

        // wrap orientation back to 0 if we try to go too high.
        if (newOrientation == -1) {
            newOrientation = 3;
        }

        // we need to change the position of block 0 if we are rotating SHAPE_I
        if (shape == SHAPE_I) {
            switch (newOrientation) {
                case 0:
                    row -= 2;
                    col -= 1;
                    break;
                case 1:
                    row -= 1;
                    col += 2;
                    break;
                case 2:
                    row += 2;
                    col += 1;
                    break;
                case 3:
                    row += 1;
                    col -= 2;
                    break;
                default: break;
            }
        }

        // create a new tetrad in the new orientation
        Tetrad newTetrad = new Tetrad(row, col, shape, newOrientation, sprites);

        // as long as the new tetrad isn't collided with something we will copy
        // its coordinates to our original tetrad.
        if (!newTetrad.isCollided(pf)) {
            coords = newTetrad.coords;
            orientation = newOrientation;
        }
    }

    /**
     * Attempt to rotate the Tetrad clockwise.  If there are any obstructions the
     * Tetrad will not be rotated.
     *
     * If rotating SHAPE_I we need to find the new position for block 0 before
     * changing the orientation.
     *
     * @param pf the PlayField object which contains this Tetrad.
     */
    public void rotateRight(PlayField pf) {
        int newOrientation = orientation + 1;
        int row = coords[0].x;
        int col = coords[0].y;

        // wrap orientation back to 0 if we try to go too high.
        if (newOrientation == 4) {
            newOrientation = 0;
        }

        // we need to change the position of block 0 if we are rotating SHAPE_I
        if (shape == SHAPE_I) {
            switch (newOrientation) {
                case 0:
                    row -= 1;
                    col += 2;
                    break;
                case 1:
                    row += 2;
                    col += 1;
                    break;
                case 2:
                    row += 1;
                    col -= 2;
                    break;
                case 3:
                    row -= 2;
                    col -= 1;
                    break;
                default: break;
            }
        }

        // create a new tetrad in the new orientation
        Tetrad newTetrad = new Tetrad(row, col, shape, newOrientation, sprites);

        // as long as the new tetrad isn't collided with something we will copy
        // its coordinates to our original tetrad.
        if (!newTetrad.isCollided(pf)) {
            coords = newTetrad.coords;
            orientation = newOrientation;
        }
    }

    /**
     * Immediately drops the Tetrad one line.  If the Tetrad is currently occupying a
     * a position directly above another block the Tetrad will land instead.
     *
     * @param pf the PlayField object which contains this Tetrad.
     * @param gps the GameplayState object.
     */
    public void softDrop(PlayField pf, GameplayState gps) {
        Tetrad newTetrad = new Tetrad(coords[0].x + 1, coords[0].y, shape, orientation, sprites);

        // as long as the new tetrad isn't collided with something we will copy
        // its coordinates to our original tetrad.
        if (!newTetrad.isCollided(pf)) {
            coords = newTetrad.coords;
        } else {
            gps.setTetradLanded(true);
        }
    }

    /**
     * Immediately drops the Tetrad as far as it can until it lands.
     *
     * @param pf the PlayField object which contains this Tetrad.
     * @param gps the GameplayState object.
     */
    public void hardDrop(PlayField pf, GameplayState gps) {
        while (!gps.isTetradLanded()) {
            this.softDrop(pf,gps);
        }
    }

    /**
     * Calls the draw method for each of the Block objects contained in this Tetrad.
     *
     * @param g the graphics context to draw on.
     * @param pf the PlayField object which contains this Tetrad.
     */
    public void draw(Graphics g, PlayField pf) {
        for (int i = 0; i < 4; i++) {
            if (coords[i].x > 3) {
                blocks[i].draw(pf.getCellXCoord(coords[i].y), pf.getCellYCoord(coords[i].x), g);
            }
        }
    }

    /**
     * Checks if any of the blocks in this tetrad are colliding with blocks in
     * the playfield or are outside the playfield.
     *
     * @param pf the PlayField object.
     * @return true if the tetrad is collided, false otherwise.
     */
    public boolean isCollided(PlayField pf) {
        for (int i = 0; i < 4; i++) {
            // CHECK LEFT AND RIGHT BOUNDARIES
            if (coords[i].y < 0 || coords[i].y > 9) {
                return true;
            } else if (coords[i].x < 0 || coords[i].x > 23) { // CHECK TOP AND BOTTOM BOUNDARIES
                return true;
            } else if ( pf.isCellOccupied(coords[i].x, coords[i].y) ) { // CHECK COLLISIONS WITH BLOCKS
                return true;
            }
        }

        return false;
    }

    private void setIShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(0, 0));
        blocks[1] = new Block(sprites.getSprite(0, 0));
        blocks[2] = new Block(sprites.getSprite(0, 0));
        blocks[3] = new Block(sprites.getSprite(0, 0));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row+2, col);
                coords[3] = new Point(row+3, col);
                break;
            case 1:
                coords[1] = new Point(row, col-1);
                coords[2] = new Point(row, col-2);
                coords[3] = new Point(row, col-3);
                break;
            case 2:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row-2, col);
                coords[3] = new Point(row-3, col);
                break;
            case 3:
                coords[1] = new Point(row, col+1);
                coords[2] = new Point(row, col+2);
                coords[3] = new Point(row, col+3);
                break;
            default: break;
        }
    }

    private void setJShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(1, 0));
        blocks[1] = new Block(sprites.getSprite(1, 0));
        blocks[2] = new Block(sprites.getSprite(1, 0));
        blocks[3] = new Block(sprites.getSprite(1, 0));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row+1, col);
                coords[3] = new Point(row+1, col-1);
                break;
            case 1:
                coords[1] = new Point(row,   col+1);
                coords[2] = new Point(row,   col-1);
                coords[3] = new Point(row-1, col-1);
                break;
            case 2:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row-1, col);
                coords[3] = new Point(row-1, col+1);
                break;
            case 3:
                coords[1] = new Point(row,   col-1);
                coords[2] = new Point(row,   col+1);
                coords[3] = new Point(row+1, col+1);
                break;
            default: break;
        }
    }

    private void setLShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(2, 0));
        blocks[1] = new Block(sprites.getSprite(2, 0));
        blocks[2] = new Block(sprites.getSprite(2, 0));
        blocks[3] = new Block(sprites.getSprite(2, 0));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row+1, col);
                coords[3] = new Point(row+1, col+1);
                break;
            case 1:
                coords[1] = new Point(row,   col+1);
                coords[2] = new Point(row,   col-1);
                coords[3] = new Point(row+1, col-1);
                break;
            case 2:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row-1, col);
                coords[3] = new Point(row-1, col-1);
                break;
            case 3:
                coords[1] = new Point(row,   col-1);
                coords[2] = new Point(row,   col+1);
                coords[3] = new Point(row-1, col+1);
                break;
            default: break;
        }
    }

    private void setSShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(3, 0));
        blocks[1] = new Block(sprites.getSprite(3, 0));
        blocks[2] = new Block(sprites.getSprite(3, 0));
        blocks[3] = new Block(sprites.getSprite(3, 0));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row,   col+1);
                coords[2] = new Point(row+1, col-1);
                coords[3] = new Point(row+1, col);
                break;
            case 1:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row-1, col-1);
                coords[3] = new Point(row,   col-1);
                break;
            case 2:
                coords[1] = new Point(row,   col-1);
                coords[2] = new Point(row-1, col+1);
                coords[3] = new Point(row-1, col);
                break;
            case 3:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row+1, col+1);
                coords[3] = new Point(row,   col+1);
                break;
            default: break;
        }
    }

    private void setZShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(0, 1));
        blocks[1] = new Block(sprites.getSprite(0, 1));
        blocks[2] = new Block(sprites.getSprite(0, 1));
        blocks[3] = new Block(sprites.getSprite(0, 1));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row,   col-1);
                coords[2] = new Point(row+1, col);
                coords[3] = new Point(row+1, col+1);
                break;
            case 1:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row,   col-1);
                coords[3] = new Point(row+1, col-1);
                break;
            case 2:
                coords[1] = new Point(row,   col+1);
                coords[2] = new Point(row-1, col);
                coords[3] = new Point(row-1,   col-1);
                break;
            case 3:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row,   col+1);
                coords[3] = new Point(row-1, col+1);
                break;
            default: break;
        }
    }

    private void setOShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(1, 1));
        blocks[1] = new Block(sprites.getSprite(1, 1));
        blocks[2] = new Block(sprites.getSprite(1, 1));
        blocks[3] = new Block(sprites.getSprite(1, 1));
        coords[1] = new Point(row,   col+1);
        coords[2] = new Point(row+1, col);
        coords[3] = new Point(row+1, col+1);
    }

    private void setTShape() {
        int row = coords[0].x;
        int col = coords[0].y;

        blocks[0] = new Block(sprites.getSprite(2, 1));
        blocks[1] = new Block(sprites.getSprite(2, 1));
        blocks[2] = new Block(sprites.getSprite(2, 1));
        blocks[3] = new Block(sprites.getSprite(2, 1));

        switch (orientation) {
            case 0:
                coords[1] = new Point(row,   col-1);
                coords[2] = new Point(row,   col+1);
                coords[3] = new Point(row-1, col);
                break;
            case 1:
                coords[1] = new Point(row-1, col);
                coords[2] = new Point(row+1, col);
                coords[3] = new Point(row,   col+1);
                break;
            case 2:
                coords[1] = new Point(row,   col+1);
                coords[2] = new Point(row,   col-1);
                coords[3] = new Point(row+1, col);
                break;
            case 3:
                coords[1] = new Point(row+1, col);
                coords[2] = new Point(row-1, col);
                coords[3] = new Point(row,   col-1);
                break;
            default: break;
        }
    }

    public int getShape() {
        return shape;
    }

    public int getOrientation() {
        return orientation;
    }

    public Block[] getBlocks() {
        return blocks;
    }

    public Point[] getCoords() {
        return coords;
    }
}
