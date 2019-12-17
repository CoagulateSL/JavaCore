package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.System.SystemConsistencyException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.util.logging.Level.*;

/**
 * @author Iain Price
 */
public abstract class ClassTools {
	private static final boolean DEBUG = false;
	private static final Object initlock = new Object();
	private static boolean initialised;
	@Nullable
	private static Set<Class<? extends Object>> classmap;
	private static int totalclasses;

	@Nonnull
	public static Set<Class<? extends Object>> getAnnotatedClasses(final Class<? extends Annotation> annotation) {
		final Set<Class<? extends Object>> classes = new HashSet<>();
		for (final Class<? extends Object> c : getClassmap()) {
			if (c.isAnnotationPresent(annotation)) { classes.add(c); }
		}
		return classes;
	}

	@Nonnull
	public static Set<Method> getAnnotatedMethods(final Class<? extends Annotation> annotation) {
		final Set<Method> methods = new HashSet<>();
		for (final Class<? extends Object> c : getClassmap()) {
			for (final Method m : c.getMethods()) {
				if (m.isAnnotationPresent(annotation)) { methods.add(m); }
			}
		}
		return methods;
	}


	@Nonnull
	public static Set<Constructor<? extends Object>> getAnnotatedConstructors(final Class<? extends Annotation> annotation) {
		final Set<Constructor<? extends Object>> constructors = new HashSet<>();
		for (final Class<? extends Object> c : getClassmap()) {
			try {
				final Constructor<? extends Object> cons = c.getConstructor();
				if (cons.isAnnotationPresent(annotation)) { constructors.add(cons); }
			} catch (@Nonnull final NoSuchMethodException | SecurityException ex) {
				// no such method - this is fine, many classes wont have a zero param constructor =)
				// securityexception - this is also fine, we'll find protected classes or other things we're not supposed to instansiate, so we wont.
			}
		}
		return constructors;
	}


	public static boolean initialised() { synchronized (initlock) { return initialised; } }

	public static void initialise() {
		try {
			synchronized (initlock) {
				if (initialised) { return; }
				Logger.getLogger(ClassTools.class.getCanonicalName()).log(CONFIG, "Commencing classpath scanning");
				Logger.getLogger(ClassTools.class.getCanonicalName()).log(CONFIG, "Classpath scanner found " + getClassmap().size() + " classes, " + totalclasses + " scanned.");
				initialised = true;
			}
		} catch (@Nonnull final Throwable t) {
			System.out.println("Class explorer leaked " + t);
		}
	}

	@Nonnull
	public static Set<Class<? extends Object>> getClasses() {
		if (initialised()) { return getClassmap(); }
		initialise();
		return getClassmap();
	}

	@Nonnull
	public static Set<Class<? extends Object>> enumerateClasses() {
		final Set<Class<? extends Object>> classes = new HashSet<>();
		if (DEBUG) { System.out.println("CLASS PATH IS " + System.getProperty("java.class.path")); }
		for (final String element : System.getProperty("java.class.path").split(Pattern.quote(System.getProperty("path.separator")))) {
			try {
				if (DEBUG) { System.out.println("Path element: " + element); }
				final File f = new File(element);
				inspectFile(f, f, classes);
			} catch (@Nonnull final IOException e) {
				if (DEBUG) {
					Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Exceptioned loading classpath element " + element + " - " + e.getLocalizedMessage());
				}
			}
		}
		if (DEBUG) {
			System.out.println("FINAL LIST OF ENUMERATED ACCEPTED CLASSES:");
			for (final Class<? extends Object> c : classes) { System.out.println(c.getCanonicalName()); }
		}
		return classes;
	}

	private static void inspectFile(@Nonnull final File f, @Nonnull final File base, @Nonnull final Set<Class<? extends Object>> classes) throws IOException {
		if (DEBUG) { System.out.println("Inspecting file " + f + " in base " + base); }
		if (f.isDirectory()) {
			recurse(f, base, classes);
			return;
		}
		if (f.getAbsolutePath().toLowerCase().endsWith(".jar")) {
			recurseJar(f, classes);
			return;
		}
		if (f.getAbsolutePath().toLowerCase().endsWith(".class")) {
			recurseClass(f.getAbsolutePath().substring(base.getAbsolutePath().length() + 1), classes);
			return;
		}
		if (DEBUG) { System.out.println("Unhandled file in inspectFile: " + f.getAbsolutePath()); }
	}

	// takes a given location (classpath element or discovered) and iterates (recursively for directories) over its contents, extracting the class name and examining that
	private static void recurse(@Nonnull final File directory, @Nonnull final File base, @Nonnull final Set<Class<? extends Object>> classes) {
		if (DEBUG) {
			System.out.println("Dir recurse in " + directory.getAbsolutePath() + " base " + base.getAbsolutePath());
		}
		final File[] content = directory.listFiles();
		if (content==null) { throw new SystemConsistencyException("Failed to enumerate directory, it returned null for listFiles()"); }
		for (final File f : content) {
			try { inspectFile(f, base, classes); } catch (@Nonnull final IOException e) {
				if (DEBUG) {
					Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Exceptioned recursing " + f + " - " + e.getLocalizedMessage());
				}
			}
		}
	}

	private static void recurseClass(String fullname, @Nonnull final Set<Class <? extends Object>> classes) {
		if (DEBUG) { System.out.println("Class consider " + fullname); }
		fullname = fullname.replaceAll("\\.class$", "");
		String relativename = "";
		for (final String element : fullname.split(Pattern.quote("\\"))) {
			if (!"".equals(relativename)) { relativename += "."; }
			relativename += element;
		}
		String classname = "";
		for (final String element : relativename.split(Pattern.quote("/"))) {
			if (!"".equals(classname)) { classname += "."; }
			classname += element;
		}
		totalclasses++;
		if (!classname.startsWith("net.coagulate.")) {
			if (DEBUG) { System.out.println("Drop " + classname + " due to prefix failure"); }
			return;
		}
		// examine named class and instansiate if appropriate.
		try {
			//System.out.println(classname);
			final Class<? extends Object> c = Class.forName(classname);
			classes.add(c);
			if (DEBUG) { System.out.println("ADDED " + classname); }
		} catch (@Nonnull final Throwable e) { // note this is usually bad.  but we can get 'errors' here we dont care about
			if (DEBUG) { System.out.println("FAILED TO ADD " + classname + " because " + e); }
			//Logger.getLogger(ClassTools.class.getCanonicalName()).log(INFO,"Failed to load class "+classname+" : "+e.getLocalizedMessage());
			Logger.getLogger(ClassTools.class.getCanonicalName()).log(SEVERE, "Failed to load class " + classname, e);
		}
		if (DEBUG) { System.out.println("Post processed " + classname); }
	}

	private static void recurseJar(@Nonnull final File f, @Nonnull final Set<Class<? extends Object>> classes) throws IOException {
		if (DEBUG) { System.out.println("JAR recurse in " + f.getAbsolutePath()); }
		if (!f.canRead()) {
			Logger.getLogger(ClassTools.class.getCanonicalName()).log(INFO, "Unreadable file accessed during class scanning:" + f.getAbsolutePath());
			return;
		}
		final ZipInputStream zip = new ZipInputStream(new FileInputStream(f.getAbsolutePath()));
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				// This ZipEntry represents a class. Now, what class does it represent?
				final String classname = entry.getName();
				recurseClass(classname, classes);
			}
			if ("META-INF/MANIFEST.MF".equals(entry.getName())) {
				if (DEBUG) { System.out.println("WE HAVE A MANIFEST!!!!"); }
				final Manifest manifest = new Manifest(zip);
				if (manifest.getMainAttributes().getValue("Class-Path") != null) {
					for (final String element : manifest.getMainAttributes().getValue("Class-Path").split(" ")) {
						if (DEBUG) { System.out.println(f.getParentFile().getCanonicalPath() + "/" + element); }
						try {
							inspectFile(new File(f.getParentFile().getCanonicalPath() + "/" + element), f.getParentFile(), classes);
						} catch (@Nonnull final Exception e) {
							if (DEBUG) {
								Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Failed to recurse MANIFEST.MF", e);
							}
						}
					}
				}
			}
		}
	}


	@Nonnull
	private static Set<Class<? extends Object>> getClassmap() {
		if (classmap==null) { classmap=enumerateClasses(); }
		return classmap;
	}

}
