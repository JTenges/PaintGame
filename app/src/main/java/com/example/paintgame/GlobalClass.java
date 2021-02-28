package com.example.paintgame;

import android.app.Application;

public class GlobalClass extends Application {
    private static String cheatCode = "5363";
    private static String code = "";
    private static boolean cheatMode = false;

    public static void addToCode(String s) {
        code += s;
        if (code.equals(cheatCode)) {
            cheatMode = true;
        }
    }

    public static boolean isCheatMode() {
        return cheatMode;
    }
}
