package com.hanulplc.customer.nonghyup.nh.page;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class NonghyupNHLoginPage {

    public static final String URL = "https://nhers.nonghyupit.com:8100";
    private static final String LOADING_IMAGE = "img[src='/MagicLine4Web/ML4Web/UI/images/loader.gif']";

    public NonghyupNHMainPage login(String userId, String userPassword, String certificatePassword) {
        inputUserId(userId);
        inputUserPassword(userPassword);
        clickLoginButton();
        inputCertificatePassword(certificatePassword);
        return page(NonghyupNHMainPage.class);
    }

    private void inputCertificatePassword(String certificatePassword) {
        switchTo().frame("dscert");
        $("#ML_window").should(appear);
        waitForLoadingImageDisappear();

        // 'mac을(를) 여시겠습니까?' 창을 닫기 위해 강제로 ESC 입력
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ESCAPE);
            robot.keyRelease(KeyEvent.VK_ESCAPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        $("#stg_hdd").click(); // '디스크' 선택
        $("#driver_div").shouldBe(visible).click(); // 드라이브 선택
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
        $(LOADING_IMAGE).should(appear, Duration.ofSeconds(10))
            .should(disappear, Duration.ofSeconds(10));
    }
}
