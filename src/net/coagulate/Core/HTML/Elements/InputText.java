package net.coagulate.Core.HTML.Elements;

public class InputText extends Input {
	@SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
	public InputText(final String name) {
		super("text");
		replaceAttribute("name",name);
	}
	
}
