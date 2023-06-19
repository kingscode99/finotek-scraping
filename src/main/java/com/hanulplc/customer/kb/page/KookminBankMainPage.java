package com.hanulplc.customer.kb.page;

import com.hanulplc.common.FinotekCaseListPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class KookminBankMainPage {

    private static final String KB_LOGO = "#header > div.logo_easyr";

    public FinotekCaseListPage goToCaseListPage() {
        $(KB_LOGO).shouldBe(visible);
        return open(KookminBankCaseListPage.URL, KookminBankCaseListPage.class);
    }
}
