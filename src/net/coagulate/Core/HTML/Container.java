package net.coagulate.Core.HTML;

import net.coagulate.Core.HTML.Elements.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {
	
	private final List<Container> contents=new ArrayList<>();
	// not everything uses attributes but it makes life so much easier to have it 'general' for cascading etc
	private final Map<String,String> attributes=new HashMap<>();
	
	@Override
	public final String toString() {
		final StringBuilder sb=new StringBuilder();
		toString(sb);
		return sb.toString();
	}
	
	protected void toString(@Nonnull final StringBuilder result) {
		for (final Container content: contents()) {
			result.append(content.toString());
		}
	}
	
	public void load(final Map<String,String> parameters) {
		for (final Container content: contents()) {
			content.load(parameters);
		}
	}
	
	public Container add(final String text) {
		contents.add(new PlainText(text));
		return this;
	}
	
	public String tagAttributes() {
		final StringBuilder attributelist=new StringBuilder();
		boolean addedanything=false;
		for (final Map.Entry<String,String> tag: attributes.entrySet()) {
			if (addedanything) {
				attributelist.append(" ");
			}
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
	
	protected void addAttribute(@Nonnull final String name,String value) {
		if (attributes.containsKey(name)) {
			value=attributes.get(name)+" "+value;
		}
		attributes.put(name,value);
	}
	
	public void styleCascade(final String s) {
		for (final Container content: contents()) {
			addAttribute("style",s);
			content.styleCascade(s);
		}
	}
	
	public void alignment(final String alignment) {
		replaceAttribute("align",alignment);
	}
	
	public void replaceAttribute(@Nonnull final String name,final String value) {
		attributes.put(name,value);
	}
	
	public Form form() {
		final Form f=new Form();
		add(f);
		return f;
	}
	
	public Container add(final Container content) {
		contents().add(content);
		return this;
	}
	
	protected final List<Container> contents() {
		return contents;
	}
	
	public Table table() {
		final Table t=new Table();
		add(t);
		return t;
	}
	
	// don't forget to overwrite toString
	public Container p(final String paragraph) {
		add(new Paragraph().add(new PlainText(paragraph)));
		return this;
	}
	
	public Paragraph p() {
		final Paragraph p=new Paragraph();
		add(p);
		return p;
	}
	
	public Anchor a() {
		final Anchor a=new Anchor();
		add(a);
		return a;
	}
	
	public Container a(final String url,final String label) {
		add(new Anchor(url,label));
		return this;
	}
	
	public Container hr() {
		add(new HorizontalRule());
		return this;
	}
	
	/**
	 * Find an element by "name" tag
	 *
	 * @param name Name to search
	 * @return The container with that name, or null if not found
	 */
	@Nullable
	public Container findByName(final String name) {
		// is it us?
		if (attributes.containsKey("name")) {
			if (attributes.get("name").equalsIgnoreCase(name)) {
				return this;
			}
		}
		// no? ask our children
		for (final Container content: contents()) {
			final Container match=content.findByName(name);
			if (match!=null) {
				return match;
			}
		}
		// not in this part of the tree then
		return null;
	}
	
	@Nonnull
	public String getAttribute(final String key,@Nonnull final String defaultvalue) {
		final String result=getAttribute(key);
		if (result==null) {
			return defaultvalue;
		}
		return result;
	}
	
	@Nullable
	public String getAttribute(final String key) {
		if (attributes.containsKey(key)) {
			return attributes.get(key);
		}
		return null;
	}
	
	public Container header1(final String header) {
		add(new Header1(header));
		return this;
	}
	
	public Container header2(final String header) {
		add(new Header2(header));
		return this;
	}
	
	public Container header3(final String header) {
		add(new Header3(header));
		return this;
	}
	
	public Container header4(final String header) {
		add(new Header4(header));
		return this;
	}
	
	public Container header5(final String header) {
		add(new Header5(header));
		return this;
	}
	
	public Container align(final String alignment) {
		last().replaceAttribute("align",alignment);
		return this;
	}
	
	private Container last() {
		return contents.get(contents.size()-1);
	}
	
	public Container submit(final String name) {
		add(new ButtonSubmit(name));
		return this;
	}
	
	public Container p(final Container content) {
		add(new Paragraph(content));
		return this;
	}
}
