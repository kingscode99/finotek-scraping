package com.hanulplc.customer.nonghyup.mutual;

import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.nonghyup.mutual.page.NonghyupMutualLoginPage;
import com.hanulplc.customer.nonghyup.mutual.page.NonghyupMutualMainPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;

@Slf4j
public class NonghyupMutualScraper extends FinotekCaseScraper {

    private static final String URL = "https://nhers.nonghyupit.com:8150/login";
    private static final String USER_ID = "hanulplc23";
    private static final String USER_PASSWORD = "hanulplc23!";
    private static final String CERTIFICATE_PASSWORD = "hanul/0514";

    @Override
    protected FinotekCaseListPage login() {
        NonghyupMutualLoginPage loginPage = open(URL, NonghyupMutualLoginPage.class);
        NonghyupMutualMainPage mainPage = loginPage.login(USER_ID, USER_PASSWORD, CERTIFICATE_PASSWORD);
        return mainPage.goToCaseListPage();
    }
}
