package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;

public enum SymbolImpact {
    @SerializedName("multiply_reward")
    MULTIPLY_REWARD,
    @SerializedName("extra_bonus")
    EXTRA_BONUS,
    @SerializedName("miss")
    MISS;

}
