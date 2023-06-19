package com.hanulplc.customer.woori;

import org.junit.jupiter.api.Test;

class WooriScraperTest {

    @Test
    void login() {
        WooriScraper scraper = new WooriScraper();
        scraper.execute();
    }
}
