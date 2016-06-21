package main.java.com.paine.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.CuentaCorriente;
import main.java.com.paine.core.repository.ClienteRepository;
import main.java.com.paine.core.repository.CuentaCorrienteRepository;


@Transactional
@Service
public class FileUploadService {
	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	public void saveFile(MultipartFile fileUpload) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Scanner s = null;
		
		//String line="";
		//File myFile;
				 
		try {
			
			//myFile = multipartToFile(fileUpload);
			//myFileCC = multipartToFile(fileUpload);
			//s = new Scanner(myFile);
			//String ext = FilenameUtils.getExtension(fileUpload.getOriginalFilename());
			//fileUpload.getInputStream();
			//java.util.Scanner scanner = new java.util.Scanner(fileUpload,"UTF-8");
			//InputStream in = new FileInputStream(new File(myFile));
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileUpload.getInputStream()));
			//StringBuilder out = new StringBuilder();
			String line;
			List<CuentaCorriente> CCs = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
	            //out.append(line);
	        
			
			//List<CuentaCorriente> CCs = new ArrayList<>();
			//while (s.hasNextLine()) {
			//while ((line = lector.readLine()) != null) {
				//String line = s.nextLine();
				String[] lineArray = line.split(";");
				//String ext = FilenameUtils.getExtension(fileUpload.getOriginalFilename());
//				Cliente cliente = clienteRepository.findOneByNroCte(Integer.parseInt(lineArray[0]));
				
				CuentaCorriente cuentaCorriente = new CuentaCorriente();
				
				//cuentaCorriente.setId(Integer.parseInt(lineArray[0]));
				cuentaCorriente.setNombre(lineArray[1]);
				cuentaCorriente.setFecha_factura(sdf.parse(lineArray[2]));
				cuentaCorriente.setNro_factura(lineArray[3]);
				cuentaCorriente.setMonto_original( Double.parseDouble(lineArray[4]));
				cuentaCorriente.setMonto_adeudado( Double.parseDouble(lineArray[5]));
				cuentaCorriente.setSuma_deuda( Double.parseDouble(lineArray[6]));
				cuentaCorriente.setFecha_vencimiento(sdf.parse(lineArray[7]));
				
				Cliente cliente = new Cliente();
				cliente.setNumeroCliente(Integer.parseInt(lineArray[0]));
				cuentaCorriente.setCliente(cliente);
				
				CCs.add(cuentaCorriente);
			}

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
	
}
