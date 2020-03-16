package com.cjy.station.classes;

import java.util.ArrayList;
import java.util.List;

public class Stations {
    private String ID;
    private String LINE_ID;
    private String STATION_ID;
    private String STATION_NAME;
    private String STATION_TYPE;
    private String FLOW_IN_MAX;
    private String FLOW_OUT_MAX;
    private String FLOW_WAIT_UP_MAX;
    private String FLOW_WAIT_DOWN_MAX;
    private String WEIGHT_FLOW_IN_OUT;
    private String WEIGHT_FLOW_WAIT;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLINE_ID() {
        return LINE_ID;
    }

    public void setLINE_ID(String LINE_ID) {
        this.LINE_ID = LINE_ID;
    }

    public String getSTATION_ID() {
        return STATION_ID;
    }

    public void setSTATION_ID(String STATION_ID) {
        this.STATION_ID = STATION_ID;
    }

    public String getSTATION_NAME() {
        return STATION_NAME;
    }

    public void setSTATION_NAME(String STATION_NAME) {
        this.STATION_NAME = STATION_NAME;
    }

    public String getSTATION_TYPE() {
        return STATION_TYPE;
    }

    public void setSTATION_TYPE(String STATION_TYPE) {
        this.STATION_TYPE = STATION_TYPE;
    }

    public String getFLOW_IN_MAX() {
        return FLOW_IN_MAX;
    }

    public void setFLOW_IN_MAX(String FLOW_IN_MAX) {
        this.FLOW_IN_MAX = FLOW_IN_MAX;
    }

    public String getFLOW_OUT_MAX() {
        return FLOW_OUT_MAX;
    }

    public void setFLOW_OUT_MAX(String FLOW_OUT_MAX) {
        this.FLOW_OUT_MAX = FLOW_OUT_MAX;
    }

    public String getFLOW_WAIT_UP_MAX() {
        return FLOW_WAIT_UP_MAX;
    }

    public void setFLOW_WAIT_UP_MAX(String FLOW_WAIT_UP_MAX) {
        this.FLOW_WAIT_UP_MAX = FLOW_WAIT_UP_MAX;
    }

    public String getFLOW_WAIT_DOWN_MAX() {
        return FLOW_WAIT_DOWN_MAX;
    }

    public void setFLOW_WAIT_DOWN_MAX(String FLOW_WAIT_DOWN_MAX) {
        this.FLOW_WAIT_DOWN_MAX = FLOW_WAIT_DOWN_MAX;
    }

    public String getWEIGHT_FLOW_IN_OUT() {
        return WEIGHT_FLOW_IN_OUT;
    }

    public void setWEIGHT_FLOW_IN_OUT(String WEIGHT_FLOW_IN_OUT) {
        this.WEIGHT_FLOW_IN_OUT = WEIGHT_FLOW_IN_OUT;
    }

    public String getWEIGHT_FLOW_WAIT() {
        return WEIGHT_FLOW_WAIT;
    }

    public void setWEIGHT_FLOW_WAIT(String WEIGHT_FLOW_WAIT) {
        this.WEIGHT_FLOW_WAIT = WEIGHT_FLOW_WAIT;
    }

    public String getWEIGHT_FLOW_TRANSFER() {
        return WEIGHT_FLOW_TRANSFER;
    }

    public void setWEIGHT_FLOW_TRANSFER(String WEIGHT_FLOW_TRANSFER) {
        this.WEIGHT_FLOW_TRANSFER = WEIGHT_FLOW_TRANSFER;
    }

    private String WEIGHT_FLOW_TRANSFER;

    private List<String> lines = new ArrayList<String>();
    ////线路换乘方向
    private List<String[]> routes = new ArrayList<String[]>();

    public List<String[]> getRoutes(){
        return this.routes;
    }
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

}
///////////
