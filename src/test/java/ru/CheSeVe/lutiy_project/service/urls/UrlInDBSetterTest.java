package ru.CheSeVe.lutiy_project.service.urls;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UrlInDBSetterTest {

    UrlInDBSetter urlInDBSetter;

    UrlInDBSetterTest(UrlInDBSetter setter) {
        this.urlInDBSetter = setter;
    }
    @Test
    public void testSetUrls() {
        urlInDBSetter.setHeroUrls();
    }
}
