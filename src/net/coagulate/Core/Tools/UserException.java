package net.coagulate.Core.Tools;

/** Checked exception indicating an error with a users request.
 * These should be intercepted by the interface and shown appropriately.
 * @author Iain Price
 */
public class UserException extends Exception {
    public UserException(String reason) { super(reason); }
    public UserException(String reason,Throwable cause) { super(reason,cause); }
}
