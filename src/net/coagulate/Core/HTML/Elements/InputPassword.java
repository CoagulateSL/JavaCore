package net.coagulate.Core.HTML.Elements;

public class InputPassword extends Input {
    public InputPassword(final String name) {
        super("password");
        replaceAttribute("name", name);
    }
}
