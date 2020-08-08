package net.coagulate.Core.HTML;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public abstract class AttributeMapTag extends GenericTagContainer {
    protected Map<String,String> attributes=new HashMap<>();
    protected void addTag(@Nonnull String tag, String content) {
        attributes.put(tag,content);
    }

    @Override
    public String tagAttributes() {
        StringBuilder attributelist=new StringBuilder();
        boolean addedanything=false;
        for(Map.Entry<String,String> tag:attributes.entrySet()) {
            if (addedanything) { attributelist.append(" "); }
            attributelist.append(tag.getKey());
            if (tag.getValue()!=null) {
                attributelist.append("=\"");
                attributelist.append(tag.getValue());
                attributelist.append("\"");
            }
            addedanything=true;
        }
        return attributelist.toString();
    }
}
