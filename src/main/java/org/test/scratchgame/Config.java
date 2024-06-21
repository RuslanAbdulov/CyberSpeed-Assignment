package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Config {
    private Integer columns; //optional
    private Integer rows; //optional
    private Map<String, Symbol> symbols;
    private Probability probabilities;

    @Data
    @Accessors(chain = true)
    public static class Symbol {
        private String symbol;
        @SerializedName("reward_multiplier")
        private double rewardMultiplier;
        private SymbolType type; //standard, bonus
        private SymbolImpact impact; //multiply_reward, extra_bonus
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



}
