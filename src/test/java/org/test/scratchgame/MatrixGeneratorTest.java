package org.test.scratchgame;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MatrixGeneratorTest {

    @Test
    void generateMatrix_singleField() {
        var config = new Config()
                .setRows(1)
                .setColumns(1)
                .setProbabilities(
                        new Config.Probability()
                                .setStandard(List.of(
                                        new Config.Probability.Standard()
                                                .setRow(0)
                                                .setColumn(0)
                                                .setBySymbol(Map.of("A", 1))
                                        )

                                )
                );
        long seed = 1L;

        var matrix = new MatrixGenerator(config, seed).generate();

        var expected = new String[][]{{"A"}};
        assertTrue(Arrays.deepEquals(expected, matrix));
    }

    @Test
    void generateMatrix_noBonus() {
        var config = new Config()
                .setRows(2)
                .setColumns(2)
                .setProbabilities(
                        new Config.Probability()
                                .setStandard(List.of(
                                                new Config.Probability.Standard()
                                                        .setRow(0)
                                                        .setColumn(0)
                                                        .setBySymbol(Map.of("A", 1, "B", 1, "C", 2))
                                        )

                                )
                );
        long seed = 2L;

        var matrix = new MatrixGenerator(config, seed).generate();

        var expected = new String[][]{{"C","B"}, {"C","A"}};
        assertTrue(Arrays.deepEquals(expected, matrix));
    }

}