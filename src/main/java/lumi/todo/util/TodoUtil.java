package lumi.todo.util;

import java.util.Optional;
import java.util.Scanner;

public class TodoUtil {

    public static boolean getConfirmation(String question) {

        System.out.print(question + " [y/n] :");

        Scanner scanner = new Scanner(System.in);
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

        scanner.close();
        return answer.get();
    }
}
