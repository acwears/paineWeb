package main.java.com.paine.core.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.service.ReciboService;

@Controller
public class ModificarReciboController {
	@Autowired
	private ReciboService reciboService;
	
	@Autowired
	private ReciboRepository reciboRepository;
    
	@RequestMapping("/modificar")
    public String modificar(Model model) {
		
    	int idRecibo=46;
    	
    	Recibo recibo = reciboService.findOne(idRecibo);
        model.addAttribute("recibo", recibo);

        
        //SUMO EL IMPORTE TOTAL DE LOS CHEQUES
        double sumaMontoCheques =0;
        if(CollectionUtils.isNotEmpty(recibo.getTpCheques())) {
        	
        	for (TpCheque cheque : recibo.getTpCheques()) {
        		sumaMontoCheques = sumaMontoCheques + cheque.getMonto();
        	}        	
        }
        model.addAttribute("sumaMontoCheques",  sumaMontoCheques);
        
        
        //SUMO EL IMPORTE TOTAL DE LOS DEPOSITOS
        double sumaMontoDepositos =0;
        if(CollectionUtils.isNotEmpty(recibo.getTpDepositos())) {
        	
        	for (TpDeposito deposito : recibo.getTpDepositos()) {
        		sumaMontoDepositos = sumaMontoDepositos + deposito.getMonto();
        	}        	
        }
        model.addAttribute("sumaMontoDepositos",  sumaMontoDepositos);
        
        
        List<Recibo> recibos = reciboService.findAll();
        model.addAttribute("recibos",  recibos);
               
        return "reciboModificar";
    }
	
	@RequestMapping("/save")
	 public String save() {
	  
	  Recibo recibo = new Recibo();
	  //recibo.setName("primero insert");
	  
	  reciboRepository.save(recibo);
	  
	        return "index";
	 }
	 
	 @RequestMapping("/update")
	 public String update() {
	  
	  Recibo recibo = reciboRepository.findOne(2);
	  //recibo.setName("modificando...");
	  reciboRepository.update(recibo);
	  
	  return "index";
	 }
}
