package main.java.com.paine.core.util;

import org.springframework.security.core.context.SecurityContextHolder;

import main.java.com.paine.core.model.AuthenticatedUser;
import main.java.com.paine.core.model.Usuario;

public class Context {

	public static final String ACCESS_DENIED = "accessDenied";
	public static final String ERROR_MSG = "errorMessage";
	public static final String SUCCESS_MSG = "successMessage";

	public static Usuario loggedUser() {
		return (Usuario) getPrincipal();
	}

	public static AuthenticatedUser authenticatedUser() {
		return (AuthenticatedUser) getPrincipal();
	}

	private static Object getPrincipal() {

		// If we are at login page, and we have never logged in before, and we have
		// any error at authentication process, getAuthentication() can return
		// null
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			return null;
		}

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof String) {
			return null;
		}

		return principal;
	}
}
