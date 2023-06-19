package com.hanulplc.customer.kb;

import com.codeborne.selenide.Configuration;
import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.kb.page.KookminBankLoginPage;
import com.hanulplc.customer.kb.page.KookminBankMainPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;

@Slf4j
public class KookminBankScraper extends FinotekCaseScraper {

    static {
        Configuration.browserSize = "1980x1000";
    }

    private static final String USER_ID = "hanul0008";
    private static final String USER_PASSWORD = "hekjyw0227!!";
    private static final String CERTIFICATE_PASSWORD = "hekjyw0220!!";

    @Override
    protected FinotekCaseListPage login() {
        KookminBankLoginPage loginPage = open(KookminBankLoginPage.URL, KookminBankLoginPage.class);
        KookminBankMainPage mainPage = loginPage.login(USER_ID, USER_PASSWORD, CERTIFICATE_PASSWORD);
        return mainPage.goToCaseListPage();
    }
}
