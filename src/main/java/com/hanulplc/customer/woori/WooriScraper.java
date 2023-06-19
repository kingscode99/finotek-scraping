package com.hanulplc.customer.woori;

import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.woori.page.WooriLoginPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;

@Slf4j
public class WooriScraper extends FinotekCaseScraper {

    private static final String USER_ID = "hanul01";
    private static final String USER_PASSWORD = "hanul/0514@";

    @Override
    protected FinotekCaseListPage login() {
        WooriLoginPage loginPage = open(WooriLoginPage.URL, WooriLoginPage.class);
        return loginPage.login(USER_ID, USER_PASSWORD);
    }
}
