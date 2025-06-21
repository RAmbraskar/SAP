package com.ea.sap;

import com.ea.annotation.TestParameters;
import com.ea.api.cpi.request.CreateExpensePostingRequest;
import com.ea.api.cpi.request.CreateTimeSheetEntryRequest;
import com.ea.api.cpi.response.CreateExpensePostingResponse;
import com.ea.api.cpi.response.CreateCpiTimeSheetEntryResponse;
import com.ea.api.netsuite.request.CreateEngagementLetterRequest;
import com.ea.api.netsuite.request.GetEngagementLetterRequest;
import com.ea.api.netsuite.request.PatchEngagementLetterRequest;
import com.ea.api.netsuite.response.CreateEngagementLetterResponse;
import com.ea.api.netsuite.response.GetEngagementLetterResponse;
import com.ea.api.netsuite.response.PatchEngagementLetterResponse;
import com.ea.api.sap.request.GetTimeSheetEntryRequest;
import com.ea.api.sap.response.CreateTimeSheetEntryResponse;
import com.ea.api.sap.response.GetTimeSheetEntryResponse;
import com.ea.config.GlobalVariables;
import com.ea.enums.LegalEntity;
import com.ea.enums.ProjectType;
import com.ea.listeners.AzureTestListener;
import com.ea.models.cpi.expense.ExpensePostingData;
import com.ea.models.cpi.timesheet.CpiTimeSheetEntryDO;
import com.ea.models.sap.project.SapProjectDO;
import com.ea.models.sap.timesheet.TimeSheetEntryDO;
import com.ea.pages.btp.BTPLoginPage;
import com.ea.utils.*;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Ignore;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.ea.pages.sap.CustomerProjectsPage;
import com.ea.pages.sap.ManagePricesSalesPage;
import com.ea.pages.sap.ManageProjectBillingPage;
import com.ea.pages.sap.SAPLoginPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Listeners(AzureTestListener.class)
public class ProfessionalServiceTaxTest extends BaseSapTest {
    private static final ProjectType PROJECT_TYPE = ProjectType.TAX_C;
    private static final String SAP_USERNAME = GlobalVariables.CONFIG.sapUsername();
    private static final String SAP_PASSWORD = GlobalVariables.CONFIG.sapPassword();
    private static final String SAP_LOGIN_URL = GlobalVariables.CONFIG.sapLoginUrl();
    private static final String BTP_LOGIN_URL = GlobalVariables.CONFIG.btpLoginUrl();
    private static final String ON_ACCOUNT_INVOICE_TYPE = "On Account";
    private static final String ON_ACCOUNT_BILLING_DUE_AMOUNT = "4000";
    private static final String ON_ACCOUNT_BILLING_DUE_DATE = DateUtil.getDateMinusDays(30, GlobalVariables.SAP_BTP_DATE_FORMAT);
    private static final String ON_ACCOUNT_INVOICE_AMOUNT_SAP = "4,000.00 USD";
    private static final String ON_ACCOUNT_INVOICE_DATE_SAP = DateUtil.getDateMinusDays(30, GlobalVariables.SAP_UI_DATE_FORMAT);
    private static final String T_AND_E_BILLING_DUE_DATE = DateUtil.getLastDayOfPreviousMonth(GlobalVariables.SAP_UI_DATE_FORMAT);
    private static SapProjectDO PROJECT_DO;
    private static String ON_ACCOUNT_BILLING_ELEMENT_NUMBER;
    private static String T_AND_E_BILLING_ELEMENT_NUMBER;
    private static String ON_ACCOUNT_WORK_PACKAGE_NUMBER;
    private static String T_AND_E_WORK_PACKAGE_NUMBER;

    @Test(priority = 201, groups = {"JOB1", "API"})
    @TestParameters(azureId = "201", recording = false)
    public void createTaxProjectTest(ITestContext context) {

        // Location at which API Logs will be saved - /report/evidence/101/<logfile>
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";

        //Create Engagement Letter - Data Preparation
        long sharePointElId = RandomUtil.generateRandomNumberLong(10);
        String projectStartDate = DateUtil.getFirstDayOfMonth(6, GlobalVariables.NS_API_DATE_FORMAT);
        String projectEndDate = DateUtil.getLastDayOfMonth(-6, GlobalVariables.NS_API_DATE_FORMAT);
        String periodEndDate = projectEndDate;
        String jsonFilePath = GlobalVariables.NS_PROJECT_JSON_MAP.get(PROJECT_TYPE);
        String legalEntity = GlobalVariables.NS_PROJECT_LEGAL_ENTITY_MAP.get(PROJECT_TYPE);

        //Create Engagement Letter - Submit Request
        CreateEngagementLetterRequest createElRequest = new CreateEngagementLetterRequest(directoryPath);
        String createElJsonBody = createElRequest.generateJsonBody(jsonFilePath, sharePointElId, projectStartDate,
                projectEndDate, periodEndDate);
        CreateEngagementLetterResponse createElResponse = createElRequest.submitRequest(createElJsonBody);

        //Create Engagement Letter - Assert Response
        Assert.assertEquals(createElResponse.getStatus(), "Success");
        Assert.assertEquals(createElResponse.getStatusCode(), 200);

        //Create Engagement Letter - Extract NetSuite ID for next request
        String internalID = createElResponse.getNetSuiteId();

        //Patch Legal Entity of engagement letter - Submit Request
        PatchEngagementLetterRequest patchElRequest = new PatchEngagementLetterRequest(directoryPath, Long.parseLong(internalID));
        String patchElJsonBody = patchElRequest.generateJsonBody(LegalEntity.valueOf(legalEntity));
        PatchEngagementLetterResponse patchEngagementLetterResponse = patchElRequest.submitRequest(patchElJsonBody);

        //Patch Legal Entity of engagement letter - Assert Response
        Assert.assertEquals(patchEngagementLetterResponse.getStatusCode(), 204);

        /*
        //Attach File to engagement letter - Submit Request
        AddDocumentToEngagementLetterRequest addDocumentRequest = new AddDocumentToEngagementLetterRequest(directoryPath);
        String addDocumentJsonBody = addDocumentRequest.generateJsonBody(GlobalVariables.NS_ATTACH_FILE_API_JSON_PATH, sharePointElId);
        AddDocumentToEngagementLetterResponse addDocumentResponse = addDocumentRequest.submitRequest(addDocumentJsonBody);

        //Attach File to engagement letter - Assert Response
        Assert.assertEquals(addDocumentResponse.getStatus().toLowerCase(), "Success".toLowerCase());
        */

        //Update NetSuite ID and SharePoint ID in Excel Report
        ExcelReportUtil.ProjectData.updateProjectHeaderData(PROJECT_TYPE, internalID
                , String.valueOf(sharePointElId), projectStartDate, projectEndDate, periodEndDate);
    }

    @Test(priority = 202, groups = {"JOB4", "API"})
    @TestParameters(azureId = "202", recording = false)
    public void getTaxProjectErpIdFromNetSuiteTest(ITestContext context) {
        // Location at which API Logs will be saved - /report/evidence/102/<logfile>
        String azureId = (String) context.getAttribute("azureId");

        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";

        //Get Engagement Letter - Submit Request
        GetEngagementLetterRequest getElRequest = new GetEngagementLetterRequest(directoryPath, Long.parseLong(ExcelReportUtil.ProjectData.getNetSuiteIdOfProject(PROJECT_TYPE)));
        GetEngagementLetterResponse getEngagementLetterResponse = getElRequest.submitRequest();

        //Get Engagement Letter - Assert Response
        Assert.assertEquals(getEngagementLetterResponse.getStatusCode(), 200);
        ExcelReportUtil.ProjectData.updateEngNumberOfProject(PROJECT_TYPE, getEngagementLetterResponse.getEngagementLetterNumber());

        Assert.assertNotNull(getEngagementLetterResponse.getProjectId());
        Assert.assertNotEquals(getEngagementLetterResponse.getProjectId(), "");

        //Update Engagement Letter ERP ID in Excel Report
        ExcelReportUtil.ProjectData.updateErpIdOfProject(PROJECT_TYPE, getEngagementLetterResponse.getProjectId());

        PROJECT_DO = new SapProjectDO.Builder(PROJECT_TYPE).build();
        ON_ACCOUNT_BILLING_ELEMENT_NUMBER = PROJECT_DO.getBillingElements().stream().filter(x -> x.getBillingElementId().endsWith(".0.2")).findFirst().get().getBillingElementId();
        T_AND_E_BILLING_ELEMENT_NUMBER = PROJECT_DO.getBillingElements().stream().filter(x -> x.getBillingElementId().endsWith(".0.1")).findFirst().get().getBillingElementId();
        ON_ACCOUNT_WORK_PACKAGE_NUMBER = PROJECT_DO.getWorkPackages().stream().filter(x -> x.getWorkPackageId().endsWith(".1.2")).findFirst().get().getWorkPackageId();
        T_AND_E_WORK_PACKAGE_NUMBER = PROJECT_DO.getWorkPackages().stream().filter(x -> x.getWorkPackageId().endsWith(".1.1")).findFirst().get().getWorkPackageId();
    }

    @Test(priority = 203, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "203")
    public void verifyTaxProjectDataInSap() {

        String projectID = ExcelReportUtil.ProjectData.getErpIdOfProject(PROJECT_TYPE);
        Browser.navigateToURL(SAP_LOGIN_URL);
        CustomerProjectsPage customerProjectsPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickPlanCustomerProjectsTile()
                        .searchProjectByProjectId(projectID);

        CustomerProjectsPage.PlanCustomerProjectsInformationTab informationTab = customerProjectsPage.getInformationTab();
        Assert.assertEquals(informationTab.getProjectID(), PROJECT_DO.getProjectId());
        Assert.assertEquals(informationTab.getProjectStatus(), PROJECT_DO.getProjectStatus());
        Assert.assertTrue(informationTab.getProfitCenter().contains(PROJECT_DO.getProfitCenter()));
        Assert.assertEquals(informationTab.getEngagementLetterNumber(), PROJECT_DO.getEngagementLetterNumber());
        Assert.assertEquals(informationTab.getOpportunityNumber(), PROJECT_DO.getOpportunityNumber());
        Assert.assertEquals(informationTab.getEngagementLetterInternalID(), PROJECT_DO.getNetSuiteInternalId());
        Assert.assertTrue(informationTab.getPrimaryServiceItem().contains(PROJECT_DO.getPrimaryService()));
        Assert.assertEquals(informationTab.getBudgetedProjRealization(), PROJECT_DO.getRealizationPercentage());
        Assert.assertEquals(informationTab.getBudgetedProjectHours(), PROJECT_DO.getBudgetedProjectHours());
        Assert.assertEquals(informationTab.getAdminChargePercentage(), PROJECT_DO.getAdminFee());
        Assert.assertEquals(informationTab.getPricingCondition(), PROJECT_DO.getPricingConditionFlag());
        Assert.assertEquals(informationTab.getAdminFlag(), PROJECT_DO.getAdminFeeFlag());

        CustomerProjectsPage.PlanCustomerProjectsWorkPackageTab workPackageTab = customerProjectsPage.clickWorkPackagesTab();
        Assert.assertEquals(workPackageTab.getAllWorkPackages(), PROJECT_DO.getWorkPackages());

        CustomerProjectsPage.PlanCustomerProjectsBillingTab billingTab = customerProjectsPage.clickBillingTab();
        Assert.assertEquals(billingTab.getAllBillingItems(), PROJECT_DO.getBillingElements());
    }

    @Test(priority = 204, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "204")
    public void verifyTaxProjectPricingConditionDataInSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        ManagePricesSalesPage managePricesSalesPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickManagePricesTile()
                        .searchProjectID(PROJECT_DO.getProjectId());
        Assert.assertEquals(new ManagePricesSalesPage().getAllPricingConditionItems(), PROJECT_DO.getPricingConditions());
    }

    @Test(priority = 205,enabled = false, groups = {"JOB4", "API"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "205", recording = false)
    @Ignore
    public void submitTimeSheetForTaxProjectTest(ITestContext context) {

        // Location at which API Logs will be saved
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";

        //Get TimeSheetEntry - Submit Request
        GetTimeSheetEntryRequest getTimeSheetEntryRequest = new GetTimeSheetEntryRequest(directoryPath);
        GetTimeSheetEntryResponse getTimeSheetEntryResponse = getTimeSheetEntryRequest.submitRequest();

        //Get TimeSheetEntry - Assert Response
        Assert.assertEquals(getTimeSheetEntryResponse.getStatusCode(), 200);

        //Get TimeSheetEntry - Extract AuthToken
        String authToken = getTimeSheetEntryResponse.getToken();
        List<String> cookies = getTimeSheetEntryResponse.getCookies();

        //Create TimeSheetEntry - Data Preparation
        String jsonFilePath = GlobalVariables.SAP_TIMESHEET_JSON_MAP.get(PROJECT_TYPE);
        String workPackageId = T_AND_E_WORK_PACKAGE_NUMBER;
        String date = DateUtil.getDateMinusMonth(1, GlobalVariables.SAP_API_DATETIME_FORMAT);
        TimeSheetEntryDO timeSheetEntryDO = TimeSheetEntryDO.getObject(jsonFilePath, workPackageId, date);

        //Create TimeSheetEntry - Submit Request
        com.ea.api.sap.request.CreateTimeSheetEntryRequest createTimeSheetEntryRequest = new com.ea.api.sap.request.CreateTimeSheetEntryRequest(directoryPath);
        CreateTimeSheetEntryResponse createTimeSheetEntryResponse = createTimeSheetEntryRequest.submitRequest(timeSheetEntryDO, authToken, cookies);

        //Create TimeSheetEntry - Assert Response
        Assert.assertEquals(createTimeSheetEntryResponse.getStatusCode(), 201);
    }

    @Test(priority = 205, groups = {"JOB4", "API"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "205", recording = false)
    public void submitCpiTimeSheetForTaxProjectTest(ITestContext context) {

        // Location at which API Logs will be saved
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";

        //Create CpiTimeSheetEntry - Data Preparation
        String jsonFilePath = GlobalVariables.CPI_TIMESHEET_JSON_MAP.get(PROJECT_TYPE);
        String workPackageId = T_AND_E_WORK_PACKAGE_NUMBER;
        String date = DateUtil.getDateMinusMonth(1, GlobalVariables.CPI_API_DATETIME_FORMAT);
        CpiTimeSheetEntryDO cpiTimeSheetEntryDO = CpiTimeSheetEntryDO.getObject(jsonFilePath, workPackageId, date);

        //Create CpiTimeSheetEntry - Submit Request
        CreateTimeSheetEntryRequest createCpiTimeSheetEntryRequest = new CreateTimeSheetEntryRequest(directoryPath);
        CreateCpiTimeSheetEntryResponse createCpiTimeSheetEntryResponse = createCpiTimeSheetEntryRequest.submitRequest(cpiTimeSheetEntryDO);

        //Create CpiTimeSheetEntry - Assert Response
        Assert.assertEquals(createCpiTimeSheetEntryResponse.getStatusCode(), 201);
    }

    @Test(priority = 206, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "206")
    public void verifyTimeSheetForTaxProjectTest() {
    }

    @Test(priority = 207, groups = {"JOB4", "API"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "207")
    public void submitExpenseForTaxProjectTest(ITestContext context) {

        // Setup
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";
        String templatePath = GlobalVariables.CPI_EXPENSE_TEMPLATE_PATH_MAP.get(PROJECT_TYPE);

        // Dynamic data
        String workPackageId = T_AND_E_WORK_PACKAGE_NUMBER;
        String formattedWorkPackageId =
                workPackageId.substring(0, 7) + "|"
                        + workPackageId.substring(7, 12) + "|"
                        + workPackageId.substring(13,16);
        String date = DateUtil.getDateMinusMonth(1,GlobalVariables.CPI_API_DATETIME_FORMAT);
        String requestID = RandomUtil.generateUUIDFromHexId();
        long batchId = DateUtil.getCurrentEpochSecond();
        long referenceID = DateUtil.getCurrentEpochSecond();

        ExpensePostingData data = new ExpensePostingData(
                formattedWorkPackageId,
                date,
                requestID,
                batchId,
                referenceID
        );

        // Submit request
        CreateExpensePostingRequest request = new CreateExpensePostingRequest(directoryPath);
        CreateExpensePostingResponse response = request.submitRawRequestFromTemplate(templatePath, workPackageId);

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 Created");
    }

    @Test(priority = 208, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "208")
    public void createTaxProjectOnAccountBillingFromBtp() {

        String billingText = "SAP Automation - ASR - On Account Invoice";
        String billingInstruction = "On Account";

        Browser.navigateToURL(BTP_LOGIN_URL);
        new BTPLoginPage()
                .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                .clickWipEditorTile()
                .enterProjectId(PROJECT_DO.getProjectId())
                .selectOpenWipExistsAsBlank()
                .clickGoButton()
                .navigateWipProjectDetailsPage(PROJECT_DO.getProjectId())
                .clickOnAccountTab()
                .clickIssueOnAccountBillPlanButton()
                .submitOnAccountBillingRequest(ON_ACCOUNT_BILLING_ELEMENT_NUMBER, ON_ACCOUNT_BILLING_DUE_DATE, ON_ACCOUNT_BILLING_DUE_AMOUNT, billingText, billingInstruction);
    }

    @Test(priority = 209, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest", "createTaxProjectOnAccountBillingFromBtp"})
    @TestParameters(azureId = "209")
    public void generateTaxProjectOnAccountInvoiceTest() {

        int billingTableRowNumber = 1;
        int sdDocumentTableRowNumber = 1;
        int pbdDocumentTableRowNumber = 1;
        String approverCode = "3";
        String approverText = "Approval Not Required";
        String currentDate = DateUtil.getDateMinusDays(0, GlobalVariables.SAP_UI_DATE_FORMAT);
        int excelRowNumber = ExcelReportUtil.InvoiceData.createInvoiceEntry(PROJECT_TYPE.name(), "On Account", ON_ACCOUNT_BILLING_ELEMENT_NUMBER);

        Browser.navigateToURL(SAP_LOGIN_URL);
        ManageProjectBillingPage.ManageProjectBillingTable
                manageProjectBillingTable =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .openManageProjectBillingAutomationTile()
                        .filterByBillingElement(ON_ACCOUNT_BILLING_ELEMENT_NUMBER)
                        .getBillingTable();
        Assert.assertEquals(manageProjectBillingTable.getBillingElementNumber(1), ON_ACCOUNT_BILLING_ELEMENT_NUMBER);
        Assert.assertEquals(manageProjectBillingTable.getProjectNumber(1), PROJECT_DO.getProjectId());

        ManageProjectBillingPage.NewPrepaymentDialog
                newPrepaymentDialog =
                manageProjectBillingTable
                        .selectCheckboxForRow(billingTableRowNumber)
                        .clickPrepaymentLink()
                        .getNewPrepaymentDialog();

        Assert.assertEquals(newPrepaymentDialog.getAllOverdueItemsCount(), 1);
        Assert.assertEquals(newPrepaymentDialog.getInvoiceTypeForRow(1), ON_ACCOUNT_INVOICE_TYPE);
        Assert.assertEquals(newPrepaymentDialog.getInvoiceAmountForRow(1), ON_ACCOUNT_INVOICE_AMOUNT_SAP);
        Assert.assertEquals(newPrepaymentDialog.getInvoiceDateForRow(1), ON_ACCOUNT_INVOICE_DATE_SAP);

        newPrepaymentDialog
                .clickPrepaymentDialogSubmitButton()
                .getSuccessDialog()
                .clickCreatePreliminaryBillingDocumentLink()
                .getSdDocumentTable()
                .updateSdNumberInExcel(excelRowNumber)
                .selectCheckboxForRow(sdDocumentTableRowNumber)
                .clickCreatePreliminaryBillingDocumentLink()
                .getPbdDocumentTable()
                .selectCheckboxForRow(pbdDocumentTableRowNumber)
                .getPbdDocumentTable()
                .updatePbdNumber(excelRowNumber)
                .navigateToDisplayPreliminaryBillingDocumentPageForRow(1)
                .clickChangeBillingDocumentButton()
                .clickDisplayHeaderDetailsIcon()
                .clickCustomFieldsTab()
                .enterInvoiceApproverAs(approverCode, approverText)
                .clickSaveButton()
                .clickBackButton()
                .clickCreateBillingDocumentsLink()
                .getCreateBillingDialog()
                .enterBillingDate(currentDate)
                .clickOkButton()
                .updateInvoiceNumber(excelRowNumber)
                .clickOnPostBillingDocument();
    }

    @Test(priority = 210, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "210")
    public void verifyOnAccountInvoiceInBtp() {

    }

    @Test(priority = 211, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "211")
    public void createTaxProjectTimeAndExpenseBillingFromSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        new SAPLoginPage()
                .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                .clickPlanCustomerProjectsTile()
                .searchProjectByProjectId(PROJECT_DO.getProjectId())
                .clickEditProjectButton()
                .getInformationTab()
                .enterInvoiceTemplateNumber("2")
                .getCustomerProjectEditPage()
                .clickBillingTab()
                .clickBillingElementRow(T_AND_E_BILLING_ELEMENT_NUMBER)
                .clickCreateBillingDueDateButton()
                .enterTimeAndExpenseEntryForLastRow(T_AND_E_BILLING_DUE_DATE)
                .clickBackButton()
                .clickSaveButtonNoMessageDialog();
    }

    @Test(priority = 212, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest", "submitCpiTimeSheetForTaxProjectTest", "createTaxProjectTimeAndExpenseBillingFromSap"})
    @TestParameters(azureId = "212")
    public void generateTaxProjectTimeAndExpenseInvoiceTest() {

        int billingTableRowNumber = 1;
        int sdDocumentTableRowNumber = 1;
        int pbdDocumentTableRowNumber = 1;
        String approverCode = "3";
        String approverText = "Approval Not Required";

        Browser.navigateToURL(SAP_LOGIN_URL);
        ManageProjectBillingPage.ManageProjectBillingTable
                manageProjectBillingTable =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .openManageProjectBillingTimeAndExpenseTile()
                        .filterByBillingElement(T_AND_E_BILLING_ELEMENT_NUMBER)
                        .getBillingTable();
        Assert.assertEquals(manageProjectBillingTable.getBillingElementNumber(1), T_AND_E_BILLING_ELEMENT_NUMBER);
        Assert.assertEquals(manageProjectBillingTable.getProjectNumber(1), PROJECT_DO.getProjectId());

        int excelRowNumber = ExcelReportUtil.InvoiceData.createInvoiceEntry(PROJECT_TYPE.name(), "Time and Expense", T_AND_E_BILLING_ELEMENT_NUMBER);

        manageProjectBillingTable
                .selectCheckboxForRow(billingTableRowNumber)
                .clickPrepareBillingLink()
                .clickSubmitButton()
                .getSuccessDialog()
                .clickCreatePreliminaryBillingDocumentLink()
                .getSdDocumentTable()
                .updateSdNumberInExcel(excelRowNumber)
                .selectCheckboxForRow(sdDocumentTableRowNumber)
                .clickCreatePreliminaryBillingDocumentLink()
                .getPbdDocumentTable()
                .selectCheckboxForRow(pbdDocumentTableRowNumber)
                .getPbdDocumentTable()
                .updatePbdNumber(excelRowNumber)
                .navigateToDisplayPreliminaryBillingDocumentPageForRow(1)
                .clickChangeBillingDocumentButton()
                .clickDisplayHeaderDetailsIcon()
                .clickCustomFieldsTab()
                .enterInvoiceApproverAs(approverCode, approverText)
                .clickSaveButton()
                .clickBackButton()
                .clickCreateBillingDocumentsLink()
                .getCreateBillingDialog()
                .enterBillingDate(T_AND_E_BILLING_DUE_DATE)
                .clickOkButton()
                .updateInvoiceNumber(excelRowNumber)
                .clickOnPostBillingDocument();
    }

    @Test(priority = 213, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"getTaxProjectErpIdFromNetSuiteTest"})
    @TestParameters(azureId = "213")
    public void verifyTimeAndExpenseInvoiceInBtp() {

    }
}