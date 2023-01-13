package net.coagulate.Core.Database;


import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Unpacks a resultset into a independant java datastructure.
 * Essentially a List of Rows, and a Row is a String,String Map of column names to values.
 * Implements iterable, thus can be .iterator()ed or foreached (for (Row row:results) { code; } )
 */
public class Results implements Iterable<ResultsRow> {
	private final List<ResultsRow> data     =new ArrayList<>();
	private       String           statement="";
	
	/**
	 * Unpack a resultset into our data structure.
	 * After this, the resultset / connection can be released (which is done in Database class)
	 *
	 * @param rs The ResultSet
	 */
	public Results(@Nonnull final ResultSet rs) {
		try {
			//final ResultSetMetaData rsmd=rs.getMetaData();
			while (rs.next()) {
				final ResultsRow r=new ResultsRow(rs);
				data.add(r);
			}
		} catch (@Nonnull final SQLException ex) {
			throw new DBException("SQLException reading a resultset from SQL:'"+statement+"'",ex);
		}
	}
	
	// ---------- INSTANCE ----------
	
	/**
	 * Iterator interface
	 *
	 * @return Iterator over the rows of the Results
	 */
	@Override
	@Nonnull
	public Iterator<ResultsRow> iterator() {
		return data.iterator();
	}
	
	/**
	 * Gets the first row of the results set.
	 *
	 * @throws NoDataException if there isn't a first row
	 */
	@Nonnull
	public ResultsRow first() {
		if (size()<1) {
			throw new NoDataException("There are no rows");
		}
		return data.get(0);
	}
	
	/**
	 * Size of results
	 *
	 * @return ResultsRow count
	 */
	public int size() {
		return data.size();
	}
	
	/**
	 * Convenience method.
	 * Returns the literal opposite of empty()
	 *
	 * @return true if the results are not empty()
	 */
	public boolean notEmpty() {
		return (!(empty()));
	}
	
	/**
	 * Convenience method
	 *
	 * @return True if zero elements
	 */
	public boolean empty() {
		return size()==0;
	}
	
	/**
	 * Get the set statement associated with this result set.
	 *
	 * @return The statement set earlier by setStatement() or the blank string.
	 */
	@Nonnull
	public String getStatement() {
		return statement;
	}
	
	/**
	 * Set the statement associated with this result set.
	 *
	 * @param stmt SQL Statement
	 */
	public void setStatement(@Nonnull final String stmt) {
		statement=stmt;
	}
}
