package com.hanulplc.customer.woori.page;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class WooriLoginPage {

    public static final String URL = "https://woori.nowrms.com/";

    public WooriCaseListPage login(String id, String password) {
        inputUserId(id);
        inputUserPassword(password);
        clickLoginButton();
        return page(WooriCaseListPage.class);
    }

    private void inputUserId(String id) {
        $(By.name("login_id")).shouldBe(visible).sendKeys(id);
    }

    private void inputUserPassword(String password) {
        executeJavaScript("arguments[0].value = '" + password + "'", $(By.name("login_pwd")));
    }

    private void clickLoginButton() {
        $("#btnLogin").click();
    }
}
