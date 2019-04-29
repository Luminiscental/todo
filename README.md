# todo

Simple todo list cli tool; can add, remove and list items plus pomodoro-style give yourself a set number of minutes to work on one.
Config file can be made at `~/.todo/config` with configurable things listed below.

If for whatever reason you want to use this run the `copyJar` task and move `todo` into your path somewhere (no windows support sorry).

## Options

Options are prefixed by `--`, currently only one supported.

* `local` - If set the todo file is looked up in the current working directory instead of your home directory.

## Config

* `todoFile` - Specifies the path relative to your home directory (if not using `--local`) to the file where items on the todo list are stored. Note that if you have items on the list and change this you will need to copy them to the new file or re-add them. Default value is `.todo/todo`.

  e.g. `todoFile = .config/todo/todo.txt` stores items in `~/.config/todo/todo.txt`, or `./.config/todo/todo.txt` if `--local` is passed.

* `timeout` - Specifies the default timeout for the `do` command in minutes. Default value is 15.

  e.g. `timeout = 2` means running `todo do "Some task"` will give you 2 minutes to work on it.

* `layout` - Specifies the layout used per item when printing items, where "$item" is replaced with the actual item. Default value is `- $item`.

  e.g. `layout = ## $item ##` results in an item `Do X` being printed as the line `## Do X ##`.
