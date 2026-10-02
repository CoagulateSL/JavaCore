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
		if (!doneHide) {
			if (sortOn > -1) {
				contents().sort((a, b) -> {
					// only a row whose FIRST cell is a <th> is a real header row; data rows may
					// legitimately contain <th> cells (e.g. ProjectsPage project link), and treating
					// those as headers makes this comparator inconsistent and the sort meaningless
					final boolean aHeader = a instanceof TableRow && ((TableRow) a).get(0) instanceof TableHeader;
					final boolean bHeader = b instanceof TableRow && ((TableRow) b).get(0) instanceof TableHeader;
					if (aHeader) { return bHeader ? 0 : -1; }
					if (bHeader) { return 1; }
					if (a instanceof TableRow && b instanceof TableRow) {
						if (sortType == SORT_TYPE.DOUBLE) {
							Double aa = Double.parseDouble(((TableRow) a).get(sortOn).contents().get(0).toString());
							Double bb = Double.parseDouble(((TableRow) b).get(sortOn).contents().get(0).toString());
							return (sortReverse ? -1 : 1) * aa.compareTo(bb);
						}
						return (sortReverse ? -1 : 1) * (((TableRow) a).get(sortOn)).toString().compareTo(((TableRow) b).get(sortOn).toString());
					}
					return 0;
				});
			}
			if (hideColumn > -1) {
				for (Container content:contents()) {
					if (content instanceof TableRow) { ((TableRow)content).contents().remove(hideColumn); }
				}
			}
		}
		return super.toString();
	}

	public void hideColumn(int i) {
		hideColumn=i;
	}

	private int hideColumn=-1;
	private boolean doneHide=false;
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
