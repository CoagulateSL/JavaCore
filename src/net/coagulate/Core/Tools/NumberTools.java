package net.coagulate.Core.Tools;

/**
 *
 * @author Iain Price
 */
public class NumberTools {

    public static String fixdp(float number, int dp) {
        System.out.println("SRC "+number);
        int whole=(int) Math.round(Math.floor(number));
        System.out.println("whole:"+whole);
        int decimal=Math.round((number-whole)*((float)(10^dp)));
        System.out.println("dec:"+decimal);
        String decstr=decimal+"";
        System.out.println("decstr:"+decstr);
        while (decstr.length()<dp) { decstr=decstr+"0"; }
        System.out.println("decstr:"+decstr);
        String out=whole+"."+decstr;
        System.out.println("OUT "+out);
        return out;
    }
    
}
