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
		int filaIni_XCadaTipoDePago = 1;
		int filaComienzoSiguienteRecibo = 1;
		int fi = 1;

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
			celda.setCellValue("Nro. Lote");
			celda.setCellStyle(my_style_encabezado);
			
			celda = fila.createCell(14);
			celda.setCellValue("Fecha");
			celda.setCellStyle(my_style_encabezado);

			// ***** FIN INICIALIZACION

			//for (Integer lote : lotes) {
			int lote = lotes;

				List<Recibo> recibos = reciboRepository.recibosParaExportar(lote);

				// try {

				// List<String> fileLines = new ArrayList<>(recibos.size());
				for (Recibo recibo : recibos) {

					Recibo reciboCompleto = reciboService.findOne(recibo.getId());

					//pongo lote y fecha lote
					String fechaLoteStr = sdf.format(reciboCompleto.getFechaLote());
					fila = hoja.getRow(fi);
					celda = fila.createCell(13);
					celda.setCellValue(lote);
					celda.setCellStyle(my_style);
					
					celda = fila.createCell(14);
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
				}

			//} // fin del for de lotes

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
	
}
