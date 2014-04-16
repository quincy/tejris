package com.quakbo.tejris;

import java.io.Serializable;

public class HighScore implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public int score;
    public String name;
    
    public HighScore()
    {
        this.score = 0;
        this.name  = "nobody";
    }
    
    public HighScore( int score, String name )
    {
        this.score = score;
        this.name  = name;
    }
}
