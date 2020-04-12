package com.cjy.station;

import com.cjy.lib.CommonUtil;
import com.cjy.lib.MysqlConn;
import com.cjy.station.lineclass.LinePCIWeight;
import com.cjy.station.lineclass.LineSection;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class PCILine {

    private List<String[]> timespan = new ArrayList<String[]>();
    private String lineId;
    private String curdate;
    private MysqlConn mysql = new MysqlConn();
    ////
    private List<LineSection> sx = new ArrayList<LineSection>();
    private List<LineSection> xx = new ArrayList<LineSection>();
    private List<LinePCIWeight> pciparams = new ArrayList<LinePCIWeight>();
    // private String
    private String sectionTable = "section_base_info";
    //////
    private Double Il_Station = 0.0;
    private Double Ilxx = 0.0;
    private Double Ilsx = 0.0;
    private Double Ilres = 0.0;

    /** */
    public void initLineSection(String dir, String[] timespan){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":59";
        String[] columns = { "SECTION_ID", "LINE_ID", "DIRECTION", "train_load" };
        String sql = "select " + CommonUtil.getString(columns) + " from section_base_info,z_op_traincapacity where LINE_ID='" +
                this.lineId + "' and DIRECTION='" + dir + "' and section_base_info.SECTION_ID=z_op_traincapacity.section_code and depart_time >= '" + stime + "' and depart_time < '" + etime + "'";

        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        /////////
        List<LineSection> sections = new ArrayList<LineSection>();
        for(Dictionary<String, String> row: rs){
            LineSection ls = new LineSection();
            ls.setDirection(row.get(columns[2]));
            ls.setLineId(row.get(columns[1]));
            ls.setSectionId(row.get(columns[0]));
            String capstr = row.get(columns[3]);
            Double cap = 0.0;
            if(capstr != null){
                cap = Double.parseDouble(capstr);
            }
            ls.setCapacity(cap);
            sections.add(ls);
        }
        if(dir.equals("上行")){
            sx = sections;
        }else{
            xx = sections;
        }
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
//        if(lid < 10){
//            realLineId = "0" + lid;
//        }
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

    /** 获取车站的平均PCI */
    public void calcAvgStationPCI(String[] timespan){
        String stime = curdate + " " + timespan[0] + ":00";
        String etime = curdate + " " + timespan[1] + ":00";
        String sql = "select avg(PCI) as ap from tcal_station where LINE_ID='" + this.lineId + "' and COUNT_TIME >= '" + stime + "' and COUNT_TIME < '" + etime + "'";
        String[] columns = { "ap" };
        List<Dictionary<String, String>> rs = mysql.select(sql, columns);
        Dictionary<String, String> row = rs.get(0);
        Double Il_Station = Double.parseDouble(row.get(columns[0]));
        this.Il_Station = Il_Station;
    }

    /** 计算区间指数 */
    public Double calcLineDirPCI(List<LineSection> secs, Double beta0){
        Integer M = secs.size() / 4 + 6;
        Double mzlT = 0.0;
        for(LineSection ls : secs){
            mzlT += ls.getCapacity();
        }
        Double Il = 10 * (mzlT / (M - 1)) / beta0;
        return Il;
    }

    public PCILine(String lineId, String curdate){
        mysql.connect();
        this.lineId = lineId;
        this.curdate = curdate;
        this.timespan = CommonUtil.initTimeSpan();
        initLineParams();
    }

    public void calcPCI(String[] times, LinePCIWeight lw){
        calcAvgStationPCI(times);

        this.initLineSection("上行", times);
        this.initLineSection("下行", times);

        this.Ilsx = this.calcLineDirPCI(sx, lw.getLOAD_MAX());
        this.Ilsx = this.calcLineDirPCI(xx, lw.getLOAD_MAX());
        Double alpha = lw.getWEIGHT_STATION();
        Double gama = lw.getWEIGHT_SECTIOn();
        Double res = alpha * this.Il_Station + gama * 0.5 * (Ilsx + Ilxx);
        this.Ilres = res;

        System.out.println(curdate + "," + times[0] + "," + times[1] + "," + String.format("%.4f", res));

    }

    public void calc(){
        LinePCIWeight lw = getPCIWEIGHTById(this.lineId);
        for(String[] times: timespan){

            if(times[1].equals("09:00")){
                Integer ii = 0;
                ii += 10;
            }

            calcPCI(times, lw);
        }
        mysql.close();
    }


}
