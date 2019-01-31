package lumi.todo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import lumi.todo.util.Config;

public class Todo {

    public static void main(String[] args) {

        String todoFileName = Config.TODO_FILE.getValue();
        String tempFileName = todoFileName + "~";

        if (args.length == 0) {

            System.err.println("Please provide a command");
            printUsage();

            System.exit(1);
            return;
        }

        TodoListAccessor accessor;

        try {

            accessor = new TodoListAccessor(todoFileName, tempFileName);

        } catch (IOException e) {

            System.err.println("Could not access todo file:");
            e.printStackTrace();

            System.exit(3);
            return;
        }

        var command = args[0];
        var params = Arrays.copyOfRange(args, 1, args.length);

        Scanner scanner = new Scanner(System.in);

        switch (command) {

        case "add":

            addCommand(accessor, params);
            break;

        case "remove":

            removeCommand(accessor, params, scanner);
            break;

        case "list":

            listCommand(accessor);
            break;

        case "do":

            doCommand(accessor, params, scanner);
            break;

        case "-f":

            // TODO: Support tab-completion maybe?
            break;

        default:

            System.err.println("Unsupported command \"" + command + "\"");
            printUsage();

            scanner.close();
            System.exit(2);
            break;
        }

        scanner.close();
    }

    public static void addCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, add expects one");
        }

        accessor.addItem(args[0]);
    }

    public static void removeCommand(TodoListAccessor accessor, String[] args, Scanner scanner) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, remove expects one");
        }

        accessor.removeItem(args[0], scanner);
    }

    public static void listCommand(TodoListAccessor accessor) {

        accessor.printItems();
    }

    public static void doCommand(TodoListAccessor accessor, String[] args, Scanner scanner) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, do expects at least one");
        }

        int minutes;

        if (args.length > 1) {

            minutes = Integer.parseInt(args[1]);

        } else {

            minutes = Integer.parseInt(Config.TIMEOUT.getValue());
        }

        accessor.doItem(args[0], minutes, scanner);
    }

    public static void printCommandUsage(String command, String description, String template, String example) {

        System.out.println();
        System.out.println("\t" + command + " - " + description);
        System.out.println("\t" + template);
        System.out.println("\tExample usage: " + example);
    }

    public static void printUsage() {

        System.out.println();
        System.out.println("Usage: todo <command> <args>");
        System.out.println("Commands:");

        printCommandUsage("add",
                          "Takes one argument and adds it as an item on the todo list",
                          "todo add <item>",
                          "todo add \"Wash the dishes\"");

        printCommandUsage("remove",
                          "Takes one argument and removes any items on the todo list matching it",
                          "todo remove <item>",
                          "todo remove wash");

        printCommandUsage("list",
                          "Lists all items in the todo list",
                          "todo list",
                          "todo list");

        printCommandUsage("do",
                          "Takes an argument item and gives you a default number of minutes (unless specified) to complete / work on the task",
                          "todo do <item> [<minutes>]",
                          "todo do wash 10");
    }
}
