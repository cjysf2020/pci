package com.cjy.station;

import com.cjy.lib.MathUtil;
import com.cjy.lib.MysqlConn;
import com.cjy.station.classes.Stations;
import com.cjy.station.classes.TransferData;

import java.sql.ResultSet;
import java.util.*;

public class PCIStation {

    private int interval = 15;
    private List<String[]> timespan = new ArrayList<String[]>();
    private List<Stations> stations = new ArrayList<Stations>();
    private Dictionary<String, Integer> transfers = new Hashtable<String, Integer>();

    private MysqlConn mysql = new MysqlConn();
    ///////
    private String inoutTable = "tcal_station_temp";
    private String transferTable = "tcal_transfer";
    private String waitTable = "z_op_platform_passenger";
    private String param1Table = "mx_station_pci_base_info";
    private String paramYLTable = "section_base_info";
    private String param2Table = "mx_hc_station_pci_base_info";
    private String paramPciTable = "mx_pci_crowd_level";
    ///////
    private String curdate = "";
    ///////PCI指数相关
    private double P1 = 0.0;
    private double P2 = 0.0;
    private double P3 = 0.0;
    private double PCI = 0.0;
    private int CROWD_LEVEL = 0;


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
    private Stations getStationById(String id){
        Stations res = null;
        for(Stations st:stations){
            if(st.getSTATION_ID().equals(id)){
                res = st;
                break;
            }
        }
        return res;
    }

    private List<String> getStationLines(String name){
        String sql = "select distinct LINE_ID from " + paramYLTable + " where SECTION_NAME like '%" + name + "%';";
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
        String[] columns = { "ID", "LINE_ID", "STATION_ID", "STATION_NAME", "STATION_TYPE", "FLOW_IN_MAX", "FLOW_OUT_MAX", "FLOW_WAIT_UP_MAX", "FLOW_WAIT_DOWN_MAX", "WEIGHT_FLOW_IN_OUT",
                "WEIGHT_FLOW_WAIT", "WEIGHT_FLOW_TRANSFER" };
        String sql = "select " + getString(columns) + " from " + param1Table + " where STATION_ID is not null limit 20;";
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        for(Dictionary<String, String> row: rs){
            Stations st = new Stations();
            st.setID(row.get(columns[0]));
            st.setLINE_ID(row.get(columns[1]));
            st.setSTATION_ID(row.get(columns[2]));
            st.setSTATION_NAME(row.get(columns[3]));
            st.setSTATION_TYPE(row.get(columns[4]));
            st.setFLOW_IN_MAX(row.get(columns[5]));
            st.setFLOW_OUT_MAX(row.get(columns[6]));
            st.setFLOW_WAIT_UP_MAX(row.get(columns[7]));
            st.setFLOW_WAIT_DOWN_MAX(row.get(columns[8]));
            st.setWEIGHT_FLOW_IN_OUT(row.get(columns[9]));
            st.setWEIGHT_FLOW_WAIT(row.get(columns[10]));
            st.setWEIGHT_FLOW_TRANSFER(row.get(columns[11]));
            st.setLines(getStationLines(row.get(columns[3])));
            stations.add(st);
        }
        // 换乘站参数
        String[] paramcols = { "STATION_ID", "START_LINE_ID", "END_LINE_ID", "FLOW_TRANSFER_MAX" };
        String sqlhx = "select " + getString(paramcols) + " from " + param2Table;
        List<Dictionary<String, String>> hxrs = mysql.select(sqlhx, paramcols);
        for(Dictionary<String, String> row: hxrs){
            String key = row.get(paramcols[0]) + "_" + row.get(paramcols[1]) + "_" + row.get(paramcols[2]);
            int num = Integer.parseInt(row.get(paramcols[3]));
            transfers.put(key, num);
        }

    }

    ///计算进出站能力饱和度
    public void calcInOut(String[] timespan, Stations st){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":59";
        String inSql = "select FLOW_IN,FLOW_OUT from " + inoutTable + " where STATION_ID='" + st.getSTATION_ID() + "' and COUNT_TIME >= '" + stime + "' and COUNT_TIME < '" + etime + "'";
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

        double[] tmpres = new double[2];
        int inMax = Integer.parseInt(st.getFLOW_IN_MAX());
        int outMax = Integer.parseInt(st.getFLOW_OUT_MAX());
        tmpres[0] = (inCount / inMax);
        tmpres[1] = (outCount / outMax);
        ////////
        P1 = MathUtil.getMax(tmpres);
        System.out.println("进出站:" + P1);
    }
    ////计算站台能力饱和度
    public void calcWaitPerson(String[] timespan, Stations st){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":59";
        String[] columns = { "ptotal", "line_id" };
        String sxsql = "select sum(platform_total) as ptotal,line_id from " + waitTable + " where STATION_TCC='" +
                st.getSTATION_ID() + "' and direction='上行' and depart_time >= '"+
                stime + "' and depart_time < '" + etime + "' group by line_id;";
        List<Dictionary<String, String>> sxrs = mysql.select(sxsql, columns);
        double sxNum = 0.0;
        for(Dictionary<String, String> row:sxrs){
            int tmpNum = Integer.parseInt(row.get(columns[0]));
            sxNum += tmpNum;
        }
        sxNum = sxNum / sxrs.size();
        ////////////////////////////////////////////
        String xxsql = "select sum(platform_total) as ptotal,line_id from " + waitTable + " where STATION_TCC='" +
                st.getSTATION_ID() + "' and direction='下行' and depart_time >= '"+
                stime + "' and depart_time < '" + etime + "' group by line_id;";
        List<Dictionary<String, String>> xxrs = mysql.select(xxsql, columns);
        double xxNum = 0.0;
        for(Dictionary<String, String> row:xxrs){
            int tmpNum = Integer.parseInt(row.get(columns[0]));
            xxNum += tmpNum;
        }
        xxNum = xxNum / xxrs.size();

        int upWaitMax = Integer.parseInt(st.getFLOW_WAIT_UP_MAX());
        int downWaitMax = Integer.parseInt(st.getFLOW_WAIT_DOWN_MAX());
        double[] tmpres = new double[2];
        tmpres[0] = (sxNum / upWaitMax);
        tmpres[1] = (xxNum / downWaitMax);

        P2 = MathUtil.getMax(tmpres);

        System.out.println("站台量:" + P2);

    }
    ///计算换乘能力饱和度
    public void calcTransfer(String[] timespan, Stations st){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":59";
        String[] columns = { "FLOW_TRANSFER" };
        List<TransferData> transferData = new ArrayList<TransferData>();

        // 必须为换乘站
        if(st.getSTATION_TYPE().equals("2")) {
            List<String[]> routes = st.getRoutes();
            if(routes.size() > 1){
                for(String[] dir:routes){
                    String sline = dir[0];
                    String eline = dir[1];
                    String sql = "select " + getString(columns) + " from " + transferTable + " where STATION_ID='" +
                            st.getSTATION_ID() + "' and COUNT_TIME >= '" + stime + "' and COUNT_TIME < '" +
                            etime + "' and START_LINE_ID='" + sline + "' and END_LINE_ID = '" + eline + "' ";
                    /////获取15分钟的换乘量
                    List<Dictionary<String, String>> rs = mysql.select(sql, columns);
                    int count = 0;
                    for(Dictionary<String, String> row:rs){
                        int tmpCount = Integer.parseInt(row.get(columns[0]));
                        count += tmpCount;
                    }
                    TransferData tfd = new TransferData(sline, eline, count);
                    transferData.add(tfd);
                }
            }
            double[] tmpres = new double[transferData.size()];
            for(int i=0;i<tmpres.length;i++){
                TransferData tmp = transferData.get(i);
                String key = st.getSTATION_ID() + "_" + tmp.getSline() + "_" + tmp.getEline();
                Integer maxNum = transfers.get(key);
                if(maxNum != null){
                    double d = (tmp.getCount() / maxNum);
                    tmpres[i] = d;
                }else{
                    tmpres[i] = 0;
                }
            }
            P3 = MathUtil.getMax(tmpres);
        }
        System.out.println("换乘量:" + P3);
    }
    ///计算车站PCI指数
    public void calcPCI(String[] timespan, Stations st){
        calcInOut(timespan, st);
        calcTransfer(timespan, st);
        calcWaitPerson(timespan, st);
        double a = Double.parseDouble(st.getWEIGHT_FLOW_IN_OUT());
        double b = Double.parseDouble(st.getWEIGHT_FLOW_WAIT());
        double c = Double.parseDouble(st.getWEIGHT_FLOW_TRANSFER());
        PCI = 10 * (a * P1 + b * P2 + c * P3);

        String[] columns = { "CROWD_LEVEL" };
        String sql = "select " + getString(columns) + " from " + paramPciTable + " where PCI_TYPE=1 and PCI_MIN <= " + PCI + " and PCI_MAX > " + PCI;
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        for(Dictionary<String, String> row: rs){
            CROWD_LEVEL = Integer.parseInt(row.get(columns[0]));
        }
    }

    public void calc(){

//        for(Stations st: stations){
//            for(String[] times: timespan){
//                calcPCI(times, st);
//            }
//        }

        Stations st = getStationById("0115");
        String[] times = {"00:00", "23:30"};
        calcPCI(times, st);

        mysql.close();
    }
}
