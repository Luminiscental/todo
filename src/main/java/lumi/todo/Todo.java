package lumi.todo;

import java.io.IOException;
import java.util.Arrays;

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

        switch (command) {

        case "add":

            addCommand(accessor, params);
            break;

        case "remove":

            removeCommand(accessor, params);
            break;

        case "list":

            listCommand(accessor);
            break;

        case "do":

            doCommand(accessor, params);
            break;

        default:

            System.err.println("Unsupported command \"" + command + "\"");
            printUsage();

            System.exit(2);
            break;
        }
    }

    public static void addCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, add expects one");
        }

        accessor.addItem(args[0]);
    }

    public static void removeCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, remove expects one");
        }

        accessor.removeItem(args[0]);
    }

    public static void listCommand(TodoListAccessor accessor) {

        accessor.printItems();
    }

    public static void doCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, do expects at least one");
        }

        int minutes;

        if (args.length > 1) {

            minutes = Integer.parseInt(args[1]);

        } else {

            minutes = Integer.parseInt(Config.TIMEOUT.getValue());
        }

        accessor.doItem(args[0], minutes);
    }

    public static void printUsage() {

        System.out.println("Usage: todo <command> <args>");
        System.out.println("Commands:");

        System.out.println("\tadd - Takes one argument and adds it as an item on the todo list");
        System.out.println("\tremove - Takes one argument and removes any items on the todo list that start with it (case insensitive)");
        System.out.println("\tlist - Lists all items in the todo list");
        System.out.println("\tdo - Takes one argument and gives you 15 minutes (or another number of minutes specified as  a second argument) to complete or work on the task");
    }
}
