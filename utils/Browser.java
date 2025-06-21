package com.ea.utils;

import com.ea.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

public class Browser {

    private Browser() {
    }

    private static final Logger LOGGER = LogManager.getLogger(Browser.class);

    public static Wait waitFor() {
        return new Wait();
    }
    public static void refreshPage(){
        String logMessage = String.format("Refresh the page");
        LOGGER.info(logMessage);
        DriverFactory.getDriver().navigate().refresh();
    }

    public static WebElement getElement(By locator) {
        String logMessage = String.format("Locate Element :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitFor().presenceOf(locator);
    }

    public static List<WebElement> getElements(By locator) {
        String logMessage = String.format("Locate Element :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitFor().presenceOfAll(locator);
    }

    public static WebElement getElement(WebElement parentElement, By locator) {
        String logMessage = String.format("Locate child element :: %s under parent element :: %s", locator.toString(), parentElement.toString());
        LOGGER.info(logMessage);
        return waitFor().presenceOfChildElement(parentElement, locator);
    }

    public static Wait waitFor(int timeout) {
        return new Wait(timeout);
    }

    public static void navigateToURL(String url) {
        String logMessage = String.format("Navigate to URL :: %s", url);
        LOGGER.info(logMessage);
        DriverFactory.getDriver().get(url);
    }

    public static void sendKeys(By locator, String value) {
        String logMessage = String.format("Send text :: %s to element :: %s", value, locator.toString());
        LOGGER.info(logMessage);
        getElement(locator).sendKeys(value);
    }

    public static void sendKeysEncrypted(By locator, String value) {
        String logMessage = String.format("Send encrypted text :: %s to element :: %s", value, locator.toString());
        LOGGER.info(logMessage);
        waitFor().clickable(locator).sendKeys(EncodeDecodeUtil.base64Decrypt(value));
    }

    public static void clearText(By locator) {
        String logMessage = String.format("Clear text from element :: %s", locator.toString());
        LOGGER.info(logMessage);
        waitFor().clickable(locator).clear();
    }

    public static void click(By locator) {
        String logMessage = String.format("Click element :: %s", locator.toString());
        LOGGER.info(logMessage);
        getElement(locator).click();
    }

    public static void click(WebElement ele) {
        String logMessage = String.format("Click element :: %s", ele.toString());
        LOGGER.info(logMessage);
        waitFor().clickable(ele).click();
    }
    public static String getText(By locator) {
        String logMessage = String.format("Get text from the element :: %s", locator.toString());
        LOGGER.info(logMessage);
        return getElement(locator).getText();
    }

    public static String getText(WebElement ele) {
        String logMessage = String.format("Get text from the element :: %s", ele.toString());
        LOGGER.info(logMessage);
        return ele.getText();
    }

    public static String scrollAndGetText(By locator) {
        Browser.scrollToWebElement(locator);
        return Browser.getText(locator);
    }

    public static String scrollAndGetText(WebElement ele) {
        Browser.scrollToWebElement(ele);
        return Browser.getText(ele);
    }

    public static void switchToIframe(By locator) {
        String logMessage = String.format("Wait for iFrame %s and switch to it", locator.toString());
        LOGGER.info(logMessage);
        waitFor().frameToBeAvailableAndSwitchToIt(locator);
   }

    public static void switchToDefaultFrame() {
        LOGGER.info("Switch to default frame");
        DriverFactory.getDriver().switchTo().defaultContent();
    }

    public static void clickByJavaScript(WebElement ele) {
        String logMessage = String.format("Click element using JavaScript :: %s", ele.toString());
        LOGGER.info(logMessage);
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].scrollIntoView(true);", ele);
        ele.click();
    }

    public static WebElement scrollToWebElement(WebElement ele) {
        String logMessage = String.format("Scroll to element :: %s", ele.toString());
        LOGGER.info(logMessage);
        Actions action = new Actions(DriverFactory.getDriver());
        action.scrollToElement(ele).build().perform();
        return ele;
    }

    public static WebElement scrollToWebElement(By locator) {
        String logMessage = String.format("Scroll to element :: %s", locator.toString());
        LOGGER.info(logMessage);
        Actions action = new Actions(DriverFactory.getDriver());
        WebElement ele = getElement(locator);
        action.scrollToElement(ele).build().perform();
        return ele;
    }

    public static String getTitle() {
        LOGGER.info("Get current page title");
        return DriverFactory.getDriver().getTitle();
    }

    public static String getAttribute(WebElement ele, String attrName) {
        String logMessage = String.format("Get attribute value :: %s from element :: %s", attrName, ele.toString());
        LOGGER.info(logMessage);
        return ele.getAttribute(attrName);
    }

    public static String getAttribute(By locator, String attrName) {
        String logMessage = String.format("Get attribute value :: %s from element :: %s", attrName, locator.toString());
        LOGGER.info(logMessage);
        return getElement(locator).getAttribute(attrName);
    }

    public static boolean isDisplayed(WebElement ele) {
        String logMessage = String.format("Verify element is displayed :: %s", ele.toString());
        LOGGER.info(logMessage);
        return ele.isDisplayed();
    }

    public static boolean isDisplayed(By locator) {
        String logMessage = String.format("Verify element is displayed :: %s", locator.toString());
        LOGGER.info(logMessage);
        return getElement(locator).isDisplayed();
    }
}
