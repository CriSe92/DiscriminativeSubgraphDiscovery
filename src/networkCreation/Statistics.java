package networkCreation;

import org.apache.commons.math3.special.*;

public class Statistics {
	
	final static float epsilon = 0.01f;
	
	private static float cbcrt(double n){
		if(n>0)
			return (float) Math.pow(n,1.0/3);
		return (float) (-1*Math.pow(-n,1.0/3));
	}

	private static float f(float rho, float x, float y) {
		return (float) ((y*y-2*rho*x*y+x*x+(1-rho*rho)*Math.log(1-rho*rho)+(2-2*rho*rho)*Math.log(2*Math.PI))/(2*rho*rho-2));
	}
	
	public static float computeCorrelation(float x, float y) {
		float rho = -2;
		float p, q, delta, theta;
		float[] r = new float[4];
		float[] fr = new float[4];
		float max = 0;
		int indMax = 0;
		
		if (Math.abs(x-y)<=epsilon*Math.abs(x)){
	        rho = 1;
	    }else{
	        if(Math.abs(x+y)<=epsilon*Math.abs(x)){//if(x == -y || y ==-x){
				rho = -1;
	        }else{
				p = x*x+y*y-1-x*x*y*y/3;
				q = -x*y + x*y*(x*x+y*y-1)/3 - 2*x*x*x*y*y*y/27;
				delta = q*q/4 + p*p*p/27;

				if(delta >= 0){
					rho = x*y/3 + cbcrt(-q/2+Math.sqrt(delta)) + cbcrt(-q/2-Math.sqrt(delta));
				}else{
					/*funzione logaritmica*/
					/*theta = angle(-q/2 +1i*sqrt(-delta));*/
					theta = (float) Math.atan2(Math.sqrt(-delta), -q/2); 

					r[1] = (float) (x*y/3 +2*Math.sqrt(-p/3)*Math.cos(theta/3));
					r[2] = (float) (x*y/3 +2*Math.sqrt(-p/3)*Math.cos((theta+2*Math.PI)/3));
					r[3] = (float) (x*y/3 +2*Math.sqrt(-p/3)*Math.cos((theta+4*Math.PI)/3)); 

					if(r[1] < 1 && r[1] > -1 ){
						fr[1] = f(r[1],x,y);
						indMax=1;
						max = fr[1];
					}
					if(r[2] < 1 && r[2] > -1){
						fr[2] = f(r[2], x, y);
						if(indMax == 0 || max < fr[2]){
							indMax = 2;
							max = fr[2];
						}
					}
					if(r[3] < 1 && r[3] > -1){
						fr[3] = f(r[3],x,y);
						if(indMax == 0 || max < fr[3]){
							indMax = 3;
						}
					}
					if(indMax == 0){
						System.out.printf("Caso delta < 0 -  Valore di rho (-2) non valido (%f, %f)\n",x,y);
						if(x==y) System.out.println("#############################");
						if(x==-y || y==-x) System.out.println("+++++++++++++++++++++++++++");
						System.exit(1);
					}else{
						rho = r[indMax];
	                }
				}
			}
		}
		return rho;
	}

	
	public static float computeProbability(float x, float y, float rho) {
		if(rho==0) return 1;
		return Math.max(P(x,rho),P(y,rho));
	}
	
	private static float P(double t, double rho){
		double rho2 = rho*rho;
		double delta = t*t*(rho2*rho2-2*rho2+1)-4*rho2*rho2+4*rho2;
		double t1 = ((rho2*t+t)-Math.sqrt(delta))/2*rho;
		double t2 = ((rho2*t+t)+Math.sqrt(delta))/2*rho;
		return normcdf(t2) - normcdf(t1);
	}

	private static float normcdf(double value){
	   double M_SQRT1_2=1/Math.sqrt(2);
	   return (float) (0.5 * Erf.erfc(-value * M_SQRT1_2));
	}
	
	public static void main(String[] args) {
		float[] v={  -2.267186641693115f,
				  -2.100381612777710f,
				  -1.435155153274536f,
				  -2.001576185226440f,
				  -1.960200309753418f,
				  -2.223513603210449f,
				  -1.048972010612488f,
				  -2.027332782745361f,
				   2.285552740097046f,
				  -2.052298307418823f};
		for(int i=0; i<v.length;i++){
			for(int j=i+1; j<v.length;j++){
				float rho = computeCorrelation(v[i],v[j]);
				float p = computeProbability(v[i],v[j],rho);
				System.out.printf("%10.7f (%10.7f)\t",rho,1-p);
			}
			System.out.println();
		}
	}

}
