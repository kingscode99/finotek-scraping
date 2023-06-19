package com.hanulplc.customer.bccard.page;

import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BCCardLoginPage {

    public static final String URL = "https://cloud.nowrms.com/login";

    public BCCardCaseListPage login(String id, String password) {
        inputUserId(id);
        inputUserPassword(password);
        clickLoginButton();
        return page(BCCardCaseListPage.class);
    }

    private void inputUserId(String id) {
        $(By.name("login_id")).shouldBe(visible).sendKeys(id);
    }

    private void inputUserPassword(String password) {
        // 비밀번호 입력 시 sendKeys() 동작하지 않아서 자바스크립트로 처리함
        executeJavaScript("arguments[0].value = '" + password + "'", $(By.name("login_pwd")));
    }

    private void clickLoginButton() {
        $("#btn_login").click();
    }

}
