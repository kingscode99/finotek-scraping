package com.hanulplc;

import com.hanulplc.common.FinotekCaseScraper;
import com.hanulplc.customer.bccard.BCCardScraper;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.closeWindow;

@Slf4j
public class Main {

    public static void main(String[] args) {
        while (true) {
            try {
                FinotekCaseScraper scraper = new BCCardScraper();
                scraper.execute();
            } catch (Exception | Error e) {
                closeWindow();
                closeWebDriver();
                log.error("신규 의뢰 스크래핑 중 예외 발생", e);
            }
        }
    }
}
