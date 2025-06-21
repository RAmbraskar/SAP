package com.ea.utils;

import com.ea.config.GlobalVariables;
import com.ea.enums.ProjectType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelReportUtil {

    private ExcelReportUtil() {
    }

    private static final boolean GENERATE_REPORT = GlobalVariables.CONFIG.sapExcelReport();
    public static final String SRC_FOLDER = "./src/main/resources/data/";
    public static final String DEST_FOLDER = GlobalVariables.CONFIG.reportPath();
    public static final String FILE_NAME = "Weekly_Regression.xlsx";
    public static final String SRC_PATH = SRC_FOLDER + FILE_NAME;
    public static final String DEST_PATH = DEST_FOLDER + FILE_NAME;


    public static void copyReportToTargetLocation() {
        copyReportToTargetLocation(false);
    }

    public static void copyReportToTargetLocation(boolean replaceIfExist) {
        if (GENERATE_REPORT) {
            File file = new File(DEST_PATH);
            if (!file.exists() || replaceIfExist) {
                try {
                    FileUtils.copyFileToDirectory(new File(SRC_PATH), new File(DEST_FOLDER));
                } catch (IOException e) {
                    throw new IllegalStateException("Exception occurred while copying file to a directory. "+e);
                }
            }
        }
    }

    public static class TestCases {
        private TestCases() {
        }

        public static final String SHEET_NAME = "Test Cases";
        public static final int COL_RESULT = 4;
        public static final int COL_AZURE_ID = 0;

        public static void updateTestResult(String azureId, String result) {
            if (GENERATE_REPORT && StringUtils.isNotBlank(azureId)) {
                try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                    Sheet sheet = workbook.getSheet(SHEET_NAME);
                    Row row = sheet.getRow(getRowNumber(azureId));              // Get Row
                    Cell cell = row.createCell(COL_RESULT);          // Create cell and set value
                    cell.setCellValue(result);
                    // Write the changes back to the existing Excel file
                    try (FileOutputStream fileOutputStream = new FileOutputStream(DEST_PATH)) {
                        workbook.write(fileOutputStream);
                    }
                } catch (IOException | EncryptedDocumentException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Get row number of project
         *
         * @return int
         */
        private static int getRowNumber(String azureId) {
            boolean isRowFound = false;
            int rowNumber = -1;
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                for (int i = 1; i < sheet.getLastRowNum(); i++) {
                    String value = getCellValue(sheet.getRow(i).getCell(COL_AZURE_ID)).split("\\.")[0];
                    if (value.equalsIgnoreCase(azureId)) {
                        rowNumber = i;
                        isRowFound = true;
                        break;
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Exception occurred while getting row number from excel. " + e);
            }

            if (isRowFound) return rowNumber;
            else throw new IllegalStateException("Azure ID: " + azureId + " not found in excel.");
        }
    }

    public static class ProjectData {
        private ProjectData() {
        }

        private static final int COL_PROJECT_NAME = 1;
        private static final int COL_NETSUITE_ID = 2;
        private static final int COL_SHAREPOINT_ID = 3;
        private static final int COL_ENG_NUMBER = 4;
        private static final int COL_ERP_PROJECT_ID = 5;
        private static final int COL_WORK_ITEM_TYPE = 6;
        private static final int COL_PROJECT_START_DATE = 7;
        private static final int COL_PROJECT_END_DATE = 8;
        private static final int COL_PERIOD_END_DATE = 9;
        private static final String SHEET_NAME = "Project_Data";

        private static int getRowNumber(String projectName) {

            boolean isRowFound = false;
            int rowNumber = -1;
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    String value = getCellValue(sheet.getRow(i).getCell(COL_PROJECT_NAME));
                    if (value.equalsIgnoreCase(projectName)) {
                        rowNumber = i;
                        isRowFound = true;
                        break;
                    }
                }
            } catch (Exception e) {
                throw new IllegalStateException("Exception occurred while getting row number from excel. " + e);
            }

            if (isRowFound) return rowNumber;
            else throw new IllegalStateException("Project: " + projectName + " not found in excel.");
        }

        private static String getColumnValue(String project, int colNo) {
            int rowNumber = getRowNumber(project);
            String value = "";
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                value = getCellValue(sheet.getRow(rowNumber).getCell(colNo));
            } catch (Exception e) {
                throw new IllegalStateException("Exception occurred while getting row number from excel. " + e);
            }
            return value;
        }

        public static String getNetSuiteIdOfProject(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_NETSUITE_ID).split("\\.")[0];
        }

        public static String getSharePointIdOfProject(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_SHAREPOINT_ID);
        }

        public static String getEngNumberOfProject(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_ENG_NUMBER);
        }

        public static String getErpIdOfProject(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_ERP_PROJECT_ID);
        }

        public static String getWorkItemTypeOfProject(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_WORK_ITEM_TYPE);
        }

        public static String getProjectStartDate(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_PROJECT_START_DATE);
        }

        public static String getProjectEndDate(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_PROJECT_END_DATE);
        }

        public static String getPeriodEndDate(ProjectType projectName) {
            return getColumnValue(projectName.name(), COL_PERIOD_END_DATE);
        }

        /**
         * Write data in Excel file.
         *
         * @param colNo :: int
         * @param value :: String
         */
        private static void updateExcelRow(String project, int colNo, String value) {
            int rowNo = getRowNumber(project);
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                Row row = sheet.getRow(rowNo);              // Get Row
                Cell cell = row.createCell(colNo);          // Create cell and set value
                cell.setCellValue(value);
                try (FileOutputStream fileOutputStream = new FileOutputStream(DEST_PATH)) {
                    workbook.write(fileOutputStream);
                }
            } catch (IOException | EncryptedDocumentException e) {
                throw new IllegalStateException("Exception occurred while updating value in excel. " + e);
            }
        }

        public static void updateNetSuiteIdOfProject(ProjectType projectName, String netSuiteID) {
            updateExcelRow(projectName.name(), COL_NETSUITE_ID, netSuiteID);
        }

        public static void updateSharePointIdOfProject(ProjectType projectName, String sharePointID) {
            updateExcelRow(projectName.name(), COL_SHAREPOINT_ID, sharePointID);
        }

        public static void updateEngNumberOfProject(ProjectType projectName, String engNumber) {
            updateExcelRow(projectName.name(), COL_ENG_NUMBER, engNumber);
        }

        public static void updateErpIdOfProject(ProjectType projectName, String erpProjectID) {
            updateExcelRow(projectName.name(), COL_ERP_PROJECT_ID, erpProjectID);
        }

        public static void updateProjectStartDate(ProjectType projectType, String projectStartDate) {
            updateExcelRow(projectType.name(), COL_PROJECT_START_DATE, projectStartDate);
        }

        public static void updateProjectEndDate(ProjectType projectType, String projectEndDate) {
            updateExcelRow(projectType.name(), COL_PROJECT_END_DATE, projectEndDate);
        }

        public static void updatePeriodEndDate(ProjectType projectType, String periodEndDate) {
            updateExcelRow(projectType.name(), COL_PERIOD_END_DATE, periodEndDate);
        }

        public static void updateProjectHeaderData(ProjectType projectType, String netSuiteInternalId, String sharePointId,
                                            String projectStartDate, String projectEndDate, String periodEndDate) {

            int rowNo = getRowNumber(projectType.name());
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                Row row = sheet.getRow(rowNo);              // Get Row
                row.createCell(COL_NETSUITE_ID).setCellValue(netSuiteInternalId);
                row.createCell(COL_SHAREPOINT_ID).setCellValue(sharePointId);
                row.createCell(COL_PROJECT_START_DATE).setCellValue(projectStartDate);
                row.createCell(COL_PROJECT_END_DATE).setCellValue(projectEndDate);
                row.createCell(COL_PERIOD_END_DATE).setCellValue(periodEndDate);

                try (FileOutputStream fileOutputStream = new FileOutputStream(DEST_PATH)) {
                    workbook.write(fileOutputStream);
                }
            } catch (IOException | EncryptedDocumentException e) {
                throw new IllegalStateException("Exception occurred while updating value in excel. " + e);
            }

        }
    }


    public static class InvoiceData {

        private InvoiceData() {
        }

        private static final int COL_PROJECT_TYPE = 0;
        private static final int COL_INVOICE_TYPE = 1;
        private static final int COL_BILLING_ELEMENT = 2;
        private static final int COL_SD_NUMBER = 3;
        private static final int COL_PBD_NUMBER = 4;
        private static final int COL_INVOICE_NUMBER = 5;
        private static final String SHEET_NAME = "Invoice_Data";

        public static int createInvoiceEntry(String projectType, String invoiceType, String billingElementNumber) {
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                Row row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(COL_PROJECT_TYPE).setCellValue(projectType);
                row.createCell(COL_INVOICE_TYPE).setCellValue(invoiceType);
                row.createCell(COL_BILLING_ELEMENT).setCellValue(billingElementNumber);
                try (FileOutputStream fileOutputStream = new FileOutputStream(DEST_PATH)) {
                    workbook.write(fileOutputStream);
                }
                return row.getRowNum();
            } catch (IOException | EncryptedDocumentException e) {
                throw new IllegalStateException("Exception occurred while updating value in excel. " + e);
            }
        }

        public static void updateSdNumber(int rowNumber, String value) {
            updateCellValue(rowNumber, COL_SD_NUMBER, value);
        }

        public static void updatePbdNumber(int rowNumber, String value) {
            updateCellValue(rowNumber, COL_PBD_NUMBER, value);
        }

        public static void updateInvoiceNumber(int rowNumber, String value) {
            updateCellValue(rowNumber, COL_INVOICE_NUMBER, value);
        }

        private static void updateCellValue(int rowNumber, int colNumber, String value) {
            try (Workbook workbook = WorkbookFactory.create(new FileInputStream(DEST_PATH))) {
                Sheet sheet = workbook.getSheet(SHEET_NAME);
                Row row = sheet.getRow(rowNumber);
                row.createCell(colNumber).setCellValue(value);
                try (FileOutputStream fileOutputStream = new FileOutputStream(DEST_PATH)) {
                    workbook.write(fileOutputStream);
                }
            } catch (IOException | EncryptedDocumentException e) {
                throw new IllegalStateException("Exception occurred while updating value in excel. " + e);
            }
        }
    }

    /**
     * Get Cell value in String format.
     *
     * @param cell :: Cell
     * @return String
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> String.valueOf(cell.getCellFormula());
            default -> "";
        };
    }
}
