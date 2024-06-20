package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Accessors(chain = true)
@Data
public class GameResult {
    private String[][] matrix;
    private double reward;
//    "applied_winning_combinations": {
//        "A": ["same_symbol_5_times", "same_symbols_vertically"],
//        "B": ["same_symbol_3_times", "same_symbols_vertically"]
//    },
    @SerializedName("applied_winning_combinations")
    private Map<String, List<String>> appliedWinningCombinations;
    @SerializedName("applied_bonus_symbol")
    private String appliedBonusSymbol;


}
