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
 
public class GameConfigState extends BasicGameState
{
    int stateID = -1;
    
    Color activeColor   = new Color(Color.cyan);
    Color inactiveColor = new Color(Color.white);
    
    int activeItem = 0;
    
    UnicodeFont uf   = null;
 
    boolean initialized = false;
    
    public GameConfigState( int stateID ) 
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
        String title = "Game Settings";
        
        int x = ( Tejris.GAME_WIDTH - uf.getWidth(title) ) / 2;
        int y = 20;
        
        uf.drawString(x, y, title);
        
        x = 50;
        y = 200;
        if ( activeItem == 0 )
        {
            uf.drawString(x, y, "Level", activeColor);
        }
        else
        {
            uf.drawString(x, y, "Level", inactiveColor);
        }
        uf.drawString(250, 200, "" + ((Tejris) sbg).NewGameLevel);
        
        x = 50;
        y = 250;
        if ( activeItem == 1 )
        {
            uf.drawString(x, y, "Height", activeColor);
        }
        else
        {
            uf.drawString(x, y, "Height", inactiveColor);
        }
        uf.drawString(250, 250, "" + ((Tejris) sbg).NewGameLineHeight );
        
        String newGameString = "Press ENTER to start game";
        String cancelString  = "Press ESC to cancel";
        
        uf.drawString((Tejris.GAME_WIDTH-uf.getWidth(newGameString))/2, 450, newGameString);
        uf.drawString((Tejris.GAME_WIDTH-uf.getWidth(cancelString))/2, 500, cancelString);
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
    
    public void pollKeyboard( GameContainer gc, StateBasedGame sbg )
    {
        Input input = gc.getInput();
        
        // QUIT GAME
        if ( input.isKeyPressed(Input.KEY_ESCAPE) )
        {
            input.clearKeyPressedRecord();
            sbg.enterState( Tejris.MAIN_MENU_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black) );
        }
        // START GAME
        if ( input.isKeyPressed(Input.KEY_ENTER) )
        {
            input.clearKeyPressedRecord();
            sbg.enterState(Tejris.GAMEPLAY_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
        }
        // UP
        if ( input.isKeyPressed(Input.KEY_UP) )
        {
            if ( activeItem == 1 )
            {
                activeItem = 0;
            }
        }
        // DOWN
        if ( input.isKeyPressed(Input.KEY_DOWN) )
        {
            if ( activeItem == 0 )
            {
                activeItem = 1;
            }
        }
        // LEFT
        if ( input.isKeyPressed(Input.KEY_LEFT) )
        {
            if ( activeItem == 0 )
            {
                if ( ((Tejris) sbg).NewGameLevel > 1 )
                {
                    ((Tejris) sbg).NewGameLevel--;
                }
            }
            else
            {
                if ( ((Tejris) sbg).NewGameLineHeight > 0 )
                {
                    ((Tejris) sbg).NewGameLineHeight--;
                }
            }
        }
        // RIGHT
        if ( input.isKeyPressed(Input.KEY_RIGHT) )
        {
            if ( activeItem == 0 )
            {
                if ( ((Tejris) sbg).NewGameLevel < 20 )
                {
                    ((Tejris) sbg).NewGameLevel++;
                }
            }
            else
            {
                if ( ((Tejris) sbg).NewGameLineHeight < 15 )
                {
                    ((Tejris) sbg).NewGameLineHeight++;
                }
            }
        }
    }
}