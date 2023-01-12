package net.coagulate.Core.HTML.Elements;

public class TableHeader extends TableData {
	
	public TableHeader(final String data) {
		super(data);
	}
	
	public TableHeader() {
	}
	
	@Override
	public String tag() {
		return "th";
	}
}
