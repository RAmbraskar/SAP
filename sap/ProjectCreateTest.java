package com.ea.sap;

import com.ea.annotation.TestParameters;
import com.ea.api.netsuite.request.AddDocumentToEngagementLetterRequest;
import com.ea.api.netsuite.request.CreateEngagementLetterRequest;
import com.ea.api.netsuite.request.GetEngagementLetterRequest;
import com.ea.api.netsuite.request.PatchEngagementLetterRequest;
import com.ea.api.netsuite.response.AddDocumentToEngagementLetterResponse;
import com.ea.api.netsuite.response.CreateEngagementLetterResponse;
import com.ea.api.netsuite.response.GetEngagementLetterResponse;
import com.ea.api.netsuite.response.PatchEngagementLetterResponse;
import com.ea.config.GlobalVariables;
import com.ea.enums.LegalEntity;
import com.ea.enums.ProjectType;
import com.ea.utils.DateUtil;
import com.ea.utils.ExcelReportUtil;
import com.ea.utils.RandomUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ProjectCreateTest extends BaseSapTest {

    @DataProvider(name = "projectData")
    public Object[][] createProjectData() {
        Object[][] data = {
                {ProjectType.ASR},
                {ProjectType.TAX_C},
                {ProjectType.BRC},
                {ProjectType.BPS},
                {ProjectType.CR},
                {ProjectType.MSMR},
                {ProjectType.TAS},
                {ProjectType.PD}
        };
        return data;
    }

    @Test(enabled = true, dataProvider = "projectData")
    @TestParameters(azureId = "1")
    public void createProjectTest1(ProjectType projectType) {

        // Location at which API Logs will be saved - /report/evidence/101/<logfile>
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + projectType.name();

        // Log file for CreateEngagementLetter, UpdateEngagementLetter, AttachFileToEngagementLetter
        String createProjectLog = directoryPath + "/createProjectLog.log";
        String patchProjectLog = directoryPath + "/patchProjectLog.log";
        String attachFileLog = directoryPath + "/attachFileLog.log";

        //Create Engagement Letter - Data Preparation
        long sharePointElId = RandomUtil.generateRandomNumberLong(10);
        String projectStartDate = DateUtil.getFirstDayOfMonth(6, GlobalVariables.NS_API_DATE_FORMAT);
        String projectEndDate = DateUtil.getLastDayOfMonth(-6, GlobalVariables.NS_API_DATE_FORMAT);
        String periodEndDate = projectEndDate;
        String jsonFilePath = GlobalVariables.NS_PROJECT_JSON_MAP.get(projectType);
        String legalEntity = GlobalVariables.NS_PROJECT_LEGAL_ENTITY_MAP.get(projectType);

        //Create Engagement Letter - Submit Request
        CreateEngagementLetterRequest createElRequest = new CreateEngagementLetterRequest(createProjectLog);
        String createElJsonBody = createElRequest.generateJsonBody(jsonFilePath, sharePointElId, projectStartDate,
                projectEndDate, periodEndDate);
        CreateEngagementLetterResponse createElResponse = createElRequest.submitRequest(createElJsonBody);

        //Create Engagement Letter - Assert Response
        Assert.assertEquals(createElResponse.getStatus(), "Success");
        Assert.assertEquals(createElResponse.getStatusCode(), 200);

        //Create Engagement Letter - Extract NetSuite ID for next request
        String internalID = createElResponse.getNetSuiteId();

        //Patch Legal Entity of engagement letter - Submit Request
        PatchEngagementLetterRequest patchElRequest = new PatchEngagementLetterRequest(patchProjectLog, Long.parseLong(internalID));
        String patchElJsonBody = patchElRequest.generateJsonBody(LegalEntity.valueOf(legalEntity));
        PatchEngagementLetterResponse patchEngagementLetterResponse = patchElRequest.submitRequest(patchElJsonBody);

        //Patch Legal Entity of engagement letter - Assert Response
        Assert.assertEquals(patchEngagementLetterResponse.getStatusCode(), 204);

        //Attach File to engagement letter - Submit Request
        AddDocumentToEngagementLetterRequest addDocumentRequest = new AddDocumentToEngagementLetterRequest(attachFileLog);
        String addDocumentJsonBody = addDocumentRequest.generateJsonBody(GlobalVariables.NS_ATTACH_FILE_API_JSON_PATH, sharePointElId);
        AddDocumentToEngagementLetterResponse addDocumentResponse = addDocumentRequest.submitRequest(addDocumentJsonBody);

        //Attach File to engagement letter - Assert Response
        Assert.assertEquals(addDocumentResponse.getStatus().toLowerCase(), "Success".toLowerCase());

        //Update NetSuite ID and SharePoint ID in Excel Report
        ExcelReportUtil.ProjectData.updateProjectHeaderData(projectType, internalID
                , String.valueOf(sharePointElId), projectStartDate, projectEndDate, periodEndDate);
    }

    @Test(enabled = true, dataProvider = "projectData")
    @TestParameters(azureId = "2")
    public void updateErpIdFromNetSuite(ProjectType projectType) {
        // Location at which API Logs will be saved - /report/evidence/102/<logfile>
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + projectType.name();

        // Log file for GetEngagementLetter
        String getProjectLog = directoryPath + "/getProjectLog.log";

        //Get Engagement Letter - Submit Request
        GetEngagementLetterRequest getElRequest = new GetEngagementLetterRequest(getProjectLog, Long.parseLong(ExcelReportUtil.ProjectData.getNetSuiteIdOfProject(projectType)));
        GetEngagementLetterResponse getEngagementLetterResponse = getElRequest.submitRequest();

        //Get Engagement Letter - Assert Response
        Assert.assertEquals(getEngagementLetterResponse.getStatusCode(), 200);
        ExcelReportUtil.ProjectData.updateEngNumberOfProject(projectType, getEngagementLetterResponse.getEngagementLetterNumber());

        Assert.assertNotNull(getEngagementLetterResponse.getProjectId());
        Assert.assertNotEquals(getEngagementLetterResponse.getProjectId(), "");

        //Update Engagement Letter ERP ID in Excel Report
        ExcelReportUtil.ProjectData.updateErpIdOfProject(projectType, getEngagementLetterResponse.getProjectId());
    }
}
