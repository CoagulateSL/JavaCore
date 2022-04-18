package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.System.SystemExecutionException;

import java.util.*;

public class TraceProfiler {

    private static final Map<String, Map<String, Integer>> profiles = new HashMap<>(); // map of profile name to map of stack trace to count

    public static void profile(final String setname) {
        final Throwable t = new SystemExecutionException("Profile tracer");
        final String signature = ExceptionTools.toString(t);
        if (!profiles.containsKey(setname)) {
            profiles.put(setname, new HashMap<>());
        }
        final Map<String, Integer> profile = profiles.get(setname);
        if (!profile.containsKey(signature)) {
            profile.put(signature, 1);
            return;
        }
        profile.replace(signature, profile.get(signature) + 1);
    }

    public static Set<String> profiles() {
        return profiles.keySet();
    }

    public static String reportProfile(final String setname) {
        // seems this class could technically be instantiable but i apparently didn't write it that way
        final Map<String, Integer> profile = new HashMap<>(profiles.get(setname)); // avoid concurrent modification exception :P hopefully
        final StringBuilder report = new StringBuilder();
        report.append("<table border=1><tr><th>Count</th><th>Trace</th></tr>");
        final Set<Integer> counts = new TreeSet<>(Comparator.reverseOrder());
        counts.addAll(profile.values());
        for (final Integer value : counts) {
            for (final Map.Entry<String, Integer> entry : profile.entrySet()) {
                if (entry.getValue().equals(value)) {
                    report.append("<tr><td>").append(value).append("</td><td><pre>").append(entry.getKey()).append("</pre></td></tr>");
                }
            }
        }
        report.append("</table>");
        return report.toString();
    }

}
