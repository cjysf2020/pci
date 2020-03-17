package com.cjy.station.classes;

public class TransferData {
    public String getSline() {
        return sline;
    }

    public void setSline(String sline) {
        this.sline = sline;
    }

    public String getEline() {
        return eline;
    }

    public void setEline(String eline) {
        this.eline = eline;
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
    }

    public TransferData(String sline, String eline, Double count){
        this.sline = sline;
        this.eline = eline;
        this.count = count;
    }

    private String sline;
    private String eline;
    private Double count;
}
