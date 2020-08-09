package net.coagulate.Core.HTML;

import java.util.Map;

public interface Container {

    void load(Map<String,String> parameters);
    // don't forget to overwrite toString
}
