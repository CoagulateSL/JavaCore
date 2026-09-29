package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

import javax.annotation.Nonnull;

/**
 * A &lt;script&gt; tag, either referencing an external source or holding inline body content.
 *
 * <p>Because a source reference and inline content are mutually exclusive, use
 * {@link #Script(String)} for a src and {@link #Script#Script()} plus {@link #inline(String)} for a
 * body.</p>
 */
public class Script extends TagPair {

	public Script() {
	}

	/**
	 * A script tag pointing at an external source.
	 *
	 * @param src URL of the script
	 */
	public Script(@Nonnull final String src) {
		addAttribute("src", src);
	}

	/**
	 * Set the inline body of this script.
	 *
	 * @param body javascript source
	 * @return this
	 */
	public Script inline(@Nonnull final String body) {
		add(new PlainText(body));
		return this;
	}

	@Override
	public String tag() {
		return "script";
	}
}
