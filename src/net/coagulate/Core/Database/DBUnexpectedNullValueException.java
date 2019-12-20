package net.coagulate.Core.Database;

public class DBUnexpectedNullValueException extends NoDataException {
	private static final long serialVersionUID=1L;

	public DBUnexpectedNullValueException(final String s) {
		super(s);
	}

	public DBUnexpectedNullValueException(final String e,
	                                      final Throwable t) {
		super(e,t);
	}
}
