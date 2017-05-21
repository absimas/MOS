package com.simas.processes;

import com.simas.Log;
import com.simas.Scheduler;
import com.simas.Utils;
import com.simas.exceptions.TIException;
import com.simas.real_machine.Command;
import com.simas.real_machine.Comparison;
import com.simas.real_machine.Memory;
import com.simas.real_machine.RealMachine;
import com.simas.resources.Element;
import com.simas.resources.Interrupt;
import com.simas.resources.Resource;
import com.sun.istack.internal.Nullable;
import java.util.List;

/**
 * User program executor.
 */
public abstract class VirtualMachine extends Process {

  static final int PRIORITY = 10;

  /**
   * Memory size: 100 words.
   */
  private static final int MEMORY_SIZE = 100 * RealMachine.WORD_SIZE;

  /**
   * {@link Memory} position of this VM.
   */
  private final int internalMemoryPosition;

  VirtualMachine(Process parent, int internalMemoryPosition, @Nullable Element... resources) {
    super(parent, PRIORITY, resources);
    this.internalMemoryPosition = internalMemoryPosition;
  }

  @Override
  public void run() {
    super.run();

    // Reset TI
    RealMachine.resetTI();

    // Execute user program
    Command command = null;
    try {
      while (true) {
        // Parse
        final String string = getCommands().get(RealMachine.IC++);
        command = Command.parse(string);

        // Execute until an exception is thrown or execution requires elevated privileges
        if (!execute(command)) {
          break;
        }

        // Increment TI
        RealMachine.incrementTI();
      }
    } catch (TIException ignored) {
      interruptParent(Interrupt.Type.TI, command);
    } catch (IllegalArgumentException | IndexOutOfBoundsException | ArithmeticException e) {
      Log.e("Invalid command encountered: %s. Exception caught: %s.", command, e);
      interruptParent(Interrupt.Type.PROGRAM_INTERRUPT, command);
    }

    Scheduler.schedule();

    // Repeat
    run();
  }

  /**
   * Send interrupt message to the parent (JobGovernor) process.
   * @param type    interruption type
   * @param command command execution that caused the interruption
   */
  protected void interruptParent(Interrupt.Type type, Command command) {
    Resource.INTERRUPT.create(this, element -> {
      element.destination = parent;
      element.type = type;
      element.failingCommand = command;
    });
  }

  /**
   * Fetch command list executed by this VM.
   */
  protected abstract List<String> getCommands();

  /**
   * Execute a command.
   * @param command  command to be executed
   * @throws IllegalArgumentException when trying to write an incomplete word
   * @throws IndexOutOfBoundsException when a command refers to a word outside the visible memory
   * @throws NumberFormatException when either {@link RealMachine#TMP} or referenced memory contains something that's not a number
   * @throws ArithmeticException when number arithmetic result does not fit in a word
   * @throws RuntimeException when a command wasn't execution because the lack of privileges
   * @return true when a command was successfully executed, false when it needs elevated privileges
   */
  protected boolean execute(Command command) throws IndexOutOfBoundsException, ArithmeticException, IllegalArgumentException {
    switch (command.type) {
      case CR:
        RealMachine.TMP = read(command.getArgument());
        break;
      case CM:
        write(command.getArgument(), RealMachine.TMP);
        break;
      case AD: {
        // Convert
        Log.d("Converting '%s' and '%s' to integers.", RealMachine.TMP, read(command.getArgument()));
        final int tmp = Integer.valueOf(RealMachine.TMP);
        final int mem = Integer.valueOf(read(command.getArgument()));

        // Add
        final int result = tmp + mem;

        // Overflow
        if (String.valueOf(result).length() > RealMachine.WORD_SIZE) {
          throw new ArithmeticException(String.format("%d + %d = %s which does not fit within a word!", tmp, mem, result));
        }

        // Save the result in TMP
        RealMachine.TMP = Utils.precedeZeroes(result, RealMachine.WORD_SIZE);
        break;
      }
      case SB: {
        // Convert
        Log.d("Converting '%s' and '%s' to integers.", RealMachine.TMP, read(command.getArgument()));
        final int tmp = Integer.valueOf(RealMachine.TMP);
        final int mem = Integer.valueOf(read(command.getArgument()));

        // Add
        final int result = tmp - mem;

        // Overflow
        if (String.valueOf(result).length() > RealMachine.WORD_SIZE) {
          throw new ArithmeticException(String.format("%d - %d = %s which does not fit within a word!", tmp, mem, result));
        }

        // Save the result in TMP
        RealMachine.TMP = Utils.precedeZeroes(result, RealMachine.WORD_SIZE);
        break;
      }
      case ML: {
        // Convert
        Log.d("Converting '%s' and '%s' to integers.", RealMachine.TMP, read(command.getArgument()));
        final int tmp = Integer.valueOf(RealMachine.TMP);
        final int mem = Integer.valueOf(read(command.getArgument()));

        // Add
        final int result = tmp * mem;

        // Overflow
        if (String.valueOf(result).length() > RealMachine.WORD_SIZE) {
          throw new ArithmeticException(String.format("%d * %d = %s which does not fit within a word!", tmp, mem, result));
        }

        // Save the result in TMP
        RealMachine.TMP = Utils.precedeZeroes(result, RealMachine.WORD_SIZE);
        break;
      }
      case MD: {
        // Convert
        Log.d("Converting '%s' and '%s' to integers.", RealMachine.TMP, read(command.getArgument()));
        final int tmp = Integer.valueOf(RealMachine.TMP);
        final int mem = Integer.valueOf(read(command.getArgument()));

        // Add
        final int result = tmp % mem;

        // Save the result in TMP
        RealMachine.TMP = Utils.precedeZeroes(result, RealMachine.WORD_SIZE);
        break;
      }
      case DV: {
        // Convert
        Log.d("Converting '%s' and '%s' to integers.", RealMachine.TMP, read(command.getArgument()));
        final int tmp = Integer.valueOf(RealMachine.TMP);
        final int mem = Integer.valueOf(read(command.getArgument()));

        // Overflow (division by 0)
        if (mem == 0) {
          throw new ArithmeticException(String.format("%d / %d. Division by zero!", tmp, mem));
        }

        // Add
        final int result = tmp / mem;

        // Save the result in TMP
        RealMachine.TMP = Utils.precedeZeroes(result, RealMachine.WORD_SIZE);
        break;
      }
      case CP:
        final String tmp = RealMachine.TMP;
        final String mem = read(command.getArgument());
        int cp = tmp.compareTo(mem);
        if (cp == 0) {
          RealMachine.C = Comparison.EQUAL;
        } else if (cp > 0) {
          RealMachine.C = Comparison.MORE;
        } else {
          RealMachine.C = Comparison.LESS;
        }
        break;
      case JP:
        // Overflow
        if (command.getArgument() > MEMORY_SIZE) {
          throw new IndexOutOfBoundsException("JP referenced an invalid memory point: " + command.getArgument());
        }

        // Write IC
        RealMachine.IC = command.getArgument();
        break;
      case JE:
        // If not equal, increment IC and leave
        if (RealMachine.C != Comparison.EQUAL) break;
        RealMachine.IC = command.getArgument();
        break;
      case JL:
        // If not less, increment IC and leave
        if (RealMachine.C != Comparison.LESS) break;
        RealMachine.IC = command.getArgument();
        break;
      case JM:
        if (RealMachine.C != Comparison.MORE) break;
        RealMachine.IC = command.getArgument();
        break;
      case HALT:
        interruptParent(Interrupt.Type.HALT, command);
        return false;
      case GD:
        interruptParent(Interrupt.Type.GD, command);
        return false;
      case PD:
        interruptParent(Interrupt.Type.PD, command);
        return false;
      case RD:
        interruptParent(Interrupt.Type.RD, command);
        return false;
      case WD:
        interruptParent(Interrupt.Type.WD, command);
        return false;
      case SD:
        interruptParent(Interrupt.Type.SD, command);
        return false;
      default:
        throw new IllegalStateException(String.format("Unexpected command '%s' was executed!", command));
    }

    return true;
  }

  /**
   * Read from memory.
   * @param position relative memory position
   * @return word that's been read
   * @throws IndexOutOfBoundsException when reading from an OOB memory position
   */
  protected final String read(int position) throws IndexOutOfBoundsException {
    return Memory.getInstance().read(position + internalMemoryPosition, RealMachine.WORD_SIZE);
  }


  /**
   * Write to memory.
   * @param position relative memory position
   * @param word     word to be written
   * @throws IndexOutOfBoundsException when writing to an OOB memory position
   * @throws IllegalArgumentException when trying to write an incomplete word
   */
  protected final void write(int position, String word) throws IndexOutOfBoundsException, IllegalArgumentException {
    if (word.length() != RealMachine.WORD_SIZE) {
      throw new IllegalArgumentException("Trying to write an incomplete word!");
    }
    Memory.getInstance().write(position + internalMemoryPosition, word);
  }

}
