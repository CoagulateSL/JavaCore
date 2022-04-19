package net.coagulate.Core.HTML.Elements;

public class InputPassword extends Input {
    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
    public InputPassword(final String name) {
        super("password");
        replaceAttribute("name", name);
    }
}
