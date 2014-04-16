package com.quakbo.tejris;

import com.quakbo.tejris.gamestates.GameplayState;
import com.quakbo.tejris.gamestates.InstructionsState;
import com.quakbo.tejris.gamestates.MainMenuState;
import com.quakbo.tejris.gamestates.GameOverState;
import com.quakbo.tejris.gamestates.GameConfigState;
import com.quakbo.tejris.gamestates.CreditsState;
import com.quakbo.tejris.gamestates.GameState;
import java.io.IOException;
import java.util.ArrayList;
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

public class Tejris extends StateBasedGame {
    public static final int MAIN_MENU_STATE    = 0;
    public static final int GAMEPLAY_STATE     = 1;
    public static final int INSTRUCTIONS_STATE = 2;
    public static final int CREDITS_STATE      = 3;
    public static final int GAME_CONFIG_STATE  = 4;
    public static final int GAME_OVER_STATE    = 5;

    public static final int GAME_WIDTH         = 800;
    public static final int GAME_HEIGHT        = 600;

    public static final String HIGH_SCORES_FILE = "TejrisHighScores";

    public static ArrayList<HighScore> highScores;
    public static SavedState highScoresFile;

    public int NewGameLineHeight;
    public int NewGameLevel;
    public boolean configSoundOn;
    public boolean configMusicOn;

    public Tejris() {
        super("Tejris");

        NewGameLineHeight = 0;
        NewGameLevel      = 0;

        this.addState(new MainMenuState(GameState.MAIN_MENU_STATE));
        this.addState(new GameplayState(GameState.GAMEPLAY_STATE));
        this.addState(new InstructionsState(GameState.INSTRUCTIONS_STATE));
        this.addState(new CreditsState(GameState.CREDITS_STATE));
        this.addState(new GameConfigState(GameState.GAME_CONFIG_STATE));
        this.addState(new GameOverState(GameState.GAME_OVER_STATE));

        this.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeInTransition(Color.black), new EmptyTransition());
    }

    public static void main(String[] args) throws SlickException {
        // create the high score list
        highScores = new ArrayList<HighScore>();
        highScoresFile = new SavedState(HIGH_SCORES_FILE);

        readHighScoresFile();

        // create the game container and start the game.
        AppGameContainer app = new AppGameContainer(new Tejris());

        app.setShowFPS(true);
        app.setVerbose(true);
        app.setMinimumLogicUpdateInterval(20);
        app.setMaximumLogicUpdateInterval(20);
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

        for (int i = 0; i < 10; i++) {
            highScores.add(i, new HighScore((int)highScoresFile.getNumber("score" + i, 0),
                                                 highScoresFile.getString("name" + i, "nobody")));
        }
    }

    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        parseConfig(gameContainer);
    }

    /**
     * Read in the config file and change basic properties of the game.
     *
     * @param gc the GameContainer object.
     */
    public void parseConfig(GameContainer gc) {
        Scanner s = new Scanner(ResourceLoader.getResourceAsStream("tejris.cfg"));
        s.useDelimiter(System.getProperty("line.separator"));

        while (s.hasNext()) {
            String line = s.nextLine();

            if (line.equals("NO_SOUND")) {
                Log.info("Sound disabled.");
                configSoundOn = false;
            } else if (line.equals("NO_MUSIC")) {
                Log.info("Music disabled");
                configMusicOn = false;
            }
        }

    	gc.setSoundOn(configSoundOn);
        gc.setMusicOn(configMusicOn);
    }
}