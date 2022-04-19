package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.User.UserInputValidationParseException;

import javax.annotation.Nonnull;

public class ValueMapper {

    public static boolean toBoolean(@Nonnull String value) {
        value = value.toLowerCase();
        if (value.isEmpty() || "0".equals(value) || "false".equals(value) || "no".equals(value) || "n".equals(value) || "off".equals(value)) {
            return false;
        }
        if ("1".equals(value) || "true".equals(value) || "yes".equals(value) || "y".equals(value) || "on".equals(value)) {
            return true;
        }
        throw new UserInputValidationParseException("Value " + value + " does not reduce to boolean");
    }
}
