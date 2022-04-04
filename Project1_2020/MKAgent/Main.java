package MKAgent;

import MKAgent.players.Bot;
import MKAgent.players.JackBot;
import MKAgent.players.SafianBot;

import java.io.*;

/**
 * The main application class. It also provides methods for communication
 * with the game engine.
 */
public class Main {

    /**
     * Input from the game engine.
     */
    private static final Reader input = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Sends a message to the game engine.
     *
     * @param msg The message.
     */
    public static void sendMsg(String msg) {
        System.out.print(msg);
        System.out.flush();
    }

    /**
     * Receives a message from the game engine. Messages are terminated by
     * a '\n' character.
     *
     * @return The message.
     * @throws IOException if there has been an I/O error.
     */
    public static String recvMsg() throws IOException {
        StringBuilder message = new StringBuilder();
        int newCharacter;

        do {
            newCharacter = input.read();
            if (newCharacter == -1)
                throw new EOFException("Input ended unexpectedly.");
            message.append((char) newCharacter);
        } while ((char) newCharacter != '\n');

        return message.toString();
    }

    /**
     * The main method, invoked when the program is started.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) throws Exception {
        // Set some defaults in case no args are given.
        int searchDepth = 14;
        String botToUse = "jack";

        // If args are given then use them.
        if (args.length == 2) {
            searchDepth = Integer.parseInt(args[0]);
            botToUse = args[1];
        }

        // Prepare bot to be used.
        Bot bot;
        switch (botToUse.toLowerCase()) {
            case "saf":
            case "safian":
            case "safianbot":
                bot = new SafianBot(7, 7, searchDepth);
                break;
            case "jack":
            case "jackbot":
            default:
                bot = new JackBot(7, 7, searchDepth);
        }

        // Start playing.
        while (!bot.isGameOver()) {
            String msg = recvMsg();
            bot.processMessage(msg);
            if (bot.isNextTurn())
                sendMsg(bot.getNextMove());
        }

        // Do we need to do anything here?
    }
}
