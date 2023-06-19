package com.hanulplc.customer.nonghyup.nh;

import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.nonghyup.nh.page.NonghyupNHLoginPage;
import com.hanulplc.customer.nonghyup.nh.page.NonghyupNHMainPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;
import static com.hanulplc.customer.nonghyup.nh.page.NonghyupNHLoginPage.URL;

@Slf4j
public class NonghyupNHScraper extends FinotekCaseScraper {

    private static final String USER_ID = "hanulplc01";
    private static final String USER_PASSWORD = "hanulplc01@";
    private static final String CERTIFICATE_PASSWORD = "hanul/0514";

    @Override
    protected FinotekCaseListPage login() {
        NonghyupNHLoginPage loginPage = open(URL, NonghyupNHLoginPage.class);
        NonghyupNHMainPage mainPage = loginPage.login(USER_ID, USER_PASSWORD, CERTIFICATE_PASSWORD);
        return mainPage.goToCaseListPage();
    }
}
