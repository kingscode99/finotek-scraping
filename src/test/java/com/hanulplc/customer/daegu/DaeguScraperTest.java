package com.hanulplc.customer.daegu;

import org.junit.jupiter.api.Test;

class DaeguScraperTest {

    @Test
    void login() {
        DaeguScraper scraper = new DaeguScraper();
        scraper.execute();
    }
}
