package com.quakbo.tejris.board;

import com.quakbo.tejris.pieces.Block;
import com.quakbo.tejris.pieces.Tetrad;
import com.quakbo.tejris.gamestates.GameplayState;
import java.util.ArrayList;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public class PlayField {
    private static final int TOP_LEFT_X   = 50;
    private static final int TOP_LEFT_Y   = -95;
    private static final int BLOCK_WIDTH  = 28;
    private static final int BLOCK_HEIGHT = 28;

    /** The array which stores the information for the PlayField. */
    private final Block[][] cells;

    /**
     * Creates a new 24x10 play field.
     */
    public PlayField() {
        cells = new Block[24][10];
    }

    /**
     * Checks if the given cell is occupied.
     *
     * @param row the row to check.
     * @param col the column to check.
     * @return true if the cell is occupied by a block or false if it is empty.
     */
    public boolean isCellOccupied(int row, int col) {
        return cells[row][col] instanceof Block;
    }

    /**
     * Inserts a Block into the given cell.
     *
     * @param row the row to add the Block to.
     * @param col the column to add the Block to.
     * @param block the Block to insert.
     * @param gps
     */
    public void addBlock(int row, int col, Block block, GameplayState gps) {
        if (row < 4) {
            gps.setGameIsLost(true);
        }

        cells[row][col] = block;
    }

    /**
     * Draws a line with a single column missing on the given row.  The line will
     * consist of random blocks.
     *
     * @param row the row to add the line to.
     * @param missingColumn the missing column.
     * @param sprites the SpriteSheet to get the block images from.
     * @param gps
     */
    public void addPartialLine(int row, int missingColumn, SpriteSheet sprites, GameplayState gps) {
        for (int col = 0; col < 10; col++) {
            int x = GameplayState.rand.nextInt(sprites.getHorizontalCount());
            int y = GameplayState.rand.nextInt(sprites.getVerticalCount());

            if (col != missingColumn) {
                addBlock(row, col, new Block(sprites.getSprite(x, y)), gps);
            }
        }
    }

    /**
     * Adds each of the given Tetrad's blocks to the play field.
     *
     * @param tetrad the tetrad to add.
     * @param gps
     */
    public void addTetrad(Tetrad tetrad, GameplayState gps) {
        for (int i = 0; i < 4; i++) {
            addBlock(tetrad.getCoords()[i].x, tetrad.getCoords()[i].y, tetrad.getBlocks()[i], gps);
        }
    }

    /**
     * Checks each row for a completed line.
     *
     * @return an integer for the number of complete lines found.
     */
    public ArrayList<Integer> checkForLines() {
        ArrayList<Integer> fullRows = new ArrayList<Integer>();

        for (int row = 23; row > 3; row--) {
            if (isRowFull(row)) {
                fullRows.add(row);
            }
        }

        return fullRows;
    }

    /**
     * Checks the given row to see if each cell is occupied.
     *
     * @param row the row number to check.
     * @returns true if each cell in the row is occupied, false otherwise.
     */
    private boolean isRowFull(int row) {
        int numBlocks = 0;

        for (int col = 0; col < 10; col++) {
            if (isCellOccupied(row, col)) {
                numBlocks++;
            }
        }

        return numBlocks == 10;
    }

    /**
     * Removes all Blocks from the given row.
     *
     * @param row the row to clear.
     */
    public void clearRow(int row) {
        for (int col = 0; col < 10; col ++) {
            cells[row][col] = null;
        }
    }

    /**
     * Moves all of the Blocks on this row down the number of rows indicated.
     *
     * @param row the row to move.
     * @param num the number of rows down to move it.
     */
    public void dropRow(int row, int num) {
        System.arraycopy(cells[row], 0, cells[row+num], 0, 10);
    }

    /**
     * Find the x-coordinate of the given column.
     *
     * @param col the column index for a given column.
     * @return the x-coordinate of the top left corner of the given column.
     */
    public int getCellXCoord(int col) {
        return TOP_LEFT_X + (col * BLOCK_WIDTH);
    }

    /**
     * Find the y-coordinate of the given row.
     *
     * @param row the row index for a given row.
     * @return the y-coordinate of the top left corner of the given row.
     */
    public int getCellYCoord(int row) {
        return TOP_LEFT_Y + (row * BLOCK_HEIGHT);
    }

    /**
     * Draws each Block in this PlayField.
     *
     * @param g the graphics context to draw on.
     */
    public void draw(Graphics g)
    {
        for (int row = 4; row < 24; row++) {
            for (int col = 0; col < 10; col++) {
                if (cells[row][col] != null) {
                    cells[row][col].draw(getCellXCoord(col), getCellYCoord(row), g);
                }
            }
        }
    }
}
