package lumi.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lumi.todo.util.TodoUtil;

public class TodoTask implements Callable<Boolean> {

    private TodoTask() {}

    public static boolean work(String taskDescription, int minutes) throws InterruptedException {

        System.out.println("Go and do \"" + taskDescription + "\" (" + minutes + " minutes)");
        System.out.print("Type \"f\" if you finish early or \"i\" if you must leave it incomplete and stop early :");

        boolean completed = false;
        ExecutorService ex = Executors.newSingleThreadExecutor();

        try {

            Future<Boolean> result = ex.submit(new TodoTask());

            try {

                completed = result.get(minutes, TimeUnit.MINUTES);

            } catch (ExecutionException e) {

                System.err.println("Error waiting for task:");
                e.printStackTrace();

            } catch (TimeoutException e) {

                System.out.println();
                System.out.println("Time up:");
                completed = TodoUtil.getConfirmation("Did you complete the task?");
                result.cancel(true);
            }

        } finally {

            ex.shutdownNow();
        }

        return completed;
    }

    @Override
    public Boolean call() throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean completed;
        String input;

        do {

            try {

                while (!br.ready()) {

                    Thread.sleep(200);
                }

                Optional<Boolean> result = Optional.empty();

                do {

                    input = br.readLine();

                    if (input.toLowerCase().startsWith("f")) {

                        result = Optional.of(true);

                    } else if (input.toLowerCase().startsWith("i")) {

                        result = Optional.of(false);

                    } else {

                        System.out.println("Please type either \"f\" or \"i\"");
                    }

                } while (result.isEmpty());

                completed = result.get();

            } catch (InterruptedException e) {

                return false;
            }

        } while ("".equals(input));

        return completed;
    }
}
