package com.ea.driver.local;

import com.ea.enums.BrowserType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LocalDriverFactory {

    private LocalDriverFactory() {

    }

    public static WebDriver getDriver(BrowserType browser) {
        switch (browser) {
            case CHROME -> {
                return new ChromeDriver();
            }
            case FIREFOX -> {
                return new FirefoxDriver();
            }
            case EDGE -> {
                return new EdgeDriver();
            }
            default -> throw new IllegalStateException("Unexpected value: " + browser);
        }
    }
}
