package net.coagulate.Database;

/** EXCLUSIVELY used to signal failure to acquire a lock.
 * 
 * Problems releasing a lock are coding errors and fall under SystemException
 *
 * @author Iain Price
 */
public class LockException extends DBException {
    public LockException(String s,Throwable e) {
        super(s,e);
    }
    public LockException(String s) {
        super(s);
    }
    
}
