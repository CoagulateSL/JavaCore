package net.coagulate.Core.HTML.Elements;

public class InputPassword extends Input {
    public InputPassword(String name) {
        super("password");
        replaceAttribute("name",name);
    }
}
