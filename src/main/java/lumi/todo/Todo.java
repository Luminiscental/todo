package lumi.todo;

import java.io.IOException;
import java.util.Scanner;

import lumi.todo.util.Input;
import lumi.todo.util.MissingCommandException;
import lumi.todo.util.Config;

public class Todo {

    public static void main(String[] args) {

        if (args.length == 0) {

            System.err.println("Please provide a command");
            printUsage();

            System.exit(1);
            return;
        }

        Input input;

        try {

            input = new Input(args);

        } catch (MissingCommandException e) {

            System.err.println("Please provide a command");
            printUsage();

            System.exit(1);
            return;
        }

        String todoFileName = Config.TODO_FILE.getValue();

        if (!input.hasOption("local")) {

            todoFileName = Config.HOME_DIR + todoFileName;
        }

        String tempFileName = todoFileName + "~";

        if (!input.validate()) {

            System.err.println("Aborting!");
            System.exit(1);
            return;
        }

        TodoListAccessor accessor;

        try {

            accessor = new TodoListAccessor(todoFileName, tempFileName);

        } catch (IOException e) {

            System.err.println("Could not access todo file:");
            e.printStackTrace();

            System.exit(1);
            return;
        }

        Scanner scanner = new Scanner(System.in);

        switch (input.getCommand()) {

        case "add":

            addCommand(accessor, input);
            break;

        case "remove":

            removeCommand(accessor, input, scanner);
            break;

        case "replace":

            replaceCommand(accessor, input, scanner);
            break;

        case "list":

            listCommand(accessor);
            break;

        case "do":

            doCommand(accessor, input, scanner);
            break;

        case "-f":

            break;

        default:

            System.err.println("Unsupported command \"" + input.getCommand() + "\"");
            printUsage();

            System.exit(1);
            break;
        }

        try {

            accessor.close();

        } catch (IOException e) {

            System.err.println("Could not write to todo file:");
            e.printStackTrace();

            System.exit(1);
            return;
        }
    }

    private static void addCommand(TodoListAccessor accessor, Input args) {

        if (args.getArguments().size() < 1) {

            System.err.println("Not enough arguments, add expects one");
        }

        accessor.addItem(args.getArguments().get(0));
    }

    private static void removeCommand(TodoListAccessor accessor, Input args, Scanner scanner) {

        if (args.getArguments().size() < 1) {

            System.err.println("Not enough arguments, remove expects one");
        }

        accessor.removeItem(args.getArguments().get(0), scanner);
    }

    private static void replaceCommand(TodoListAccessor accessor, Input args, Scanner scanner) {

        if (args.getArguments().size() < 2) {

            System.err.println("Not enough arguments, replace expects two");
        }

        var pattern = args.getArguments().get(0);
        var replacement = args.getArguments().get(1);
        accessor.replaceItem(pattern, replacement, scanner);
    }

    private static void listCommand(TodoListAccessor accessor) {

        accessor.printItems();
    }

    private static void doCommand(TodoListAccessor accessor, Input args, Scanner scanner) {

        var argCount = args.getArguments().size();
        if (argCount < 1) {

            System.err.println("Not enough arguments, do expects at least one");
        }

        int minutes;

        if (argCount > 1) {

            minutes = Integer.parseInt(args.getArguments().get(1));

        } else {

            minutes = Integer.parseInt(Config.TIMEOUT.getValue());
        }

        accessor.doItem(args.getArguments().get(0), minutes, scanner);
    }

    private static void printCommandUsage(String command, String description, String template, String example) {

        System.out.println();
        System.out.println("\t" + command + " - " + description);
        System.out.println("\t" + template);
        System.out.println("\tExample usage: " + example);
    }

    private static void printUsage() {

        System.out.println();
        System.out.println("Usage: todo <command> <args> [<options>]");

        System.out.println("Options:");
        System.out.println();
        System.out.println("\t--local - If set the list is read from the todo file path relative to the current directory instead of relative to your home directory.");

        System.out.println("Commands:");

        printCommandUsage("add",
                          "Takes one argument and adds it as an item on the todo list",
                          "todo add <item>",
                          "todo add \"Wash the dishes\"");

        printCommandUsage("remove",
                          "Takes one argument and removes any items on the todo list matching it",
                          "todo remove <item>",
                          "todo remove wash");

        printCommandUsage("replace",
                          "Takes two arguments; the start of an item to replace followed by what to replace it with",
                          "todo replace <item> <replacement>",
                          "todo replace wash \"Dry the dishes\"");

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
