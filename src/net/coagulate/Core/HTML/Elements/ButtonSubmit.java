package net.coagulate.Core.HTML.Elements;

public class ButtonSubmit extends Button {

    public ButtonSubmit(final String name) {
        super("submit");
        replaceAttribute("name", name);
        replaceAttribute("value", name);
        contents().add(new PlainText(name));
    }
}
