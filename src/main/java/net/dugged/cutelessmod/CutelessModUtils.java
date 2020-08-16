package net.dugged.cutelessmod;

import net.minecraft.util.text.TextFormatting;

public class CutelessModUtils {
    public static TextFormatting returnColourForMSPT(final int mspt) {
        if (mspt <= 40) {
            return TextFormatting.GREEN;
        } else if (mspt <= 45) {
            return TextFormatting.YELLOW;
        } else if (mspt <= 50) {
            return TextFormatting.GOLD;
        } else {
            return TextFormatting.RED;
        }
    }

    public static TextFormatting returnColourForTPS(final int tps) {
        if (tps >= 20) {
            return TextFormatting.GREEN;
        } else if (tps >= 15) {
            return TextFormatting.GOLD;
        } else {
            return TextFormatting.RED;
        }
    }
}
