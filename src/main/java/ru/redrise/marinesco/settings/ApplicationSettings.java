package ru.redrise.marinesco.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "marinesco.library")
public class ApplicationSettings {
    private static final String ALLOW_REGISTRATION = "allow_registration";
    
    private String filesLocation = "";

    private KeyValueRepository keyValueRepository;

    private boolean registrationAllowed;

    public ApplicationSettings(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
        initAllowRegistraionValue();
    }

    private void initAllowRegistraionValue() {
        try{
            this.registrationAllowed = keyValueRepository.findById(ApplicationSettings.ALLOW_REGISTRATION).get().getM_value();
        }
        catch (Exception e){
            // Application first run, thus no data on the DB
            registrationAllowed = true;
        }
        
    }

    public synchronized void setAllowRegistraion(boolean value) {
        keyValueRepository.save(new KeyValue(ApplicationSettings.ALLOW_REGISTRATION, value));
        this.registrationAllowed = value;
    }

    public synchronized boolean isRegistrationAllowed() {
        return registrationAllowed;
    }

    public String getFilesLocation() {
        return filesLocation;
    }

    public void setFilesLocation(String location) {
        filesLocation = location;
    }

}
