package com.cjy.lib;

public class MathUtil {
    public static double getMax(double[] arrays){
        double mx = 0;
        for(double d: arrays){
            if(d > mx){
                mx = d;
            }
        }
        return mx;
    }
    public static double getMin(double[] arrays){
        double mx = 99999;
        for(double d: arrays){
            if(d < mx){
                mx = d;
            }
        }
        return mx;
    }
}
