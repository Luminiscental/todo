package lumi.todo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import lumi.todo.util.Config;
import lumi.todo.util.TodoUtil;

public class TodoListAccessor {

    private final String todoFileName;
    private final String tempFileName;

    private List<String> items;

    TodoListAccessor(String todoFileName, String tempFileName) throws IOException {

        this.todoFileName = todoFileName;
        this.tempFileName = tempFileName;

        File todoList = new File(Config.HOME_DIR + todoFileName);

        if (!todoList.isFile()) {

            todoList.getParentFile().mkdirs();
            todoList.createNewFile();
        }

        items = getItems();
    }

    private List<String> getItems() throws IOException {

        var input = new BufferedReader(new FileReader(Config.HOME_DIR + todoFileName));

        List<String> result = input.lines()
            .collect(Collectors.toList());

        input.close();

        return result;
    }

    void close() throws IOException {
    
        var todoFile = new File(Config.HOME_DIR + todoFileName);
        var tempFile = new File(Config.HOME_DIR + tempFileName);

        var output = new PrintWriter(new FileWriter(tempFile));

        items
            .forEach(output::println);

        output.close();

        todoFile.delete();

        if (!tempFile.renameTo(todoFile)) {

            System.err.println("Could not update file");
        }
    }

    void addItem(String item) {

        addItem(item, true);
    }

    private void addItem(String item, boolean display) {

        if (items.contains(item)) {

            System.out.println("Warning: Item is already in the list!");
            return;
        }

        items.add(item);

        if (display) {
            
            System.out.println("Added item \"" + item + "\"");
        }
    }

    private List<String> getMatchingItems(String item) {

        return items.stream()
            .filter(line -> line.toLowerCase().startsWith(item.toLowerCase()))
            .collect(Collectors.toList());
    }

    private Optional<String> pickOneItem(List<String> possibleItems, Scanner scanner) {

        if (!TodoUtil.getConfirmation("Pick one?", scanner)) {

            return Optional.empty();

        } else {

            System.out.print("Choose an item [1-" + possibleItems.size() + "] :");
            boolean chosen = false;

            int index;

            do {

                index = scanner.nextInt();
                scanner.nextLine();

                if (index < 1 || index > possibleItems.size()) {

                    if (TodoUtil.getConfirmation("Invalid index, cancel?", scanner)) {

                        return Optional.empty();

                    } else {

                        System.out.print("Please choose a valid index [1-" + possibleItems.size() + "] :");
                    }

                } else {

                    chosen = true;
                }

            } while (!chosen);

            final int chosenIndex = index - 1;
            return Optional.of(possibleItems.get(chosenIndex));
        }
    }

    boolean removeItem(String item, Scanner scanner) {

        return removeItem(item, scanner, "remove", true);
    }

    private boolean removeItem(String item, Scanner scanner, String action, boolean allowMultiple) {

        String actionUpper = action.toUpperCase().substring(0, 1) + action.substring(1);

        List<String> itemsToRemove = getMatchingItems(item);

        if (items.size() == 0) {

            System.out.println("Warning: No items to " + action);
            return false;
        }

        if (itemsToRemove.size() == 0) {

            System.out.println("Warning: Could not find any items matching \"" + item.toLowerCase() + "...\"");
            return false;
        }

        if (itemsToRemove.size() > 1) {

            System.out.println("Warning: Multiple lines match:");

            System.out.println();

            for (int i = 0; i < itemsToRemove.size(); i++) {

                var line = itemsToRemove.get(i);
                System.out.println("[" + (i + 1) + "] \"" + line + "\"");
            }

            System.out.println();

            if (!allowMultiple || !TodoUtil.getConfirmation(actionUpper + " all?", scanner)) {

                Optional<String> chosenItem = pickOneItem(itemsToRemove, scanner);

                if (chosenItem.isPresent()) {

                    itemsToRemove = List.of(chosenItem.get());

                } else {

                    System.out.println("No items were " + action + "d");
                    return false;
                }
            }
        }

        items.removeAll(itemsToRemove);

        if (itemsToRemove.size() > 1) {

            System.out.println(actionUpper + "d " + itemsToRemove.size() + " items:");

            itemsToRemove
                .forEach(line -> System.out.println("\"" + line + "\""));

        } else if (itemsToRemove.size() == 1) {

            System.out.println(actionUpper + "d item \"" + itemsToRemove.get(0) + "\"");

        }

        return itemsToRemove.size() != 0;
    }

    void replaceItem(String item, String replacement, Scanner scanner) {

        boolean removed = removeItem(item, scanner, "replace", false);

        if (removed) {

            addItem(replacement, false);
            System.out.println("\"" + replacement + "\" is now on the todo list");

        } else {

            System.err.println("Warning: \"" + replacement + "\" was not added to the list");
        }
    }

    void printItems() {

        if (items.size() == 0) {

            System.out.println("Nothing to do, well done!");

        } else {

            String layout = Config.LAYOUT.getValue();

            items.stream()
                .map(item -> layout.replace("$item", item))
                .forEach(System.out::println);
        }
    }

    void doItem(String item, int minutes, Scanner scanner) {

        var matchingItems = getMatchingItems(item);

        if (matchingItems.size() == 0) {

            System.err.println("Could not find any item matching \"" + item.toLowerCase() + "...\"");

        } else if (matchingItems.size() > 1) {

            System.err.println("Item \"" + item.toLowerCase() + "...\" is ambiguous, could be any of:");

            System.out.println();

            for (int i = 0; i < matchingItems.size(); i++) {

                var line = matchingItems.get(i);
                System.out.println("[" + (i + 1) + "] \"" + line + "\"");
            }

            System.out.println();

            Optional<String> chosenItem = pickOneItem(matchingItems, scanner);

            if (chosenItem.isEmpty()) {

                System.out.println("Not doing anything");
                return;
            }
        }

        try {

            boolean completed = TodoTask.work(matchingItems.get(0), minutes, scanner);

            if (completed) {

                removeItem(matchingItems.get(0), scanner);
            }

        } catch (InterruptedException e) {

            System.err.println("Task wait interrupted:");
            e.printStackTrace();
        }
    }
}
