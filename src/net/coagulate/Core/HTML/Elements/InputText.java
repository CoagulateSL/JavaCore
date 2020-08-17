package net.coagulate.Core.HTML.Elements;

public class InputText extends Input {

    public InputText(String name) {
        super("text");
        replaceAttribute("name",name);
    }

}
