package net.coagulate.Core.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.Manifest;
import static java.util.logging.Level.CONFIG;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author Iain Price
 */
public abstract class ClassTools {
    private static final boolean DEBUG=true;
    private static final Object initlock=new Object();
    private static boolean initialised=false;
    private static Set<Class> classmap=null;
    
    @SuppressWarnings("unchecked")
    public static Set<Class> getAnnotatedClasses(Class<? extends Annotation> annotation) {
        Set<Class> classes=new HashSet<>();
        for (Class c:classmap) {
            if (c.isAnnotationPresent(annotation)) { classes.add(c); }
        }
        return classes;
    }
    
    public static Set<Method> getAnnotatedMethods(Class<? extends Annotation> annotation) {
        Set<Method> methods=new HashSet<>();
        for (Class c:classmap) {
            for (Method m:c.getMethods()) {
                if (m.isAnnotationPresent(annotation)) { methods.add(m); }
            }
        }
        return methods;
    }
    
    
    
    
    
    
    public static boolean initialised() { synchronized(initlock) { return initialised; } }
    public static void initialise() {
        try {
            synchronized(initlock) { 
                if (initialised) { return; }
                Logger.getLogger(ClassTools.class.getCanonicalName()).log(CONFIG,"Commencing classpath scanning");
                classmap=enumerateClasses();
                Logger.getLogger(ClassTools.class.getCanonicalName()).log(CONFIG,"Classpath scanner found "+classmap.size()+" classes");
                initialised=true;
            }
        }
        catch (Throwable t) {
            System.out.println("Class explorer leaked "+t);
        }
    }
    public static Set<Class> getClasses() {
        if (initialised()) { return classmap; }
        initialise(); 
        return classmap;
    }
        
    public static Set<Class> enumerateClasses() throws IOException {
        Set<Class> classes=new HashSet<>(); 
        if (DEBUG) { System.out.println("CLASS PATH IS "+System.getProperty("java.class.path")); }
        for (String element : System.getProperty("java.class.path").split(Pattern.quote(System.getProperty("path.separator")))) {
            if (DEBUG) { System.out.println("Path element: "+element); }
            File f=new File(element);
            inspectFile(f,f,classes);
        }
        Enumeration<URL> resources = ClassTools.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (resources.hasMoreElements()) {
            try {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                for (String m:manifest.getEntries().keySet()) { System.out.println("Manifest produced "+m); }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (DEBUG) { System.out.println("FINAL LIST OF ENUMERATED ACCEPTED CLASSES:"); 
            for (Class c:classes) { System.out.println(c.getCanonicalName()); }
        }
        return classes;
    }
    
    private static void inspectFile(File f,File base,Set<Class> classes) throws IOException {
        if (f.isDirectory()) { recurse(f,base,classes); return;}
        if (f.getAbsolutePath().toLowerCase().endsWith(".jar")) { recurseJar(f,classes); return; }
        if (f.getAbsolutePath().toLowerCase().endsWith(".class")) { recurseClass(f.getAbsolutePath().substring(base.getAbsolutePath().length()+1),classes); return; }
        if (DEBUG) { System.out.println("Unhandled file in inspectFile: "+f.getAbsolutePath()); }
    }
            
    // takes a given location (classpath element or discovered) and iterates (recursively for directories) over its contents, extracting the class name and examining that
    private static void recurse(File directory,File base,Set<Class> classes) throws IOException{
        if (DEBUG) { System.out.println("Dir recurse in "+directory.getAbsolutePath()+" base "+base.getAbsolutePath()); }
        File[] content=directory.listFiles();
        for (File f:content) {
            inspectFile(f,base,classes);
        }
    }
    
    private static void recurseClass(String fullname,Set<Class> classes) throws IOException {
        if (DEBUG) { System.out.println("Class consider "+fullname); }
        fullname=fullname.replaceAll("\\.class$","");
        String relativename="";
        for (String element:fullname.split(Pattern.quote("\\"))) {
            if (!relativename.equals("")) { relativename+="."; }
            relativename+=element;
        }
        String classname="";
        for (String element:relativename.split(Pattern.quote("/"))) {
            if (!classname.equals("")) { classname+="."; }
            classname+=element;
        }
        
        if (!classname.startsWith("net.coagulate.")) { return; }
        // examine named class and instansiate if appropriate.
        try {
            Class c=Class.forName(classname);
            classes.add(c);
        } catch (Throwable e) { // note this is usually bad.  but we can get 'errors' here we dont care about
            //Logger.getLogger(ClassTools.class.getCanonicalName()).log(INFO,"Failed to load class "+classname+" : "+e.getLocalizedMessage());
        }
    }

    private static void recurseJar(File f, Set<Class> classes) throws IOException {
        if (DEBUG) { System.out.println("JAR recurse in "+f.getAbsolutePath()); }
        ZipInputStream zip = new ZipInputStream(new FileInputStream(f.getAbsolutePath()));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // This ZipEntry represents a class. Now, what class does it represent?
                String classname = entry.getName();
                recurseClass(classname,classes);
            }
            if (entry.getName().equals("META-INF/MANIFEST.MF")) { System.out.println("WE HAVE A MANIFEST!!!!"); 
                Manifest manifest=new Manifest(zip);
                for (Object a:manifest.getMainAttributes().keySet()) {
                    System.out.println("Main Attribute: "+a.toString());
                }
                System.out.println("AAA:"+manifest.getMainAttributes().getValue("Class-Path"));
                //byte[] array=new byte[(int)entry.getSize()];
                //zip.read(array);
                //System.out.println(new String(array));
            }
        }
    }
        
    
}
