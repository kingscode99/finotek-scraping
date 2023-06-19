package com.hanulplc.common;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;

import static com.codeborne.selenide.Selenide.refresh;

@Slf4j
public abstract class FinotekCaseScraper {

    private static final int DEFAULT_WAIT_MILLISECONDS = 10_000; // 30초

    // Chrome 외 다른 브라우저를 사용해야 한다면 각 구현체의 static 블록에서 설정을 변경하세요.
    static {
        Configuration.browser = Browsers.CHROME;
        Configuration.browserSize = "1980x1000";
    }

    public final void execute() {
        FinotekCaseListPage caseListPage = login();

        while (true) {
            LocalTime now = LocalTime.now();
            if (now.isAfter(LocalTime.of(21, 0))) {
                log.info("==> 21시 이후이므로 스크래핑 종료함");
                System.exit(0);
            }

            caseListPage = caseListPage.searchByConditions();

            if (caseListPage.hasNoCases()) {
                log.info("==> '등기의뢰' 상태인 의뢰가 존재하지 않습니다. ({}초 후 재시작...)", DEFAULT_WAIT_MILLISECONDS / 1_000);
                waitForSeconds(DEFAULT_WAIT_MILLISECONDS);
                refresh();
                continue;
            }

            caseListPage.scrapeCases();

            log.info("스크래핑 완료, {}초 후 재시작...", DEFAULT_WAIT_MILLISECONDS / 1_000);
            waitForSeconds(DEFAULT_WAIT_MILLISECONDS);
        }
    }

    protected void waitForSeconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract FinotekCaseListPage login();

}
