package com.hanulplc.customer.kb.page;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class KookminBankLoginPage {

    public static final String URL = "https://irosf.kbstar.com:80/login";

    public KookminBankMainPage login(String userId, String userPassword, String certificatePassword) {
        inputUserId(userId);
        inputUserPassword(userPassword);
        clickLoginButton();
        inputCertificatePassword(certificatePassword);
        return page(KookminBankMainPage.class);
    }

    private void inputUserId(String userId) {
        executeJavaScript("arguments[0].value = '" + userId + "'", $("#wrap > div > div > div > div > div > ul > li:nth-child(1) > input"));
    }

    private void inputUserPassword(String userPassword) {
        executeJavaScript("arguments[0].value = '" + userPassword + "'", $("#wrap > div > div > div > div > div > ul > li:nth-child(2) > input"));
    }

    private void clickLoginButton() {
        $("#btnLogin").click();
    }

    private void inputCertificatePassword(String certificatePassword) {
        switchTo().frame("dscert");
        $("#ML_window").should(appear);
        $("img[src='/MagicLine4Web/ML4Web/UI/images/loader.gif']").shouldBe(visible).shouldNotBe(visible);
        $$("#tabledataTable > tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        $("#input_cert_pw").should(appear).sendKeys(certificatePassword + "\n");
    }
}
