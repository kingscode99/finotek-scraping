package com.hanulplc.common;

import com.hanulplc.customer.daegu.DaeguScraper;
import org.junit.jupiter.api.Test;

class FinotekCaseScraperTest {

    @Test
    void daegu() {
        FinotekCaseScraper caseScraper = new DaeguScraper();
        caseScraper.execute();
    }

}
