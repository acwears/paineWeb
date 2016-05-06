package main.java.com.paine.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.repository.ReciboRepository;

@Controller
public class ModificarReciboController {
	
	@Autowired
	private ReciboRepository reciboRepository;
    
	@RequestMapping("/modificar")
    public String modificar(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
		
		Recibo recibo = reciboRepository.findOne(2222);
        model.addAttribute("name", name);
        model.addAttribute("recibo", recibo);
        
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
