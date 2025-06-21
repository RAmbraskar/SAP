package com.ea.sap;

import com.ea.annotation.TestParameters;
import com.ea.config.GlobalVariables;
import com.ea.pages.sap.ProjectControlEnterprisePage;
import com.ea.pages.sap.SAPLoginPage;
import com.ea.utils.Browser;
import com.ea.utils.DateUtil;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ProfessionalServiceCapitalTest extends BaseSapTest {
    private static final String SAP_LOGIN_URL = GlobalVariables.CONFIG.sapLoginUrl();
    private static final String SAP_USERNAME = GlobalVariables.CONFIG.sapUsername();
    private static final String SAP_PASSWORD = GlobalVariables.CONFIG.sapPassword();
    private static final String PROJECT_NAME = "Regression_Test";
    private static final String PROJECT_ID ="C.1201";
    private static final String PROJECT_VALUE = "01";

    @Test(groups = {"JOB4", "UI"})
    @TestParameters(azureId = "501")
    public void createCapitalProject() throws InterruptedException {
        String currentDate = DateUtil.getDateMinusDays(0, GlobalVariables.SAP_UI_DATE_FORMAT);

        Browser.navigateToURL(SAP_LOGIN_URL);
        ProjectControlEnterprisePage.ProjectPage projectPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickProjectControlEnterprisePageTile()
                        .SelectProjectId(PROJECT_ID)
                        .copyProject()
                        .selectNewProject(PROJECT_NAME,PROJECT_ID,currentDate)
                        .createCapitalProject();
        Assert.assertEquals(projectPage.getProcessingStatus(), "Created", "Project status does not match the expected value.");
        projectPage
                .SelectProcessingStatus();
        Assert.assertEquals(projectPage.getProcessingStatus(), "Released", "Project status does not match the expected value.");
        projectPage
                .SelectRelatedApps()
                .editWorkpackage()
                .editProjectType(PROJECT_VALUE)
                .saveWbsElement();
    }
}
