package com.hanulplc.common;

import com.codeborne.selenide.ElementsCollection;

public interface FinotekCaseListPage {

    FinotekCaseListPage scrapeCases();

    FinotekCaseListPage searchByConditions();

    boolean hasNoCases();

    @Deprecated
    default ElementsCollection searchCases() {
        return null;
    }
}
