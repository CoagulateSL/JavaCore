package net.coagulate.Core.Tools;

/**
 *
 * @author Iain Price
 */
public class NumberTools {

    public static String fixdp(float number, int dp) {
        System.out.println("SRC "+number);
        int whole=(int) Math.round(Math.floor(number));
        int decimal=Math.round((number-whole)*((float)(10^dp)));
        String decstr=decimal+"";
        while (decstr.length()<dp) { decstr=decstr+"0"; }
        String out=whole+"."+decstr;
        System.out.println("OUT "+out);
        return out;
    }
    
}
