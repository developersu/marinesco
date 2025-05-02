package ru.redrise.marinesco;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSettingsForm {

    @NotNull
    @NotEmpty(message = "Display name could not be blank")
    public String displayname;
    @Pattern(regexp = "^[a-zA-Z0-9!#+\"\\-<>%^&*$@]{8,32}$|^$",
            message = "Password must be at least 8 characters long. Should not exceed 32 characters")
    public String newPassword;

    public boolean isNewPasswordSet(){
        return ! newPassword.isBlank();
    }
}
