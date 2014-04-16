package com.quakbo.tejris.gamestates;

import com.quakbo.tejris.Tejris;
import com.quakbo.tejris.gamestates.GameState;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

public class CreditsState extends BasicGameState {
    GameState gameState;
    int yPos       = 0;

    boolean initialized = false;

    UnicodeFont uf = null;
    Scanner s      = null;

    ArrayList<String> credits = null;

    public CreditsState(GameState gameState) {
       this.gameState = gameState;
    }

    public int getID() {
        return gameState.getId();
    }

    @SuppressWarnings("unchecked")
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        uf = new UnicodeFont(new Font("SansSerif", Font.PLAIN, 12), 22, false, false);
        uf.getEffects().add(new ColorEffect());
        uf.addAsciiGlyphs();
        uf.loadGlyphs();

        // Read in the credits file.
        credits = new ArrayList<String>();

        s = new Scanner(ResourceLoader.getResourceAsStream("credits.txt"));
        s.useDelimiter(System.getProperty("line.separator"));

        while (s.hasNext()) {
            String line = s.nextLine();
            credits.add(line + "\n");
        }

        yPos = Tejris.GAME_HEIGHT + 100;

        initialized = true;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        Iterator<String> itr = credits.iterator();

        int y = yPos;

        while (itr.hasNext()) {
            String next = itr.next();

            int width  = uf.getWidth(next);
            int height = uf.getHeight(next);

            int x = (Tejris.GAME_WIDTH - width) / 2;

            // Check if this string is visible
            if (y < Tejris.GAME_WIDTH && y + height > 0) {
                uf.drawString(x, y, next);
            }

            y += height + 10;
        }

        if (y < -50) {
            Log.info("Leaving CreditsState.");
            yPos = 700;
            sbg.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        initialized = false;
        pollKeyboard(gc, sbg);
        yPos--;
    }

    /**
     * Act on user input from the keyboard.
     *
     * @param gc the gameContainer.
     * @param sbg the StateBasedGame object.
     */
    public void pollKeyboard(GameContainer gc, StateBasedGame sbg) {
        Input input = gc.getInput();

        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            sbg.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
    	Log.info("Entered CreditsState.");
    	Log.info("Music is on = " + gc.isMusicOn());
    	Log.info("Sound is on = " + gc.isSoundOn());
        if (!initialized) {
            try {
                this.init(gc, sbg);
            } catch (SlickException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (true) { //gc.isMusicOn() )
            Music music;

            Log.info("About to play credits music.");
            try {
                music = new Music("credits.ogg");
                music.play();
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }
}
