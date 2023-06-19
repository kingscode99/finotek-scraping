package com.hanulplc.customer.nonghyup.nh.page;

import com.hanulplc.common.FinotekCaseListPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class NonghyupNHMainPage {

    private static final String NH_LOGO = "#header > div > div.logo";
    private static final String GO_TO_CASE_LIST_PAGE = "applList('ST_10')";

    public FinotekCaseListPage goToCaseListPage() {
        $(NH_LOGO).shouldBe(visible);
        executeJavaScript(GO_TO_CASE_LIST_PAGE);
        return page(NonghyupNHCaseListPage.class);
    }
}
