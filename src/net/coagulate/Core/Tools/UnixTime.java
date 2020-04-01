package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Iain Price
 */
public abstract class UnixTime {

	public static final int SECOND=1;
	public static final int MINUTE=60*SECOND;
	public static final int HOUR=60*MINUTE;
	public static final int DAY=24*HOUR;
	public static final int WEEK=7*DAY;
	public static final int MONTH=4*WEEK;
	public static final int YEAR=365*DAY;

	// ---------- STATICS ----------

	/**
	 * Convert a specific date time into unixtime
	 *
	 * @param timezone Time zone
	 * @param day      Day of month
	 * @param month    Month of year
	 * @param year     Year (2 digit or 4.  2 digit assumed post y2k)
	 * @param hour     Hour
	 * @param minute   Minute
	 *
	 * @return Representative unixtime
	 */
	public static int create(final String timezone,
	                         final int day,
	                         int month,
	                         int year,
	                         final int hour,
	                         final int minute) {
		final DateFormat dfslt=DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
		dfslt.setTimeZone(TimeZone.getTimeZone(timezone));
		if (year<100) {
			year+=2000;
		} // y2k1 bug.   but then we have a 2037 bug too.
		final Calendar cal=Calendar.getInstance(TimeZone.getTimeZone(timezone));
		//System.out.println(day+" "+month+" "+year+" "+hour+" "+minute);
		month--;
		//System.out.println(day+" "+month+" "+year+" "+hour+" "+minute);
		cal.set(year,month,day,hour,minute,0);
		final Date d=cal.getTime();
		long l=d.getTime();
		l=(l/(long) 1000.0);
		return (int) l;
	}

	/**
	 * Convenience method to get time in "unix seconds-since-epoch" format, as used by LSL llGetUnixTime().
	 * This clock and format is used as our base clock.
	 * Note this breaks circa 2037.  T-20 years at time of writing.
	 * But then so does Second Life's mechanic of time, so they must change something before then (or die before then).
	 * At which point we'll probably just convert + follow suit in an actual maintenance window.  In 20 years time.  Lol.
	 *
	 * @return Current clock, unix epoch format (number of seconds since 01/01/1970 00:00:00 GMT).
	 */
	public static int getUnixTime() {
		return (int) (new Date().getTime()/1000.0);
	}

	@Nonnull
	public static String fromUnixTime(final int date,
	                                  @Nonnull final String timezone) {
		final DateFormat df=DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM);
		df.setTimeZone(TimeZone.getTimeZone(timezone));
		return fromUnixTime(date,df);
	}

	@Nonnull
	public static String fromUnixTime(@Nonnull final String date,
	                                  final String timezone) {
		return fromUnixTime(Integer.parseInt(date),timezone);
	}

	/**
	 * Calculates the relative difference in timestamps.
	 *
	 * @param unixtime Unix time to compare to now
	 *
	 * @return seconds difference, -ve for before now, +ve for after now, 0=now
	 */
	public static int relativeToNow(final int unixtime) {
		return getUnixTime()-unixtime;
	}

	/**
	 * Convert a duration (scalar time units) to a string without seconds
	 *
	 * @param seconds How many seconds the duration is
	 *
	 * @return Duration as a string (e.g. 3h 2m)
	 */
	@Nonnull
	public static String duration(final int seconds) {
		return duration(seconds,false);
	}

	/**
	 * Convert a duration (scalar time units) to a string
	 *
	 * @param t       How many seconds the duration is
	 * @param precise Include number of seconds in the output
	 *
	 * @return Duration as a string (e.g. 3h 2m 1s)
	 */
	@Nonnull
	public static String duration(int t,
	                              final boolean precise) {
		String prefix="";
		if (t<0) {
			prefix="T-";
			t=-t;
		}
		final int seconds=t%60;
		t=t/60;
		final int minutes=t%60;
		t=t/60;
		final int hours=t%24;
		t=t/24;
		final int days=t%7;
		t=t/7;
		final int weeks=t%4;
		t=t/4;
		final int months=t;
		if (precise) {
			boolean o=false;
			String r=prefix;
			if (months>0) {
				r=r+months+"mo ";
				o=true;
			}
			if (weeks>0 || o) {
				r=r+weeks+"w ";
				o=true;
			}
			if (days>0 || o) {
				r=r+days+"d ";
				o=true;
			}
			if (hours>0 || o) {
				r=r+hours+"h ";
				o=true;
			}
			if (minutes>0 || o) {
				r=r+minutes+"m ";
				o=true;
			}
			if (seconds>0 || o) {
				r=r+seconds+"s ";
				//o = true;
			}
			return r;
		}
		String r=prefix;
		int steps=0;
		if (months>0) {
			r=r+months+"mo";
			steps++;
		}
		if (weeks>0 || steps>0) {
			if (weeks>0) {
				r=r+weeks+"w";
			}
			steps++;
		}
		if (steps==2) {
			return r;
		}
		if (days>0 || steps>0) {
			if (days>0) {
				r=r+days+"d";
			}
			steps++;
		}
		if (steps==2) {
			return r;
		}
		if (hours>0 || steps>0) {
			if (hours>0) {
				r=r+hours+"h";
			}
			steps++;
		}
		if (steps==2) {
			return r;
		}
		if (minutes>0 || steps>0) {
			if (minutes>0) {
				r=r+minutes+"m";
			}
			steps++;
		}
		if (steps==2) {
			return r;
		}
		if (seconds>0 || steps>0) {
			if (seconds>0) {
				r=r+seconds+"s";
			}
			//steps++;
		}
		return r;
	}

	/**
	 * Express the difference between timestamps as a string
	 *
	 * @param unixtime Time to compare the difference with
	 *
	 * @return Time between now and then expressed as a duration, with seconds
	 */
	@Nonnull
	public static String durationRelativeToNow(final int unixtime) {
		return durationRelativeToNow(unixtime,true);
	}

	/**
	 * Express the difference between timestamps as a string
	 *
	 * @param unixtime    Time to compare the difference with
	 * @param withseconds Include the seconds in the duration string
	 *
	 * @return Time between now and then expressed as a duration, with optional seconds
	 */
	@Nonnull
	public static String durationRelativeToNow(final int unixtime,
	                                           final boolean withseconds) {
		return duration(relativeToNow(unixtime),withseconds);
	}

	// ----- Internal Statics -----
	@Nonnull
	private static String fromUnixTime(final int date,
	                                   @Nonnull final DateFormat df) {
		return df.format(new Date(((long) (date))*((long) 1000)));
	}

}
