package main.java.com.paine.core.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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

import main.java.com.paine.core.dto.view.LotesAExportarDto;
import main.java.com.paine.core.dto.view.UsuarioDto;
import main.java.com.paine.core.model.Banco;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.Role;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.BancoRepository;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.service.FileService;
import main.java.com.paine.core.service.ReciboService;
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

	@Autowired
	private ReciboRepository reciboRepository;

	@Autowired
	private ReciboService reciboService;

	@Autowired
	private BancoRepository bancoRepository;

	@Autowired
	private ResourceLoader resourceLoader;

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

		if (fileService.hayDatosExportacion()) {
			return JsonMessageResult.success();
		}

		return JsonMessageResult.error();
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/exportarRecibos")
	public void exportarRecibos(HttpServletResponse response, Integer[] lotes, Model model) throws ParseException {

		ServletOutputStream out = null;

		try {

			for (Integer loteNro : lotes) {

				response.setContentType("text/plain");
				response.setHeader("Content-Disposition", "attachment;filename=recibos.txt");
				response.setHeader("Set-Cookie", "fileDownload=true; path=/");
				out = response.getOutputStream();

				List<String> datosReciboExportacion = fileService.exportarRecibos(Context.loggedUser(), loteNro);
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

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/exportarRecibos/excel")
	public void exportarRecibosExcel(HttpServletResponse response, Integer[] lotes, Model model) throws ParseException {

		try {

			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=recibos.xls");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			generarExcel(lotes, response.getOutputStream());

		} catch (IOException e) {
			log.error("Error exportando los recibos", e);
		}
	}

	private FileOutputStream generarExcel(Integer[] lotes, ServletOutputStream outputStream) throws IOException {
		// **** INICIALIZACION DEL ARCHIVO EXCEL
		FileOutputStream archivo = null;
		int filaIni_XCadaTipoDePago = 0;
		int filaComienzoSiguienteRecibo = 0;
		int fi = 0;

		// linea cabeza
		// Resource resource =
		// resourceLoader.getResource("classpath:cwears.xls");
		// File file = resource.getFile();
		// InputStream inputStream = resource.getInputStream();

		/*
		 * HttpServletResponse response = null; ServletOutputStream out = null;
		 * 
		 * response.setContentType("application/vnd.ms-excel");
		 * response.setHeader("Content-Disposition",
		 * "attachment;filename=recibos.xls"); response.setHeader("Set-Cookie",
		 * "fileDownload=true; path=/"); out = response.getOutputStream();
		 */

		//

		String nomFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String rutaArchivo = System.getProperty("user.home") + "/Recibos_" + nomFile + ".xls";
		File archivoXLS = new File(rutaArchivo);
		// File archivoXLS = resource.getFile();// new File(rutaArchivo);
		Workbook libro = new HSSFWorkbook();

		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String fechaReciboStr;

		try {
			archivoXLS.createNewFile();
			archivo = new FileOutputStream(archivoXLS);
			Sheet hoja = libro.createSheet("recibos");

			Row fila;
			Cell celda;

			for (int f = 0; f < 10000; f++) {
				fila = hoja.createRow(f);
			}

			// fila = hoja.createRow(0);
			fila = hoja.getRow(0);
			celda = fila.createCell(0);
			celda.setCellValue("Recibo");

			celda = fila.createCell(1);
			celda.setCellValue("Cliente");

			celda = fila.createCell(2);
			celda.setCellValue("Factura");

			celda = fila.createCell(3);
			celda.setCellValue("Importe");

			celda = fila.createCell(4);
			celda.setCellValue("");

			celda = fila.createCell(5);
			celda.setCellValue("Valor Imp.");

			celda = fila.createCell(6);
			celda.setCellValue("Banco");

			celda = fila.createCell(7);
			celda.setCellValue("Numero");

			celda = fila.createCell(8);
			celda.setCellValue("Fecha");

			celda = fila.createCell(9);
			celda.setCellValue("Efectivo");

			celda = fila.createCell(10);
			celda.setCellValue("Retencion");

			celda = fila.createCell(11);
			celda.setCellValue("Deposito");

			// ***** FIN INICIALIZACION

			for (Integer lote : lotes) {

				List<Recibo> recibos = reciboRepository.recibosParaExportar(lote);

				// try {

				// List<String> fileLines = new ArrayList<>(recibos.size());
				for (Recibo recibo : recibos) {

					Recibo reciboCompleto = reciboService.findOne(recibo.getId());

					fechaReciboStr = sdf.format(recibo.getFecha());

					filaIni_XCadaTipoDePago = filaComienzoSiguienteRecibo;
					filaIni_XCadaTipoDePago++;
					filaComienzoSiguienteRecibo = filaIni_XCadaTipoDePago;
					fi = filaIni_XCadaTipoDePago;
					fila = hoja.getRow(fi);

					// nro de recibo
					celda = fila.createCell(0);
					celda.setCellValue(recibo.getNumero());
					// nombre cliente
					celda = fila.createCell(1);
					celda.setCellValue(recibo.getCliente().getNombre());
					// nro factura
					celda = fila.createCell(2);
					celda.setCellValue(recibo.getNumero());// falta
					// factura importe
					celda = fila.createCell(3);
					celda.setCellValue(recibo.getNumero());// falta

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}

					// ****************** CUERPO DEL RECIBO
					fi = filaIni_XCadaTipoDePago;
					// EFECTIVO
					TpEff efectivo = reciboCompleto.getTpEff();
					if (efectivo != null) {

						fila = hoja.getRow(fi);
						celda = fila.createCell(9);
						celda.setCellValue(efectivo.getMonto());
					}

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}

					// DEPOSITO:
					fi = filaIni_XCadaTipoDePago;
					String fechaDepositoStr;
					for (TpDeposito depositoss : reciboCompleto.getTpDepositos()) {
						fechaDepositoStr = sdf.format(depositoss.getFecha());
					}

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}

					// CHEQUE
					fi = filaIni_XCadaTipoDePago;
					String fechaChequeStr;
					for (TpCheque cheques : reciboCompleto.getTpCheques()) {

						// *** busco el nombre del banco
						Banco banco = new Banco();
						banco = bancoRepository.findOne(cheques.getBanco().getId());
						// ** end busco nombre banco

						// ini excel
						// factura importe
						fila = hoja.getRow(fi);
						celda = fila.createCell(5);
						celda.setCellValue(cheques.getMonto());

						celda = fila.createCell(6);
						celda.setCellValue(cheques.getBanco().getNombre()); // viene
																			// vacio

						celda = fila.createCell(7);
						celda.setCellValue(cheques.getNumero());

						celda = fila.createCell(8);
						celda.setCellValue(cheques.getFechaDeposito());
						fi++;
						// fin excel

						fechaChequeStr = sdf.format(cheques.getFechaDeposito());
					}

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}

					// RETENCION scar
					/*
					 * for(TpRetencion retenciones :
					 * reciboCompleto.getTpRetenciones()){
					 * 
					 * //traigo el codigo de imputacion TipoDePago tipoPago =
					 * new TipoDePago(); tipoPago =
					 * tipoDePagoRepository.findOne(retenciones.getTipoPago());
					 * //end
					 * 
					 * //cabeceraTR2(recibo.getNumero(), "16", "133301",
					 * retenciones.getMonto()); cabeceraTR2(recibo.getNumero(),
					 * tipoPago.getCodigo(), tipoPago.getImputacion(),
					 * retenciones.getMonto());
					 * 
					 * sb.append("                                  ");
					 * sb.append("16"); //tipo de pago, sacar de tabla tipo de
					 * pagos
					 * //sb.append(StringUtils.leftPad(retenciones.getSucursal()
					 * , 4, "0"));
					 * sb.append(StringUtils.leftPad(Integer.toString(
					 * retenciones.getAnio()), 4, "0"));
					 * sb.append(StringUtils.leftPad(retenciones.getNumero(), 4,
					 * "0")); //sb.append("REG"); //regimen, ver si va
					 * //sb.append("BASECALCU"); //base calculo, ver si va
					 * //sb.append("PORCE"); //porcentaje, ver si va
					 * //sb.append("JU"); //juris, ver si va
					 * 
					 * fileLines.add(sb.toString()); }
					 */
				}

			} // fin del for de lotes

			/*
			 * fila = hoja.getRow(fi+1); celda = fila.createCell(3); String
			 * formula = "=SUM(D2:D" + fi + ")";
			 * celda.setCellFormula("formula");
			 * 
			 * celda = fila.createCell(5); formula = "=SUM(F2:F" + fi + ")";
			 * celda.setCellFormula("formula");
			 * 
			 * celda = fila.createCell(9); formula = "=SUM(J2:J" + fi + ")";
			 * celda.setCellFormula("formula");
			 */

			libro.write(outputStream);
			archivo.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return archivo;
	}

	private List<Integer> lotesParaExportar(LotesAExportarDto lotesAExportarDto) throws ParseException {
		List<Integer> listadoLoteId = new ArrayList<>();

		if (ArrayUtils.isNotEmpty(lotesAExportarDto.getLoteId())) {
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
