package com.quakbo.tejris.gamestates;

import com.quakbo.tejris.Tejris;
import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class GameConfigState extends BasicGameState
{
    GameState gameState;

    Color activeColor   = new Color(Color.cyan);
    Color inactiveColor = new Color(Color.white);

    int activeItem = 0;

    UnicodeFont uf   = null;

    boolean initialized = false;

    public GameConfigState(GameState gameState) {
       this.gameState = gameState;
    }

    public int getID() {
        return gameState.getId();
    }

    @SuppressWarnings("unchecked")
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        uf = new UnicodeFont(new Font("SansSerif", Font.PLAIN, 32), 32, false, false);
        uf.getEffects().add(new ColorEffect());
        uf.addAsciiGlyphs();
        uf.loadGlyphs();

        initialized = true;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        String title = "Game Settings";

        int x = (Tejris.GAME_WIDTH - uf.getWidth(title)) / 2;
        int y = 20;

        uf.drawString(x, y, title);

        x = 50;
        y = 200;
        if (activeItem == 0) {
            uf.drawString(x, y, "Level", activeColor);
        } else {
            uf.drawString(x, y, "Level", inactiveColor);
        }
        uf.drawString(250, 200, "" + ((Tejris) sbg).getNewGameLevel());

        x = 50;
        y = 250;
        if (activeItem == 1) {
            uf.drawString(x, y, "Height", activeColor);
        } else {
            uf.drawString(x, y, "Height", inactiveColor);
        }
        uf.drawString(250, 250, "" + ((Tejris) sbg).getNewGameLineHeight());

        String newGameString = "Press ENTER to start game";
        String cancelString  = "Press ESC to cancel";

        uf.drawString((Tejris.GAME_WIDTH-uf.getWidth(newGameString))/2, 450, newGameString);
        uf.drawString((Tejris.GAME_WIDTH-uf.getWidth(cancelString))/2, 500, cancelString);
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        pollKeyboard(gc, sbg);
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame sbg) {
        if (!initialized) {
            try {
                this.init(gc, sbg);
            } catch (SlickException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void pollKeyboard(GameContainer gc, StateBasedGame sbg) {
        Input input = gc.getInput();

        // QUIT GAME
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            input.clearKeyPressedRecord();
            sbg.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        // START GAME
        if (input.isKeyPressed(Input.KEY_ENTER)) {
            input.clearKeyPressedRecord();
            sbg.enterState(GameState.GAMEPLAY_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        // UP
        if (input.isKeyPressed(Input.KEY_UP)) {
            if (activeItem == 1) {
                activeItem = 0;
            }
        }
        // DOWN
        if (input.isKeyPressed(Input.KEY_DOWN)) {
            if (activeItem == 0) {
                activeItem = 1;
            }
        }
        // LEFT
        if (input.isKeyPressed(Input.KEY_LEFT)) {
            if (activeItem == 0) {
                if (((Tejris) sbg).getNewGameLevel() > 1) {
                    ((Tejris) sbg).decrementNewGameLevel();
                }
            } else {
                if (((Tejris) sbg).getNewGameLineHeight() > 0) {
                    ((Tejris) sbg).decrementNewGameLinHeight();
                }
            }
        }
        // RIGHT
        if (input.isKeyPressed(Input.KEY_RIGHT)) {
            if (activeItem == 0) {
                if (((Tejris) sbg).getNewGameLevel() < 20) {
                    ((Tejris) sbg).incrementNewGameLevel();
                }
            } else {
                if (((Tejris) sbg).getNewGameLineHeight() < 15) {
                    ((Tejris) sbg).incrementNewGameLinHeight();
                }
            }
        }
    }
}
