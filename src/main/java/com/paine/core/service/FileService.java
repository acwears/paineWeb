package main.java.com.paine.core.service;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import main.java.com.paine.core.model.Banco;
import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.CuentaCorriente;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TipoDePago;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.BancoRepository;
import main.java.com.paine.core.repository.ClienteRepository;
import main.java.com.paine.core.repository.CuentaCorrienteRepository;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.repository.TipoDePagoRepository;

@Transactional
@Service
public class FileService {
	
	public StringBuilder sb = new StringBuilder();
	
	@Autowired
	private ReciboService reciboService;
	
	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;

	@Autowired
	private ReciboRepository reciboRepository;
	
	@Autowired
	private BancoRepository bancoRepository;
	
	@Autowired
	private TipoDePagoRepository tipoDePagoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;

	public void saveFile(MultipartFile fileUpload) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Scanner s = null;

		// String line="";
		// File myFile;

		try {

			// myFile = multipartToFile(fileUpload);
			// myFileCC = multipartToFile(fileUpload);
			// s = new Scanner(myFile);
			// String ext =
			// FilenameUtils.getExtension(fileUpload.getOriginalFilename());
			// fileUpload.getInputStream();
			// java.util.Scanner scanner = new
			// java.util.Scanner(fileUpload,"UTF-8");
			// InputStream in = new FileInputStream(new File(myFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileUpload.getInputStream()));
			// StringBuilder out = new StringBuilder();
			String line;
			List<CuentaCorriente> CCs = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				// out.append(line);

				// List<CuentaCorriente> CCs = new ArrayList<>();
				// while (s.hasNextLine()) {
				// while ((line = lector.readLine()) != null) {
				// String line = s.nextLine();
				String[] lineArray = line.split(";");
				// String ext =
				// FilenameUtils.getExtension(fileUpload.getOriginalFilename());
				// Cliente cliente =
				// clienteRepository.findOneByNroCte(Integer.parseInt(lineArray[0]));

				CuentaCorriente cuentaCorriente = new CuentaCorriente();

				// cuentaCorriente.setId(Integer.parseInt(lineArray[0]));
				cuentaCorriente.setNombre(lineArray[1]);
				cuentaCorriente.setFecha_factura(sdf.parse(lineArray[2]));
				cuentaCorriente.setNro_factura(lineArray[3]);
				cuentaCorriente.setMonto_original(Double.parseDouble(lineArray[4]));
				cuentaCorriente.setMonto_adeudado(Double.parseDouble(lineArray[5]));
				
				if(StringUtils.isEmpty(lineArray[6])){
					cuentaCorriente.setSuma_deuda(0);
				}
				else{
					cuentaCorriente.setSuma_deuda(Double.parseDouble(lineArray[6]));
				}
				
				cuentaCorriente.setFecha_vencimiento(sdf.parse(lineArray[7]));

				Cliente cliente = new Cliente();
				cliente.setNumeroCliente(Integer.parseInt(lineArray[0]));
				cuentaCorriente.setCliente(cliente);

				CCs.add(cuentaCorriente);
			}
			cuentaCorrienteRepository.delete();
			cuentaCorrienteRepository.saveCC(CCs);

		} catch (Exception ex) {
			System.out.println("Mensaje: " + ex.getMessage());
		} finally {
			// Cerramos el archivo
			try {
				if (s != null)
					s.close();
			} catch (Exception ex2) {
				System.out.println("Mensaje 2: " + ex2.getMessage());
			}
		}
	}

	public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
		File convFile = new File(multipart.getOriginalFilename());
		multipart.transferTo(convFile);
		return convFile;
	}

	/**
	 * Chequea si existen datos del recibo para exportar
	 * 
	 * @param usuario
	 * @return
	 */
	public boolean hayDatosExportacion() {
		List<Recibo> recibos = reciboRepository.recibosParaExportar(0); //este control no va con el cambio de exportacion por lotes
		return CollectionUtils.isNotEmpty(recibos);
	}

	/**
	 * Prepara los datos que serán exportados. Cada item de la lista,
	 * corresponde a una línea del archivo TXT que será generado.
	 * 
	 * @return
	 */
	public List<String> exportarRecibos(Usuario usuario, int lote) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		String fechaReciboStr;
		String fechaLoteStr;
		
		List<Recibo> recibos = reciboRepository.recibosParaExportar(lote);

		if (CollectionUtils.isEmpty(recibos)) {
			return null;
		}
		
		
		List<String> fileLines = new ArrayList<>(recibos.size());
		for (Recibo recibo : recibos) {
			
			Recibo reciboCompleto = reciboService.findOne(recibo.getId());
			
			fechaReciboStr = sdf.format(recibo.getFecha());
			fechaLoteStr = sdf.format(recibo.getFechaLote());
		
			sb = new StringBuilder();
			
			//CABECERA X CADA RECIBO
			sb.append("1");
			sb.append(StringUtils.leftPad(Long.toString(recibo.getNumero()), 8, "0"));
			sb.append(fechaLoteStr); //antes fechaReciboStr
			sb.append(StringUtils.leftPad(Integer.toString(recibo.getCliente().getNumeroCliente()), 8, "0"));
			fileLines.add(sb.toString());
			

			
			
			//****************** CUERPO DEL RECIBO
			//EFECTIVO
			TpEff efectivo = reciboCompleto.getTpEff();
			if(efectivo != null){
							
				cabeceraTR2(recibo.getNumero(), "06", "121001", efectivo.getMonto());
				fileLines.add(sb.toString());
				

			}
			
			//DEPOSITO:
			String fechaDepositoStr;
			for(TpDeposito depositoss : reciboCompleto.getTpDepositos()){
				fechaDepositoStr = sdf.format(depositoss.getFecha());
				
				cabeceraTR2(recibo.getNumero(), "14", "121209", depositoss.getMonto());
				
				//sb.append("A"); //pago de terceros, A ó B VER
				//sb.append(StringUtils.leftPad(Integer.toString(depositoss.getBanco().getCodigo()),11,"0"));
				sb.append("                                  ");
				sb.append(fechaDepositoStr);
				//sb.append("071"); //Serie, VER
				fileLines.add(sb.toString());
			}
			
			//CHEQUE
			String fechaChequeStr;
			for(TpCheque cheques : reciboCompleto.getTpCheques()){
				
				//*** busco el nombre del banco
				Banco banco = new Banco();
				banco = bancoRepository.findOne(cheques.getBanco().getId());
				//** end busco nombre banco
				
				
				fechaChequeStr = sdf.format(cheques.getFechaDeposito());
				cabeceraTR2(recibo.getNumero(), "03", "121002", cheques.getMonto());
				
				sb.append(StringUtils.leftPad(banco.getAbreviatura(),11," "));
				sb.append(StringUtils.leftPad(Integer.toString(cheques.getNumero()), 8, "0"));
				sb.append(StringUtils.leftPad(cheques.getCuit(), 11, "0"));
				sb.append("    ");
				sb.append(fechaChequeStr);
				fileLines.add(sb.toString());
			}
			
			//RETENCION scar
			/*for(TpRetencion retenciones : reciboCompleto.getTpRetenciones()){
				
				//traigo el codigo de imputacion
				TipoDePago tipoPago = new TipoDePago();
				tipoPago = tipoDePagoRepository.findOne(retenciones.getTipoPago());
				//end
				
				//cabeceraTR2(recibo.getNumero(), "16", "133301", retenciones.getMonto());
				cabeceraTR2(recibo.getNumero(), tipoPago.getCodigo(), tipoPago.getImputacion(), retenciones.getMonto());
				
				sb.append("                                  ");
				sb.append("16"); //tipo de pago, sacar de tabla tipo de pagos
				//sb.append(StringUtils.leftPad(retenciones.getSucursal(), 4, "0"));
				sb.append(StringUtils.leftPad(Integer.toString(retenciones.getAnio()), 4, "0"));
				sb.append(StringUtils.leftPad(retenciones.getNumero(), 4, "0"));
				//sb.append("REG"); //regimen, ver si va
				//sb.append("BASECALCU"); //base calculo, ver si va
				//sb.append("PORCE"); //porcentaje, ver si va
				//sb.append("JU"); //juris, ver si va
				
				fileLines.add(sb.toString());
			}*/
		}

		reciboRepository.modifyExportados(recibos, usuario);

		return fileLines;
		
	}
	
	public void cabeceraTR2(long reciboNro, String tipo, String imputacion, double monto){
		String montoStr;
		
		montoStr = String.format("%.2f", monto);
		montoStr = montoStr.replace(".", ",");
		
		sb = new StringBuilder();
		sb.append("2");
		sb.append(StringUtils.leftPad(Long.toString(reciboNro), 8, "0"));
		sb.append(tipo); //tipo de pago, en el futuro si agregan mas de un banco sacar de tabla "tipo_de_pagos"
		sb.append(imputacion); //imputacion, en el futuro si agregan mas de un banco sacar de tabla "tipo_de_pagos"
		sb.append(StringUtils.leftPad(montoStr,9,"0"));
	}
	
	public List<String> exportarRecibos______LEO(Usuario usuario) {

		List<Recibo> recibos = reciboRepository.recibosParaExportar(0);

		if (CollectionUtils.isEmpty(recibos)) {
			return null;
		}

		List<String> fileLines = new ArrayList<>(recibos.size());
		for (Recibo recibo : recibos) {
			String aux="";
			
			StringBuilder sb = new StringBuilder();
			
			sb.append("1");
			sb.append(StringUtils.leftPad(Long.toString(recibo.getNumero()), 8, "0"));
			sb.append(recibo.getFecha());
			sb.append(StringUtils.leftPad(Integer.toString(recibo.getCliente().getNumeroCliente()), 8, "0"));
			
			sb.append("2");
			sb.append(StringUtils.leftPad(Long.toString(recibo.getNumero()), 8, "0"));
			
			
			sb.append(recibo.getDescuento()).append(",");
			sb.append(recibo.getId()).append(",");
			sb.append(recibo.getImporteSumaFacturas()).append(",");
			sb.append(recibo.getImporteTotal()).append(",");
			sb.append(recibo.getCliente().getId()).append(",");
			sb.append(recibo.getCliente().getNombre());
			sb.append(recibo.getCliente().getNumeroCliente());
			
			fileLines.add(sb.toString());
		}

		reciboRepository.modifyExportados(recibos, usuario);

		return fileLines;
	}
	
	//file upload Agenda
	public void saveAgenda(MultipartFile fileUpload) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Scanner s = null;
		boolean bandera = false;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileUpload.getInputStream()));
			String line;
			List<Cliente> Clientes = new ArrayList<>();
			
			while ((line = reader.readLine()) != null) {

				String[] lineArray = line.split(";");

				if(bandera){
				
					//preguntar aca si existe el cliente en la gaenda
					
					Cliente cltes = new Cliente();
	
					cltes.setNumeroCliente(Integer.parseInt(lineArray[0]));
					cltes.setNombre(lineArray[1]);
	
					if(!StringUtils.isEmpty(lineArray[2])){
						cltes.setDomicilio(lineArray[2]);
					}
					else{
						cltes.setDomicilio("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[3])){
						cltes.setLocalidad(lineArray[3]);
					}
					else{
						cltes.setLocalidad("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[4])){
						cltes.setCp(lineArray[4]);
					}
					else{
						cltes.setCp("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[5])){
						cltes.setZona(lineArray[5]);
					}
					else{
						cltes.setZona("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[6])){
						cltes.setVendedor(lineArray[6]);
					}
					else{
						cltes.setVendedor("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[7])){
						cltes.setTransporte(lineArray[7]);
					}
					else{
						cltes.setTransporte("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[8])){
						cltes.setEsCliente(lineArray[8]);
					}
					else{
						cltes.setEsCliente("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[9])){
						cltes.setEsVendedor(lineArray[9]);
					}
					else{
						cltes.setEsVendedor("-");
					}
					
					if(!StringUtils.isEmpty(lineArray[10])){
						cltes.setEsTransporte(lineArray[10]);
					}
					else{
						cltes.setEsTransporte("-");
					}
	
	
					Clientes.add(cltes);
				
				} //end if bandera para que no tome la primer linea
				else{
					bandera = true;
				}
				
			} //end while
			
			//cuentaCorrienteRepository.saveCC(Clientes);
			clienteRepository.saveCliente(Clientes);

		} catch (Exception ex) {
			System.out.println("Mensaje: " + ex.getMessage());
		} finally {
			// Cerramos el archivo
			try {
				if (s != null)
					s.close();
			} catch (Exception ex2) {
				System.out.println("Mensaje 2: " + ex2.getMessage());
			}
		}
	}
}
