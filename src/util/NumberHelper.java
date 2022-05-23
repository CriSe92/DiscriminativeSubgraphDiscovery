package util;

public class NumberHelper {
	public static float factor = 10000;
	
	public static short toShort(float n) {
		short res = (short)Math.round(n*factor);
		//System.out.println(""+n+" --> "+res);
		return res;
	}
	
	public static float toFloat(short n) {
		float res = n/factor;
		//System.out.println(""+n+" --> "+res);
		return res;
	}
	
	public static void main(String[] args) {
		short n = 9856;
		System.out.println("toFloat: "+NumberHelper.toFloat(n));
		float t = 0.98f;
		System.out.println("toShort: "+NumberHelper.toShort(t));	
	}
}
