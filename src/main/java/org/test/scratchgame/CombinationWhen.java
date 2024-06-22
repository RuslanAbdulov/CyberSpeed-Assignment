package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;

public enum CombinationWhen {
    @SerializedName("same_symbols")
    SAME_SYMBOLS,
    @SerializedName("linear_symbols")
    LINEAR_SYMBOLS;
}
