package com.cjy.station;

import com.cjy.lib.MysqlConn;
import com.cjy.lib.CommonUtil;
import com.cjy.station.lineclass.LinePCIWeight;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.List;

public class PCINet {
    private MysqlConn mysql = new MysqlConn();
    private String curdate;
    private List<String[]> timespan = new ArrayList<String[]>();
    private List<LinePCIWeight> pciparams = new ArrayList<LinePCIWeight>();

    public PCINet(String curdate){
        mysql.connect();
        this.curdate = curdate;
        this.timespan = CommonUtil.initTimeSpan();
        this.initLineParams();
    }

    public void initLineParams(){
        String[] columns = { "LINE_ID", "LINE_NAME", "LOAD_MAX", "WEIGHT_STATION", "WEIGHT_SECTION", "WEIGHT_LINE" };
        String sql = "select " + CommonUtil.getString(columns) + " from mx_line_pci_weight";
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        /////////
        List<LinePCIWeight> sections = new ArrayList<LinePCIWeight>();
        for(Dictionary<String, String> row: rs){
            LinePCIWeight lw = new LinePCIWeight();
            lw.setLINE_ID(row.get(columns[0]));
            lw.setLINE_NAME(row.get(columns[1]));
            lw.setLOAD_MAX(Double.parseDouble(row.get(columns[2])));
            lw.setWEIGHT_STATION(Double.parseDouble(row.get(columns[3])));
            lw.setWEIGHT_SECTIOn(Double.parseDouble(row.get(columns[4])));
            lw.setWEIGHT_LINE(Double.parseDouble(row.get(columns[5])));
            sections.add(lw);
        }
        this.pciparams = sections;
    }

    public LinePCIWeight getPCIWEIGHTById(String lineId){
        Integer lid = Integer.parseInt(lineId);
        String realLineId = lid.toString();

        LinePCIWeight weight = null;
        for(LinePCIWeight lw : pciparams){
            Integer tid = Integer.parseInt(lw.getLINE_ID());
            if(tid == lid){
                weight = lw;
                break;
            }
        }
        return weight;
    }

    public void calcPCI(String[] timespan){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":00";
        String[] columns = { "LINE_ID", "PCI" };
        String sql = "select " + CommonUtil.getString(columns) + " from tcal_line where COUNT_TIME >= '" + stime + "' and COUNT_TIME < '" + etime + "'";
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);

        Double result = 0.0;
        for(Dictionary<String, String> row: rs){
            String lineId = row.get(columns[0]);
            Double pci = Double.parseDouble(row.get(columns[1]));
            LinePCIWeight lw = getPCIWEIGHTById(lineId);
            if(lw != null) {
                result += lw.getWEIGHT_LINE() * pci;
            }
        }

        // InsertData()

        System.out.println(curdate + "," + timespan[0] + "," + timespan[1] + "," + String.format("%.4f", result));
    }

    public Integer getLevel(Double pci){
        String[] columns = { "CROWD_LEVEL" };
        String sql = "select " + CommonUtil.getString(columns) + " from mx_pci_crowd_level where PCI_TYPE=3 and PCI_MIN <= " + pci + " and PCI_MAX > " + pci;
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        Integer clevel = 0;
        for(Dictionary<String, String> row: rs){
            clevel = Integer.parseInt(row.get(columns[0]));
        }
        return clevel;
    }

    public void InsertData(String[] times, Double pci){
        String stime = curdate + " " + times[0] + ":00";
        String etime = curdate + " " + times[1] + ":00";
        String[] columns = { "fin", "fout", "ftran", "LINE_NAME" };
        String sql = "select sum(FLOW_IN) as fin, sum(FLOW_OUT) as fout, sum(FLOW_TRANSFER) as ftran, LINE_NAME from tcal_line where and COUNT_TIME>='" + stime + "' and COUNT_TIME < '" + etime + "' ";
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        Dictionary<String, String> row = rs.get(0);
        Integer fin = Integer.parseInt(row.get(columns[0]));
        Integer fout = Integer.parseInt(row.get(columns[1]));
        Integer ftran = Integer.parseInt(row.get(columns[2]));
        String linename = row.get(columns[3]);
        Date now = new Date();
        Integer level = getLevel(pci);

        ///TODO: String isql = "insert into tcal_net ";

        System.out.println(sql);
    }

    public void calc(){
        for(String[] times: timespan){
            calcPCI(times);
        }
        mysql.close();
    }
}
