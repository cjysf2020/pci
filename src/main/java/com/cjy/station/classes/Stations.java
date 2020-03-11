package com.cjy.station.classes;

import java.util.ArrayList;
import java.util.List;

public class Stations {
    private String STATIONID;
    private String STATIONNAME;
    private String TCCCODE;
    private List<String> lines = new ArrayList<String>();
    //////
    private List<String[]> routes = new ArrayList<String[]>();

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        List<String[]> tmpres = new ArrayList<String[]>();
        for(int i=0;i<lines.size() - 1;i++){
            for(int j=i+1;j<lines.size();j++){
                String sl = lines.get(i);
                String el = lines.get(j);
                String[] tmp1 = { sl, el };
                String[] tmp2 = { el, sl };
                tmpres.add(tmp1);
                tmpres.add(tmp2);
            }
        }
        this.routes = tmpres;
        this.lines = lines;
    }

    public String getSTATIONID() {
        return STATIONID;
    }

    public void setSTATIONID(String STATIONID) {
        this.STATIONID = STATIONID;
    }

    public String getSTATIONNAME() {
        return STATIONNAME;
    }

    public void setSTATIONNAME(String STATIONNAME) {
        this.STATIONNAME = STATIONNAME;
    }

    public String getTCCCODE() {
        return TCCCODE;
    }

    public void setTCCCODE(String TCCCODE) {
        this.TCCCODE = TCCCODE;
    }

}
