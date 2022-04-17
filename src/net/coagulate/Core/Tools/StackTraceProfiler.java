package net.coagulate.Core.Tools;

import java.util.HashMap;
import java.util.Map;

public class StackTraceProfiler extends Thread {
    private static final Map<String,Map<String, Map<Integer,Integer>>> profiled=new HashMap<>();

    public static void profile() {
        for (final StackTraceElement[] stackTrace : Thread.getAllStackTraces().values()) {
            for (final StackTraceElement stackTraceElement : stackTrace) {
                record(stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber());
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {Thread.sleep(1000);profile();} catch (final Exception ignore) {
            }
        }
    }

    private static void record(final String className, final String methodName, final int lineNumber) {
        if (!className.startsWith("net.coagulate")) {
            return;
        }
        if (!profiled.containsKey(className)) {
            profiled.put(className, new HashMap<>());
        }
        final Map<String, Map<Integer, Integer>> classProfile = profiled.get(className);
        if (!classProfile.containsKey(methodName)) {
            classProfile.put(methodName, new HashMap<>());
        }
        final Map<Integer, Integer> methodProfile = classProfile.get(methodName);
        if (!methodProfile.containsKey(lineNumber)) {
            methodProfile.put(lineNumber, 1);
        } else {
            methodProfile.put(lineNumber, methodProfile.get(lineNumber) + 1);
        }
    }

    public static String htmlDump() {
        final StringBuilder ret = new StringBuilder();
        ret.append("<table><tr><th>Class</th><th>Method</th><th>Line Number</th><th>Count</th></tr>");
        for (final Map.Entry<String, Map<String, Map<Integer, Integer>>> perClass : profiled.entrySet()) {
            int classTotal = 0;
            for (final Map.Entry<String, Map<Integer, Integer>> perMethod : perClass.getValue().entrySet()) {
                int methodTotal = 0;
                for (final Map.Entry<Integer, Integer> perLine : perMethod.getValue().entrySet()) {
                    ret.append("<tr><td>").append(perClass.getKey()).append("</td><td>").append(perMethod.getKey()).append("</td><td>").append(perLine.getKey()).append("</td><td>").append(perLine.getValue()).append("</td></tr>");
                    methodTotal += perLine.getValue();
                }
                ret.append("<tr><td>").append(perClass.getKey()).append("</td><td>").append(perMethod.getKey()).append("</td><td></td><td>").append(methodTotal).append("</td></tr>");
                classTotal += methodTotal;
            }
            ret.append("<tr><td>").append(perClass.getKey()).append("</td><td></td><td></td><td>").append(classTotal).append("</td></tr>");
        }
        ret.append("</table>");
        return ret.toString();
    }
}
