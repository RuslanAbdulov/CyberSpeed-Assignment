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
    @SerializedName("applied_winning_combinations")
    private Map<String, List<String>> appliedWinningCombinations;
    @SerializedName("applied_bonus_symbol")
    private String appliedBonusSymbol;

}
