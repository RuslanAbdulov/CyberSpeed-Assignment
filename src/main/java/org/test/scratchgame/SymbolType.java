package org.test.scratchgame;

import com.google.gson.annotations.SerializedName;

public enum SymbolType {
    @SerializedName("standard")
    STANDARD,
    @SerializedName("bonus")
    BONUS;
}
