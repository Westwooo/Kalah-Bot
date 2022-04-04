// 
// Decompiled by Procyon v0.5.36
// 

package ManKalahVerbose;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeoutException;

public class GameEngine {
    private static final Side startingSide;
    private static final int holes = 7;
    private static final int seeds = 7;
    private static final long agentTimeout = 3600000L;
    private static final boolean printBoardToStderr = true;
    private static final boolean allowSwapping = true;

    static {
        startingSide = Side.SOUTH;
    }

    private final Kalah kalah;
    private Player playerNorth;
    private Player playerSouth;

    private GameEngine(final Kalah kalah, final Player playerNorth, final Player playerSouth) {
        this.kalah = kalah;
        this.playerNorth = playerNorth;
        this.playerSouth = playerSouth;
    }

    public static void main(final String[] args) throws IOException, URISyntaxException {
        if (args.length != 2) {
            System.err.println("There have to be exactly two arguments, each being the path to an\nexecutable agent application. The two agents are started\nand play a Kalah match against each other, the first agent\nbeing the starting player.\n\nThe output to standard output will consist of exactly two lines,\none per agent where the first line will be for the first agent\nand the second for the second. Each line will have the following\nformat:\n   ( \"0\" | \"1\" )   \" \"   <RESPONSETIME>\nwhere the first number is a 1 if the agent won the game (0 for\nboth in case of a draw) and <RESPONSETIME> gives the agent's\naverage response time in milliseconds.If, for example, agent 2 won (needing 423 milliseconds per move\non the average), the output would look like this:\n0 117\n1 423\nA draw would give something like:\n0 258\n0 912");
            System.exit(-1);
        }

        // Get working dir in which jar is contained.
        File currentWorkingDir = new File(
                GameEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI()
        ).getParentFile();

        ProcessBuilder player1ProcessBuilder = new ProcessBuilder(args[0].split(" "));
        player1ProcessBuilder.directory(currentWorkingDir);
        player1ProcessBuilder.redirectError(new File("player_1_error_log.txt"));
        Process agentProcess1;
        try {
            agentProcess1 = player1ProcessBuilder.start();
        } catch (IOException e) {
            agentProcess1 = null;
            System.err.println("Couldn't run \"" + args[0] + "\" because of the following error: " + e.getMessage());
            System.exit(-1);
        }

        ProcessBuilder player2ProcessBuilder = new ProcessBuilder(args[1].split(" "));
        player2ProcessBuilder.directory(currentWorkingDir);
        player2ProcessBuilder.redirectError(new File("player_2_error_log.txt"));
        Process agentProcess2;
        try {
            agentProcess2 = player2ProcessBuilder.start();
        } catch (IOException e) {
            agentProcess2 = null;
            System.err.println("Couldn't run \"" + args[1] + "\" because of the following error: " + e.getMessage());
            System.exit(-1);
        }

        Player playerNorth;
        Player playerSouth;
        if (GameEngine.startingSide == Side.NORTH) {
            playerNorth = new Player(1, args[0], agentProcess1, Side.NORTH);
            playerSouth = new Player(2, args[1], agentProcess2, Side.SOUTH);
        } else {
            playerSouth = new Player(1, args[0], agentProcess1, Side.SOUTH);
            playerNorth = new Player(2, args[1], agentProcess2, Side.NORTH);
        }
        playerNorth.startReaderThread();
        playerSouth.startReaderThread();
        final Board board = new Board(7, 7);
        final Kalah kalah = new Kalah(board);
        final PrintingBoardObserver observer = new PrintingBoardObserver(System.err);
        board.addObserver(observer);
        observer.update(board, null);
        final GameEngine game = new GameEngine(kalah, playerNorth, playerSouth);
        try {
            Thread.sleep(500L);
        } catch (InterruptedException ex) {
        }
        final Player abortingPlayer = game.runMatch(GameEngine.startingSide);
        game.evaluate(abortingPlayer);
        playerNorth.getReaderThread().finish();
        playerSouth.getReaderThread().finish();
        try {
            playerNorth.getReaderThread().join(100L);
        } catch (InterruptedException ex2) {
        }
        try {
            playerSouth.getReaderThread().join(100L);
        } catch (InterruptedException ex3) {
        }
        try {
            Thread.sleep(500L);
        } catch (InterruptedException ex4) {
        }
        try {
            agentProcess1.destroy();
        } catch (Exception ex5) {
        }
        try {
            agentProcess2.destroy();
        } catch (Exception ex6) {
        }
        System.exit(0);
    }

    private Player runMatch(final Side startingSide) {
        final Timer responseTimer = new Timer();
        Player abortingPlayer = null;
        boolean skipEndMessages = false;
        Label_0048:
        {
            if (startingSide == Side.NORTH) {
                final Player activePlayer = this.playerNorth;
                final Player waitingPlayer = this.playerSouth;
                break Label_0048;
            }
            Player activePlayer = this.playerSouth;
            Player waitingPlayer = this.playerNorth;
            try {
                try {
                    this.playerNorth.sendMsg(Protocol.createStartMsg(Side.NORTH));
                } catch (IOException e) {
                    abortingPlayer = this.playerNorth;
                    throw e;
                }
                try {
                    this.playerSouth.sendMsg(Protocol.createStartMsg(Side.SOUTH));
                } catch (IOException e) {
                    abortingPlayer = this.playerSouth;
                    throw e;
                }
                responseTimer.start();
                boolean gameOver = false;
                int moveCount = 1;
                while (!gameOver) {
                    final long timeout = 3600000L - activePlayer.getOverallResponseTime();
                    final String agentMessage = activePlayer.getReaderThread().recvMsg(timeout);
                    responseTimer.stop();
                    activePlayer.incrementMoveCount();
                    activePlayer.incrementOverallResponseTime(responseTimer.time());
                    responseTimer.reset();
                    final MsgType msgType = Protocol.getMessageType(agentMessage);
                    if (msgType == MsgType.SWAP && moveCount == 2) {
                        this.playerNorth.changeSide();
                        this.playerSouth.changeSide();
                        Player tmpPlayer = this.playerNorth;
                        this.playerNorth = this.playerSouth;
                        this.playerSouth = tmpPlayer;
                        tmpPlayer = activePlayer;
                        activePlayer = waitingPlayer;
                        waitingPlayer = tmpPlayer;
                        System.err.println("Move: Swap");
                        activePlayer.sendMsg(Protocol.createSwapInfoMsg(this.kalah.getBoard()));
                        responseTimer.start();
                    } else {
                        if (msgType != MsgType.MOVE) {
                            throw new InvalidMessageException("Expected a move message.");
                        }
                        final int hole = Protocol.interpretMoveMsg(agentMessage);
                        if (hole < 1) {
                            throw new InvalidMessageException("Expected a positive hole number but got " + hole + ".");
                        }
                        final Move move = new Move(activePlayer.getSide(), hole);
                        if (!this.kalah.isLegalMove(move)) {
                            throw new IllegalMoveException();
                        }
                        Side turn = this.kalah.makeMove(move);
                        if (moveCount == 1) {
                            turn = waitingPlayer.getSide();
                        }
                        gameOver = this.kalah.gameOver();
                        if (turn != activePlayer.getSide()) {
                            final Player tmpPlayer2 = activePlayer;
                            activePlayer = waitingPlayer;
                            waitingPlayer = tmpPlayer2;
                        }
                        try {
                            waitingPlayer.sendMsg(Protocol.createStateMsg(move, this.kalah.getBoard(), gameOver, false));
                        } catch (IOException e2) {
                            abortingPlayer = waitingPlayer;
                            throw e2;
                        }
                        activePlayer.sendMsg(Protocol.createStateMsg(move, this.kalah.getBoard(), gameOver, true));
                        responseTimer.start();
                    }
                    ++moveCount;
                }
            } catch (InvalidMessageException e3) {
                abortingPlayer = activePlayer;
                System.err.println("Error: Invalid message. " + e3.getMessage() + " Agent " + abortingPlayer.getName() + " does not obey the protocol.");
            } catch (IllegalMoveException e4) {
                abortingPlayer = activePlayer;
                System.err.println("Error: Agent " + abortingPlayer.getName() + " tried to perform an illegal move.");
            } catch (TimeoutException e5) {
                abortingPlayer = activePlayer;
                System.err.println("Error: Agent " + abortingPlayer.getName() + " timed out.");
            } catch (IOException e) {
                if (abortingPlayer == null) {
                    abortingPlayer = activePlayer;
                }
                System.err.println("Error: Connection to agent " + abortingPlayer.getName() + " broke down. " + e.getMessage());
                final Player sanePlayer = (abortingPlayer == this.playerNorth) ? this.playerSouth : this.playerNorth;
                try {
                    sanePlayer.sendMsg(Protocol.createEndMsg());
                } catch (IOException ex) {
                }
                skipEndMessages = true;
            }
        }
        if (!skipEndMessages) {
            final String endMessage = Protocol.createEndMsg();
            try {
                this.playerNorth.sendMsg(endMessage);
            } catch (IOException ex2) {
            }
            try {
                this.playerSouth.sendMsg(endMessage);
            } catch (IOException ex3) {
            }
        }
        return abortingPlayer;
    }

    private void evaluate(final Player abortingPlayer) {
        boolean northWon = false;
        boolean southWon = false;
        int score = 0;
        if (abortingPlayer == this.playerNorth) {
            southWon = true;
        } else if (abortingPlayer == this.playerSouth) {
            northWon = true;
        } else {
            final int seedDifference = this.kalah.getBoard().getSeedsInStore(Side.NORTH) - this.kalah.getBoard().getSeedsInStore(Side.SOUTH);
            if (seedDifference > 0) {
                northWon = true;
            } else if (seedDifference < 0) {
                southWon = true;
            }
            score = seedDifference;
            if (score < 0) {
                score = -score;
            }
        }
        System.err.println();
        if (northWon || southWon) {
            System.err.print("WINNER: Player ");
            if (northWon) {
                System.err.println(this.playerNorth.getPlayerNumber() + " (" + this.playerNorth.getName() + ")");
            } else if (southWon) {
                System.err.println(this.playerSouth.getPlayerNumber() + " (" + this.playerSouth.getName() + ")");
            }
        } else {
            System.err.println("DRAW");
        }
        if (abortingPlayer != null) {
            System.err.println("MATCH WAS ABORTED");
        } else {
            System.err.println("SCORE: " + score);
        }
        final long millisecsPerMoveSouth = (this.playerSouth.getMoveCount() == 0) ? 0L : (this.playerSouth.getOverallResponseTime() / this.playerSouth.getMoveCount());
        final long millisecsPerMoveNorth = (this.playerNorth.getMoveCount() == 0) ? 0L : (this.playerNorth.getOverallResponseTime() / this.playerNorth.getMoveCount());
        System.err.println("\nPlayer " + this.playerSouth.getPlayerNumber() + " (" + this.playerSouth.getName() + "): " + this.playerSouth.getMoveCount() + " moves, " + millisecsPerMoveSouth + " milliseconds per move");
        System.err.println("Player " + this.playerNorth.getPlayerNumber() + " (" + this.playerNorth.getName() + "): " + this.playerNorth.getMoveCount() + " moves, " + millisecsPerMoveNorth + " milliseconds per move");
        System.err.println();
        final String resultsPlayerNorth = (northWon ? "1" : "0") + " " + millisecsPerMoveNorth;
        final String resultsPlayerSouth = (southWon ? "1" : "0") + " " + millisecsPerMoveSouth;
        if (this.playerNorth.getPlayerNumber() == 1) {
            System.out.println(resultsPlayerNorth);
            System.out.println(resultsPlayerSouth);
        } else {
            System.out.println(resultsPlayerSouth);
            System.out.println(resultsPlayerNorth);
        }
    }
}
