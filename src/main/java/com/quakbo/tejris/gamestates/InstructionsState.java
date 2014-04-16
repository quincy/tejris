package com.quakbo.tejris.gamestates;

import com.quakbo.tejris.gamestates.GameState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

public class InstructionsState extends BasicGameState {
    GameState gameState;
    boolean initialized = false;
    Image instructionsBackground = null;

    public InstructionsState(GameState gameState) {
       this.gameState = gameState;
    }

    public int getID() {
        return gameState.getId();
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        instructionsBackground = new Image("InstructionsBackground.png");
        initialized = true;
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        instructionsBackground.draw(0,0);
    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
        pollKeyboard(gc,sbg);
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

        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            sbg.enterState(GameState.MAIN_MENU_STATE.getId(), new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
    }
}
