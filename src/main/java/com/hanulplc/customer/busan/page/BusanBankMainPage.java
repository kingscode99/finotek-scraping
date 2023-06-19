package com.hanulplc.customer.busan.page;

import com.hanulplc.common.FinotekCaseListPage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BusanBankMainPage {

    public static final String URL = "https://busan.nowrms.com";
    private static final String BUSAN_LOGO = "#header > div > div.logo";

    public FinotekCaseListPage goToCaseListPage() {
        $(BUSAN_LOGO).shouldBe(visible);
        return open(BusanBankCaseListPage.URL, BusanBankCaseListPage.class);
    }
}
