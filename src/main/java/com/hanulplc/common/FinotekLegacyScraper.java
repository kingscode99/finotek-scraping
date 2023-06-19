package com.hanulplc.common;

import com.codeborne.selenide.ElementsCollection;

public interface FinotekLegacyScraper {

    @Deprecated
    void scrapeNewCases(ElementsCollection caseElements);
}
