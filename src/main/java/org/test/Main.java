package org.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.test.scratchgame.Config;
import org.test.scratchgame.GameEngine;
import org.test.scratchgame.GameResult;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

public class Main {
    public static void main(String[] args) {
//        --config config.json --betting-amount 100
        if (args.length != 4) {
            exit("Wrong number of arguments");
        }

        Double bettingAmount = null;
        Config config = null;
        for (int i = 0; i + 1 < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("--config")){
                //TODO throw exception or exit
                String fileName = args[i + 1];
                Gson gson = new Gson();
                JsonReader reader = null;
                try {
                    reader = new JsonReader(new FileReader(fileName));
                } catch (FileNotFoundException e) {
                    exit("Config file %s not found".formatted(fileName));
                }
                Type configType = new TypeToken<Config>() {}.getType();
                config = gson.fromJson(reader, configType);
            }

            if (args[i].equalsIgnoreCase("--betting-amount")){
                String bettingAmountRaw = args[i + 1];
                bettingAmount = Double.parseDouble(bettingAmountRaw);
            }

        }

        if (bettingAmount == null || config == null) {
            exit("Use parameters in the format --config config.json --betting-amount 100");
        }

        var gameEngine = new GameEngine(config);
        var gameResult = gameEngine.play(bettingAmount);

        System.out.println(gameResult);
    }

    private static void exit(String message) {
        System.out.println(message);
        System.exit(1);
    }
}





























