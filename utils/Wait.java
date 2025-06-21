package com.ea.utils;

import com.ea.config.GlobalVariables;
import com.ea.driver.DriverFactory;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;

public class Wait {

    private final org.openqa.selenium.support.ui.Wait<WebDriver> waitObj;
    private static final Logger LOGGER = LogManager.getLogger(Wait.class);

    public Wait() {
        this(GlobalVariables.CONFIG.explicitTimeout());
    }

    public Wait(int timeout) {
        String logMessage = String.format("Initialize WebDriverWait with timeout of %d seconds..", timeout);
        LOGGER.info(logMessage);
        waitObj = new FluentWait<WebDriver>(DriverFactory.getDriver())
                .withTimeout(Duration.ofSeconds(timeout))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    public WebElement visibilityOf(By locator) {
        String logMessage = String.format("Wait for element to be visible :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement visibilityOf(WebElement ele) {
        String logMessage = String.format("Wait for element to be visible :: %s", ele.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.visibilityOf(ele));
    }

    public WebElement clickable(By locator) {
        String logMessage = String.format("Wait for element to be clickable :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement clickable(WebElement ele) {
        String logMessage = String.format("Wait for element to be clickable :: %s", ele.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.elementToBeClickable(ele));
    }

    @SneakyThrows
    public void delay(int timeout) {
        String logMessage = String.format("Wait for hard coded delay :: %d", timeout);
        LOGGER.info(logMessage);
        Thread.sleep(timeout);
    }

    public WebElement presenceOf(By locator) {
        String logMessage = String.format("Wait for element to be present :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> presenceOfAll(By locator) {
        String logMessage = String.format("Wait for element to be present :: %s", locator.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement presenceOfChildElement(WebElement parentElement, By locator) {
        String logMessage = String.format("Wait for child element :: %s to be present under parent element :: %s", locator.toString(), parentElement.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.presenceOfNestedElementLocatedBy(parentElement, locator));
    }

    public Alert alert() {
        LOGGER.info("Wait for alert dialog to be present");
        return waitObj.until(ExpectedConditions.alertIsPresent());
    }

    public void invisibilityOf(By locator) {
        String logMessage = String.format("Wait for element to be invisible :: %s", locator.toString());
        LOGGER.info(logMessage);
        waitObj.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public void invisibilityOfAllElements(By locator) {
        String logMessage = String.format("Wait for element to be invisible :: %s", locator.toString());
        LOGGER.info(logMessage);
        waitObj.until(ExpectedConditions.invisibilityOfAllElements(DriverFactory.getDriver().findElements(locator)));
    }

    public void activeJQueryToComplete() {
        LOGGER.info("Wait for all JQuery operations to complete");
        waitObj.until(ExpectedConditions.jsReturnsValue("return jQuery.active == 0"));
    }

    public boolean textContains(By locator, String expectedText) {
        String logMessage = String.format("Wait for text %s to be present in element %s", expectedText, locator.toString());
        LOGGER.info(logMessage);
        return waitObj.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
    }

    public void frameToBeAvailableAndSwitchToIt(By locator) {
        String logMessage = String.format("Wait for frame to be visible and switch to it %s", locator.toString());
        LOGGER.info(logMessage);
        waitObj.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public void numberOfElementsToBe(By locator, int count) {
        String logMessage = String.format("Wait for number of elements in list %s to be %d", locator.toString(), count);
        LOGGER.info(logMessage);
        waitObj.until(ExpectedConditions.numberOfElementsToBe(locator, count));
    }

    public void elementToBeDisabled(By locator) {
        String logMessage = String.format("Wait for element to be disabled :: %s", locator.toString());
        LOGGER.info(logMessage);
        waitObj.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(locator)));
    }

}
