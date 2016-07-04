package main.java.com.paine.core.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import main.java.com.paine.core.dto.view.ReciboDto;
import main.java.com.paine.core.model.Banco;
import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.CuentaCorriente;
import main.java.com.paine.core.model.Descuento;
import main.java.com.paine.core.model.Factura;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.BancoRepository;
import main.java.com.paine.core.repository.ClienteRepository;
import main.java.com.paine.core.repository.CuentaCorrienteRepository;
import main.java.com.paine.core.repository.TpDepositoRepository;
import main.java.com.paine.core.service.CuentaCorrienteService;
import main.java.com.paine.core.service.ReciboService;
import main.java.com.paine.core.util.Context;

@Controller
public class IngresarReciboController {

	private Log log = LogFactory.getLog(IngresarReciboController.class);

	@Autowired
	private TpDepositoRepository tpDepositoRepository;

	@Autowired
	private BancoRepository bancoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;

	@Autowired
	private ReciboService reciboService;

	@Autowired
	private CuentaCorrienteService cuentaCorrienteService;
	
	@RequestMapping("/ingresar1")
	public String ingresar(@RequestParam(value = "name", required = false, defaultValue = "World") String name,
			Model model) {
		
		log.info("THIS IS THE LOGGER *** We are in ingresar controller ***");
		int idRecibo = 2222;

		try {

			List<TpDeposito> tpDeposito = tpDepositoRepository.findAll(idRecibo);
			model.addAttribute("tpDeposito", tpDeposito);

			List<Banco> bancos = bancoRepository.cargarBancos();
			model.addAttribute("bancos", bancos);

			if (Context.loggedUser().isAdmin()) {
				List<Cliente> clientes = clienteRepository.cargarClientes();
				model.addAttribute("clientes", clientes);
			}
			else{
				List<Cliente> clientes = clienteRepository.cargarClientesByVendedor(Context.loggedUser().getCodigo());
				model.addAttribute("clientes", clientes);
			}
			
			if (Context.loggedUser().isAdmin()) {
				List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCC();
				model.addAttribute("cuentasCorrientes", cuentasCorrientes);	
			}
			else{
				List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCCByVendedor(Context.loggedUser().getCodigo());
				model.addAttribute("cuentasCorrientes", cuentasCorrientes);	
			}
			
			model.addAttribute("reciboDto", new ReciboDto());
			
		} catch (Exception e) {
			log.info("THIS IS THE LOGGER *** There is exception! ***", e);
		}

		return "reciboNuevo";
	}

	@RequestMapping("/listarCCByCustomer")
	public String listarCCByCustomer(@RequestParam Integer clienteNro, Model model) {
		
		try {
	
			List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCCByCustomer(clienteNro);
			model.addAttribute("cuentasCorrientes", cuentasCorrientes);	
			model.addAttribute("reciboDto", new ReciboDto());
			
		} catch (Exception e) {
			log.info("THIS IS THE LOGGER *** There is exception! ***", e);
		}

		return "ccSelectedCustomer";
	}
	
	@RequestMapping("/recibo/salvar")
	public String salvar(@ModelAttribute("reciboDto") ReciboDto reciboDto, Model model) {

		Recibo recibo = null;

		try {

			recibo = criarRecibo(reciboDto);
			reciboService.salvar(recibo);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Mostrar mensaje suceso al cliente
		return "reciboNuevo";
	}

	private Recibo criarRecibo(ReciboDto reciboDto) throws ParseException {
		Cliente cliente = clienteRepository.findOne(reciboDto.getReciboClienteId());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Salvar recibo
		Recibo recibo = new Recibo();
		recibo.setNumero(reciboDto.getReciboNumero());
		recibo.setFecha(sdf.parse(reciboDto.getReciboFecha()));
		recibo.setObservaciones(reciboDto.getReciboObservaciones());
		recibo.setFechaProceso(sdf.parse(reciboDto.getReciboFechaProceso()));
		recibo.setImporteSumaFacturas(reciboDto.getReciboImporteSumaFacturas());
		recibo.setImporteTotal(reciboDto.getReciboImporteTotal());
		recibo.setCliente(cliente);
		recibo.setDescuento(reciboDto.getReciboTotalDescuento());

		Usuario usuario = new Usuario();
		usuario.setCodigo(Context.loggedUser().getCodigo());
		usuario.setId(Context.loggedUser().getId());
		usuario.setApellido(Context.loggedUser().getApellido());
		usuario.setNombre(Context.loggedUser().getNombre());
		usuario.setEmail(Context.loggedUser().getEmail());
		usuario.setPwd(Context.loggedUser().getPwd());
		usuario.setRole(Context.loggedUser().getRole());
		usuario.setUsuarioCriador(Context.loggedUser().getUsuarioCriador());
		recibo.setUsuario(usuario);
		//recibo.setUsuarioCriador(Context.loggedUser().getUsuarioCriador());

		// salvo efectivo

		TpEff tpEff = new TpEff();
		tpEff.setRecibo(recibo);
		tpEff.setMonto(reciboDto.getEfectivoImporte());
		recibo.setTpEff(tpEff);
		// tpEffRepository.save(tpEff);
		
		//if(StringUtils.isNotEmpty(reciboDto.getRetencionNumero()[0])) {
		if(ArrayUtils.isNotEmpty(reciboDto.getRetencionImporte())) {
		//if(StringUtils.isNotEmpty(reciboDto.getRetencionNumero()[0])) {
			TpRetencion tpRetencion = new TpRetencion();
			tpRetencion.setRecibo(recibo);
			tpRetencion.setNumero(reciboDto.getRetencionNumero()[0]);
			tpRetencion.setMonto(reciboDto.getRetencionImporte()[0]);
			tpRetencion.setAnio(reciboDto.getRetencionYear()[0]);
			tpRetencion.setSucursal(reciboDto.getRetencionSucursal()[0]);
			recibo.addRetencion(tpRetencion);
		}
		
		//tpRetencion.setNumero(reciboDto.getRetencionNumero());
		// tpRetencion.setMonto(reciboDto.getRetencionImporte());
		// tpRetencion.setAnio(reciboDto.getRetencionYear());
		// tpRetencion.setSucursal(reciboDto.getRetencionSucursal());
		// tpRetencionRepository.save(tpRetencion);

		// Criar facturas
		if(ArrayUtils.isNotEmpty(reciboDto.getFacturaNro())){
			List<Factura> facturas = new ArrayList<>();
			for (int i = 0; i < reciboDto.getFacturaNro().length; i++) {
	
				Factura factura = new Factura();
	
				factura.setRecibo(recibo);
				factura.setMonto(reciboDto.getFacturaMonto()[i]);
				factura.setNumero(reciboDto.getFacturaNro()[i]);
				factura.setFecha(sdf.parse(reciboDto.getFacturaFecha()[i]));
	
				facturas.add(factura);
			}
			recibo.setFacturas(facturas);
		}
		// Salvar facturas
		// facturaRepository.save(facturas);

		// Criar cheques
		if(ArrayUtils.isNotEmpty(reciboDto.getChequeCuit())) {
			List<TpCheque> cheques = new ArrayList<>();
			for (int i = 0; i < reciboDto.getChequeBanco().length; i++) {
	
				TpCheque cheque = new TpCheque();
	
				cheque.setRecibo(recibo);
				cheque.setBanco(reciboDto.getChequeBanco()[i]);
				cheque.setMonto(reciboDto.getChequeImporte()[i]);
				cheque.setNumero(reciboDto.getChequeNro()[i]);
				cheque.setFechaDeposito(sdf.parse(reciboDto.getChequeFechaDeposito()[i]));
				cheque.setCuit(reciboDto.getChequeCuit()[i]);
	
				cheques.add(cheque);
			}
			recibo.setTpCheques(cheques);
		}
		// Salvar cheques
		// tpChequeRepository.save(cheques);

		// Criar depositos
		if(ArrayUtils.isNotEmpty(reciboDto.getDepositoImporte())) {
			List<TpDeposito> depositos = new ArrayList<>();
			for (int i = 0; i < reciboDto.getDepositoImporte().length; i++) {
				TpDeposito deposito = new TpDeposito();
	
				deposito.setRecibo(recibo);
				deposito.setBanco(reciboDto.getDepositoBanco()[i]);
				deposito.setMonto(reciboDto.getDepositoImporte()[i]);
				deposito.setFecha(sdf.parse(reciboDto.getDepositoFecha()[i]));
	
				depositos.add(deposito);
			}
			recibo.setTpDepositos(depositos);
		}
		// salvar depositos
		// tpDepositoRepository.save(depositos);

		// Criar descuentos
		
		if(ArrayUtils.isNotEmpty(reciboDto.getDescuentoPorcentaje())) {
			List<Descuento> descuentos = new ArrayList<>();
			for (int i = 0; i < reciboDto.getDescuentoPorcentaje().length; i++) {
				Descuento descuento = new Descuento();
	
				descuento.setRecibo(recibo);
				descuento.setPorcentaje(reciboDto.getDescuentoPorcentaje()[i]);
				descuento.setDescripcion(reciboDto.getDescuentoDescripcion()[i]);
	
				descuentos.add(descuento);
			}
			recibo.setDescuentos(descuentos);
		}
		// salvar descuentos
		// descuentoRepository.save(descuentos);

		// reciboService.salvar(recibo);

		return recibo;
	}
}
