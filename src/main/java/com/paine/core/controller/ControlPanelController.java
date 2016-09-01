package main.java.com.paine.core.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import main.java.com.paine.core.dto.view.LoteDto;
import main.java.com.paine.core.dto.view.LotesAExportarDto;
import main.java.com.paine.core.dto.view.UsuarioDto;
import main.java.com.paine.core.model.Role;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.service.FileService;
import main.java.com.paine.core.service.UsuarioService;
import main.java.com.paine.core.util.Context;
import main.java.com.paine.core.util.JsonMessageResult;

@Controller
public class ControlPanelController {

	public static final Log log = LogFactory.getLog(ControlPanelController.class);

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private FileService fileService;

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
		return "salvarUsuario";
	}
	
	@Secured({ "ROLE_ADMIN" })
	@ResponseBody
	@RequestMapping("/controlPanel/check/export")
	public JsonMessageResult hayDatosParaExportar() {

		if(fileService.hayDatosExportacion()) {
			return JsonMessageResult.success();
		}

		return JsonMessageResult.error();
	}	

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/exportarRecibos")
	public void exportarRecibos(HttpServletResponse response, @ModelAttribute("lotesAExportarDto") LotesAExportarDto lotesAExportarDto, Model model) throws ParseException {

		ServletOutputStream out = null;

		List<Integer> listadoLotesId = new ArrayList<>();
		
		try {
			
			listadoLotesId = lotesParaExportar(lotesAExportarDto);
			
			for (int i = 0; i < listadoLotesId.size(); i++) {
				String sLote;
				sLote = listadoLotesId.get(i).toString();
				sLote = "attachment;lote" + sLote + "=recibos.txt";
				response.setContentType("text/plain");
				//response.setHeader("Content-Disposition", "attachment;filename=recibos.txt");
				response.setHeader("Content-Disposition", sLote);
				out = response.getOutputStream();
				
				List<String> datosReciboExportacion = fileService.exportarRecibos(Context.loggedUser(), listadoLotesId.get(i));
				for (String datosRecibo : datosReciboExportacion) {
					out.println(datosRecibo);
				}
			}

		} catch (IOException e) {

			log.error("Error exportando los recibos", e);

		} finally {

			try {

				out.flush();
				out.close();

			} catch (IOException e) {
				log.error("Error cerrando output stream", e);
			}
		}
	}
	
	private List<Integer> lotesParaExportar (LotesAExportarDto lotesAExportarDto) throws ParseException {
		List<Integer> listadoLoteId = new ArrayList<>();
		
		if(ArrayUtils.isNotEmpty(lotesAExportarDto.getLoteId())) {
			for (int i = 0; i < lotesAExportarDto.getLoteId().length; i++) {
				listadoLoteId.add(lotesAExportarDto.getLoteId()[i]);
			}
		}
		return listadoLoteId;
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/actualizar/{usuarioId}")
	public String mostrarActualizar(@PathVariable Integer usuarioId, Model model) {

		Usuario usuario = usuarioService.find(usuarioId);
		model.addAttribute("usuarioDto", UsuarioDto.crearDto(usuario));
		model.addAttribute("roles", Role.values());

		return "salvarUsuario";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/salvar")
	public ModelAndView salvar(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, Model model) {

		try {

			if (usuarioDto.getId() == null) {

				// Validar el DTO antes de criar el usuário
				if (validate(usuarioDto) == false) {
					return this.listado(null, "Todos los campos son obligatorios en la creación de nuevo usuário");
				}

				Usuario existeUsuario = usuarioService.findByEmail(usuarioDto.getEmail());
				if (existeUsuario != null) {
					return this.listado(null, "El usuário no fue creado porque ya existe!");
				}

				// Todo ok, criar usuário
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

				// Si el usuario, no actualizo el password (lo dejó vacio),
				// cargar el password en el DTO antes de validarlo, para
				// que no de error
				if (StringUtils.isEmpty(usuarioDto.getPwd()) && StringUtils.isEmpty(usuarioDto.getPwdConfirmacion())) {
					usuarioDto.setPwd(usuario.getPwd());
					usuarioDto.setPwdConfirmacion(usuario.getPwd());
				}

				// Ahora si validar el usuarioDto
				if (validate(usuarioDto) == false) {
					return this.listado(null, "Algún campo obligatório no fue cargado");
				}

				// Todo ok, actualizar el usuario
				usuario.setNombre(usuarioDto.getNombre());
				usuario.setCodigo(usuarioDto.getCodigo());
				usuario.setEmail(usuarioDto.getEmail());
				usuario.setRole(Role.getByValue(usuarioDto.getRole()));
				usuario.setPwd(usuarioDto.getPwd());
				usuarioService.actualizar(usuario);

				return this.listado("Usuario actualizado com suceso!", null);
			}

		} catch (Exception e) {
			log.error(e);
			return this.listado(null, "Error interno");
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@ResponseBody
	@RequestMapping(value = "/controlPanel/upload")
	public JsonMessageResult uploadFile(@RequestParam MultipartFile fileUpload) {

		try {

			log.info("Uploading file");

			if (fileUpload == null || fileUpload.isEmpty()) {
				log.info("File contain is empty, uploading aborted");
				return JsonMessageResult.error().result("FILE_EMPTY");
			}

			if (!fileUpload.getOriginalFilename().contains(".txt")) {
				log.info("Invalid file extension");
				return JsonMessageResult.error().result("INVALID_FILE_EXTENSION");
			}

			fileService.saveFile(fileUpload);

		} catch (Exception e) {
			log.error("Error uploading profile picture");
			return JsonMessageResult.error();
		}

		return JsonMessageResult.success();
	}

	private boolean validate(UsuarioDto usuarioDto) {

		boolean result = true;

		if (StringUtils.isEmpty(usuarioDto.getNombre())) {
			result = false;
		} else if (StringUtils.isEmpty(usuarioDto.getEmail())) {
			result = false;
		} else if (StringUtils.isEmpty(usuarioDto.getPwd())) {
			result = false;
		} else if (StringUtils.isEmpty(usuarioDto.getPwdConfirmacion())) {
			result = false;
		} else if (StringUtils.isEmpty(usuarioDto.getRole())) {
			result = false;
		} else if (!usuarioDto.getPwd().equals(usuarioDto.getPwd())) {
			result = false;
		}

		return result;
	}
}
