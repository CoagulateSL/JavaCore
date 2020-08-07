package net.coagulate.Core.HTML;

import java.util.Map;

public interface Container {

    String toHTML();
    void load(Map<String,String> parameters);

}
