package com.ea.config;

import com.ea.enums.ProjectType;

import java.util.EnumMap;
import java.util.Map;

public class GlobalVariables {

    private GlobalVariables() {

    }

    public static RunConfig CONFIG = RunConfig.load();
    public static Map<ProjectType, String> NS_PROJECT_JSON_MAP = new EnumMap<>(ProjectType.class);
    public static Map<ProjectType, String> SAP_PROJECT_JSON_MAP = new EnumMap<>(ProjectType.class);
    public static Map<ProjectType, String> NS_PROJECT_LEGAL_ENTITY_MAP = new EnumMap<>(ProjectType.class);
    public static Map<ProjectType, String> SAP_TIMESHEET_JSON_MAP = new EnumMap<>(ProjectType.class);
    public static Map<ProjectType, String> CPI_TIMESHEET_JSON_MAP = new EnumMap<>(ProjectType.class);
    public static Map<ProjectType, String> CPI_EXPENSE_TEMPLATE_PATH_MAP = new EnumMap<>(ProjectType.class);
    public static final String NS_API_DATE_FORMAT = "MM/dd/yyyy";
    public static final String SAP_BTP_DATE_FORMAT = "MM/dd/yyyy";
    public static final String SAP_UI_DATE_FORMAT = "dd.MM.yyyy";
    public static final String SAP_API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String CPI_API_DATETIME_FORMAT = "yyyy-MM-dd";
    public static final String NS_ATTACH_FILE_API_JSON_PATH = "/data/NetSuite/Project/Json/AttachDocumentToEL.json";

    static {
        loadNsProjectJsonFileMap();
        loadSapProjectJsonFileMap();
        loadNsProjectLegalEntityMap();
        loadSapTimeSheetJsonFileMap();
        loadCpiTimeSheetJsonFileMap();
        loadCpiExpenseTemplatePathMap();
    }

    private static void loadNsProjectJsonFileMap() {
        NS_PROJECT_JSON_MAP.clear();
        NS_PROJECT_JSON_MAP.put(ProjectType.ASR, "/data/NetSuite/Project/Json/ASR.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.TAX_C, "/data/NetSuite/Project/Json/TAX_C.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.BRC, "/data/NetSuite/Project/Json/BRC.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.BPS, "/data/NetSuite/Project/Json/BPS.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.CR, "/data/NetSuite/Project/Json/CR.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.MSMR, "/data/NetSuite/Project/Json/MSMR.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.TAS, "/data/NetSuite/Project/Json/TAS.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.PD, "/data/NetSuite/Project/Json/PD.json");
        NS_PROJECT_JSON_MAP.put(ProjectType.ADV_RPM, "/data/NetSuite/Project/Json/ADV_RPM.json");
    }

    private static void loadSapProjectJsonFileMap() {
        SAP_PROJECT_JSON_MAP.clear();
        SAP_PROJECT_JSON_MAP.put(ProjectType.ASR, "/data/SAP/Project/Json/ASR.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.TAX_C, "/data/SAP/Project/Json/TAX_C.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.BRC, "/data/SAP/Project/Json/BRC.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.BPS, "/data/SAP/Project/Json/BPS.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.CR, "/data/SAP/Project/Json/CR.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.MSMR, "/data/SAP/Project/Json/MSMR.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.TAS, "/data/SAP/Project/Json/TAS.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.PD, "/data/SAP/Project/Json/PD.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.IND, "/data/SAP/Project/Json/IND.json");
        SAP_PROJECT_JSON_MAP.put(ProjectType.ADV_RPM, "/data/SAP/Project/Json/ADV_RPM.json");
    }

    private static void loadNsProjectLegalEntityMap() {
        NS_PROJECT_LEGAL_ENTITY_MAP.clear();
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.ASR, "EISNERAMPER_LLP");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.TAX_C, "EISNER_ADVISORY_GROUP_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.BRC, "EISNER_ADVISORY_GROUP_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.BPS, "EISNER_ADVISORY_GROUP_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.CR, "EA_COMPENSATION_RESOURCE");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.MSMR, "EISNERAMPER_MANAGED_TECHNOLOGY_SERVICES_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.TAS, "EISNER_ADVISORY_GROUP_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.PD, "EISNERAMPER_MANAGED_TECHNOLOGY_SERVICES_LLC");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.IND, "EA(INDIA) CONS PVT.");
        NS_PROJECT_LEGAL_ENTITY_MAP.put(ProjectType.ADV_RPM, "EAG_RPM_PARTNERS_LLC");
    }

    private static void loadSapTimeSheetJsonFileMap() {
        SAP_TIMESHEET_JSON_MAP.clear();
        SAP_TIMESHEET_JSON_MAP.put(ProjectType.ASR, "/data/Sap/TimeEntry/Json/ASR.json");
        SAP_TIMESHEET_JSON_MAP.put(ProjectType.TAX_C, "/data/Sap/TimeEntry/Json/TAX.json");
        SAP_TIMESHEET_JSON_MAP.put(ProjectType.IND, "/data/Sap/TimeEntry/Json/IND.json");
        SAP_TIMESHEET_JSON_MAP.put(ProjectType.ADV_RPM, "/data/Sap/TimeEntry/Json/ADV_RPM.json");
    }

    private static void loadCpiTimeSheetJsonFileMap(){
        CPI_TIMESHEET_JSON_MAP.clear();
        CPI_TIMESHEET_JSON_MAP.put(ProjectType.ASR, "/data/cpi/timesheet/ASR.json");
        CPI_TIMESHEET_JSON_MAP.put(ProjectType.TAX_C, "/data/cpi/timesheet/TAX.json");
        CPI_TIMESHEET_JSON_MAP.put(ProjectType.IND, "/data/cpi/timesheet/IND.json");
        CPI_TIMESHEET_JSON_MAP.put(ProjectType.ADV_RPM, "/data/cpi/timesheet/ADV_RPM.json");
    }

    private static void loadCpiExpenseTemplatePathMap() {
        CPI_EXPENSE_TEMPLATE_PATH_MAP.clear();
        CPI_EXPENSE_TEMPLATE_PATH_MAP.put(ProjectType.ASR, "/data/cpi/expense/ASR_PAYLOAD.txt");
        CPI_EXPENSE_TEMPLATE_PATH_MAP.put(ProjectType.TAX_C, "/data/cpi/expense/TAX_PAYLOAD.txt");
        CPI_EXPENSE_TEMPLATE_PATH_MAP.put(ProjectType.IND, "/data/cpi/expense/IND_PAYLOAD.txt");
        CPI_EXPENSE_TEMPLATE_PATH_MAP.put(ProjectType.ADV_RPM, "/data/cpi/expense/ADV_PAYLOAD.txt");
    }
}