package net.coagulate.Core.Tools;

import java.util.HashMap;
import java.util.Map;

public class StackTraceProfiler {
    private static final Map<String,Map<String, Map<Integer,Integer>>> profiled=new HashMap<>();

    public static void profile() {
        for (StackTraceElement[] stackTrace:Thread.getAllStackTraces().values()) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                record(stackTraceElement.getClassName(),stackTraceElement.getMethodName(),stackTraceElement.getLineNumber());
            }
        }
    }

    private static void record(String className, String methodName, int lineNumber) {
        if (!profiled.containsKey(className)) { profiled.put(className, new HashMap<>()); }
        Map<String,Map<Integer,Integer>> classProfile=profiled.get(className);
        if (!classProfile.containsKey(methodName)) { classProfile.put(methodName, new HashMap<>()); }
        Map<Integer,Integer> methodProfile=classProfile.get(methodName);
        if (!methodProfile.containsKey(lineNumber)) { methodProfile.put(lineNumber,1); }
        else { methodProfile.put(lineNumber,methodProfile.get(lineNumber)+1); }
    }

    public static String htmlDump() {
        StringBuilder ret=new StringBuilder();
        ret.append("<table><tr><th>Class</th><th>Method</th><th>Line Number</th><th>Count</th></tr>");
        for (Map.Entry<String, Map<String, Map<Integer, Integer>>> perClass:profiled.entrySet()) {
            int classTotal=0;
            for (Map.Entry<String, Map<Integer, Integer>> perMethod:perClass.getValue().entrySet()) {
                int methodTotal=0;
                for (Map.Entry<Integer, Integer> perLine:perMethod.getValue().entrySet()) {
                    ret.append("<tr><td>").append(perClass.getKey()).append("</td><td>").append(perMethod.getKey()).append("</td><td>").append(perLine.getKey()).append("</td><td>").append(perLine.getValue()).append("</td></tr>");
                    methodTotal+=perLine.getValue();
                }
                ret.append("<tr><td>").append(perClass.getKey()).append("</td><td>").append(perMethod.getKey()).append("</td><td></td><td>").append(methodTotal).append("</td></tr>");
                classTotal+=methodTotal;
            }
            ret.append("<tr><td>").append(perClass.getKey()).append("</td><td></td><td></td><td>").append(classTotal).append("</td></tr>");
        }
        ret.append("</table>");
        return ret.toString();
    }
}
