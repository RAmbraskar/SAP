package com.ea.listeners;

import com.ea.annotation.TestParameters;
import com.ea.config.GlobalVariables;
import com.ea.driver.DriverFactory;
import com.ea.utils.ExcelReportUtil;
import com.ea.utils.ScreenCaptureUtil;
import com.ea.utils.ScreenRecorderUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.lang.reflect.Method;

public class AzureTestListener implements ITestListener {

    private static final String MSG_START_TEST_CASE = "#### START TEST CASE ####";
    private static final String MSG_TEST_CASE_PASS = "#### TEST CASE PASS ####";
    private static final String MSG_TEST_CASE_FAILED = "#### TEST CASE FAILED ####";
    private static final String MSG_TEST_CASE_FAILED_ERROR = "TEST EXCEPTION :: %s";
    private static final String MSG_TEST_CASE_SKIPPED = "#### TEST CASE SKIPPED ####";
    private static final String MSG_TEST_CASE_METHOD_NAME = "TEST METHOD NAME :: %s";
    private static final String MSG_TEST_CASE_AZURE_ID = "TEST AZURE ID :: %s";
    private static final String MSG_TEST_CASE_AZURE_ID_NOT_SET = "TEST AZURE ID IS NOT SET";
    private static final Logger LOGGER = LogManager.getLogger(AzureTestListener.class);

    private String getAzureId(ITestResult result) {
        ITestListener.super.onTestStart(result);
        Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
        if (testMethod.isAnnotationPresent(TestParameters.class)) {
            String azureId = testMethod.getAnnotation(TestParameters.class).azureId();
            LOGGER.info(()->MSG_TEST_CASE_AZURE_ID.formatted(azureId));
            return azureId;
        }
        LOGGER.info(MSG_TEST_CASE_AZURE_ID_NOT_SET);
        return "";
    }

    private boolean getTestRecordingFlag(ITestResult result) {
        boolean canRecord = true;
        ITestListener.super.onTestStart(result);
        Method testMethod = result.getMethod().getConstructorOrMethod().getMethod();
        if (testMethod.isAnnotationPresent(TestParameters.class)) {
            canRecord = testMethod.getAnnotation(TestParameters.class).recording();
        }
        return canRecord;
    }

    private String getTestMethodName(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        LOGGER.info(()->MSG_TEST_CASE_METHOD_NAME.formatted(methodName));
        return methodName;
    }

    @Override
    public void onTestStart(ITestResult result) {
        ITestListener.super.onTestStart(result);

        LOGGER.info(MSG_START_TEST_CASE);
        String methodName = getTestMethodName(result);
        String azureId = getAzureId(result);
        boolean recordingFlag = getTestRecordingFlag(result);
        File file = new File(GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId);
        file.mkdirs();
        ScreenRecorderUtil.startRecord(azureId, methodName, recordingFlag);
        result.getTestContext().setAttribute("azureId", azureId);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ITestListener.super.onTestSuccess(result);
        ExcelReportUtil.TestCases.updateTestResult(getAzureId(result), "PASS");
        ScreenRecorderUtil.stopRecord(getTestRecordingFlag(result));
        LOGGER.info(MSG_TEST_CASE_PASS);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ITestListener.super.onTestFailure(result);
        if (DriverFactory.getDriver() != null) {
            ScreenCaptureUtil.storeScreenShotAt(GlobalVariables.CONFIG.reportPath() + "/screenshot/" + getAzureId(result) + ".png");
        }
        ExcelReportUtil.TestCases.updateTestResult(getAzureId(result), "FAIL");
        ScreenRecorderUtil.stopRecord(getTestRecordingFlag(result));
        LOGGER.error(()->MSG_TEST_CASE_FAILED_ERROR.formatted(result.getThrowable().toString()));
        LOGGER.info(MSG_TEST_CASE_FAILED);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ITestListener.super.onTestSkipped(result);
        ExcelReportUtil.TestCases.updateTestResult(getAzureId(result), "SKIP");
        ScreenRecorderUtil.stopRecord(getTestRecordingFlag(result));
        LOGGER.info(MSG_TEST_CASE_SKIPPED);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
        ExcelReportUtil.TestCases.updateTestResult(getAzureId(result), "FAIL");
        ScreenRecorderUtil.stopRecord(getTestRecordingFlag(result));
        LOGGER.error(()->MSG_TEST_CASE_FAILED_ERROR.formatted(result.getThrowable().toString()));
        LOGGER.info(MSG_TEST_CASE_FAILED);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
        ExcelReportUtil.TestCases.updateTestResult(getAzureId(result), "FAIL");
        ScreenRecorderUtil.stopRecord(getTestRecordingFlag(result));
        LOGGER.error(()->MSG_TEST_CASE_FAILED_ERROR.formatted(result.getThrowable().toString()));
        LOGGER.error(()->MSG_TEST_CASE_FAILED_ERROR.formatted(ExceptionUtils.getStackTrace(result.getThrowable())));
        LOGGER.info(MSG_TEST_CASE_FAILED);
    }

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        ITestListener.super.onFinish(context);
    }
}
