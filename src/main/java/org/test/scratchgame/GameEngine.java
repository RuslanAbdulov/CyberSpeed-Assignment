package org.test.scratchgame;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameEngine {
    private final Config config;

    public GameResult play(double bettingAmount) {
        final var matrix = new MatrixGenerator(config).generate();

        return new GameResult()
                .setMatrix(matrix);
               // TODO set the rest
    }

}
