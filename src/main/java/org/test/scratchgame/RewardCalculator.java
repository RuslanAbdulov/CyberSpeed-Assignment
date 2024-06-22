package org.test.scratchgame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RewardCalculator {
    private final Config config;

    public RewardCalculator(Config config) {
        this.config = config;
    }

    public GameResult calculate(String[][] gameMatrix, double bettingAmount) {
        //optimization to scan gameMatrix only once for all same symbol combinations
        var symbolCount = countSymbols(gameMatrix);

        var appliedWinningCombinations = new HashMap<String, List<String>>();
        config.getWinCombinations().forEach((key, value) -> {
            var appliedToSymbols = appliedToSymbols(value, gameMatrix, symbolCount);
            appliedToSymbols.forEach(
                    symbol -> appliedWinningCombinations.merge(symbol, List.of(key), (v1, v2) -> {
                        var combinations = new ArrayList<String>();
                        combinations.addAll(v1);
                        combinations.addAll(v2);
                        return combinations;
                    })
            );
        });

        appliedWinningCombinations.replaceAll(
                (key, value) -> leaveHighestCombinationPerGroup(value));

        var appliedBonusSymbol = symbolCount.keySet().stream()
                .filter(symbol -> config.getBonusSymbols().contains(symbol))
                .findFirst().orElse(null);

        var reward = calcReward(appliedWinningCombinations, appliedBonusSymbol, bettingAmount);

        return new GameResult()
                .setMatrix(gameMatrix)
                .setReward(reward)
                .setAppliedWinningCombinations(appliedWinningCombinations)
                .setAppliedBonusSymbol(appliedBonusSymbol);
    }

    private double calcReward(Map<String, List<String>> appliedWinningCombinations,
                              String bonusSymbol,
                              double bettingAmount) {
        var reward = 0.0;
        for (Map.Entry<String, List<String>> entry : appliedWinningCombinations.entrySet()) {
            var symbolMultiplier = config.getSymbols().get(entry.getKey()).getRewardMultiplier();
            var combinationsMultiplier = entry.getValue().stream()
                    .map(combination -> config.getWinCombinations().get(combination).getRewardMultiplier())
                    .reduce((a, b) -> a * b)
                    .orElse(1.0);
            var symbolTotal = bettingAmount * symbolMultiplier * combinationsMultiplier;
            reward += symbolTotal;
        }

        reward = applyBonus(bonusSymbol, reward);

        return reward;
    }

    private double applyBonus(String appliedBonusSymbol, double reward) {
        if (appliedBonusSymbol == null) {
            return reward;
        }
        double finalReward = reward;
        var bonusSymbol = config.getSymbols().get(appliedBonusSymbol);
        switch (bonusSymbol.getImpact()) {
            case MISS -> finalReward = 0.0;
            case EXTRA_BONUS -> finalReward += bonusSymbol.getExtra();
            case MULTIPLY_REWARD -> finalReward *= bonusSymbol.getRewardMultiplier();
            default -> {}
        }
        return finalReward;
    }

    private List<String> leaveHighestCombinationPerGroup(List<String> winCombinations) {
        var groupedWinCombinations = winCombinations.stream()
                .collect(Collectors.groupingBy(combination ->
                        config.getWinCombinations().get(combination).getGroup()));
        groupedWinCombinations.values().forEach(value ->
                value.sort(Comparator.comparing(combination ->
                                config.getWinCombinations().get(combination).getRewardMultiplier())
                        .reversed()));

        return groupedWinCombinations.values().stream()
                .map(combinations -> combinations.stream().findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<String> appliedToSymbols(Config.WinCombination winCombination,
                                          String[][] gameMatrix,
                                          Map<String, Integer> symbolCount) {
        if (winCombination.getWhen() == CombinationWhen.SAME_SYMBOLS) {
            return getSameSymbolCombinationAppliedToSymbols(winCombination, symbolCount);
        }
        if (winCombination.getWhen() == CombinationWhen.LINEAR_SYMBOLS) {
            return getLinearCombinationAppliedToSymbols(winCombination, gameMatrix);
        }
        return List.of();
    }

    private static List<String> getSameSymbolCombinationAppliedToSymbols(Config.WinCombination winCombination,
                                                                         Map<String, Integer> symbolCount) {
        return symbolCount.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(winCombination.getCount()) > 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private ArrayList<String> getLinearCombinationAppliedToSymbols(
            Config.WinCombination winCombination, String[][] gameMatrix) {
        var appliedToSymbols = new ArrayList<String>();
        for (String[] fieldLine : winCombination.getCoveredAreas()) {
            var symbolsInLine = new HashSet<String>();
            for (String field : fieldLine) {
                var position = field.split(":");
                var row = Integer.parseInt(position[0]);
                var col = Integer.parseInt(position[1]);
                symbolsInLine.add(gameMatrix[row][col]);
            }
            if (symbolsInLine.size() == 1 && config.getStandardSymbols().containsAll(symbolsInLine)) {
                appliedToSymbols.addAll(symbolsInLine);
            }
        }
        return appliedToSymbols;
    }

    private Map<String, Integer> countSymbols(String[][] gameMatrix) {
        var symbolCount = new HashMap<String, Integer>();
        for (int row = 0; row < gameMatrix.length; row++) {
            for (int col = 0; col < gameMatrix[row].length; col++) {
                symbolCount.merge(gameMatrix[row][col], 1, Integer::sum);
            }
        }
        return symbolCount;
    }

}
