package main.java.com.paine.core.config;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import main.java.com.paine.core.model.Usuario;

@ControllerAdvice
public class LoggedUser {
	
	@ModelAttribute("loggedUser")
    public Usuario getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (Usuario) authentication.getPrincipal();
    }
}
