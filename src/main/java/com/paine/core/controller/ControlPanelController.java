package main.java.com.paine.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.UsuarioRepository;
import main.java.com.paine.core.util.Context;

@Controller
public class ControlPanelController {
	@Autowired
	private UsuarioRepository usuarioRepository;	
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping("/controlPanel")
    public String listado(Model model) {
		
		Context.loggedUser();
		List<Usuario> usuarios = usuarioRepository.cargarUsuarios();
        model.addAttribute("usuarios", usuarios);
		
		return "controlPanel";
	}
}
