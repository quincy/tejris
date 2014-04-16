package com.quakbo.tejris;

import java.awt.Font;
import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;
import org.newdawn.slick.util.Log;
 
public class MainMenuState extends BasicGameState 
{
    int stateID = -1;
    Image imgBackground   = null;
    Image imgNewGame      = null;
    Image imgInstructions = null;
    Image imgCredits      = null;
    Image imgExit         = null;
    Image imgCursor       = null;
    Point cursorPosition  = null;
    UnicodeFont uf        = null;
    Music menuMusic       = null;
    Sound menuCursorMove  = null;
    Sound menuSelection   = null;
 
    public MainMenuState( int stateID ) 
    {
       this.stateID = stateID;
    }
 
    @Override
    public int getID() 
    {
        return stateID;
    }
 
    @SuppressWarnings("unchecked")
    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException 
    {
        cursorPosition  = new Point( 0, 250 );
        imgBackground   = new Image("MainMenu.png");
        imgNewGame      = new Image("newGame.png");
        imgInstructions = new Image("instructions.png");
        imgCredits      = new Image("credits.png");
        imgExit         = new Image("exit.png");
        imgCursor       = new Image("cursor.png");
        
        menuMusic       = new Music("gameplayMusic.ogg");
        menuCursorMove  = new Sound("click.ogg");
        menuSelection   = new Sound("click.ogg");
        
        uf = new UnicodeFont( new Font("Monospaced", Font.PLAIN, 20), 20, false, false );
        uf.getEffects().add(new ColorEffect());
        uf.addAsciiGlyphs();
        uf.loadGlyphs();
    }
 
    public void render( GameContainer gc, StateBasedGame sbg, Graphics g ) throws SlickException 
    {
        imgBackground.draw(0,0);
        imgNewGame.draw(0,250);
        imgInstructions.draw(0,325);
        imgCredits.draw(0,400);
        imgExit.draw(0,475);
        imgCursor.draw(cursorPosition.x, cursorPosition.y);
        
        // draw the high score list
        int x = 500;
        int y = 200;
        
        uf.drawString(x, y, "High Scores" );
        for ( int i = 0; i < Tejris.highScores.size(); i++ )
        {
            String score = String.format( "%-12s %11d", Tejris.highScores.get(i).name, Tejris.highScores.get(i).score );
            uf.drawString(x, y+=25, score );
        }
    }
 
    public void update( GameContainer gc, StateBasedGame sbg, int delta ) throws SlickException 
    {
        pollKeyboard(gc,sbg);
    }
    
    public void enter( GameContainer gc, StateBasedGame sbg )
    {
        menuMusic.loop();
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
        
        if ( input.isKeyPressed(Input.KEY_ESCAPE) )
        {
        	menuCursorMove.play();
            quitProgram();
        }
        
        if ( input.isKeyPressed(Input.KEY_DOWN) )
        {
        	menuCursorMove.play();
        	
            if ( cursorPosition.y == 475 )
            {
                cursorPosition.y = 250;
            }
            else
            {
                cursorPosition.y += 75;
            }
        }
        else if ( input.isKeyPressed(Input.KEY_UP) )
        {
        	menuCursorMove.play();
            
        	if ( cursorPosition.y == 250 )
            {
                cursorPosition.y = 475;
            }
            else
            {
                cursorPosition.y -= 75;
            }
        }
        else if ( input.isKeyPressed(Input.KEY_ENTER) )
        {
        	menuSelection.play();
            menuMusic.stop();
            
        	if ( cursorPosition.y == 250 )
            {
                sbg.enterState(Tejris.GAME_CONFIG_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            }
            else if ( cursorPosition.y == 325 )
            {
                sbg.enterState(Tejris.INSTRUCTIONS_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            }
            else if ( cursorPosition.y == 400 )
            {
                sbg.enterState(Tejris.CREDITS_STATE, new FadeOutTransition(Color.black), new FadeInTransition(Color.black));
            }
            else if ( cursorPosition.y == 475 )
            {
                quitProgram();
            }
        }
    }
    
    /**
     * Exits the program immediately.
     */
    public void quitProgram()
    {
        Log.info("User exited program.");
        System.exit(0);
    }
}