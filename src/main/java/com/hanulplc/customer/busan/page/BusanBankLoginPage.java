package com.hanulplc.customer.busan.page;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class BusanBankLoginPage {

    public static final String URL = "https://busan.nowrms.com/login";

    public BusanBankMainPage login(String userId, String userPassword) {
        inputUserId(userId);
        inputUserPassword(userPassword);
        clickLoginButton();
        return page(BusanBankMainPage.class);
    }

    private void inputUserId(String userId) {
        executeJavaScript("arguments[0].value = '" + userId + "'", $("#login_id"));
    }

    private void inputUserPassword(String userPassword) {
        executeJavaScript("arguments[0].value = '" + userPassword + "'", $("#login_pwd"));
    }

    private void clickLoginButton() {
        actions().click($("#btn_login")).pause(Duration.ofSeconds(5)).build().perform();
    }
}
