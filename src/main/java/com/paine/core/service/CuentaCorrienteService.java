package main.java.com.paine.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.com.paine.core.model.CuentaCorriente;
import main.java.com.paine.core.repository.CuentaCorrienteRepository;

@Service
@Transactional
public class CuentaCorrienteService {
	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;
	
	public List<CuentaCorriente> find(int nroCliente){
		List<CuentaCorriente> ccs = cuentaCorrienteRepository.cargarCCByCustomer(nroCliente);
		return ccs;
	}
}
