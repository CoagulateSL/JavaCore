package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.System.SystemBadValueException;
import net.coagulate.Core.Exceptions.User.UserInputValidationParseException;

import javax.annotation.Nonnull;

public class ValueMapper {

    public static boolean toBoolean(@Nonnull String value) {
        value=value.toLowerCase();
        if (value.isEmpty() || value.equals("0") || value.equals("false") || value.equals("no") || value.equals("n") || value.equals("off")) { return false; }
        if (value.equals("1") || value.equals("true") || value.equals("yes") || value.equals("y") || value.equals("on")) { return true; }
        throw new UserInputValidationParseException("Value "+value+" does not reduce to boolean");
    }
}
