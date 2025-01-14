package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import java.util.concurrent.ArrayBlockingQueue;

/** Accumulates data and can give up some stats on that data */
public class Stats {
	private final ArrayBlockingQueue<Float> storage;
	private final int                       maxSize;
	
	/**
	 * Create a stats store.
	 *
	 * @param size How many elements we store.
	 */
	public Stats(final int size) {
		maxSize=size;
		storage=new ArrayBlockingQueue<>(maxSize);
	}
	
	/**
	 * Add a value to the stats pool.
	 * Pushing a value off if necessary
	 *
	 * @param value The value to add
	 */
	public void add(final float value) {
		synchronized(storage) {
			if (storage.size()==maxSize) {
				storage.poll();
			}
			try {
				storage.put(value);
			} catch (final InterruptedException ignore) {
			}
		}
	}
	
	/**
	 * Return the current Statistics
	 *
	 * @return A Statistics object
	 */
	@Nonnull
	public Statistics statistics() {
		synchronized(storage) {
			if (storage.isEmpty()) {
				return new Statistics(0,0,0,0,0);
			}
			float elements=0;
			float min=Float.MAX_VALUE;
			float max=Float.MIN_VALUE;
			float mean=0;
			final Float[] values=storage.toArray(new Float[0]);
			for (final float f: values) {
				elements++;
				if (min>f) {
					min=f;
				}
				if (f>max) {
					max=f;
				}
				mean+=f;
			}
			mean=mean/elements;
			float standardDeviation=0;
			for (final float f: values) {
				standardDeviation+=((f-mean)*(f-mean));
			}
			standardDeviation=((float)Math.sqrt(standardDeviation/elements));
			return new Statistics((int)elements,min,max,mean,standardDeviation);
		}
	}
	
	/** Statistics */
	public record Statistics(int elements,float min,float max,float average,float stdDev) {
		public String toString() {
			return "[Size:"+elements+", Max:"+max+", Min:"+min+", Avg:"+average+", StdDev:"+stdDev+"]";
		}
	}
}
