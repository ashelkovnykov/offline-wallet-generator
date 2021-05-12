package com.ashelkov.wallet.exceptions;

/**
 * Thrown when System.console() is not available (null).
 * See: https://docs.oracle.com/javase/7/docs/api/java/io/Console.html
 */
public class NullConsoleException extends Exception {
  public NullConsoleException(String message) {
    super(message);
  }
}