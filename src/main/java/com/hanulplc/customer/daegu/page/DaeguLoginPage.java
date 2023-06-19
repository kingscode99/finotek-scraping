package com.hanulplc.customer.daegu.page;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverConditions;
import com.hanulplc.common.FinotekCaseListPage;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class DaeguLoginPage {

    public static final String URL = "https://daegu.nowrms.com/login";

    public FinotekCaseListPage login(String id, String password) {
        SelenideElement loginIdElement = $(By.name("login_id"));
        loginIdElement.shouldBe(visible);
        executeJavaScript("arguments[0].value = '" + id + "'", loginIdElement);
        executeJavaScript("arguments[0].value = '" + password + "'", $(By.name("login_pwd"))); // sendKeys()가 동작하지 않아 JS로 처리함
        $("#btn_login").click();
        webdriver().shouldHave(WebDriverConditions.url(DaeguCaseListPage.URL));
        return page(DaeguCaseListPage.class);
    }
}
