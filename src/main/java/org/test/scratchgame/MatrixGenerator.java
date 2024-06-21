package org.test.scratchgame;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MatrixGenerator {
    private final Random random;
    private final Config config;

    private boolean bonusPlaced = false;

    public MatrixGenerator(Config config) {
        this.config = config;
        this.random = new Random();
    }

    //construct generator producing repeatable results
    public MatrixGenerator(Config config, long randomSeed) {
        this.config = config;
        this.random = new Random(randomSeed);
    }

    public String[][] generate() {
        String[][] matrix = new String[config.getRows()][config.getColumns()];
        for (int row = 0; row < config.getRows(); row++) {
            for (int column = 0; column < config.getColumns(); column++) {
                matrix[row][column] = generateRandomSymbol(row, column);
            }
        }
        return matrix;
    }

    //based on output format I assume that only one bonus symbol is allowed
    private String generateRandomSymbol(int row, int column) {
        final var standardProbabilities = getCellStandardProbabilities(row, column);
        if (bonusPlaced || config.getProbabilities().getBonus() == null) {
            return generateRandomSymbol(standardProbabilities);
        }

        final var bonusProbabilities = config.getProbabilities().getBonus().getBySymbol();
        final var unitedProbabilities = new HashMap<>(standardProbabilities);
        unitedProbabilities.putAll(bonusProbabilities);
        final var generatedSymbol = generateRandomSymbol(unitedProbabilities);
        if (bonusProbabilities.containsKey(generatedSymbol)) {
            bonusPlaced = true;
        }
        return generatedSymbol;
    }

    private String generateRandomSymbol(Map<String, Integer> probabilities) {
        final var weightedDistribution = new HashMap<Integer, String>();
        int totalWeight = 0;
        for (Map.Entry<String, Integer> probabilityEntry : probabilities.entrySet()) {
            for (int i = 0; i < probabilityEntry.getValue(); i++) {
                weightedDistribution.put(totalWeight, probabilityEntry.getKey());
                totalWeight++;
            }
        }
        var nextInt = random.nextInt(totalWeight);
        return weightedDistribution.get(nextInt);
    }

    private Map<String, Integer> getCellStandardProbabilities(int row, int column) {
        return config.getProbabilities().getStandard().stream()
                .filter(it -> it.getRow() == row && it.getColumn() == column)
                .map(Config.Probability.Standard::getBySymbol)
                .findFirst()
                .orElseGet(this::getStandardFallbackProbabilities);
    }

    private Map<String, Integer> getStandardFallbackProbabilities() {
        return config.getProbabilities().getStandard().stream()
                .findFirst()
                .map(Config.Probability.Standard::getBySymbol)
                .orElseThrow();
    }

}
