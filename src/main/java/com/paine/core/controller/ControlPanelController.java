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
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import main.java.com.paine.core.model.Descuento;
import main.java.com.paine.core.model.Factura;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.Role;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;
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

	//public static final CellFormatType general;
	
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
			generarExcel(lotes, response.getOutputStream()); //generarExcel(lotes, response.getOutputStream());

		} catch (IOException e) {
			log.error("Error exportando los recibos", e);
		}
	}
	
	private FileOutputStream generarExcel(Integer[] lotes, ServletOutputStream outputStream) throws IOException {
		
		
		// **** INICIALIZACION DEL ARCHIVO EXCEL
		FileOutputStream archivo = null;
		int filaIni_XCadaTipoDePago = 4;
		int filaComienzoSiguienteRecibo = 4;
		int fi = 4;

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

		//parte de fuente
		DataFormat format = libro.createDataFormat();
		
		CellStyle my_style = libro.createCellStyle();
		Font my_font = libro.createFont();
		my_font.setFontName("Calibri");
		my_font.setFontHeightInPoints((short)8);
		my_style.setFont(my_font);
		//my_style.setDataFormat(CellFormatType.GENERAL.values());
		//my_style.setDataFormat(format.getFormat("0.0"));
		
		
		/*my_style.setBorderBottom(CellStyle.BORDER_THIN);
		my_style.setBorderLeft(CellStyle.BORDER_THIN);
		my_style.setBorderRight(CellStyle.BORDER_THIN);
		my_style.setBorderTop(CellStyle.BORDER_THIN);*/
		
		CellStyle my_style_encabezado = libro.createCellStyle();
		my_style_encabezado.setBorderBottom(CellStyle.BORDER_THIN);
		my_style_encabezado.setBorderLeft(CellStyle.BORDER_THIN);
		my_style_encabezado.setBorderRight(CellStyle.BORDER_THIN);
		my_style_encabezado.setBorderTop(CellStyle.BORDER_THIN);
		my_style_encabezado.setFillBackgroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		my_style_encabezado.setFillForegroundColor(IndexedColors.GREY_80_PERCENT.getIndex());
		//end fuente
		
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
			
			//lote y fecha
			fila = hoja.getRow(0);
			celda = fila.createCell(0);
			celda.setCellValue("Nro. Lote");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(1);
			celda.setCellValue("Fecha");
			celda.setCellStyle(my_style_encabezado);
			
			//end lote y fecha

			
			// fila = hoja.createRow(0);
			fila = hoja.getRow(3); //hoja.getRow(0);
			celda = fila.createCell(0);
			celda.setCellValue("Recibo");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(1);
			celda.setCellValue("Cliente");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(2);
			celda.setCellValue("Factura");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(3);
			celda.setCellValue("Importe");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(4);
			celda.setCellValue("");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(5);
			celda.setCellValue("Valor Imp.");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(6);
			celda.setCellValue("Banco");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(7);
			celda.setCellValue("Numero");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(8);
			celda.setCellValue("Fecha");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(9);
			celda.setCellValue("Efectivo");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(10);
			celda.setCellValue("Retencion");
			celda.setCellStyle(my_style_encabezado);

			celda = fila.createCell(11);
			celda.setCellValue("Deposito");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(12);
			celda.setCellValue("Fecha");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(13);
			celda.setCellValue("Descuento");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(14);
			celda.setCellValue("Descripción");
			celda.setCellStyle(my_style_encabezado);

			// ***** FIN INICIALIZACION

			for (Integer lote : lotes) {

				List<Recibo> recibos = reciboRepository.recibosParaExportar(lote);

				// try {

				// List<String> fileLines = new ArrayList<>(recibos.size());
				for (Recibo recibo : recibos) {

					Recibo reciboCompleto = reciboService.findOne(recibo.getId());

					//pongo lote y fecha lote
					String fechaLoteStr = sdf.format(reciboCompleto.getFechaLote());
					fila = hoja.getRow(1); //fila = hoja.getRow(fi);
					celda = fila.createCell(0); //fila.createCell(13);
					celda.setCellValue(lote);
					celda.setCellStyle(my_style);
					
					celda = fila.createCell(1); //fila.createCell(14);
					celda.setCellValue(fechaLoteStr);
					celda.setCellStyle(my_style);
					//end
					
					
					fechaReciboStr = sdf.format(recibo.getFecha());

					filaIni_XCadaTipoDePago = filaComienzoSiguienteRecibo;
					//filaIni_XCadaTipoDePago++;
					filaComienzoSiguienteRecibo = filaIni_XCadaTipoDePago;
					fi = filaIni_XCadaTipoDePago;
					fila = hoja.getRow(fi);

					// nro de recibo
					celda = fila.createCell(0);
					celda.setCellValue(recibo.getNumero());
					celda.setCellStyle(my_style);
					// nombre cliente
					celda = fila.createCell(1);
					celda.setCellValue(recibo.getCliente().getNombre());
					celda.setCellStyle(my_style);
					
					
					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}

					// FACTURA:
					fi = filaIni_XCadaTipoDePago;
					//arranca facturas
					for (Factura fac : reciboCompleto.getFacturas()) {
						// nro factura
						fila = hoja.getRow(fi);
						celda = fila.createCell(2); //2
						celda.setCellValue(fac.getNumero());
						celda.setCellStyle(my_style);
						// factura importe
						celda = fila.createCell(3); //3
						celda.setCellValue(fac.getMonto());
						celda.setCellStyle(my_style);
						
						fi++;
					}
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
						celda.setCellStyle(my_style);
						
						fi++; //ver si va
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
						
						fila = hoja.getRow(fi);
						celda = fila.createCell(11);
						celda.setCellValue(depositoss.getMonto());
						celda.setCellStyle(my_style);
						
						celda = fila.createCell(12);
						celda.setCellValue(fechaDepositoStr);
						celda.setCellStyle(my_style);
						
						fi++;
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
						celda.setCellStyle(my_style);

						celda = fila.createCell(6);
						celda.setCellValue(cheques.getBanco().getAbreviatura()); // viene
						celda.setCellStyle(my_style);

						celda = fila.createCell(7);
						celda.setCellValue(cheques.getNumero());
						celda.setCellStyle(my_style);

						fechaChequeStr = sdf.format(cheques.getFechaDeposito());
						celda = fila.createCell(8);
						celda.setCellValue(fechaChequeStr);
						celda.setCellStyle(my_style);
						
						fi++;
						// fin excel

						
					}

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}
					
					//RETENCION
					fi = filaIni_XCadaTipoDePago;
					for(TpRetencion retenciones : reciboCompleto.getTpRetenciones()){
						fila = hoja.getRow(fi);
						celda = fila.createCell(10);
						celda.setCellValue(retenciones.getMonto());
						celda.setCellStyle(my_style);
						
						fi++;
					}

					// codigo para sber en que fila arranca el siguiente recibo
					if (filaComienzoSiguienteRecibo < fi) {
						filaComienzoSiguienteRecibo = fi;
					}
					
					//DESCUENTO
					fi = filaIni_XCadaTipoDePago;
					for(Descuento descuento : reciboCompleto.getDescuentos()){
						fila = hoja.getRow(fi);
						celda = fila.createCell(13);
						celda.setCellValue(descuento.getPorcentaje());
						celda.setCellStyle(my_style);
						
						celda = fila.createCell(14);
						celda.setCellValue(descuento.getDescripcion());
						celda.setCellStyle(my_style);
						
						fi++;
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

			hoja.autoSizeColumn(0);
			hoja.autoSizeColumn(1);
			hoja.autoSizeColumn(2);
			hoja.autoSizeColumn(3);
			hoja.autoSizeColumn(4);
			hoja.autoSizeColumn(5);
			hoja.autoSizeColumn(6);
			hoja.autoSizeColumn(7);
			hoja.autoSizeColumn(8);
			hoja.autoSizeColumn(9);
			hoja.autoSizeColumn(10);
			hoja.autoSizeColumn(11);
			hoja.autoSizeColumn(12);
			hoja.autoSizeColumn(13);
			hoja.autoSizeColumn(14);
			
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
	@RequestMapping("/controlPanel/nuevo")
	public String crearNuevo(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto, Model model) {
		model.addAttribute("roles", Role.values());
		return "salvarUsuario";
	}	

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/controlPanel/salvar")
	public ModelAndView salvar(@ModelAttribute("usuarioDto") UsuarioDto usuarioDto) {

		try {

			if (usuarioDto.getId() == null) {

				// Validar el DTO antes de criar el usuário
				ModelAndView validationResult = validate(usuarioDto, true);
				if(validationResult != null) {
					return validationResult;
				}

				// Todo ok, criar usuário
				Usuario usuario = new Usuario();
				usuario.setNombre(usuarioDto.getNombre());
				usuario.setCodigo(usuarioDto.getCodigo());
				usuario.setEmail(usuarioDto.getEmail());
				usuario.setRole(Role.getByValue(usuarioDto.getRole()));
				usuario.setPwd(usuarioDto.getPwd());
				usuarioService.salvar(usuario);

				// Mensaje de suceso
				ModelAndView mav = new ModelAndView("salvarUsuario");
				mav.addObject("roles", Role.values());
				mav.addObject("successMessage", "El usuário foi criado con suceso!");
				return mav;

			} else {

				Usuario usuario = usuarioService.find(usuarioDto.getId());

				// Validar el DTO antes de criar el usuário
				ModelAndView validationResult = validate(usuarioDto, false);
				if(validationResult != null) {
					return validationResult;
				}

				// Todo ok, actualizar el usuario
				usuario.setNombre(usuarioDto.getNombre());
				usuario.setCodigo(usuarioDto.getCodigo());
				usuario.setEmail(usuarioDto.getEmail());
				usuario.setRole(Role.getByValue(usuarioDto.getRole()));
				usuario.setPwd(usuarioDto.getPwd());
				usuarioService.actualizar(usuario);

				// Mensaje de suceso
				ModelAndView mav = new ModelAndView("salvarUsuario");
				mav.addObject("roles", Role.values());
				mav.addObject("successMessage", "El usuário foi actualizado con suceso!");
				return mav;
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

	private ModelAndView validate(UsuarioDto usuarioDto, boolean newUsuario) {

		boolean emptyValues = false;
		if (StringUtils.isEmpty(usuarioDto.getNombre())) {
			emptyValues = true;
		} else if (StringUtils.isEmpty(usuarioDto.getEmail())) {
			emptyValues = true;
		} else if (StringUtils.isEmpty(usuarioDto.getPwd())) {
			emptyValues = true;
		} else if (StringUtils.isEmpty(usuarioDto.getPwdConfirmacion())) {
			emptyValues = true;
		} else if (StringUtils.isEmpty(usuarioDto.getRole())) {
			emptyValues = true;
		} 
		
		if(emptyValues) {
			ModelAndView mav = new ModelAndView("salvarUsuario");
			mav.addObject("roles", Role.values());
			mav.addObject("errorMessage", "Todos los campos son obligatorios en la creación de nuevo usuário");
			return mav;
		}
		
		if (!usuarioDto.getPwd().equals(usuarioDto.getPwdConfirmacion())) {
			ModelAndView mav = new ModelAndView("salvarUsuario");
			mav.addObject("roles", Role.values());
			mav.addObject("errorMessage", "Los password no coinciden");
			return mav;
		}
		
		Usuario existeUsuario = usuarioService.findByEmail(usuarioDto.getEmail());
		if (newUsuario && existeUsuario != null) {
			ModelAndView mav = new ModelAndView("salvarUsuario");
			mav.addObject("roles", Role.values());
			mav.addObject("errorMessage", "El usuário no fue creado porque ya existe!");
			return mav;					
		}		
		
		return null;
	}
}
