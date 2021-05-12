package com.ashelkov.wallet.io.validate;

import java.lang.IllegalArgumentException;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.ashelkov.wallet.io.ConsoleUtils;
import com.ashelkov.wallet.exceptions.NullConsoleException;

public class PasswordValidator implements IParameterValidator {

    @Override
    public void validate(String name, String value) throws ParameterException {
      boolean passwordConfirmed = false;

      try {
        passwordConfirmed = ConsoleUtils.confirmPasswordsMatch(value);  
      } catch (IllegalArgumentException e)  {
        e.printStackTrace();
        throw new ParameterException("Password confirmation canceled");
      } catch (NullConsoleException e) {
        e.printStackTrace();
        throw new ParameterException("Console not available for password confirmation");
      }

      if(!passwordConfirmed) {
        throw new ParameterException("Passphrases do not match");
      }
    }
}
