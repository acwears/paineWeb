package main.java.com.paine.core.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.service.ReciboService;

@Controller
public class VerReciboController {

	private Log log = LogFactory.getLog(ModificarReciboController.class);

	@Autowired
	private ReciboService reciboService;

	// @Autowired
	// private ReciboRepository reciboRepository;
	
	@RequestMapping("/listarRecibos")
	public String listado(Model model) {
		
		try {

			List<Recibo> recibos = reciboService.findAll();
			model.addAttribute("recibos", recibos);

		} catch (Exception e) {
			log.error("Error interno");
			return "internalError";
		}

		return "reciboVerListado";
	}

	@RequestMapping("/verRecibo")
	public String verRecibo(@RequestParam Integer reciboId, Model model) {
		
		try {

			Recibo recibo = reciboService.findOne(reciboId);
			model.addAttribute("recibo", recibo);

			// SUMO EL IMPORTE TOTAL DE LOS CHEQUES
			double sumaMontoCheques = 0;
			if (CollectionUtils.isNotEmpty(recibo.getTpCheques())) {

				for (TpCheque cheque : recibo.getTpCheques()) {
					sumaMontoCheques = sumaMontoCheques + cheque.getMonto();
				}
			}
			model.addAttribute("sumaMontoCheques", sumaMontoCheques);

			// SUMO EL IMPORTE TOTAL DE LOS DEPOSITOS
			double sumaMontoDepositos = 0;
			if (CollectionUtils.isNotEmpty(recibo.getTpDepositos())) {

				for (TpDeposito deposito : recibo.getTpDepositos()) {
					sumaMontoDepositos = sumaMontoDepositos + deposito.getMonto();
				}
			}
			model.addAttribute("sumaMontoDepositos", sumaMontoDepositos);

		} catch (Exception e) {
			log.error(e);
			log.error("Error interno");
			return "internalError";
		}

		return "reciboVer";
	}
}

// reciboRepository.agregoFacturas(idRecibo);
// reciboRepository.agregoDepositos(idRecibo);
// reciboRepository.agregoRetencion(idRecibo);
// reciboRepository.agregoEff(idRecibo);
// reciboRepository.agregoCliente(6);
// //(recibo.getCliente().getNumeroCliente());

/*
 * List<QryVer> qryVer = qryRepository.findAll(idRecibo);
 * model.addAttribute("qryVer", qryVer);
 * 
 * TpEff tpEff = tpEffRepository.findOne(idRecibo); model.addAttribute("tpEff",
 * tpEff);
 * 
 * List<TpCheque> tpCheque = tpChequeRepository.findAll(idRecibo);
 * model.addAttribute("tpCheque", tpCheque);
 * 
 * List<TpDeposito> tpDeposito = tpDepositoRepository.findAll(idRecibo);
 * model.addAttribute("tpDeposito", tpDeposito);
 * 
 * 
 * 
 * for(int x=0;x<recibo.getTpCheques().size();x++) { sumaMontoCheques =
 * sumaMontoCheques + recibo.getTpCheques().get(x).getMonto(); }
 */