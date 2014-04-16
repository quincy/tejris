package com.quakbo.tejris;

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
 
public class GameOverState extends BasicGameState
{
    int stateID = -1;
    
    UnicodeFont uf = null;
    
    boolean initialized = false;
 
    public GameOverState( int stateID ) 
    {
       this.stateID = stateID;
    }
 
    public int getID() 
    {
        return stateID;
    }
 
    @SuppressWarnings("unchecked")
    public void init( GameContainer gc, StateBasedGame sbg ) throws SlickException 
    {
        uf = new UnicodeFont( new Font("SansSerif", Font.PLAIN, 32), 32, false, false );
        uf.getEffects().add(new ColorEffect());
        uf.addAsciiGlyphs();
        uf.loadGlyphs();
        
        initialized = true;
    }
 
    public void render( GameContainer gc, StateBasedGame sbg, Graphics g ) throws SlickException 
    {
        g.setColor(Color.black);
        g.fillRect(0, 0, Tejris.GAME_WIDTH, Tejris.GAME_HEIGHT);
        
        String[] msg = { "GAME OVER", "Press 'N' for New Game", "Press 'Esc' to Quit" };
        
        int x = (Tejris.GAME_WIDTH  - uf.getWidth(msg[0]) ) / 2;
        int y = ( (Tejris.GAME_HEIGHT - uf.getHeight(msg[0]) ) / 2 ) - uf.getHeight(msg[0]);
        uf.drawString(x, y, msg[0]);
        
        x = (Tejris.GAME_WIDTH  - uf.getWidth(msg[1]) ) / 2;
        y = (Tejris.GAME_HEIGHT - uf.getHeight(msg[1])) / 2;
        uf.drawString(x, y, msg[1]);
        
        x = (Tejris.GAME_WIDTH  - uf.getWidth(msg[2]) ) / 2;
        y = ( (Tejris.GAME_HEIGHT - uf.getHeight(msg[2])) / 2 ) + uf.getHeight(msg[2]);
        uf.drawString(x, y, msg[2]);
    }
 
    public void update( GameContainer gc, StateBasedGame sbg, int delta ) throws SlickException 
    {
        pollKeyboard(gc, sbg);
    }
    
    @Override
    public void enter( GameContainer gc, StateBasedGame sbg )
    {
        if ( ! initialized )
        {
            try
            {
                this.init(gc, sbg);
            }
            catch (SlickException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Act on user input from the keyboard.
     * 
     * @param gc the gameContainer.
     * @param sbg the StateBasedGame object.
     */
    public void pollKeyboard( GameContainer gc, StateBasedGame sbg )
    {
        Input input = gc.getInput();
        
        // QUIT GAME
        if ( input.isKeyPressed(Input.KEY_ESCAPE) )
        {
            sbg.enterState( Tejris.MAIN_MENU_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black) );
        }
        // NEW GAME
        else if ( input.isKeyPressed(Input.KEY_N) )
        {
            sbg.enterState( Tejris.GAMEPLAY_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black) );
        }
    }
}