package net.coagulate.Core.Tools;

/**
 *
 * @author Iain Price
 */
public class NumberTools {
    private static final boolean debug=true;
    public static String fixdp(float number, int dp) {
        if (debug) { System.out.println("SRC "+number+" for dp "+dp+" = "+(10^dp)); }
        int whole=(int) Math.round(Math.floor(number));
        if (debug) { System.out.println("whole:"+whole); }
        int decimal=Math.round((number-((float)whole))*((float)(10^dp)));
        if (debug) { System.out.println("dec:"+decimal); }
        String decstr=decimal+"";
        if (debug) { System.out.println("decstr:"+decstr); }
        while (decstr.length()<dp) { decstr=decstr+"0"; }
        if (debug) { System.out.println("decstr:"+decstr); }
        String out=whole+"."+decstr;
        if (debug) { System.out.println("OUT "+out); }
        return out;
    }
    
}
