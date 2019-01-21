package lumi.todo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lumi.todo.util.Config;
import lumi.todo.util.TodoUtil;

public class TodoListAccessor {

    private final String todoFileName;
    private final String tempFileName;
    private final String homeDir;

    TodoListAccessor(String todoFileName, String tempFileName) throws IOException {

        this.todoFileName = todoFileName;
        this.tempFileName = tempFileName;
        this.homeDir = System.getProperty("user.home") + "/";

        File todoList = new File(homeDir + todoFileName);

        if (!todoList.isFile()) {

            todoList.getParentFile().mkdirs();
            todoList.createNewFile();
        }
    }

    void addItem(String item) {

        try {

            var output = new PrintWriter(new FileWriter(homeDir + todoFileName, true));
            output.println(item);
            output.close();

            System.out.println("Added item \"" + item + "\"");

        } catch (IOException e) {

            System.err.println("Could not add item:");
            e.printStackTrace();
        }
    }

    List<String> getItems() throws IOException {

        List<String> result = new ArrayList<>();

        var input = new BufferedReader(new FileReader(homeDir + todoFileName));
        input.lines().forEach(result::add);
        input.close();

        return result;
    }

    void replaceList(List<String> newItems) throws IOException {
    
        var todoFile = new File(homeDir + todoFileName);
        var tempFile = new File(homeDir + tempFileName);

        var output = new PrintWriter(new FileWriter(tempFile));

        newItems.forEach(output::println);

        output.close();

        todoFile.delete();

        if (!tempFile.renameTo(todoFile)) {

            System.err.println("Could not update file");
        }
    }

    List<String> getMatchingItems(String item) throws IOException {

        return getItems().stream().filter(line -> line.toLowerCase().startsWith(item.toLowerCase())).collect(Collectors.toList());
    }

    void removeItem(String item) {

        List<String> itemsRemaining;
        List<String> itemsToRemove;

        try {

            itemsRemaining = getItems();
            itemsToRemove = getMatchingItems(item);

        } catch (IOException e) {

            System.err.println("Could not open todo list:");
            e.printStackTrace();

            return;
        }

        if (itemsRemaining.size() == 0) {

            System.out.println("Warning: no items to remove");
            return;
        }

        if (itemsToRemove.size() == 0) {

            System.out.println("Warning: could not find any items matching \"" + item.toLowerCase() + "...\"");
            return;
        }

        if (itemsToRemove.size() > 1) {

            System.out.println("Warning: Multiple lines match:");

            itemsToRemove.forEach(line -> System.out.println("\"" + line + "\""));

            if (!TodoUtil.getConfirmation("Remove all?")) {

                System.out.println("No items were removed");
                return;
            }
        }

        itemsRemaining.removeAll(itemsToRemove);

        try {

            replaceList(itemsRemaining);

        } catch (IOException e) {

            System.err.println("Could not remove item(s):");
            e.printStackTrace();
        }

        if (itemsToRemove.size() > 1) {

            System.out.println("Removed " + itemsToRemove.size() + " items:");
            itemsToRemove.forEach(line -> System.out.println("\"" + line + "\""));

        } else if (itemsToRemove.size() == 1) {

            System.out.println("Removed item \"" + itemsToRemove.get(0) + "\"");

        }
    }

    void printItems() {

        try {

            var items = getItems();

            if (items.size() == 0) {

                System.out.println("Nothing to do, well done!");

            } else {

                String layout = Config.LAYOUT.getValue();

                items.stream()
                     .map(item -> layout.replace("$item", item))
                     .forEach(System.out::println);
            }

        } catch (IOException e) {

            System.err.println("Could not open todo list:");
            e.printStackTrace();
        }
    }

    void doItem(String item, int minutes) {

        try {

            var items = getMatchingItems(item);

            if (items.size() == 0) {

                System.err.println("Could not find any item matching \"" + item.toLowerCase() + "...\"");

            } else if (items.size() > 1) {

                System.err.println("Item \"" + item.toLowerCase() + "...\" is ambiguous, could be any of:");
                items.forEach(line -> System.err.println("- " + line));

            } else {

                try {

                    boolean completed = TodoTask.work(items.get(0), minutes);

                    if (completed) {

                        removeItem(items.get(0));
                    }

                } catch (InterruptedException e) {

                    System.err.println("Task wait interrupted:");
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {

            System.err.println("Could not open todo list:");
            e.printStackTrace();
        }
    }
}
