package ru.CheSeVe.lutiy_project.service.urls;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UrlInDBSetterTest {

    @Autowired
    UrlInDBSetter urlInDBSetter;
    @Test
    public void testSetUrls() {
        urlInDBSetter.setHeroUrls();
    }
}
