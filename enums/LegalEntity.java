package com.ea.enums;

public enum LegalEntity {

    EISNERAMPER_LLP(1),
    EISNER_ADVISORY_GROUP_LLC(2),
    EA_COMPENSATION_RESOURCE(11),
    EISNERAMPER_MANAGED_TECHNOLOGY_SERVICES_LLC(115),
    EAG_RPM_PARTNERS_LLC(117);

    private final int value;

    LegalEntity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
