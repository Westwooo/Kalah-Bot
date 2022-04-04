package MKAgent.heuristics;

import MKAgent.gameplay.Board;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Heuristic {

    private static final ConcurrentHashMap<Integer, Double> heuristicScoreCache = new ConcurrentHashMap<>();

    /**
     * Returns board evaluation score, estimated using heuristics using cached
     * values if available and caching the result if not.
     *
     * @return double estimating who is winning i.e. positive values mean South is winning,
     * and negative values mean North is winning. The greater the absolute
     * value of the number, the more that side is estimated to be winning
     * by.
     */
    public final double evaluateWithCaching(Board board) {
        Double score = heuristicScoreCache.get(board.hashCode());
        if (score == null) {
            score = evaluate(board);
            heuristicScoreCache.put(board.hashCode(), score);
        }
        return score;
    }

    /**
     * Returns board evaluation score, estimated using heuristics.
     *
     * @return double estimating who is winning i.e. positive values mean South is winning,
     * and negative values mean North is winning. The greater the absolute
     * value of the number, the more that side is estimated to be winning
     * by.
     */
    public abstract double evaluate(Board board);

}
