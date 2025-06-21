package com.ea.driver.remote;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteFirefoxDriver {

    private RemoteFirefoxDriver() {
    }

    protected static WebDriver getDriver(String platform, String remoteUrl) throws MalformedURLException {
        FirefoxOptions options = new FirefoxOptions();
        options.setPlatformName(platform);
        return new RemoteWebDriver(new URL(remoteUrl), options);
    }
}
