package com.ea.driver.remote;

import com.ea.enums.BrowserType;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public class RemoteDriverFactory {

    private RemoteDriverFactory() {
    }

    public static WebDriver getDriver(BrowserType browser, String platform, String remoteUrl) {
        try {
            switch (browser) {
                case CHROME -> {
                    return RemoteChromeDriver.getDriver(platform, remoteUrl);
                }
                case FIREFOX -> {
                    return RemoteFirefoxDriver.getDriver(platform, remoteUrl);
                }
                case EDGE -> {
                    return RemoteEdgeDriver.getDriver(platform, remoteUrl);
                }
                default -> throw new IllegalStateException("Unexpected value: " + browser);
            }
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Unable to initialize driver. "+e);
        }
    }
}
