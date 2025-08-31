package ru.CheSeVe.lutiy_project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.CheSeVe.lutiy_project.entity.AppSettings;
import ru.CheSeVe.lutiy_project.repository.AppSettingsRepository;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    AppSettingsRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (!repository.existsById("last_match_id")){
            repository.save(new AppSettings("last_match_id", "8431022067"));
        }
    }
}
