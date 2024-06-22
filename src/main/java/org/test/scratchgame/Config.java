package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class Config {
    private Integer columns;
    private Integer rows;
    private Map<String, Symbol> symbols;
    private Probability probabilities;
    @SerializedName("win_combinations")
    private Map<String, WinCombination> winCombinations;

    @Data
    @Accessors(chain = true)
    public static class Symbol {
        private String symbol;
        @SerializedName("reward_multiplier")
        private double rewardMultiplier;
        private SymbolType type;
        private SymbolImpact impact;
        private double extra;
    }

    @Data
    @Accessors(chain = true)
    public static class Probability {
        @SerializedName("standard_symbols")
        private List<Standard> standard;

        @SerializedName("bonus_symbols")
        private Bonus bonus;

        @Data
        @Accessors(chain = true)
        public static class Standard {
            private int column;
            private int row;
            @SerializedName("symbols")
            private Map<String, Integer> bySymbol;
        }

        @Data
        @Accessors(chain = true)
        public static class Bonus {
            @SerializedName("symbols")
            private Map<String, Integer> bySymbol;
        }

    }

    @Data
    @Accessors(chain = true)
    public static class WinCombination {
        @SerializedName("reward_multiplier")
        private double rewardMultiplier;
        private CombinationWhen when;
        private Integer count;
        private String group;
        @SerializedName("covered_areas")
        private String[][] coveredAreas;
    }

    public Set<String> getStandardSymbols() {
        return getSymbols().entrySet().stream()
                .filter(entry -> entry.getValue().getType() == SymbolType.STANDARD)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<String> getBonusSymbols() {
        return getSymbols().entrySet().stream()
                .filter(entry -> entry.getValue().getType() == SymbolType.BONUS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public Set<String> getSameSymbolsWinCombinations() {
        return getWinCombinations().entrySet().stream()
                .filter(entry -> entry.getValue().getWhen() == CombinationWhen.SAME_SYMBOLS)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

}
