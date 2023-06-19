package com.hanulplc.customer.shinhan.page;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class ShinhanBankLoginPage {

    public static final String URL = "https://register.shinhan.com/lawfirm/";

    public ShinhanBankMainPage login(String certificatePassword) {
        $("#page_loding > img").shouldBe(visible).shouldNotBe(visible, Duration.ofSeconds(10));
        clickLoginButton();
        inputCertificatePassword(certificatePassword);
        return page(ShinhanBankMainPage.class);
    }

    private void clickLoginButton() {
        switchTo().frame("bizMain");
        $("#signform > div > a > img").click();
    }

    private void inputCertificatePassword(String certificatePassword) {
        $("#xwup_body").should(appear);
        $$("#xwup_cert_table > table > tbody tr").shouldHave(sizeGreaterThanOrEqual(1));
        actions().click($("#xwup_certselect_tek_input1"))
            .pause(Duration.ofSeconds(1)) // 비밀번호를 너무 빨리 입력하려고 하면 입력되지 않음
            .sendKeys(certificatePassword)
            .click($("#xwup_OkButton"))
            .perform();
    }
}
