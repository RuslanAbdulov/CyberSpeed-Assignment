package org.test.scratchgame;

public class GameEngine {

    private final MatrixGenerator matrixGenerator;
    private final RewardCalculator rewardCalculator;

    public GameEngine(Config config) {
        this.matrixGenerator = new MatrixGenerator(config);
        this.rewardCalculator = new RewardCalculator(config);
    }

    public GameResult play(double bettingAmount) {
        final var gameMatrix = matrixGenerator.generate();
        return rewardCalculator.calculate(gameMatrix, bettingAmount);
    }

}
