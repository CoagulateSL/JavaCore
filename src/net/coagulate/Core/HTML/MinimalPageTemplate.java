package net.coagulate.Core.HTML;

public class MinimalPageTemplate extends PageTemplate {
    @Override
    public String getHeader() { return "<html><head><title>Minimal Template Active</title></head><body>"; }

    @Override
    public String getFooter() {
        return "</body></html>";
    }
}
