package lumi.todo.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TodoUtil {

    public static String convertSnakeToCamel(String snake) {

        String pascalCase = Arrays.stream(snake.split("\\_"))
            .map(String::toLowerCase)
            .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
            .collect(Collectors.joining());

        return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
    }

    public static boolean getConfirmation(String question, Scanner scanner) {

        System.out.print(question + " [y/n] :");

        Optional<Boolean> answer = Optional.empty();

        do {

            var input = scanner.nextLine();

            if (input.toLowerCase().startsWith("y")) {

                answer = Optional.of(true);

            } else if (input.toLowerCase().startsWith("n")) {

                answer = Optional.of(false);

            } else {

                System.out.println();
                System.out.println("Please answer with \"y\" or \"n\"");
            }

        } while (answer.isEmpty());

        return answer.get();
    }
}
