package main.java.com.paine.core.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.Descuento;
import main.java.com.paine.core.model.Factura;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;
import main.java.com.paine.core.repository.DescuentoRepository;
import main.java.com.paine.core.repository.FacturaRepository;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.repository.TpChequeRepository;
import main.java.com.paine.core.repository.TpDepositoRepository;
import main.java.com.paine.core.repository.TpEffRepository;
import main.java.com.paine.core.repository.TpRetencionRepository;

@Service
@Transactional
public class ReciboService {
	
	@Autowired
	private ReciboRepository reciboRepository;
	 
	@Autowired
	private TpEffRepository tpEffRepository;
	
	@Autowired
	private FacturaRepository facturaRepository;
	
	@Autowired
	private TpChequeRepository tpChequeRepository;
	
	@Autowired
	private TpDepositoRepository tpDepositoRepository;
	
	@Autowired
	private DescuentoRepository descuentoRepository;
	
	@Autowired
	private TpRetencionRepository tpRetencionRepository;
	
	public Recibo findOne(int id) {
		
    	Recibo recibo = reciboRepository.findOne(id);
    	List<TpCheque> cheques = reciboRepository.agregoCheques(id);
    	List<Factura> facturas = reciboRepository.agregoFacturas(id);
    	List<TpDeposito> tpDepositos = reciboRepository.agregoDepositos(id);
    	List<TpRetencion> tpRetenciones = reciboRepository.agregoRetencion(id);
    	TpEff tpEff = reciboRepository.agregoEff(id);
    	Cliente cliente = reciboRepository.agregoCliente(id);
    	   	
    	recibo.setFacturas(facturas);
    	recibo.setTpCheques(cheques);
    	recibo.setTpDepositos(tpDepositos);
    	recibo.setTpRetenciones(tpRetenciones);
    	recibo.setTpEff(tpEff);
    	recibo.setCliente(cliente);

		return recibo;
	}
	
	public List<Recibo> findAll(){
		List<Recibo> recibos = reciboRepository.listarRecibos();
		return recibos;
	}
	
	public void salvar(Recibo recibo) {
		  
		reciboRepository.save(recibo);
		
		List<Factura> facturas = recibo.getFacturas();
		if(CollectionUtils.isNotEmpty(facturas)) {
			facturaRepository.save(facturas);
		}
		
		List<TpRetencion> tpRetencion = recibo.getTpRetenciones();
		if(CollectionUtils.isNotEmpty(tpRetencion)){
			tpRetencionRepository.save(tpRetencion);
		}
		
		TpEff tpEff = recibo.getTpEff();
		if(tpEff.getMonto() != 0) { 
			tpEffRepository.save(tpEff);
		}
		 
		List<TpCheque> cheques = recibo.getTpCheques();
		if(CollectionUtils.isNotEmpty(cheques)) {
			tpChequeRepository.save(cheques);
		}
		
		List<TpDeposito> depositos = recibo.getTpDepositos();
		if(CollectionUtils.isNotEmpty(depositos)) {
			tpDepositoRepository.save(depositos);
		}
		
		List<Descuento> descuentos = recibo.getDescuentos();
		if(CollectionUtils.isNotEmpty(descuentos)) {
			descuentoRepository.save(descuentos);
		}
	}
}
