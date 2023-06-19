package com.hanulplc.customer.nonghyup.mutual.page;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.*;

public class NonghyupMutualLoginPage {

    private static final String LOADING_IMAGE = "img[src='/MagicLine4Web/ML4Web/UI/images/loader.gif']";

    public NonghyupMutualMainPage login(String userId, String userPassword, String certificatePassword) {
        inputUserId(userId);
        inputUserPassword(userPassword);
        clickLoginButton();
        inputCertificatePassword(certificatePassword);
        return page(NonghyupMutualMainPage.class);
    }

    private void inputCertificatePassword(String certificatePassword) {
        switchTo().frame("dscert");
        $("#ML_window").should(appear);
        waitForLoadingImageDisappear();
        $("#row1dataTable > td:nth-child(2) > span").click();
        $("#input_cert_pw").should(appear).sendKeys(certificatePassword + "\n");
    }

    private void clickLoginButton() {
        $("#btn_login").click();
    }

    private void inputUserPassword(String userPassword) {
        executeJavaScript("arguments[0].value = '" + userPassword + "'", $("#login_pwd"));
    }

    private void inputUserId(String userId) {
        waitForLoadingImageDisappear();
        executeJavaScript("arguments[0].value = '" + userId + "'", $("#login_id"));
    }

    private void waitForLoadingImageDisappear() {
        $(LOADING_IMAGE).should(appear).should(disappear);
    }

}
