package com.cjy;

import com.cjy.station.PCIStation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App {

    public static void main(String[] args) throws SQLException {
        System.out.println("Hello World");

        System.out.println("提交");
        PCIStation pciStation = new PCIStation("2020-03-08");
        pciStation.calc();
        System.out.println(pciStation.getTimespan());
    }
}
