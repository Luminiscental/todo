package lumi.todo;

import java.util.Arrays;

public class Todo {

    // TODO: Use a config file to customize this
    public static final String TODO_FILE_NAME = "todo_list_test";
    public static final String TEMP_FILE_NAME = TODO_FILE_NAME + "~";

    public static void main(String[] args) {

        if (args.length == 0) {

            System.err.println("Please provide a command");
            printUsage();

            System.exit(1);
        }

        // Leave space after the command
        System.out.println();

        var accessor = new TodoListAccessor(TODO_FILE_NAME, TEMP_FILE_NAME);

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

        System.exit(0);
    }

    public static void removeCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, remove expects one");
        }

        accessor.removeItem(args[0]);

        System.exit(0);
    }

    public static void listCommand(TodoListAccessor accessor) {

        accessor.printItems();
        System.exit(0);
    }

    public static void doCommand(TodoListAccessor accessor, String[] args) {

        if (args.length < 1) {

            System.err.println("Not enough arguments, do expects at least one");
        }

        int minutes;

        if (args.length > 1) {

            minutes = Integer.parseInt(args[1]);

        } else {

            minutes = 15;
        }

        accessor.doItem(args[0], minutes);

        System.exit(0);
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
