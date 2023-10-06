package ru.redrise.marinesco;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserSettingsForm {

    @NotNull
    @NotEmpty(message = "Display name could not be blank")
    public String displayname;
    public String newPassword;

    public boolean isNewPasswordSet(){
        return ! newPassword.isBlank();
    }
    
    public boolean isNewPasswordValid(){
        final int newPasswordLength = newPassword.length();
        return newPasswordLength > 8 && newPasswordLength < 32;
    }
}
