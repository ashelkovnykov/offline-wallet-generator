package com.ashelkov.wallet.io;

import java.io.Console;
import java.lang.IllegalArgumentException;

import com.ashelkov.wallet.exceptions.NullConsoleException;

public class ConsoleUtils {  
  /**
   * Confirms passphrase by prompting user to re-enter and compares by value
   *
   * @param passphrase passphrase from the first prompt
   * @return whether or not passphrases match
   */
  public static boolean confirmPasswordsMatch(String passphrase)
    throws IllegalArgumentException, NullConsoleException {
    String confirmation = getInputFromUser();

    return passphrase.equals(confirmation);
  }

  /**
   * Prompts user for passphrase confirmation via console
   *
   * @return passphrase confirmation from console
   */
  private static String getInputFromUser()
    throws IllegalArgumentException, NullConsoleException {
    Console cons = System.console();
    if(cons == null) {
      throw new NullConsoleException("No console available, this method requires interactive mode");
    }

    String output = "";
    char[] passwd = cons.readPassword("[%s]", "Confirm the passphrase:");
    if(passwd == null) {
      throw new IllegalArgumentException("Password confirmation canceled");
    }
    // See: https://docs.oracle.com/javase/7/docs/api/java/io/Console.html#readPassword%28%29
    if (passwd != null) {
        output = new String(passwd).trim();
        java.util.Arrays.fill(passwd, ' ');
    }

    return output;
  }
}
