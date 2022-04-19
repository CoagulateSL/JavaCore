package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.User.UserConfigurationRecursionException;

public class JavaTools {

    public static void limitRecursionUserException(final int maxdepth) {
        if (Thread.currentThread().getStackTrace().length > maxdepth) {
            throw new UserConfigurationRecursionException("Possible recursion detected");
        }
    }

}
