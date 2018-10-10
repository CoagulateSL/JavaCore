package net.coagulate.Core.Database;

/** Exception thrown when one row is expected but multiple are found.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class TooMuchDataException extends DBException {
    public TooMuchDataException(String s) { super(s); }
    public TooMuchDataException(String e,Throwable t) { super(e,t); }
}