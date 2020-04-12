package com.cjy.lib;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    private static int interval = 15;
    private static List<String[]> timespan = new ArrayList<String[]>();

    public static List<String[]> initTimeSpan(){
        int hour = 0;
        int minute = 0;
        while(hour < 24) {
            int nm = minute + interval;
            int nh = hour;
            if(nm >= 60){
                nh = hour + 1;
                nm = 0;
            }
            String[] tmp = {getTime(hour, minute), getTime(nh, nm)};
            timespan.add(tmp);
            hour = nh;
            minute = nm;
        }
        return timespan;
    }
    private static String getTime(int hour, int minute){
        String h = hour < 10 ? "0" + hour : hour + "";
        String m = minute < 10 ? "0" + minute : minute + "";
        return h + ":" + m;
    }

    public static String getString(String[] cols){
        String tmp = "";
        for(int i=0;i<cols.length;i++){
            if(i == cols.length - 1){
                tmp += cols[i];
            }else{
                tmp += cols[i] + ",";
            }
        }
        return tmp;
    }

    public static double[] getRangePCI(double[] pcis){
        double mmax = MathUtil.getMax(pcis);
        if(mmax < 9.0){
            return pcis;
        }
        double k = 9.6 / mmax;
        double[] res = new double[pcis.length];
        for(int i=0;i<pcis.length;i++){
            res[i] = k * pcis[i];
        }
        return res;
    }
}
