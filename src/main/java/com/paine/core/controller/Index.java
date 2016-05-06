package main.java.com.paine.core.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Index {

	private Log log = LogFactory.getLog(Index.class);
	
    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
    	log.info("We are at INDEX");
        model.addAttribute("name", name);
        return "index";
    }
    
    @RequestMapping("/greeting/maxi")
    public String sayHelloToMaxi(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", "Maxi Puto");
        return "greeting";
    }
    
   /* @RequestMapping("/ingresar")
    public String ingresar(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "reciboNuevo";
    }*/
}