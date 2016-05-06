package main.java.com.paine.core.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import main.java.com.paine.core.dto.view.UsuarioDto;
import main.java.com.paine.core.model.Role;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.service.UsuarioService;

@Controller
public class ControlPanelController {
	@Autowired
	private UsuarioService usuarioService;

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel")
	public ModelAndView listado(String successMessage, String errorMessage) {

		List<Usuario> usuarios = usuarioService.cargarUsuarios();

		ModelAndView model = new ModelAndView("controlPanel");
		model.addObject("usuarios", usuarios);
		model.addObject("successMessage", successMessage);
		model.addObject("errorMessage", errorMessage);

		return model;
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/nuevo")
	public String crearNuevo(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, Model model) {
		model.addAttribute("roles", Role.values());
		return "nuevoUsuario";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/actualizar/{usuarioId}")
	public String mostrarActualizar(@PathVariable Integer usuarioId, Model model) {

		Usuario usuario = usuarioService.find(usuarioId);
		model.addAttribute("usuarioDto", UsuarioDto.crearDto(usuario));
		model.addAttribute("roles", Role.values());

		return "nuevoUsuario";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/salvar")
	public ModelAndView salvar(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, Model model) {
		
		if(validate(usuarioDto) == false) {
			return this.listado(null, "Form Validation Error");			
		}

		try {

			if (usuarioDto.getId() == null) {

				Usuario usuario = new Usuario();
				usuario.setNombre(usuarioDto.getNombre());
				usuario.setCodigo(usuarioDto.getCodigo());
				usuario.setEmail(usuarioDto.getEmail());
				usuario.setRole(Role.getByValue(usuarioDto.getRole()));
				usuario.setPwd(usuarioDto.getPwd());
				usuarioService.salvar(usuario);

				return this.listado("Usuario criado com suceso!", null);

			} else {

				Usuario usuario = usuarioService.find(usuarioDto.getId());
				usuario.setNombre(usuarioDto.getNombre());
				usuario.setCodigo(usuarioDto.getCodigo());
				usuario.setEmail(usuarioDto.getEmail());
				usuario.setRole(Role.getByValue(usuarioDto.getRole()));
				usuario.setPwd(usuarioDto.getPwd());
				usuarioService.actualizar(usuario);

				return this.listado("Usuario actualizado com suceso!", null);
			}
			
		} catch (Exception e) {
			return this.listado(null, "Error interno");
		}
	}

	private boolean validate(UsuarioDto usuarioDto) {

		boolean result = true;
		
		if(StringUtils.isEmpty(usuarioDto.getNombre())) {
			result = false;
		} else if(StringUtils.isEmpty(usuarioDto.getEmail())) {
			result = false;			
		} else if(StringUtils.isEmpty(usuarioDto.getPwd())) {
			result = false;
		} else if(StringUtils.isEmpty(usuarioDto.getPwdConfirmacion())) {
			result = false;
		} else if(StringUtils.isEmpty(usuarioDto.getRole())) {
			result = false;
		} else if(!usuarioDto.getPwd().equals(usuarioDto.getPwd())) {
			result = false;
		}
		
		return result;
	}
}
