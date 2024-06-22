package org.test;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.test.scratchgame.Config;
import org.test.scratchgame.GameEngine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;

public class Main {
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        if (args.length != 4) {
            exit("Wrong number of arguments");
        }

        Double bettingAmount = null;
        Config config = null;
        for (int i = 0; i + 1 < args.length; i += 2) {
            if (args[i].equalsIgnoreCase("--config")){
                String fileName = args[i + 1];
                config = readConfig(fileName);
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

        System.out.println(gson.toJson(gameResult));
    }

    private static Config readConfig(String fileName) {
        Type configType = new TypeToken<Config>(){}.getType();
        try {
            JsonReader reader = new JsonReader(new FileReader(fileName));
            return gson.fromJson(reader, configType);
        } catch (FileNotFoundException e) {
            exit("Config file %s not found".formatted(fileName));
        } catch (JsonParseException e) {
            exit("Failed to parse config file %s".formatted(fileName));
        }
        throw new IllegalStateException();
    }

    private static void exit(String message) {
        System.out.println(message);
        System.exit(1);
    }
}
