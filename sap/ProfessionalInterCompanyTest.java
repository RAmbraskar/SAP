package com.ea.sap;

import com.ea.annotation.TestParameters;
import com.ea.config.GlobalVariables;
import com.ea.enums.ProjectType;
import com.ea.listeners.AzureTestListener;
import com.ea.pages.sap.ManageBillingDocumentsPage;
import com.ea.pages.sap.ProjectControlEnterprisePage;
import com.ea.pages.sap.ReassignCostsandRevenuesPage;
import com.ea.pages.sap.SAPLoginPage;
import com.ea.utils.Browser;
import com.ea.utils.DateUtil;
import com.ea.utils.ExcelReportUtil;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(AzureTestListener.class)
public class ProfessionalInterCompanyTest extends BaseSapTest {
    private static final ProjectType PROJECT_TYPE = ProjectType.INTERCOMPANY;
    private static final String SAP_LOGIN_URL = GlobalVariables.CONFIG.sapLoginUrl();
    private static final String SAP_USERNAME = GlobalVariables.CONFIG.sapUsername();
    private static final String SAP_PASSWORD = GlobalVariables.CONFIG.sapPassword();

    @Test(groups = {"JOB4", "UI"})
    @TestParameters(azureId = "601")
    public void createPostCostAllocation() {
        Browser.navigateToURL(SAP_LOGIN_URL);
        ReassignCostsandRevenuesPage reassignCostsandRevenuesPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickReassignCostsandReveneuTile()
                        .createDocument();
        Assert.assertEquals(reassignCostsandRevenuesPage.getDocumentStatus(), "Posted", "Document status does not match the expected value.");
    }

    @Test(groups = {"JOB4", "UI"}, dependsOnMethods = {"createPostCostAllocation"})
    @TestParameters(azureId = "602")
    public void generateBillingRequest(ITestContext context) {
        String documentNumber= "132985";

        Browser.navigateToURL(SAP_LOGIN_URL);
        String billingDocumentRequest =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickGenerateIntercompanyBillingRequestTile()
                        .navigateToNewJobPage()
                        .schedulingOption(documentNumber)
                        .navigateToJobLog()
                        .getBillingDocumentRequest();
        context.setAttribute("BillingDocumentRequest", billingDocumentRequest);
    }

    @Test(groups = {"JOB4", "UI"}, dependsOnMethods = {"createPostCostAllocation", "generateBillingRequest"})
    @TestParameters(azureId = "603")
    public void generateIntercompanyInvoice(ITestContext context) {
        String billingDocumentNumber = (String) context.getAttribute("BillingDocumentRequest");
        int sdDocumentTableRowNumber = 1;
        int excelRowNumber = ExcelReportUtil.InvoiceData.createInvoiceEntry(PROJECT_TYPE.name(), "", "");

        Browser.navigateToURL(SAP_LOGIN_URL);
        ManageBillingDocumentsPage manageBillingDocumentsPage =
                new SAPLoginPage()
                        .performLoginSuccess(SAP_USERNAME, SAP_PASSWORD)
                        .clickCreateBillingDocumentTile()
                        .searchSdDocument(billingDocumentNumber)
                        .getSdDocumentTable()
                        .updateSdNumberInExcel(excelRowNumber)
                        .selectCheckboxForRow(sdDocumentTableRowNumber)
                        .clickCreateBillingDocumentLink()
                        .saveInvoice()
                        .clickOnPostBillingDocument()
                        .updateInvoiceNumber(excelRowNumber);
        Assert.assertEquals(manageBillingDocumentsPage.getBillingDocumentStatus(), "Completed", "Billing document status does not match the expected value.");
    }
}
