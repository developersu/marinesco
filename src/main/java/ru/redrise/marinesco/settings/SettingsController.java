package ru.redrise.marinesco.settings;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import ru.redrise.marinesco.library.InpxScanner;

@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {
    private ApplicationSettings applicationSettings;

    private InpxScanner inpxScanner;

    public SettingsController(ApplicationSettings applicationSettings,
            InpxScanner inpxScanner) {
        this.applicationSettings = applicationSettings;
        this.inpxScanner = inpxScanner;
    }

    @GetMapping
    public String getPage(@ModelAttribute("rescanError") String err, @ModelAttribute("rescanOk") String passStatus) {
        return "settings";
    }

    @ModelAttribute(name = "allowRegistration")
    public Boolean setRegistrationSetting() {
        return applicationSettings.isRegistrationAllowed();
    }
    
    @ModelAttribute(name = "lastScanErrors")
    public String setLastRunErrors(){
        if (InpxScanner.getLastRunErrors() != null)
            return "Last run attempt failed: "+InpxScanner.getLastRunErrors();
        return null;
    }

    @GetMapping("/allow_registration/{sw}")
    public String switchRegistration(@PathVariable("sw") Boolean sw) {
        applicationSettings.setAllowRegistraion(sw);

        return "redirect:/settings";
    }

    @GetMapping("/rescan")
    public RedirectView rescan(RedirectAttributes redirectAttributes) {
        final RedirectView redirectView = new RedirectView("/settings", true);

        if (inpxScanner.reScan())
            redirectAttributes.addAttribute("rescanOk", "Rescan started");
        else
            redirectAttributes.addAttribute("rescanError", "Rescan is currently in progress");

        return redirectView;
    }
}
