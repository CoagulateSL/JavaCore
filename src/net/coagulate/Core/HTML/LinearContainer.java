package net.coagulate.Core.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class LinearContainer implements Container {

    private final List<Container> contents=new ArrayList<>();

    @Override
    public String toHTML() {
        StringBuilder result=new StringBuilder();
        for (Container content:contents) {
            result.append(content.toHTML());
        }
        return result.toString();
    }

    @Override
    public void load(Map<String, String> parameters) {
        for (Container content:contents) { content.load(parameters); }
    }

    public void add(Container content) { contents.add(content); }
}
