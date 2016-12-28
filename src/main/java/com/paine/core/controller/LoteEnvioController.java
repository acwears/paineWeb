package main.java.com.paine.core.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.com.paine.core.dto.view.LoteDto;
import main.java.com.paine.core.model.Banco;
import main.java.com.paine.core.model.Descuento;
import main.java.com.paine.core.model.Factura;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;
import main.java.com.paine.core.repository.BancoRepository;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.service.ReciboService;

@Controller
public class LoteEnvioController {
	private Log log = LogFactory.getLog(LoteEnvioController.class);
	
	@Autowired
	private ReciboService reciboService;
	
	@Autowired
	private ReciboRepository reciboRepository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@RequestMapping("/envioLotes")
	public String listado(Model model) {
		
		try {

			List<Recibo> recibos = reciboService.recibosNoEnviados();
			model.addAttribute("recibos", recibos);
			
			int nroLote = reciboRepository.maxLote();
			model.addAttribute("nroLote", nroLote);

		} catch (Exception e) {
			log.error("Error interno");
			return "internalError";
		}
		
		return "loteEnvio";
	}
	
	@RequestMapping("/salvarLote")
	public String salvar(@ModelAttribute("loteDto") LoteDto loteDto, Model model) {
		int nroLote = reciboRepository.maxLote();
		List<Integer> listadoRecibosId = new ArrayList<>();
		
		try {
			listadoRecibosId = recibosParaLotear(loteDto);
			reciboService.salvarLote(listadoRecibosId);
			
			//recibo = criarRecibo(reciboDto);
			//reciboService.salvar(recibo);

		} catch (Exception e) {
			log.error("Error tratando de guardar el recibo");
			model.addAttribute("errorMessage", "Se produjo un error al intentar enviar el Lote");
			return "mensajeInformativo";
		}
		
		model.addAttribute("successMessage", "El Lote " + nroLote + " fue enviado con exito!");
		return "mensajeInformativo";
	}
	
	private List<Integer> recibosParaLotear (LoteDto loteDto) throws ParseException {
		List<Integer> listadoRecibosId = new ArrayList<>();
		
		if(ArrayUtils.isNotEmpty(loteDto.getReciboId())) {
			for (int i = 0; i < loteDto.getReciboId().length; i++) {
				listadoRecibosId.add(loteDto.getReciboId()[i]);
			}
		}
		return listadoRecibosId;
	}
	
	@RequestMapping("/verLotes")
	public String listadoVer(Model model) {
		
		try {

			List<Recibo> recibos = reciboService.lotesEnviados();
			model.addAttribute("recibos", recibos);
			
		} catch (Exception e) {
			log.error("Error interno");
			return "internalError";
		}

		return "loteVer";
	}
	
	@RequestMapping("/verLotesAgrupados")
	public String listadoVerLotesAgrupados(Model model) {
		
		try {

			List<Recibo> recibos = reciboService.lotesEnviadosAgrupados();
			model.addAttribute("recibos", recibos);
			
		} catch (Exception e) {
			log.error("Error interno");
			return "internalError";
		}

		return "loteVerAgrupado";
	}
	
	//******************************************************************************************
	//****************** COMIENZO EXPORTACION DE LOTES *****************************************
	//******************************************************************************************
	
	@RequestMapping("/cargarLotes")
	public String cargarLotes(Model model) {
		
		try {

			List<Recibo> recibos = reciboService.recibosNoExportados();
			model.addAttribute("recibos", recibos);
			
			//int nroLote = reciboRepository.maxLote();
			//model.addAttribute("nroLote", nroLote);

		} catch (Exception e) {
			log.error("Error interno");
			return "internalError";
		}
		
		return "loteExportar";
	}
	
	@RequestMapping("/exportarLotes") //este no lo uso, los exporta a llos lotes de controlpanelcontroller
	public String exportarLotes(@ModelAttribute("loteDto") LoteDto loteDto, Model model) {
		int nroLote = reciboRepository.maxLote();
		List<Integer> listadoRecibosId = new ArrayList<>();
		
		try {
			listadoRecibosId = recibosParaLotear(loteDto);
			reciboService.salvarLote(listadoRecibosId);
			
		} catch (Exception e) {
			log.error("Error tratando de guardar el recibo");
			model.addAttribute("errorMessage", "Se produjo un error al intentar enviar el Lote");
			return "mensajeInformativo";
		}
		
		model.addAttribute("successMessage", "El Lote " + nroLote + " fue enviado con exito!");
		return "mensajeInformativo";
	}
	
	//@RequestMapping("/controlPanel/exportarRecibos")
	//public void exportarRecibos(HttpServletResponse response, Integer[] lotes, Model model) throws ParseException {
	
	//abrir excel al hacer clic en un lote
	@RequestMapping("/verUnLote")
	public String verRecibo(HttpServletResponse response, @RequestParam Integer loteId) {
		
		try {

			//Recibo recibo = reciboService.findOne(loteId);
			//model.addAttribute("recibo", recibo);
			/////////exportarRecibosExcel(loteId);
			//model.addAttribute("recibo", loteId);

		} catch (Exception e) {
			log.error(e);
			log.error("Error interno");
			return "internalError";
		}

		return null; //"verUnLote";
	}
	
	@RequestMapping("/crearExcelPorUnLote")
	public void exportarRecibosExcel(HttpServletResponse response, @RequestParam Integer loteId) throws ParseException {
		//HttpServletResponse response=null;
		
		try {
			
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=recibos.xls");
			response.setHeader("Set-Cookie", "fileDownload=true; path=/");
			generarExcel(loteId, response.getOutputStream()); //generarExcel(lotes, response.getOutputStream());

		} catch (IOException e) {
			log.error("Error exportando los recibos", e);
		}
	}
	
private FileOutputStream generarExcel(Integer lotes, ServletOutputStream outputStream) throws IOException {
		
		
		// **** INICIALIZACION DEL ARCHIVO EXCEL
		FileOutputStream archivo = null;
		int filaIni_XCadaTipoDePago = 4;
		int filaComienzoSiguienteRecibo = 4;
		int fi = 4;

		String nomFile = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String rutaArchivo = System.getProperty("user.home") + "/Recibos_" + nomFile + ".xls";
		File archivoXLS = new File(rutaArchivo);
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
			fila = hoja.getRow(3);
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
			celda.setCellValue("Observaciones");
			celda.setCellStyle(my_style_encabezado);
			
			/*celda = fila.createCell(13);
			celda.setCellValue("Descuento");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(14);
			celda.setCellValue("Descripción");
			celda.setCellStyle(my_style_encabezado);*/

			// ***** FIN INICIALIZACION

			//for (Integer lote : lotes) {
			//new 12/12/2016
			double montoRecibo = 0;
			double montoFacturas = 0;
			double montoDescuento = 0;
			
			int lote = lotes;

				List<Recibo> recibos = reciboRepository.recibosParaExportar(lote);

				// try {

				// List<String> fileLines = new ArrayList<>(recibos.size());
				for (Recibo recibo : recibos) {

					Recibo reciboCompleto = reciboService.findOne(recibo.getId());

					
					
					//pongo lote y fecha lote
					String fechaLoteStr = sdf.format(reciboCompleto.getFechaLote());
					fila = hoja.getRow(1);
					celda = fila.createCell(0);
					celda.setCellValue(lote);
					celda.setCellStyle(my_style);
					
					celda = fila.createCell(1);
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
					
					//OBSERVACIONES
					celda = fila.createCell(13);
					celda.setCellValue(recibo.getObservaciones());
					celda.setCellStyle(my_style);
					
					//new 12/12/2016
					montoRecibo = recibo.getImporteTotal();
					montoFacturas = recibo.getImporteSumaFacturas();
					montoDescuento = recibo.getDescuento();
					
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
					
					//new 12/12/2016
					if(montoRecibo > montoFacturas){
						fila = hoja.getRow(fi);
						celda = fila.createCell(2);
						celda.setCellValue("a cuenta");
						celda.setCellStyle(my_style);
						
						celda = fila.createCell(3); //3
						celda.setCellValue(montoRecibo - montoFacturas + montoDescuento);
						celda.setCellStyle(my_style);
						
						fi++;
					}
					
					//ini DESCUENTO
					for(Descuento descuento : reciboCompleto.getDescuentos()){
						fila = hoja.getRow(fi);
						
						celda = fila.createCell(2);
						celda.setCellValue(descuento.getDescripcion());
						celda.setCellStyle(my_style);
						
						celda = fila.createCell(3);
						celda.setCellValue(descuento.getPorcentaje()*-1);
						celda.setCellStyle(my_style);
						
						fi++;
					}
					//end new descuento y descripcion
					
					
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
				/*	fi = filaIni_XCadaTipoDePago;
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
					}*/
				}

			//} // fin del for de lotes

			  String formula="";
			  fila = hoja.getRow(filaComienzoSiguienteRecibo+1);
			  
			  celda = fila.createCell(2);
			  celda.setCellStyle(my_style);
			  celda.setCellValue("TOTALES");
			  
			  celda = fila.createCell(3); 
			  celda.setCellStyle(my_style);
			  formula = "SUM(D5:D" + filaComienzoSiguienteRecibo + ")";
			  celda.setCellFormula(formula);
			  
			  celda = fila.createCell(5);
			  celda.setCellStyle(my_style);
			  formula = "SUM(F5:F" + filaComienzoSiguienteRecibo + ")";
			  celda.setCellFormula(formula);
			  
			  celda = fila.createCell(9);
			  celda.setCellStyle(my_style);
			  formula = "SUM(J5:J" + filaComienzoSiguienteRecibo + ")";
			  celda.setCellFormula(formula);
			  
			  celda = fila.createCell(10);
			  celda.setCellStyle(my_style);
			  formula = "SUM(K5:K" + filaComienzoSiguienteRecibo + ")";
			  celda.setCellFormula(formula);
			  
			  celda = fila.createCell(11);
			  celda.setCellStyle(my_style);
			  formula = "SUM(L5:L" + filaComienzoSiguienteRecibo + ")";
			  celda.setCellFormula(formula);
			 

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

@RequestMapping("/crearExcelPorRecibosALotear")
public void exportarRecibosExcelALotear(HttpServletResponse response, @RequestParam Integer[] recibosPre) throws ParseException {
	//HttpServletResponse response=null;
	
	try {
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=recibos.xls");
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		generarExcelResivosPrevisualizar(recibosPre, response.getOutputStream()); //generarExcel(lotes, response.getOutputStream());

	} catch (IOException e) {
		log.error("Error exportando los recibos", e);
	}
}

private FileOutputStream generarExcelResivosPrevisualizar(Integer[] lotes, ServletOutputStream outputStream) throws IOException {
	
	
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
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
		//--- celda.setCellValue("Nro. Lote");
		//--- celda.setCellStyle(my_style_encabezado);
		
		celda = fila.createCell(1);
		//--- celda.setCellValue("Fecha");
		//--- celda.setCellStyle(my_style_encabezado);
		
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
		celda.setCellValue("Observaciones");
		celda.setCellStyle(my_style_encabezado);
		
	/*	celda = fila.createCell(13);
		celda.setCellValue("Descuento");
		celda.setCellStyle(my_style_encabezado);
		
		celda = fila.createCell(14);
		celda.setCellValue("Descripción");
		celda.setCellStyle(my_style_encabezado); */

		// ***** FIN INICIALIZACION
		//new 12/12/2016
		double montoRecibo = 0;
		double montoFacturas = 0;
		double montoDescuento = 0;
		
		for (Integer lote : lotes) {

			List<Recibo> recibos = reciboRepository.recibosParaPrevisualizar(lote); //lote en realidad (en este caso) es nro de recibo

			// try {

			// List<String> fileLines = new ArrayList<>(recibos.size());
			for (Recibo recibo : recibos) {

				Recibo reciboCompleto = reciboService.findOne(recibo.getId());

				//pongo lote y fecha lote
				String fechaLoteStr = ""; //--- sdf.format(reciboCompleto.getFechaLote());
				fila = hoja.getRow(1); 
				celda = fila.createCell(0); 
				//---celda.setCellValue(lote);
				celda.setCellStyle(my_style);
				
				celda = fila.createCell(1); //fila.createCell(14);
				//---celda.setCellValue(fechaLoteStr);
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
				
				//OBSERVACIONES
				celda = fila.createCell(13);
				celda.setCellValue(recibo.getObservaciones());
				celda.setCellStyle(my_style);
				
				//new 12/12/2016
				montoRecibo = recibo.getImporteTotal();
				montoFacturas = recibo.getImporteSumaFacturas();
				montoDescuento = recibo.getDescuento();
				
				
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
				
				//new 12/12/2016
				if(montoRecibo > montoFacturas){
					fila = hoja.getRow(fi);
					celda = fila.createCell(2);
					celda.setCellValue("a cuenta");
					celda.setCellStyle(my_style);
					
					celda = fila.createCell(3); //3
					celda.setCellValue(montoRecibo - montoFacturas + montoDescuento);
					celda.setCellStyle(my_style);
					
					fi++;
				}
				
				//ini DESCUENTO
				for(Descuento descuento : reciboCompleto.getDescuentos()){
					fila = hoja.getRow(fi);
					
					celda = fila.createCell(2);
					celda.setCellValue(descuento.getDescripcion());
					celda.setCellStyle(my_style);
					
					celda = fila.createCell(3);
					celda.setCellValue(descuento.getPorcentaje()*-1);
					celda.setCellStyle(my_style);
					
					fi++;
				}
				//end new descuento y descripcion
				
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
			/*	fi = filaIni_XCadaTipoDePago;
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
				}*/
				
				
				
				
				
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

		  String formula="";
		  fila = hoja.getRow(filaComienzoSiguienteRecibo+1);
		  
		  celda = fila.createCell(2);
		  celda.setCellStyle(my_style);
		  celda.setCellValue("TOTALES");
		  
		  celda = fila.createCell(3); 
		  celda.setCellStyle(my_style);
		  formula = "SUM(D5:D" + filaComienzoSiguienteRecibo + ")";
		  celda.setCellFormula(formula);
		  
		  celda = fila.createCell(5);
		  celda.setCellStyle(my_style);
		  formula = "SUM(F5:F" + filaComienzoSiguienteRecibo + ")";
		  celda.setCellFormula(formula);
		  
		  celda = fila.createCell(9);
		  celda.setCellStyle(my_style);
		  formula = "SUM(J5:J" + filaComienzoSiguienteRecibo + ")";
		  celda.setCellFormula(formula);
		  
		  celda = fila.createCell(10);
		  celda.setCellStyle(my_style);
		  formula = "SUM(K5:K" + filaComienzoSiguienteRecibo + ")";
		  celda.setCellFormula(formula);
		  
		  celda = fila.createCell(11);
		  celda.setCellStyle(my_style);
		  formula = "SUM(L5:L" + filaComienzoSiguienteRecibo + ")";
		  celda.setCellFormula(formula);

		  
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
	
}
