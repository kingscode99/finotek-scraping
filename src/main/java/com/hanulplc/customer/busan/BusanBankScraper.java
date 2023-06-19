package com.hanulplc.customer.busan;

import com.hanulplc.common.FinotekCaseListPage;
import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.busan.page.BusanBankLoginPage;
import com.hanulplc.customer.busan.page.BusanBankMainPage;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.webdriver;

@Slf4j
public class BusanBankScraper extends FinotekCaseScraper {

    private static final String USER_ID = "qntks08";
    private static final String USER_PASSWORD = "qntks08!";

    @Override
    protected FinotekCaseListPage login() {
        BusanBankLoginPage loginPage = open(BusanBankLoginPage.URL, BusanBankLoginPage.class);
        BusanBankMainPage mainPage = loginPage.login(USER_ID, USER_PASSWORD);

        // 보안 프로그램 설치 페이지로 이동한 경우, 메인 페이지로 이동
        if (webdriver().driver().url().contains("/securityInstall")) {
            mainPage = open(BusanBankMainPage.URL, BusanBankMainPage.class);
        }

        return mainPage.goToCaseListPage();
    }
}
