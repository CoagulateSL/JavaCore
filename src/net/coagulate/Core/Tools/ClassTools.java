package net.coagulate.Core.Tools;

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
	private static boolean initialised = false;
	private static Set<Class> classmap = null;
	private static int totalclasses = 0;

	@SuppressWarnings("unchecked")
	public static Set<Class> getAnnotatedClasses(Class<? extends Annotation> annotation) {
		Set<Class> classes = new HashSet<>();
		for (Class c : classmap) {
			if (c.isAnnotationPresent(annotation)) { classes.add(c); }
		}
		return classes;
	}

	public static Set<Method> getAnnotatedMethods(Class<? extends Annotation> annotation) {
		Set<Method> methods = new HashSet<>();
		for (Class c : classmap) {
			for (Method m : c.getMethods()) {
				if (m.isAnnotationPresent(annotation)) { methods.add(m); }
			}
		}
		return methods;
	}


	public static Set<Constructor> getAnnotatedConstructors(Class<? extends Annotation> annotation) {
		Set<Constructor> constructors = new HashSet<>();
		for (Class c : classmap) {
			try {
				Constructor cons = c.getConstructor(new Class[0]);
				if (cons.isAnnotationPresent(annotation)) { constructors.add(cons); }
			} catch (NoSuchMethodException | SecurityException ex) {
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
				classmap = enumerateClasses();
				Logger.getLogger(ClassTools.class.getCanonicalName()).log(CONFIG, "Classpath scanner found " + classmap.size() + " classes, " + totalclasses + " scanned.");
				initialised = true;
			}
		} catch (Throwable t) {
			System.out.println("Class explorer leaked " + t);
		}
	}

	public static Set<Class> getClasses() {
		if (initialised()) { return classmap; }
		initialise();
		return classmap;
	}

	public static Set<Class> enumerateClasses() throws IOException {
		Set<Class> classes = new HashSet<>();
		if (DEBUG) { System.out.println("CLASS PATH IS " + System.getProperty("java.class.path")); }
		for (String element : System.getProperty("java.class.path").split(Pattern.quote(System.getProperty("path.separator")))) {
			try {
				if (DEBUG) { System.out.println("Path element: " + element); }
				File f = new File(element);
				inspectFile(f, f, classes);
			} catch (IOException e) {
				if (DEBUG) {
					Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Exceptioned loading classpath element " + element + " - " + e.getLocalizedMessage());
				}
			}
		}
		if (DEBUG) {
			System.out.println("FINAL LIST OF ENUMERATED ACCEPTED CLASSES:");
			for (Class c : classes) { System.out.println(c.getCanonicalName()); }
		}
		return classes;
	}

	private static void inspectFile(File f, File base, Set<Class> classes) throws IOException {
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
	private static void recurse(File directory, File base, Set<Class> classes) throws IOException {
		if (DEBUG) {
			System.out.println("Dir recurse in " + directory.getAbsolutePath() + " base " + base.getAbsolutePath());
		}
		File[] content = directory.listFiles();
		for (File f : content) {
			try { inspectFile(f, base, classes); } catch (IOException e) {
				if (DEBUG) {
					Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Exceptioned recursing " + f + " - " + e.getLocalizedMessage());
				}
			}
		}
	}

	private static void recurseClass(String fullname, Set<Class> classes) throws IOException {
		if (DEBUG) { System.out.println("Class consider " + fullname); }
		fullname = fullname.replaceAll("\\.class$", "");
		String relativename = "";
		for (String element : fullname.split(Pattern.quote("\\"))) {
			if (!relativename.equals("")) { relativename += "."; }
			relativename += element;
		}
		String classname = "";
		for (String element : relativename.split(Pattern.quote("/"))) {
			if (!classname.equals("")) { classname += "."; }
			classname += element;
		}
		totalclasses++;
		if (!classname.startsWith("net.coagulate.")) {
			if (DEBUG) { System.out.println("Drop " + classname + " due to prefix failure"); }
			return;
		}
		// examine named class and instansiate if appropriate.
		try {
			Class c = Class.forName(classname);
			classes.add(c);
			if (DEBUG) { System.out.println("ADDED " + classname); }
		} catch (Throwable e) { // note this is usually bad.  but we can get 'errors' here we dont care about
			if (DEBUG) { System.out.println("FAILED TO ADD " + classname + " because " + e); }
			//Logger.getLogger(ClassTools.class.getCanonicalName()).log(INFO,"Failed to load class "+classname+" : "+e.getLocalizedMessage());
			Logger.getLogger(ClassTools.class.getCanonicalName()).log(SEVERE, "Failed to load class " + classname, e);
		}
		if (DEBUG) { System.out.println("Post processed " + classname); }
	}

	private static void recurseJar(File f, Set<Class> classes) throws IOException {
		if (DEBUG) { System.out.println("JAR recurse in " + f.getAbsolutePath()); }
		if (!f.canRead()) {
			Logger.getLogger(ClassTools.class.getCanonicalName()).log(INFO, "Unreadable file accessed during class scanning:" + f.getAbsolutePath());
			return;
		}
		ZipInputStream zip = new ZipInputStream(new FileInputStream(f.getAbsolutePath()));
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
				// This ZipEntry represents a class. Now, what class does it represent?
				String classname = entry.getName();
				recurseClass(classname, classes);
			}
			if (entry.getName().equals("META-INF/MANIFEST.MF")) {
				if (DEBUG) { System.out.println("WE HAVE A MANIFEST!!!!"); }
				Manifest manifest = new Manifest(zip);
				if (manifest.getMainAttributes().getValue("Class-Path") != null) {
					for (String element : manifest.getMainAttributes().getValue("Class-Path").split(" ")) {
						if (DEBUG) { System.out.println(f.getParentFile().getCanonicalPath() + "/" + element); }
						try {
							inspectFile(new File(f.getParentFile().getCanonicalPath() + "/" + element), f.getParentFile(), classes);
						} catch (Exception e) {
							if (DEBUG) {
								Logger.getLogger(ClassTools.class.getCanonicalName()).log(WARNING, "Failed to recurse MANIFEST.MF", e);
							}
						}
					}
				}
			}
		}
	}


}
