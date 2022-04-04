package MKAgent.players;

import MKAgent.gameplay.InvalidMessageException;

public interface Bot {
    void processMessage(String msg) throws InvalidMessageException;

    String getNextMove() throws Exception;

    boolean isGameOver();

    boolean isNextTurn();
}