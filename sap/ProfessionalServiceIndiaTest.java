package com.ea.sap;

import com.ea.annotation.TestParameters;
import com.ea.api.cpi.request.CreateExpensePostingRequest;
import com.ea.api.cpi.request.CreateTimeSheetEntryRequest;
import com.ea.api.cpi.response.CreateExpensePostingResponse;
import com.ea.api.cpi.response.CreateCpiTimeSheetEntryResponse;
import com.ea.api.sap.request.GetTimeSheetEntryRequest;
import com.ea.api.sap.response.CreateTimeSheetEntryResponse;
import com.ea.api.sap.response.GetTimeSheetEntryResponse;
import com.ea.config.GlobalVariables;
import com.ea.enums.ProjectType;
import com.ea.listeners.AzureTestListener;
import com.ea.models.cpi.expense.ExpensePostingData;
import com.ea.models.cpi.timesheet.CpiTimeSheetEntryDO;
import com.ea.models.sap.project.SapProjectDO;
import com.ea.models.sap.timesheet.TimeSheetEntryDO;
import com.ea.pages.sap.*;
import com.ea.utils.Browser;
import com.ea.utils.DateUtil;
import com.ea.utils.ExcelReportUtil;
import com.ea.utils.RandomUtil;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Ignore;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Listeners(AzureTestListener.class)
public class ProfessionalServiceIndiaTest extends BaseSapTest {

    private static final ProjectType PROJECT_TYPE = ProjectType.IND;
    private static final String SAP_USERNAME = GlobalVariables.CONFIG.sapUsername();
    private static final String SAP_PASSWORD = GlobalVariables.CONFIG.sapPassword();
    private static final String SAP_LOGIN_URL = GlobalVariables.CONFIG.sapLoginUrl();
    private static final String BTP_LOGIN_URL = GlobalVariables.CONFIG.btpLoginUrl();
    private static final String ON_ACCOUNT_INVOICE_TYPE = "On Account";
    private static final String ON_ACCOUNT_BILLING_DUE_AMOUNT = "5000";
    private static final String ON_ACCOUNT_BILLING_DUE_DATE = DateUtil.getDateMinusDays(30, GlobalVariables.SAP_UI_DATE_FORMAT);
    private static final String ON_ACCOUNT_INVOICE_AMOUNT_SAP = "5,000.00 INR";
    private static final String ON_ACCOUNT_INVOICE_DATE_SAP = DateUtil.getDateMinusDays(30, GlobalVariables.SAP_UI_DATE_FORMAT);
    private static final String T_AND_E_BILLING_DUE_DATE = DateUtil.getLastDayOfPreviousMonth(GlobalVariables.SAP_UI_DATE_FORMAT);
    private static SapProjectDO PROJECT_DO;
    private static String ON_ACCOUNT_BILLING_ELEMENT_NUMBER;
    private static String T_AND_E_BILLING_ELEMENT_NUMBER;
    private static String ON_ACCOUNT_WORK_PACKAGE_NUMBER;
    private static String T_AND_E_WORK_PACKAGE_NUMBER;
    private static final String CLONE_PROJECT_ID = "101254000004";
    private static String NEW_PROJECT_ID;


    @Test(priority = 301, groups = {"JOB1", "UI"})
    @TestParameters(azureId = "301")
    public void createIndiaProjectTest(ITestContext context) {

        //GET LAST PROJECT ID
        Browser.navigateToURL(SAP_LOGIN_URL);
        String lastProjectId =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickCustomerProjectHeaderDataTile()
                        .getLastProjectId();

        //STORE DATA IN EXCEL AND CREATE PROJECT DO
        NEW_PROJECT_ID = String.valueOf(Long.parseLong(lastProjectId) + 1);
        String projectStartDate = DateUtil.getFirstDayOfMonth(6, GlobalVariables.NS_API_DATE_FORMAT);
        String projectEndDate = DateUtil.getLastDayOfMonth(-6, GlobalVariables.NS_API_DATE_FORMAT);
        String periodEndDate = projectEndDate;
        ExcelReportUtil.ProjectData.updateErpIdOfProject(PROJECT_TYPE, NEW_PROJECT_ID);
        ExcelReportUtil.ProjectData.updateProjectHeaderData(PROJECT_TYPE, "", "", projectStartDate, projectEndDate, periodEndDate);
        PROJECT_DO = new SapProjectDO.Builder(PROJECT_TYPE).build();
        String projectStartDateCopyFormat = DateUtil.changeFormat(PROJECT_DO.getProjectStartDate(), GlobalVariables.SAP_UI_DATE_FORMAT, "MMMM dd, yyyy");

        //COPY PROJECT
        new SAPCommonPage().clickOnSapLogo()
                .clickPlanCustomerProjectsTile()
                .searchProjectByProjectId(CLONE_PROJECT_ID)
                .clickCopyProjectButton()
                .selectWorkPackagesAndServicesRadioOption()
                .enterNewStartData(projectStartDateCopyFormat)
                .clickCopyButton()
                .enterProjectName(PROJECT_DO.getProjectName())
                .enterDuration(PROJECT_DO.getProjectStartDate() + " - " + PROJECT_DO.getProjectEndDate())
                .enterEngagementYear(PROJECT_DO.getEngagementYear())
                .enterProjectId(PROJECT_DO.getProjectId())
                .clickCreateButton();

        //MOVE PROJECT TO CONTRACT PREPARATION
        CustomerProjectsEditPage.MessageBox messageBox =
                new SAPCommonPage().clickOnSapLogo()
                        .clickPlanCustomerProjectsTile()
                        .searchProjectByProjectId(PROJECT_DO.getProjectId())
                        .clickEditProjectButton()
                        .getInformationTab()
                        .setProjectStageAsContractPreparation();

        Assert.assertTrue(messageBox.getAllMessages().stream().anyMatch(x->x.trim().equalsIgnoreCase("Please review the preferred billing solution for this project as you cannot change this setting after the Contract Preparation stage. To update this setting, change the stage to In Planning.")));

        messageBox
                .clickCloseButton()
                .clickSaveButtonNoMessageDialog();

        //ADD BILLING ELEMENTS
        messageBox =
                new SAPCommonPage()
                        .clickOnSapLogo()
                        .clickPlanCustomerProjectsTile()
                        .searchProjectByProjectId(PROJECT_DO.getProjectId())
                        .clickEditProjectButton()
                        .clickBillingTab()
                        .addBillingElementData(1, "Time and Expenses", PROJECT_DO.getWorkPackages().get(0).getWorkPackageName(), "0.01")
                        .clickCreateBillingButton()
                        .addBillingElementData(2, "Time and Expenses", PROJECT_DO.getWorkPackages().get(1).getWorkPackageName(), "0.01")
                        .getCustomerProjectEditPage()
                        .clickSaveButtonNoMessageDialog()
                        .getMessageBox();

        Assert.assertTrue(messageBox.getAllMessages().stream().anyMatch(x->x.startsWith("Sales Order Service") && x.endsWith("has been saved.")));

        //MOVE PROJECT TO IN EXECUTION
        messageBox
                .clickCloseButton()
                .clickSaveButtonNoMessageDialog();

        messageBox =
                new SAPCommonPage().clickOnSapLogo()
                        .clickPlanCustomerProjectsTile()
                        .searchProjectByProjectId(PROJECT_DO.getProjectId())
                        .clickEditProjectButton()
                        .getInformationTab()
                        .setProjectStageAsInExecution()
                        .getCustomerProjectEditPage()
                        .clickSaveButton();

        Assert.assertTrue(messageBox.getAllMessages().stream().anyMatch(x->x.equalsIgnoreCase("Project updated")));
        Assert.assertTrue(messageBox.getAllMessages().stream().anyMatch(x->x.equalsIgnoreCase("Baseline version created.")));
    }

    @Test(priority = 302, groups = {"JOB4", "API"})
    @TestParameters(azureId = "", recording = false)
    public void setupExpectedObjectForIndiaProjectTest(ITestContext context) {
        PROJECT_DO = new SapProjectDO.Builder(PROJECT_TYPE).build();
        ON_ACCOUNT_BILLING_ELEMENT_NUMBER = PROJECT_DO.getBillingElements().stream().filter(x->x.getBillingElementId().endsWith(".0.2")).findFirst().get().getBillingElementId();
        T_AND_E_BILLING_ELEMENT_NUMBER = PROJECT_DO.getBillingElements().stream().filter(x->x.getBillingElementId().endsWith(".0.1")).findFirst().get().getBillingElementId();
        ON_ACCOUNT_WORK_PACKAGE_NUMBER = PROJECT_DO.getWorkPackages().stream().filter(x->x.getWorkPackageId().endsWith(".1.2")).findFirst().get().getWorkPackageId();
        T_AND_E_WORK_PACKAGE_NUMBER = PROJECT_DO.getWorkPackages().stream().filter(x->x.getWorkPackageId().endsWith(".1.1")).findFirst().get().getWorkPackageId();
    }

    @Test(priority = 303, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "303")
    public void verifyIndiaProjectDataInSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        CustomerProjectsPage customerProjectsPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickPlanCustomerProjectsTile()
                        .searchProjectByProjectId(PROJECT_DO.getProjectId());

        CustomerProjectsPage.PlanCustomerProjectsInformationTab informationTab = customerProjectsPage.getInformationTab();
        Assert.assertEquals(informationTab.getProjectID(), PROJECT_DO.getProjectId());
        Assert.assertEquals(informationTab.getProjectStatus(), PROJECT_DO.getProjectStatus());
        Assert.assertTrue(informationTab.getProfitCenter().contains(PROJECT_DO.getProfitCenter()));
        Assert.assertEquals(informationTab.getOpportunityNumber(), PROJECT_DO.getOpportunityNumber());
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

    @Test(priority = 304, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "304")
    public void verifyIndiaProjectPricingConditionDataInSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        ManagePricesSalesPage managePricesSalesPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickManagePricesTile()
                        .searchProjectID(PROJECT_DO.getProjectId());
        Assert.assertEquals(new ManagePricesSalesPage().getAllPricingConditionItems(), PROJECT_DO.getPricingConditions());
    }

    @Test(priority = 305,enabled = false, groups = {"JOB4", "API"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "305", recording = false)
    @Ignore
    public void submitTimeSheetForIndiaProjectTest(ITestContext context) {

        // Location at which API Logs will be saved
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId+"/";

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

    @Test(priority = 305, groups = {"JOB4", "API"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "305", recording = false)
    public void submitCpiTimeSheetForIndiaProjectTest(ITestContext context) {

        // Location at which API Logs will be saved
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId+"/";

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

    @Test(priority = 306, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "306")
    public void verifyTimeSheetForIndiaProjectTest() {
    }

    @Test(priority = 307, groups = {"JOB4", "API"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "307")
    public void submitExpenseForIndiaProjectTest(ITestContext context) {

        // Setup
        String azureId = (String) context.getAttribute("azureId");
        String directoryPath = GlobalVariables.CONFIG.reportPath() + GlobalVariables.CONFIG.evidenceFolder() + azureId + "/";
        String templatePath = GlobalVariables.CPI_EXPENSE_TEMPLATE_PATH_MAP.get(PROJECT_TYPE);

        // Dynamic data
        String workPackageId = T_AND_E_WORK_PACKAGE_NUMBER;

        // Submit request
        CreateExpensePostingRequest request = new CreateExpensePostingRequest(directoryPath);
        CreateExpensePostingResponse response = request.submitRawRequestFromTemplate(templatePath,workPackageId);

        // Assertions
        Assert.assertEquals(response.getStatusCode(), 200, "Expected status code 200 Created");
    }

    @Test(priority = 308, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "308")
    public void createIndiaProjectOnAccountBillingFromSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        new SAPLoginPage()
                .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                .clickPlanCustomerProjectsTile()
                .searchProjectByProjectId(PROJECT_DO.getProjectId())
                .clickEditProjectButton()
                .clickBillingTab()
                .clickBillingElementRow(ON_ACCOUNT_BILLING_ELEMENT_NUMBER)
                .clickCreateBillingDueDateButton()
                .enterOnAccountEntryForLastRow(ON_ACCOUNT_BILLING_DUE_DATE, ON_ACCOUNT_BILLING_DUE_AMOUNT)
                .clickBackButton()
                .clickSaveButtonNoMessageDialog();

    }

    @Test(priority = 309, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest", "createIndiaProjectOnAccountBillingFromSap"})
    @TestParameters(azureId = "309")
    public void generateIndiaProjectOnAccountInvoiceTest() {

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

    @Test(priority = 310, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "310")
    public void verifyOnAccountInvoiceInBtp() {

    }

    @Test(priority = 311, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "311")
    public void createIndiaProjectTimeAndExpenseBillingFromSap() {

        Browser.navigateToURL(SAP_LOGIN_URL);
        new SAPLoginPage().performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
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

    @Test(priority = 312, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest", "submitCpiTimeSheetForIndiaProjectTest", "createIndiaProjectTimeAndExpenseBillingFromSap"})
    @TestParameters(azureId = "312")
    public void generateIndiaProjectTimeAndExpenseInvoiceTest() {

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

    @Test(priority = 313, enabled = false, groups = {"JOB4", "UI"}, dependsOnMethods = {"setupExpectedObjectForIndiaProjectTest"})
    @TestParameters(azureId = "313")
    public void verifyTimeAndExpenseInvoiceInBtp() {

    }
}
