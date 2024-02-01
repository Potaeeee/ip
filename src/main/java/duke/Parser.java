package duke;

import java.util.Arrays;
import java.util.List;

/**
 * Parser parses the commands a user writes and provides helper functions
 */
public class Parser {

  private static final String BY_CMD = "/by";
  private static final String FROM_CMD = "/from";
  private static final String TO_CMD = "/to";

  private static String cmdJoin(String[] xs) {
    return String.join(" ", xs);
  }

  private static boolean isNumber(String str) {
    return str.matches("-?\\d+(\\.\\d+)?");
  }

  /**
   * Returns a subarray containing the elements from index {@code a} to {@code b - 1}
   * incluive of the specified array.
   *
   * Similar to {@link Arrays#copyOfRange(Object[], int, int)}, but
   * provides a more concise way to slice a subarray.
   *
   * @param <T> the component type of the array
   * @param xs the array from which a range is to be copied
   * @param a the initial index (inclusive) to start the range
   * @param b the final index (exclusive) to end the range
   * @return a new array containing the specified range from the original array
   * @throws ArrayIndexOutOfBoundsException if {@code a} is negative,
   *         {@code b} is larger than the length of the array, or
   *         {@code a > b}
   */
  static <T> T[] range(T[] xs, int a, int b) {
    return Arrays.copyOfRange(xs, a, b);
  }

  /**
   * Parses the command string provided by the user.
   *
   * Takes a command string and processes it to identify command and arguments.
   *
   * @param cmdString The command string to be parsed.
   * @return An array of strings representing the parsed command. The first element of the
   *       returned array is the command type, and the rest are the command arguments.
   * @throws DukeExceptio
   */
  static String[] parseCommand(String cmdString) throws DukeException {
    String[] cmdSplit = cmdString.split(" ");
    String command = cmdSplit[0];
    switch (command) {
      case "end":
      case "list":
        return new String[] { command };
      case "mark":
      case "unmark":
      case "delete":
        {
          String ferr1 = "%s command: expected an integer argument.";
          if (cmdSplit.length != 2) throw new DukeException(
            String.format(ferr1, command)
          );
          String idxString = cmdSplit[1];
          if (!isNumber(idxString)) throw new DukeException(
            String.format(ferr1, command)
          );
          return new String[] { command, idxString };
        }
      case "todo":
        {
          if (cmdSplit.length < 2) throw new DukeException(
            "todo command: description cannot be empty."
          );
          String taskStr = cmdJoin(range(cmdSplit, 1, cmdSplit.length));
          return new String[] { command, taskStr };
        }
      case "deadline":
        {
          List<String> cmds = Arrays.asList(cmdSplit);
          String ferr1 = "deadline command: expected `%s` argument.";
          String ferr2 = "deadline command: %s description cannot be empty.";
          if (!cmds.contains(BY_CMD)) throw new DukeException(
            String.format(ferr1, BY_CMD)
          );
          int by_idx = cmds.indexOf(BY_CMD);
          String taskStr = cmdJoin(range(cmdSplit, 1, by_idx));
          String deadline = cmdJoin(range(cmdSplit, by_idx + 1, cmds.size()));
          if (taskStr.length() == 0) throw new DukeException(
            String.format(ferr2, "task")
          );
          if (deadline.length() == 0) throw new DukeException(
            String.format(ferr2, "deadline")
          );
          return new String[] { command, taskStr, deadline };
        }
      case "event":
        {
          List<String> cmds = Arrays.asList(cmdSplit);
          String ferr1 = "event command: expected `%s` argument.";
          String ferr2 = "event command: %s description cannot be empty.";
          String ferr3 =
            "event command: `%s` argument expected before `%s` argument.";
          if (!cmds.contains(FROM_CMD)) throw new DukeException(
            String.format(ferr1, FROM_CMD)
          );
          if (!cmds.contains(TO_CMD)) throw new DukeException(
            String.format(ferr1, TO_CMD)
          );
          int fromIdx = cmds.indexOf(FROM_CMD);
          int toIdx = cmds.indexOf(TO_CMD);
          if (toIdx < fromIdx) throw new DukeException(
            String.format(ferr3, FROM_CMD, TO_CMD)
          );
          String taskStr = cmdJoin(range(cmdSplit, 1, fromIdx));
          String fromStr = cmdJoin(range(cmdSplit, fromIdx + 1, toIdx));
          String toStr = cmdJoin(range(cmdSplit, toIdx + 1, cmds.size()));
          if (taskStr.length() == 0) throw new DukeException(
            String.format(ferr2, "task")
          );
          if (fromStr.length() == 0) throw new DukeException(
            String.format(ferr2, "from")
          );
          if (toStr.length() == 0) throw new DukeException(
            String.format(ferr2, "to")
          );
          return new String[] { command, taskStr, fromStr, toStr };
        }
      default:
        throw new DukeException(
          String.format("Unhandled command: %s", command)
        );
    }
  }
}