package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;

import java.util.Map;

public class PlainText extends Container {

    @Override
    public void load(Map<String, String> parameters) {}

    private final String text;
    public PlainText(String text) { this.text=text; }
    public String toString() { return text; }
}
