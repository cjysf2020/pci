package com.cjy;

import com.cjy.station.PCILine;
import com.cjy.station.PCINet;
import com.cjy.station.PCIStation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException {
        System.out.println("开始计算");


        System.out.println("时间,开始时间,结束时间,PCI");
        PCIStation pciStation = new PCIStation("2017-10-30");
        pciStation.calc();
        System.out.println("计算完成");

//        System.out.println("时间,开始时间,结束时间,PCI");
//        PCILine line = new PCILine("01", "2017-10-30");
//        line.calc();

//        System.out.println("时间,开始时间,结束时间,PCI");
//        PCINet net = new PCINet("2017-10-30");
//        net.calc();
    }
}
