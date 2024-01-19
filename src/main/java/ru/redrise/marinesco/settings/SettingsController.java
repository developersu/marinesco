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
import ru.redrise.marinesco.library.RepackZip;

@Controller
@RequestMapping("/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SettingsController {
    private ApplicationSettings applicationSettings;

    private InpxScanner inpxScanner;
    private RepackZip repackZip;

    public SettingsController(ApplicationSettings applicationSettings,
            InpxScanner inpxScanner,
            RepackZip repackZip) {
        this.applicationSettings = applicationSettings;
        this.inpxScanner = inpxScanner;
        this.repackZip = repackZip;
    }

    @GetMapping
    public String getPage(@ModelAttribute("rescanError") String err,
            @ModelAttribute("rescanOk") String passStatus,
            @ModelAttribute("repack") String repackStatus) {
        return "settings";
    }

    @ModelAttribute(name = "allowRegistration")
    public Boolean setRegistrationSetting() {
        return applicationSettings.isRegistrationAllowed();
    }

    @ModelAttribute(name = "lastScanErrors")
    public String setLastRunErrors() {
        if (InpxScanner.getLastRunErrors() != "")
            return "Last run attempt failed: " + InpxScanner.getLastRunErrors();
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
            redirectAttributes.addAttribute("rescanError", "Rescan could be currently in progress");

        return redirectView;
    }

    @GetMapping("/repack")
    public RedirectView repack(RedirectAttributes redirectAttributes) {
        if (repackZip.repack())
            redirectAttributes.addAttribute("repack", "Repack process started.");
        else
            redirectAttributes.addAttribute("repack", "Repack process could be currently in progress");

        return new RedirectView("/settings", true);
    }

    @ModelAttribute(name = "repack_lastrun")
    public String setLastRunRepackErrors() {
        if (repackZip.getLastExecTime() != "")
            return "Last time executed: " + repackZip.getLastExecTime();
        return null;
    }
}
