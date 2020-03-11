package com.cjy.station;

import com.cjy.lib.MysqlConn;
import com.cjy.station.classes.Stations;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class PCIStation {

    private int interval = 15;
    private List<String[]> timespan = new ArrayList<String[]>();
    private List<Stations> stations = new ArrayList<Stations>();

    private MysqlConn mysql = new MysqlConn();
    ///////
    private String inoutTable = "tcal_station_temp";
    private String transferTable = "tcal_transfer";
    ///////
    private String curdate = "";

    public PCIStation(String curdate){
        mysql.connect();
        this.curdate = curdate;
        this.initTimeSpan();
        this.initStations();
    }

    public List<String[]> getTimespan(){
        return timespan;
    }

    private String getTime(int hour, int minute){
        String h = hour < 10 ? "0" + hour : hour + "";
        String m = minute < 10 ? "0" + minute : minute + "";
        return h + ":" + m;
    }
    public void initTimeSpan(){
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
    }
    private String getString(String[] cols){
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

    private List<String> getStationLines(String name){
        String sql = "select distinct LINE_ID from section_base_info where SECTION_NAME like '%" + name + "%';";
        String[] columns = { "LINE_ID" };
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        List<String> lines = new ArrayList<String>();
        for(int i=0;i<rs.size();i++){
            Dictionary<String, String> row = rs.get(i);
            lines.add(row.get(columns[0]));
        }
        return lines;
    }
    public void initStations(){
        String[] columns = { "STATIONID", "STATIONNAME", "TCCCODE" };
        String sql = "select " + getString(columns) + " from station where STATIONID is not null limit 30;";
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        for(Dictionary<String, String> row: rs){
            Stations st = new Stations();
            st.setSTATIONID(row.get(columns[0]));
            st.setSTATIONNAME(row.get(columns[1]));
            st.setTCCCODE(row.get(columns[2]));
            st.setLines(getStationLines(row.get(columns[1])));
            stations.add(st);
        }
    }

    ///
    public void calcInOut(String[] timespan, String stationId){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":59";
        String inSql = "select FLOW_IN,FLOW_OUT from " + inoutTable + " where STATION_ID='" + stationId + "' and COUNT_TIME >= '" + stime + "' and COUNT_TIME < '" + etime + "' limit 10";
        String[] columns = { "FLOW_IN", "FLOW_OUT" };
        List<Dictionary<String, String>> rs = mysql.select(inSql, columns);
        int inCount = 0;
        int outCount = 0;
        for(Dictionary<String, String> row: rs){
            int icount = Integer.parseInt(row.get(columns[0]));
            int ocount = Integer.parseInt(row.get(columns[1]));
            inCount += icount;
            outCount += ocount;
        }
        System.out.println(inCount);
        System.out.println(outCount);
    }
    public void calcWaitPerson(){

    }
    public void calcTransfer(){

    }
    public void calcPCI(){

    }

    public void calc(){
        // for(String[] times: timespan){
        String[] times = {"10:00", "10:30"};
        calcInOut(times, "0110");
        System.out.println(stations.size());
        //}

        mysql.close();
    }
}
