package net.coagulate.Core.HTML.Elements;

public class ButtonSubmit extends Button {
	
	@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
	public ButtonSubmit(final String name) {
		super("submit");
		replaceAttribute("name",name);
		replaceAttribute("value",name);
		contents().add(new PlainText(name));
	}
}
