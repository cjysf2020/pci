package com.cjy;

import com.cjy.station.PCIStation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException {
        System.out.println("开始计算");
//        double a = 10;
//        int b = 10000;
//        double c= a / b;
//        System.out.println(c);
        System.out.println("时间,开始时间,结束时间,P2");
        PCIStation pciStation = new PCIStation("2017-10-17");
        pciStation.calc();
        System.out.println("计算完成");
    }
}
