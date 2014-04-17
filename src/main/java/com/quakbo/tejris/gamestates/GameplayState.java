package com.quakbo.tejris.gamestates;

import com.quakbo.tejris.score.HighScore;
import com.quakbo.tejris.board.PlayField;
import com.quakbo.tejris.Tejris;
import com.quakbo.tejris.pieces.Tetrad;
import com.quakbo.tejris.gamestates.GameState;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Font;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;

public class GameplayState extends BasicGameState {
    /* Size and positioning. */
    private static final int BLOCK_WIDTH    = 28;
    private static final int BLOCK_HEIGHT   = 28;
    private static final int NEW_TETRAD_ROW = 2;
    private static final int NEW_TETRAD_COL = 5;

    /* Constants for points */
    private static final int POINTS_SINGLE    = 100;
    private static final int POINTS_DOUBLE    = 250;
    private static final int POINTS_TRIPLE    = 500;
    private static final int POINTS_TEJRIS    = 1000;
    private static final int POINTS_SOFT_DROP = 10;
    //private static final int POINTS_HARD_DROP = 10;

    /* Input timing */
    private static final int KEY_REPEAT_DELAY       = 200;
    private static final int SOFT_DROP_REPEAT_DELAY = 50;

    private static final int GAME_WON_CREDITS_DELAY = 5000;

    /* Tetrad drop rate per level */
    private static final int[] SPEED = { 1000, 950, 900, 850, 800,
                                         750,  700, 650, 600, 550,
                                         500,  450, 400, 350, 300,
                                         250,  200, 150, 100, 50   };

    public static final Random rand = new Random();

    int score                   = 0;
    int pointsMultiplier        = 1;
    int pointsToAward           = 0;
    int level                   = 1;
    int totalLinesCleared       = 0;
    int timeSinceLastDrop       = 0;
    int timeSinceKeyRepeat      = 0;
    int timeSinceSoftDropRepeat = 0;
    int timeSinceGameWon        = 0;

    boolean initialized   = false;
    private boolean tetradLanded = false;
    private boolean gameIsLost = false;
    boolean gameIsWon     = false;

    PlayField pf          = null;
    Tetrad nextTetrad     = null;
    Tetrad currTetrad     = null;
    Tetrad swappedTetrad  = null;
    SpriteSheet sprites   = null;
    Image imgBackground   = null;
    UnicodeFont uf        = null;
    UnicodeFont scoreFont = null;
    Music gameplayMusic   = null;

    private Image[] shapes = null;
    private final GameState gameState;

    /**
     * Constructor for the GameplayState.
     *
     * @param gameState
     */
    public GameplayState(GameState gameState) {
       this.gameState = gameState;
    }

    public int getID() {
        return gameState.getId();
    }

    @SuppressWarnings("unchecked")
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        score             = 0;
        pointsMultiplier  = 1;
        pointsToAward     = 0;
        level             = ((Tejris) sbg).getNewGameLevel();
        totalLinesCleared = 0;
        gameIsLost        = false;
        tetradLanded      = false;

        pf      = new PlayField();
        sprites = new SpriteSheet("blocks.png", BLOCK_WIDTH, BLOCK_HEIGHT, 0);

        shapes = new Image[7];
        shapes[0]     = new Image("IShape.png");
        shapes[1]     = new Image("JShape.png");
        shapes[2]     = new Image("LShape.png");
        shapes[3]     = new Image("SShape.png");
        shapes[4]     = new Image("ZShape.png");
        shapes[5]     = new Image("OShape.png");
        shapes[6]     = new Image("TShape.png");
        imgBackground = new Image("BackgroundLevel01.png");
        gameplayMusic = new Music("gameplayMusic.ogg");

        currTetrad = getNextTetrad();
        nextTetrad = getNextTetrad();

        uf = new UnicodeFont(new Font("SansSerif", Font.PLAIN, 32), 32, false, false);
        uf.getEffects().add(new ColorEffect());
        uf.addAsciiGlyphs();
        uf.loadGlyphs();

        scoreFont = new UnicodeFont( new Font("Monospaced", Font.PLAIN, 40), 40, false, false );
        scoreFont.getEffects().add(new ColorEffect());
        scoreFont.addAsciiGlyphs();
        scoreFont.loadGlyphs();

        // Fill in incomplete lines up to the starting line height
        if (((Tejris) sbg).getNewGameLineHeight() > 0) {
            int randomColumn = rand.nextInt(10);

            for (int row = 23; row > 23-((Tejris) sbg).getNewGameLineHeight(); row--) {
                pf.addPartialLine(row, randomColumn, sprites, this);
            }
        }

        initialized = true;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        if (gc.isPaused()) {
            String msg = "PAUSED";

            int x = (Tejris.GAME_WIDTH  - uf.getWidth(msg)) / 2;
            int y = (Tejris.GAME_HEIGHT - uf.getHeight(msg)) / 2;

            g.setColor(Color.black);
            g.fillRect(0, 0, Tejris.GAME_WIDTH, Tejris.GAME_HEIGHT);
            uf.drawString(x, y, msg);
        } else {
            // draw the background
            imgBackground.draw(0,0);

            // draw the play field blocks and the current Tetrad
            pf.draw(g);
            currTetrad.draw(g,pf);

            // draw score
            int x = 515;
            int y = 75;

            String msg = String.format("%011d", score);
            scoreFont.drawString(x, y, msg);

            // multiplier
            x = 525;
            y = 130;
            scoreFont.drawString(x, y, "x" + pointsMultiplier);

            // level
            x = 575;
            y = 200;
            scoreFont.drawString(x, y, "Level " + level);

            // totalLinesCleared
            y = 245;
            scoreFont.drawString(x, y, "Lines " + totalLinesCleared);

            // draw next tetrad
            x = 345;
            y = 10;
            shapes[nextTetrad.getShape()].draw(x, y);

            // draw swapped
            if (swappedTetrad != null) {
                x = 345;
                y = 150;
                shapes[swappedTetrad.getShape()].draw(x, y);
            }

            if (gameIsWon)
            {
                String winmsg = "Congratulations!  A winner is you!";
                uf.drawString((Tejris.GAME_WIDTH-uf.getWidth(winmsg))/2, (Tejris.GAME_HEIGHT-uf.getHeight(winmsg))/2, winmsg);

                if (timeSinceGameWon > GAME_WON_CREDITS_DELAY) {
                    sbg.enterState(GameState.CREDITS_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
                }
            }
        }
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        initialized = false;

        // update timing information
        timeSinceLastDrop       += delta;
        timeSinceKeyRepeat      += delta;
        timeSinceSoftDropRepeat += delta;

        // Check if the game has been won
        if (totalLinesCleared > 199)
        {
            timeSinceGameWon += delta;
            gameIsWon = true;
            return;
        }

        // Poll for user input.
        pollKeyboard(gc,sbg);

        // If a new Tetrad landed we need to add its blocks to the play field,
        // check for complete lines, and delete the current tetrad.
        if (tetradLanded) {
            tetradLanded = false;
            pf.addTetrad(currTetrad, this);

            // check if any of the blocks from the tetrad are above the playfield.
            if (gameIsLost) {
                addHighScore();
                sbg.enterState(GameState.GAME_OVER_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            }

            ArrayList<Integer> linesCompleted = pf.checkForLines();
            if (linesCompleted.size() > 0) {
                // short pause whenever we are clearing lines.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                removeLines(linesCompleted, pf);
            }

            currTetrad = null;
        }

        // Generate a new Tetrad if necessary.
        if (currTetrad == null) {
            currTetrad = nextTetrad;
            nextTetrad = getNextTetrad();
        }

        // award points
        awardPoints();

        // check if the level needs to be increased
        if (totalLinesCleared >= level*10) {
            level++;
        }

        // Whenever the deltas add up to a number greater than the SPEED for the
        // current level the current tetrad is forced to do a soft drop.
        if (timeSinceLastDrop > SPEED[level-1]) {
            currTetrad.softDrop(pf,this);
            timeSinceLastDrop -= SPEED[level-1];
        }
    }

    public void enter(GameContainer gc, StateBasedGame sbg) {
        try {
            this.init(gc, sbg);
        } catch (SlickException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gameplayMusic.loop();
    }

    /**
     * Adds the high score to the list if it's high enough.
     */
    public void addHighScore() {
        for (int i = 0; i < Tejris.highScores.size(); i++) {
            if (score > Tejris.highScores.get(i).score) {
                String name = "nobody";
                name = (String) JOptionPane.showInputDialog("Please enter your name", name);

                HighScore hs = new HighScore(score, name);
                Tejris.highScores.add(i, hs);
                Tejris.highScores.remove(10);
                i = 11;
            }
        }

        Tejris.writeHighScoresFile();
    }

    /**
     * Removes the lines in the list from the play field and causes all blocks
     * above them to fall.
     *
     * @param rows the list of rows to remove.
     * @param pf the PlayField object.
     */
    public void removeLines(ArrayList<Integer> rows, PlayField pf) {
        int rowsCleared = 0;

        // clear the rows that need clearing
        for (int row = 23; row > -1; row--) {
            if (rows.contains(row)) {
                pf.clearRow(row);
                rowsCleared++;
            } else if (rowsCleared > 0) {
                pf.dropRow(row, rowsCleared);
            }
        }

        switch (rows.size()) {
            case 1:
                totalLinesCleared += 1;
                pointsToAward += POINTS_SINGLE;
                pointsMultiplier = 1;
                break;
            case 2:
                totalLinesCleared += 2;
                pointsToAward += POINTS_DOUBLE;
                pointsMultiplier = 1;
                break;
            case 3:
                totalLinesCleared += 3;
                pointsToAward += POINTS_TRIPLE;
                pointsMultiplier = 1;
                break;
            case 4:
                totalLinesCleared += 4;
                pointsToAward += POINTS_TEJRIS;
                pointsMultiplier++;
                break;
            default: break;
        }
    }

    /**
     * Adds the given points to the player's score.
     */
    public void awardPoints() {
        score += pointsToAward * pointsMultiplier;
        pointsToAward = 0;
    }

    /**
     * Returns a random Tetrad.
     *
     * @return a random Tetrad.
     */
    public Tetrad getNextTetrad() {
        int shape = rand.nextInt(7);
        int orientation = rand.nextInt(3);
        tetradLanded = false;

        return new Tetrad(NEW_TETRAD_ROW, NEW_TETRAD_COL, shape, orientation, sprites);
    }

    /**
     * Swaps the current Tetrad with the Tetrad in the Swap area.  If there is
     * no Tetrad in the Swap area the current Tetrad is placed in the Swap area
     * and a new Tetrad is generated.
     */
    public void getSwappedTetrad() {
        if (swappedTetrad != null) {
            Tetrad newTetrad = new Tetrad(NEW_TETRAD_ROW, NEW_TETRAD_COL, swappedTetrad.getShape(), swappedTetrad.getOrientation(), sprites);
            swappedTetrad = currTetrad;
            currTetrad = newTetrad;
        } else {
            swappedTetrad = currTetrad;
            currTetrad = nextTetrad;
            nextTetrad = getNextTetrad();
        }
    }

    /**
     * Act on user input from the keyboard.
     *
     * @param gc the gameContainer.
     * @param sbg the StateBasedGame object.
     */
    public void pollKeyboard(GameContainer gc, StateBasedGame sbg) {
        Input input = gc.getInput();

        // QUIT GAME
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            input.clearKeyPressedRecord();

            int answer = JOptionPane.showConfirmDialog(
                    null,
                    "Really abandon this game and return to the menu?",
                    "Quit Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (answer == JOptionPane.YES_OPTION) {
                sbg.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            }
        } else if ( input.isKeyPressed(Input.KEY_N) ) { // NEW GAME
            input.clearKeyPressedRecord();

            int answer = JOptionPane.showConfirmDialog(
                    null,
                    "Really start new game?",
                    "New Game",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (answer == JOptionPane.YES_OPTION) {
                try {
                    this.init(gc, sbg);
                } catch (SlickException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        // PAUSE GAME
        if (input.isKeyPressed(Input.KEY_P)) {
            if (gc.isPaused()) {
                Log.info("Game Resumed");
                gc.setPaused(false);
                input.clearKeyPressedRecord();
            } else {
                Log.info("Game Paused");
                gc.setPaused(true);
                input.clearKeyPressedRecord();
            }
        }

        // STOP PROCESSING INPUT IF GAME IS PAUSED
        if (gc.isPaused()) {
            input.clearKeyPressedRecord();
            return;
        }

        // MOVE LEFT
        if (input.isKeyDown(Input.KEY_LEFT) && timeSinceKeyRepeat > KEY_REPEAT_DELAY) {
            timeSinceKeyRepeat = 0;
            currTetrad.moveLeft(pf);
        } else if (input.isKeyDown(Input.KEY_RIGHT) && timeSinceKeyRepeat > KEY_REPEAT_DELAY) { // MOVE RIGHT
            timeSinceKeyRepeat = 0;
            currTetrad.moveRight(pf);
        }

        // SOFT DROP
        if (input.isKeyDown(Input.KEY_DOWN) && timeSinceSoftDropRepeat > SOFT_DROP_REPEAT_DELAY) {
            pointsToAward += POINTS_SOFT_DROP;
            timeSinceSoftDropRepeat = 0;
            currTetrad.softDrop(pf, this);
        }

        /*// HARD DROP
        if ( input.isKeyPressed(Input.KEY_SPACE) )
        {
            pointsToAward += POINTS_HARD_DROP * ( 24 - currTetrad.coords[0].x );
            currTetrad.hardDrop(pf, this);
        }*/

        // ROTATE LEFT
        if (input.isKeyDown(Input.KEY_X) && timeSinceKeyRepeat > KEY_REPEAT_DELAY) {
            timeSinceKeyRepeat = 0;
            currTetrad.rotateLeft(pf);
        } else if (( input.isKeyDown(Input.KEY_C) || input.isKeyDown(Input.KEY_UP)) && timeSinceKeyRepeat > KEY_REPEAT_DELAY) { // ROTATE RIGHT
            timeSinceKeyRepeat = 0;
            currTetrad.rotateRight(pf);
        }

        // SWAP
        if (input.isKeyPressed(Input.KEY_Z)) {
            getSwappedTetrad();
        }

        input.clearKeyPressedRecord();
    }

    public void setGameIsLost(boolean gameIsLost) {
        this.gameIsLost = gameIsLost;
    }

    public boolean isGameLost() {
        return gameIsLost;
    }

    public boolean isTetradLanded() {
        return tetradLanded;
    }

    public void setTetradLanded(boolean tetradLanded) {
        this.tetradLanded = tetradLanded;
    }
}