package com.ea.utils;

import com.ea.driver.DriverFactory;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;

public class ScreenCaptureUtil {

    private ScreenCaptureUtil() {
    }

    public static byte[] getBytes() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    public static String getBase64() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.BASE64);
    }

    public static File getFile() {
        return ((TakesScreenshot) DriverFactory.getDriver()).getScreenshotAs(OutputType.FILE);
    }

    @SneakyThrows
    public static void storeScreenShotAt(String destFilePath) {
        FileUtils.copyFile(getFile(), new File(destFilePath));
    }
}
