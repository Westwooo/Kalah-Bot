package MKAgent.heuristics;

import MKAgent.gameplay.Board;

public class QuestionableHeuristic extends Heuristic {

    @Override
    public double evaluate(Board board) {
        HoardSeedsInRightMostPit h1 = new HoardSeedsInRightMostPit();
        KeepMovesOnMySideHeuristic h2 = new KeepMovesOnMySideHeuristic();
        HaveManyMovesHeuristic h3 = new HaveManyMovesHeuristic();
        MaximiseSeedsInStoreHeuristic h4 = new MaximiseSeedsInStoreHeuristic();

        MinimiseOpponentSeeds h6 = new MinimiseOpponentSeeds();

        double h1Score = h1.evaluate(board);
        double h2Score = h2.evaluate(board);
        double h3Score = h3.evaluate(board);
        double h4Score = h4.evaluate(board);
        double h6Score = h6.evaluate(board);

        double h1Weight = 0.198649;
        double h2Weight = 0.190084;
        double h3Weight = 0.370793;
        double h4Weight = 0.198649;
        double h6Weight = 0.565937;

        return (h1Weight * h1Score) +
                (h2Weight * h2Score) +
                (h3Weight * h3Score) +
                (h4Weight * h4Score) +
                (h6Weight * h6Score);
    }

}
