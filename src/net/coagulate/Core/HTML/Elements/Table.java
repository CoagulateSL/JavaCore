package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Table extends TagPair {
	@Override
	public String tag() {
		return "table";
	}
	
	@Override
	public Container add(final Container content) {
		if (content instanceof TableRow) { contents().add(content); return content; }
		throw new SystemImplementationException("You can not add content directly to a table object (you need a row)");
	}
	
	public Table collapsedBorder() {
		border();
		addAttribute("style","border-collapse: collapse;");
		return this;
	}
	
	public Table border() {
		addAttribute("border","1");
		return this;
	}
	
	public TableRow row() {
		final TableRow row=new TableRow();
		contents().add(row);
		return row;
	}
	
	@Override
	public Container add(final String text) {
		throw new SystemImplementationException("You can not add content directly to a table object (you need a row)");
	}

	public String toString() {
		if (sortOn>-1) {
			contents().sort((a,b)->{
				if (a instanceof TableRow) {
					for (Container c:a.contents()) {
						if (c instanceof TableHeader) { return -1; }
					}
				}
				if (b instanceof TableRow) {
					for (Container c:b.contents()) {
						if (c instanceof TableHeader) { return 1; }
					}
				}
				if (a instanceof TableRow && b instanceof TableRow) {
					if (sortType==SORT_TYPE.DOUBLE) {
						Double aa=Double.parseDouble(((TableRow)a).get(sortOn).contents().get(0).toString());
						Double bb=Double.parseDouble(((TableRow)b).get(sortOn).contents().get(0).toString());
						return (sortReverse?-1:1)*aa.compareTo(bb);
					}
					return (sortReverse?-1:1)*(((TableRow)a).get(sortOn)).toString().compareTo(((TableRow)b).get(sortOn).toString());
				}
				return 0;
			});
		}
		return super.toString();
	}

	public enum SORT_TYPE {STRING,DOUBLE};
	private SORT_TYPE sortType=SORT_TYPE.STRING;
	private int sortOn=-1;
	private boolean sortReverse=false;
	public Table sortType(SORT_TYPE t) { sortType=t; return this; }
	public Table sortReverse() { sortReverse(true); return this; }
	public Table sortReverse(boolean reverse) { sortReverse=reverse; return this; }
    public Table sort(int i) {
		sortOn=i;
		return this;
    }
}
