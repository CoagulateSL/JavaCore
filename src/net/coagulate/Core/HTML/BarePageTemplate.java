package net.coagulate.Core.HTML;

/**
 * A template that emits no markup at all, for serving non-HTML content such as javascript or css
 * through the normal page pipeline.
 */
public class BarePageTemplate extends PageTemplate {

	@Override
	public String getHeader() {
		return "";
	}

	@Override
	public String getFooter() {
		return "";
	}
}
