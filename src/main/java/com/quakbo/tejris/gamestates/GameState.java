package com.quakbo.tejris.gamestates;

/**
 * The different states the game can be in.
 */
public enum GameState {
    MAIN_MENU_STATE(0),
    GAMEPLAY_STATE(1),
    INSTRUCTIONS_STATE(2),
    CREDITS_STATE(3),
    GAME_CONFIG_STATE(4),
    GAME_OVER_STATE(5);

    private final int id;

    GameState(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
