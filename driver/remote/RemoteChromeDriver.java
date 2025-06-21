package com.ea.driver.remote;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteChromeDriver {

    private RemoteChromeDriver() {
    }

    protected static WebDriver getDriver(String platform, String remoteUrl) throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.setPlatformName(platform);
        return new RemoteWebDriver(new URL(remoteUrl), options);
    }
}
