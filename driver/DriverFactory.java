package com.ea.driver;

import com.ea.config.GlobalVariables;
import com.ea.driver.local.LocalDriverFactory;
import com.ea.driver.remote.RemoteDriverFactory;
import com.ea.enums.BrowserType;
import com.ea.enums.ExecutionMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class DriverFactory {

    private DriverFactory() {
    }
    private static ThreadLocal<WebDriver> threadLocalDriver = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    /**
     * Create a new WebDriver instance and adds the same to ThreadLocal instance.
     * Function should ideally be called within Before methods
     *
     * @param browser       - Enum
     * @param executionMode - Enum
     * @param platform      - String
     * @param remoteUrl     - String
     */
    public static void initializeDriver(BrowserType browser, ExecutionMode executionMode, String platform, String remoteUrl) {

        logger.info("Launch driver with configuration :: Browser => %s, Execution mode => %s, Platform => %s".formatted(browser.name(), executionMode.name(), platform));
        switch (executionMode) {
            case LOCAL -> threadLocalDriver.set(LocalDriverFactory.getDriver(browser));
            case REMOTE -> threadLocalDriver.set(RemoteDriverFactory.getDriver(browser, platform, remoteUrl));
            default -> throw new IllegalStateException("Unexpected value: " + executionMode);
        }
    }

    public static void initializeDriver() {
        initializeDriver(GlobalVariables.CONFIG.browser(), GlobalVariables.CONFIG.executionMode(),
                GlobalVariables.CONFIG.platform(), GlobalVariables.CONFIG.remoteUrl());
    }

    /**
     * Returns the WebDriver instance specific to current thread.
     * Function should be used whenever application needs a driver object.
     * Application should not store the same into as a local WebDriver variable within any class.
     *
     * @return - WebDriver
     */
    public static WebDriver getDriver() {
        return threadLocalDriver.get();
    }

    /**
     * Quits WebDriver instance and removes the current instance from ThreadLocal.
     */
    public static void quitDriver() {
        threadLocalDriver.get().quit();
        threadLocalDriver.remove();
    }
}
