package net.coagulate.Core.HTML.Elements;

public class InputText extends Input {

    public InputText(final String name) {
        super("text");
        replaceAttribute("name", name);
    }

}
