package com.ea.sap;

import com.ea.driver.DriverFactory;
import com.ea.utils.ExcelReportUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

public class    BaseSapTest {

    @BeforeSuite(alwaysRun = true)
    public void setupReport() {
        System.out.println("INSIDE BEFORE SUITE -> SETUP REPORT");
        ExcelReportUtil.copyReportToTargetLocation();
    }

    @BeforeMethod(onlyForGroups = {"UI"}, alwaysRun = true)
    public void loadBrowser() {
        System.out.println("INSIDE BEFORE METHOD -> JOB 4");
        DriverFactory.initializeDriver();
        DriverFactory.getDriver().manage().window().maximize();
    }

    @AfterMethod(onlyForGroups = {"UI"}, alwaysRun = true)
    public void terminateBrowser() {
        System.out.println("INSIDE AFTER METHOD -> JOB 4");
        DriverFactory.quitDriver();
    }
}
