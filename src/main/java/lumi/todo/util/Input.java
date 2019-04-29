package lumi.todo.util;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Input {

    private String command;
    private List<String> options;
    private List<String> arguments;

    private static final List<String> validOptions = List.of("local");

    public Input(String[] argArray) {

        var rawArgs = List.of(argArray);

        Predicate<String> optionPred = arg -> arg.startsWith("--");

        options = rawArgs.stream()
            .filter(optionPred)
            .map(option -> option.substring(2))
            .collect(Collectors.toList());

        arguments = rawArgs.stream()
            .filter(Predicate.not(optionPred))
            .collect(Collectors.toList());

        command = arguments.get(0);
        arguments.remove(0);
    }

    public boolean isValid() {

        return options.stream()
            .anyMatch(Predicate.not(option -> validOptions.contains(option)));
    }

    public boolean validate() {

        for (String option : options) {

            if (!validOptions.contains(option)) {

                System.err.println("Unrecognized option\"" + option + "\"!");
                return false;
            }
        }

        return true;
    }

    public boolean hasOption(String option) {

        return options.contains(option);
    }

    public String getCommand() {

        return command;
    }

    public List<String> getOptions() {

        return options;
    }

    public List<String> getArguments() {

        return arguments;
    }

    public String getArgument(int index) {

        return arguments.get(index);
    }
}
