package com.quakbo.tejris;

import com.quakbo.tejris.score.HighScore;
import com.quakbo.tejris.gamestates.GameplayState;
import com.quakbo.tejris.gamestates.InstructionsState;
import com.quakbo.tejris.gamestates.MainMenuState;
import com.quakbo.tejris.gamestates.GameOverState;
import com.quakbo.tejris.gamestates.GameConfigState;
import com.quakbo.tejris.gamestates.CreditsState;
import com.quakbo.tejris.gamestates.GameState;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SavedState;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

/**
 * Tejris sets up the various states that make up the game and starts game at
 * the main menu.
 */
public class Tejris extends StateBasedGame {
    /** The width of the GUI window. */
    public static final int GAME_WIDTH = 800;
    /** The height of the GUI window. */
    public static final int GAME_HEIGHT = 600;
    /** The path to the high scores file. */
    public static final String HIGH_SCORES_FILE = "TejrisHighScores";
    /** The number of high scores that will be kept. */
    private static final int MAX_NUM_SCORES = 10;
    /** The list of high scores. */
    public static List<HighScore> highScores;
    /** The high scores file. */
    public static SavedState highScoresFile;

    private int newGameLineHeight;
    private int newGameLevel;
    private boolean configSoundOn;
    private boolean configMusicOn;

    /**
     * Creates the instance of the Tejris StateBasedGame.
     */
    public Tejris() {
        super("Tejris");

        newGameLineHeight = 0;
        newGameLevel      = 0;

        this.addState(new MainMenuState(GameState.MAIN_MENU_STATE));
        this.addState(new GameplayState(GameState.GAMEPLAY_STATE));
        this.addState(new InstructionsState(GameState.INSTRUCTIONS_STATE));
        this.addState(new CreditsState(GameState.CREDITS_STATE));
        this.addState(new GameConfigState(GameState.GAME_CONFIG_STATE));
        this.addState(new GameOverState(GameState.GAME_OVER_STATE));

        this.enterState(
                GameState.MAIN_MENU_STATE.getId(),
                new FadeInTransition(Color.black),
                new EmptyTransition());
    }

    /**
     * The main method for the game.
     * @param args the command line arguments to the program.
     * @throws SlickException if the game engine has a problem.
     */
    public static void main(final String[] args) throws SlickException {
        // create the high score list
        highScores = new ArrayList<HighScore>();
        highScoresFile = new SavedState(HIGH_SCORES_FILE);

        readHighScoresFile();

        // create the game container and start the game.
        final AppGameContainer app = new AppGameContainer(new Tejris());
        final int updateInterval = 20; // ms

        app.setShowFPS(true);
        app.setVerbose(true);
        app.setMinimumLogicUpdateInterval(updateInterval);
        app.setMaximumLogicUpdateInterval(updateInterval);
        app.setDisplayMode(GAME_WIDTH, GAME_HEIGHT, false);
        app.start();
    }

    /**
     * Writes the contents of the highScores array to the high scores muffin.
     */
    public static void writeHighScoresFile() {
        for (int i = 0; i < highScores.size(); i++) {
            highScoresFile.setNumber("score" + i, highScores.get(i).score);
            highScoresFile.setString("name" + i, highScores.get(i).name);
        }

        try {
            highScoresFile.save();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Reads the contents of the high scores muffin into the high scores array.
     */
    public static void readHighScoresFile() {
        try {
            highScoresFile.load();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (int i = 0; i < MAX_NUM_SCORES; i++) {
            highScores.add(i,
                    new HighScore((int) highScoresFile.getNumber("score" + i, 0),
                            highScoresFile.getString("name" + i, "nobody")));
        }
    }

    @Override
    public void initStatesList(final GameContainer gameContainer) throws SlickException {
        parseConfig(gameContainer);
    }

    /**
     * Read in the config file and change basic properties of the game.
     *
     * @param gameContainer the GameContainer object.
     */
    public final void parseConfig(final GameContainer gameContainer) {
        final Scanner scanner = new Scanner(ResourceLoader.getResourceAsStream("tejris.cfg"));
        scanner.useDelimiter(System.getProperty("line.separator"));

        while (scanner.hasNext()) {
            final String line = scanner.nextLine();

            if ("NO_SOUND".equals(line)) {
                Log.info("Sound disabled.");
                configSoundOn = false;
            } else if ("NO_MUSIC".equals(line)) {
                Log.info("Music disabled");
                configMusicOn = false;
            }
        }

        gameContainer.setSoundOn(configSoundOn);
        gameContainer.setMusicOn(configMusicOn);
    }

    /**
     * @return the line height for new games.
     */
    public final int getNewGameLineHeight() {
        return newGameLineHeight;
    }

    /**
     * @return the level for new games.
     */
    public final int getNewGameLevel() {
        return newGameLevel;
    }

    /**
     * @return true id sound is on; false otherwise.
     */
    public final boolean isConfigSoundOn() {
        return configSoundOn;
    }

    /**
     * Sets the sound on or off.
     * @param configSoundOn state for the sound.
     */
    public final void setConfigSoundOn(final boolean configSoundOn) {
        this.configSoundOn = configSoundOn;
    }

    public boolean isConfigMusicOn() {
        return configMusicOn;
    }

    public void setConfigMusicOn(boolean configMusicOn) {
        this.configMusicOn = configMusicOn;
    }

    public void decrementNewGameLinHeight() {
        newGameLineHeight--;
    }

    public void decrementNewGameLevel() {
        newGameLevel--;
    }

    public void incrementNewGameLevel() {
        newGameLevel++;
    }

    public void incrementNewGameLinHeight() {
        newGameLineHeight++;
    }
}
