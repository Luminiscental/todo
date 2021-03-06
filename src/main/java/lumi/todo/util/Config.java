package lumi.todo.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public enum Config {

    TODO_FILE(".todo/todo"),
    TIMEOUT("15"),
    LAYOUT("- $item");

    private static final String CONFIG_FILE = ".todo/config";
    public static final String HOME_DIR = System.getProperty("user.home") + "/";

    private final String variableName;
    private final String defaultValue;

    Config(String defaultValue) {

        this.variableName = TodoUtil.convertSnakeToCamel(name());
        this.defaultValue = defaultValue;
    }

    public String getValue() {

        try {

            var input = new BufferedReader(new FileReader(HOME_DIR + CONFIG_FILE));

            var lines = input.lines()
                .collect(Collectors.toList());

            input.close();

            for (var line : lines) {

                var split = line.split("=");

                if (split.length > 2) {

                    System.err.println("Invalid syntax, only one assignment allowed per line");
                    System.err.println("In \"" + line + "\"");
                }

                if (split[0].trim().equals(variableName)) {

                    if (split.length < 2) {

                        System.err.println("Invalid syntax, lines must assign to the value");
                        System.err.println("In \"" + line + "\"");
                    }

                    var value = split[1].trim();

                    for (var configVariable : Config.values()) {

                        if (value.equals(configVariable.variableName)) {

                            return configVariable.getValue();
                        }
                    }

                    return value;
                }
            }

        } catch (IOException e) {

        } catch (StackOverflowError e) {

            System.err.println("Circular assignment in config file! Using default values");
        }

        return defaultValue;
    }
}
