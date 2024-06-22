package org.test.scratchgame;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


class RewardCalculatorTest {

    @Test
    void calculate_2SymbolsWin() {
        String[][] gameMatrix = new String[][] {
                {"A", "A", "B"},
                {"A", "+1000", "B"},
                {"A", "A", "B"}};

        var gameResult = new RewardCalculator(prepareConfig()).calculate(gameMatrix, 100);

        assertEquals(6600, gameResult.getReward());
        assertEquals("+1000", gameResult.getAppliedBonusSymbol());
        assertEquals(2, gameResult.getAppliedWinningCombinations().size());

        var combinationA = gameResult.getAppliedWinningCombinations().get("A");
        assertNotNull(combinationA);
        assertEquals(2, combinationA.size());
        assertTrue(combinationA.contains("same_symbol_5_times"));
        assertTrue(combinationA.contains("same_symbols_vertically"));

        var combinationB = gameResult.getAppliedWinningCombinations().get("B");
        assertEquals(2, combinationB.size());
        assertTrue(combinationB.contains("same_symbol_3_times"));
        assertTrue(combinationB.contains("same_symbols_vertically"));
    }

    @Test
    void calculate_miss() {
        String[][] gameMatrix = new String[][] {
                {"A", "A", "B"},
                {"A", "B", "B"},
                {"A", "MISS", "B"}};

        var gameResult = new RewardCalculator(prepareConfig()).calculate(gameMatrix, 100);

        assertEquals(0, gameResult.getReward());
        assertEquals("MISS", gameResult.getAppliedBonusSymbol());
        assertEquals(2, gameResult.getAppliedWinningCombinations().size());

        var combinationA = gameResult.getAppliedWinningCombinations().get("A");
        assertNotNull(combinationA);
        assertEquals(2, combinationA.size());
        assertTrue(combinationA.contains("same_symbol_4_times"));
        assertTrue(combinationA.contains("same_symbols_vertically"));

        var combinationB = gameResult.getAppliedWinningCombinations().get("B");
        assertEquals(2, combinationB.size());
        assertTrue(combinationB.contains("same_symbol_4_times"));
        assertTrue(combinationB.contains("same_symbols_vertically"));
    }

    @Test
    void calculate_1Symbol3Combinations() {
        String[][] gameMatrix = new String[][] {
                {"A", "A", "A"},
                {"A", "C", "C"},
                {"A", "B", "B"}};

        var gameResult = new RewardCalculator(prepareConfig()).calculate(gameMatrix, 100);

        assertEquals(10000, gameResult.getReward());
        assertNull(gameResult.getAppliedBonusSymbol());
        assertEquals(1, gameResult.getAppliedWinningCombinations().size());

        var combinations = gameResult.getAppliedWinningCombinations().get("A");
        assertNotNull(combinations);
        assertEquals(3, combinations.size());
        assertTrue(combinations.contains("same_symbol_5_times"));
        assertTrue(combinations.contains("same_symbols_vertically"));
        assertTrue(combinations.contains("same_symbols_horizontally"));
    }

    @Test
    void calculate_reduceSameGroupCombinations() {
        String[][] gameMatrix = new String[][] {
                {"A", "A", "A"},
                {"A", "C", "C"},
                {"A", "B", "B"}};

        var config = prepareConfig();
        config.getWinCombinations()
                .get("same_symbols_horizontally")
                .setGroup("linear_symbols");
        config.getWinCombinations()
                .get("same_symbols_vertically")
                .setGroup("linear_symbols");

        var gameResult = new RewardCalculator(config).calculate(gameMatrix, 100);

        assertEquals(5000, gameResult.getReward());
        assertNull(gameResult.getAppliedBonusSymbol());
        assertEquals(1, gameResult.getAppliedWinningCombinations().size());

        var combinations = gameResult.getAppliedWinningCombinations().get("A");
        assertNotNull(combinations);
        assertEquals(2, combinations.size());
        assertTrue(combinations.contains("same_symbol_5_times"));
        assertTrue(combinations.contains("same_symbols_vertically") || combinations.contains("same_symbols_horizontally"));
    }

    @Test
    void calculate_sameSymbolsCountMoreThanMaxCombination() {
        String[][] gameMatrix = new String[][] {
                {"A", "A", "A"},
                {"A", "A", "C"},
                {"A", "B", "B"}};

        var config = prepareConfig();

        var gameResult = new RewardCalculator(config).calculate(gameMatrix, 100);

        assertEquals(50000, gameResult.getReward());
        assertNull(gameResult.getAppliedBonusSymbol());
        assertEquals(1, gameResult.getAppliedWinningCombinations().size());

        var combinations = gameResult.getAppliedWinningCombinations().get("A");
        assertNotNull(combinations);
        assertEquals(4, combinations.size());
        assertTrue(combinations.contains("same_symbol_5_times"));
        assertFalse(combinations.contains("same_symbol_4_times"));
        assertFalse(combinations.contains("same_symbol_3_times"));
    }

    private Config prepareConfig() {
        var symbols = new HashMap<String, Config.Symbol>();
        symbols.put("A", new Config.Symbol()
                .setRewardMultiplier(5)
                .setType(SymbolType.STANDARD));
        symbols.put("B", new Config.Symbol()
                .setRewardMultiplier(3)
                .setType(SymbolType.STANDARD));
        symbols.put("C", new Config.Symbol()
                .setRewardMultiplier(2.5)
                .setType(SymbolType.STANDARD));
        symbols.put("+1000", new Config.Symbol()
                .setExtra(1000)
                .setType(SymbolType.BONUS)
                .setImpact(SymbolImpact.EXTRA_BONUS));
        symbols.put("5x", new Config.Symbol()
                .setRewardMultiplier(5)
                .setType(SymbolType.BONUS)
                .setImpact(SymbolImpact.MULTIPLY_REWARD));
        symbols.put("MISS", new Config.Symbol()
                .setType(SymbolType.BONUS)
                .setImpact(SymbolImpact.MISS));

        var winCombinations = new HashMap<String, Config.WinCombination>();
        winCombinations.put("same_symbol_3_times", new Config.WinCombination()
                .setRewardMultiplier(1)
                .setWhen(CombinationWhen.SAME_SYMBOLS)
                .setCount(3)
                .setGroup("same_symbols"));
        winCombinations.put("same_symbol_4_times", new Config.WinCombination()
                .setRewardMultiplier(1.5)
                .setWhen(CombinationWhen.SAME_SYMBOLS)
                .setCount(4)
                .setGroup("same_symbols"));
        winCombinations.put("same_symbol_5_times", new Config.WinCombination()
                .setRewardMultiplier(5)
                .setWhen(CombinationWhen.SAME_SYMBOLS)
                .setCount(5)
                .setGroup("same_symbols"));

        winCombinations.put("same_symbols_horizontally", new Config.WinCombination()
                .setRewardMultiplier(2)
                .setWhen(CombinationWhen.LINEAR_SYMBOLS)
                .setGroup("horizontally_linear_symbols")
                .setCoveredAreas(new String[][]{
                        {"0:0", "0:1", "0:2"},
                        {"1:0", "1:1", "1:2"},
                        {"2:0", "2:1", "2:2"}}));
        winCombinations.put("same_symbols_vertically", new Config.WinCombination()
                .setRewardMultiplier(2)
                .setWhen(CombinationWhen.LINEAR_SYMBOLS)
                .setGroup("vertically_linear_symbols")
                .setCoveredAreas(new String[][]{
                        {"0:0", "1:0", "2:0"},
                        {"0:1", "1:1", "2:1"},
                        {"0:2", "1:2", "2:2"}}));
        winCombinations.put("same_symbols_diagonally_left_to_right", new Config.WinCombination()
                .setRewardMultiplier(5)
                .setWhen(CombinationWhen.LINEAR_SYMBOLS)
                .setGroup("ltr_diagonally_linear_symbols")
                .setCoveredAreas(new String[][]{
                        {"0:0", "1:1", "2:2"}}));
        winCombinations.put("same_symbols_diagonally_right_to_left", new Config.WinCombination()
                .setRewardMultiplier(5)
                .setWhen(CombinationWhen.LINEAR_SYMBOLS)
                .setGroup("rtl_diagonally_linear_symbols")
                .setCoveredAreas(new String[][]{
                        {"0:2", "1:1", "2:0"}}));

        return new Config()
                .setSymbols(symbols)
                .setWinCombinations(winCombinations);
    }

}