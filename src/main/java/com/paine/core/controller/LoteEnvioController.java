package main.java.com.paine.core.controller;

import java.util.ArrayList;
import java.util.List;
import java.text.ParseException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import main.java.com.paine.core.dto.view.LoteDto;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.service.ReciboService;

@Controller
public class LoteEnvioController {
	private Log log = LogFactory.getLog(LoteEnvioController.class);
	
	@Autowired
	private ReciboService reciboService;
	
	@Autowired
	private ReciboRepository reciboRepository;
	
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
		
		model.addAttribute("successMessage", "El Lote fue enviado!");
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
	
}
