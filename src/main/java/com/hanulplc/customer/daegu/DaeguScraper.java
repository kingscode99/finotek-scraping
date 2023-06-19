package com.hanulplc.customer.daegu;

import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.daegu.page.DaeguLoginPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;

@Slf4j
public class DaeguScraper extends FinotekCaseScraper {

    private static final String USER_ID = "sipkang";
    private static final String USER_PASSWORD = "hanul/0514!";

    @Override
    protected FinotekCaseListPage login() {
        DaeguLoginPage loginPage = open(DaeguLoginPage.URL, DaeguLoginPage.class);
        return loginPage.login(USER_ID, USER_PASSWORD);
    }
}
