package com.ea.driver.remote;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteEdgeDriver {

    private RemoteEdgeDriver() {
    }

    protected static WebDriver getDriver(String platform, String remoteUrl) throws MalformedURLException {
        EdgeOptions options = new EdgeOptions();
        options.setPlatformName(platform);
        return new RemoteWebDriver(new URL(remoteUrl), options);
    }
}
