package ru.redrise.marinesco.settings;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ru.redrise.marinesco.library.InpxScanner;

//@Slf4j
@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {
    private KeyValueRepository keyValueRepository;
    private ApplicationSettings applicationSettings;

    private InpxScanner inpxScanner;

    public SettingsController(KeyValueRepository keyValueRepository, ApplicationSettings applicationSettings, InpxScanner inpxScanner){
        this.keyValueRepository = keyValueRepository;
        this.applicationSettings = applicationSettings;
        this.inpxScanner = inpxScanner;
    }

    @GetMapping
    public String getPage() {
        
        return "settings";
    }
    @ModelAttribute(name = "allowRegistration")
    public Boolean setRegistrationSetting(){
        return applicationSettings.isRegistrationAllowed();
        //return keyValueRepository.findById(ApplicationSettings.ALLOW_REGISTRATION).get().getM_value();
    }
    
    @GetMapping("/allow_registration/{sw}")
    public String switchRegistration(@PathVariable("sw") Boolean sw){
        //log.info("{}", sw);
        //keyValueRepository.save(new KeyValue(ApplicationSettings.ALLOW_REGISTRATION, sw));
        applicationSettings.setAllowRegistraion(sw);
        
        return "redirect:/settings";
    }

    @GetMapping("/rescan")
    public String rescan(){
        inpxScanner.reScan();
        
        return "redirect:/settings";
    }
}
