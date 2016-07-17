package main.java.com.paine.core.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
import main.java.com.paine.core.service.ReciboService;
import main.java.com.paine.core.util.Context;

@Controller
public class ModificarReciboController {

	private Log log = LogFactory.getLog(ModificarReciboController.class);

	@Autowired
	private ReciboService reciboService;

	@Autowired
	private BancoRepository bancoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;

	@RequestMapping("/listarRecibosModificar")
	public String listado(Model model) {

		try {

			List<Recibo> recibos = reciboService.findAll();
			model.addAttribute("recibos", recibos);

		} catch (Exception e) {
			log.error("Error interno", e);
			return "internalError";
		}

		return "reciboModificarListado";
	}

	@RequestMapping("/modificar")
	public ModelAndView modificar(@RequestParam Integer reciboId, String successMessage, String errorMessage) {

		ModelAndView model = new ModelAndView("reciboModificar");

		List<Banco> bancos = bancoRepository.cargarBancos();
		model.addObject("bancos", bancos);

		// List<Cliente> clientes = clienteRepository.cargarClientes();
		// model.addObject("clientes", clientes);
		if (Context.loggedUser().isAdmin()) {
			List<Cliente> clientes = clienteRepository.cargarClientes();
			model.addObject("clientes", clientes);
		} else {
			List<Cliente> clientes = clienteRepository.cargarClientesByVendedor(Context.loggedUser().getCodigo());
			model.addObject("clientes", clientes);
		}

		if (Context.loggedUser().isAdmin()) {
			List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCC();
			model.addObject("cuentasCorrientes", cuentasCorrientes);
		} else {
			List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCCByVendedor(Context.loggedUser().getCodigo());
			model.addObject("cuentasCorrientes", cuentasCorrientes);
		}

		Recibo recibo = reciboService.findOne(reciboId);
		model.addObject("recibo", recibo);

		// SUMO EL IMPORTE TOTAL DE LAS RETENCIONES
		double sumaMontoRetenciones = 0;
		if (CollectionUtils.isNotEmpty(recibo.getTpRetenciones())) {

			for (TpRetencion ret : recibo.getTpRetenciones()) {
				sumaMontoRetenciones = sumaMontoRetenciones + ret.getMonto();
				// creo que no break;
			}
		}
		model.addObject("sumaMontoRetenciones", sumaMontoRetenciones);

		// SUMO EL IMPORTE TOTAL DE LOS CHEQUES
		double sumaMontoCheques = 0;
		if (CollectionUtils.isNotEmpty(recibo.getTpCheques())) {

			for (TpCheque cheque : recibo.getTpCheques()) {
				sumaMontoCheques = sumaMontoCheques + cheque.getMonto();
			}
		}
		model.addObject("sumaMontoCheques", sumaMontoCheques);

		// SUMO EL IMPORTE TOTAL DE LOS DEPOSITOS
		double sumaMontoDepositos = 0;
		if (CollectionUtils.isNotEmpty(recibo.getTpDepositos())) {

			for (TpDeposito deposito : recibo.getTpDepositos()) {
				sumaMontoDepositos = sumaMontoDepositos + deposito.getMonto();
			}
		}
		model.addObject("sumaMontoDepositos", sumaMontoDepositos);

		List<Recibo> recibos = reciboService.findAll();
		model.addObject("recibos", recibos);

		model.addObject("successMessage", successMessage);
		model.addObject("errorMessage", errorMessage);

		return model;
	}
	
	@RequestMapping("/recibo/modificar")
	//public String salvar(@RequestParam Integer reciboId, @ModelAttribute("reciboDto") ReciboDto reciboDto, Model model) {
	public String salvar(@ModelAttribute("reciboDto") ReciboDto reciboDto, Model model) {
		Recibo recibo = null;

		try {

			recibo = criarRecibo(reciboDto); //108
			//recibo = criarRecibo(reciboDto, reciboId);
			reciboService.modify(recibo);

		} catch (Exception e) {
			log.error("Error tratando de actualizar el recibo");
			model.addAttribute("errorMessage", "Se produjo un error al intentar modificar el recibo");
			return "mensajeInformativo";
		}

		model.addAttribute("successMessage", "El recibo fue modificado!");
		return "mensajeInformativo";
	}

	//private Recibo criarRecibo(ReciboDto reciboDto, int idReciboModificar) throws ParseException {
	private Recibo criarRecibo(ReciboDto reciboDto) throws ParseException {
		Cliente cliente = clienteRepository.findOne(reciboDto.getReciboClienteId());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Salvar recibo
		Recibo recibo = new Recibo();
		recibo.setId(reciboDto.getReciboId()); //recibo.setId(idReciboModificar);
		recibo.setNumero(reciboDto.getReciboNumero());
		recibo.setFecha(sdf.parse(reciboDto.getReciboFecha()));
		recibo.setObservaciones(reciboDto.getReciboObservaciones());
		recibo.setFechaProceso(sdf.parse(reciboDto.getReciboFechaProceso()));
		recibo.setImporteSumaFacturas(reciboDto.getReciboImporteSumaFacturas());
		recibo.setImporteTotal(reciboDto.getReciboImporteTotal());
		recibo.setCliente(cliente);
		recibo.setDescuento(reciboDto.getReciboTotalDescuento());

		Usuario usuario = new Usuario();
		usuario.setId(234);
		recibo.setUsuarioCriador(usuario);

		// Criar depositos
		if (ArrayUtils.isNotEmpty(reciboDto.getDepositoImporte())) {
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
		// Criar cheques
		if (ArrayUtils.isNotEmpty(reciboDto.getChequeImporte())) {
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
		// Criar facturas
		if (ArrayUtils.isNotEmpty(reciboDto.getFacturaNro())) {
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
		
		// Criar descuentos
		if (ArrayUtils.isNotEmpty(reciboDto.getDescuentoPorcentaje())) {
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
		// efectivo
		TpEff tpEff = new TpEff();
		tpEff.setRecibo(recibo);
		tpEff.setMonto(reciboDto.getEfectivoImporte());
		recibo.setTpEff(tpEff);

		// retencion
		if (ArrayUtils.isNotEmpty(reciboDto.getRetencionImporte())) {
			List<TpRetencion> retenciones = new ArrayList<>();
			for (int i = 0; i < reciboDto.getRetencionNumero().length; i++) {
				TpRetencion tpRetencion = new TpRetencion();
				
				tpRetencion.setRecibo(recibo);
				tpRetencion.setNumero(reciboDto.getRetencionNumero()[i]);
				tpRetencion.setMonto(reciboDto.getRetencionImporte()[i]);
				tpRetencion.setAnio(reciboDto.getRetencionYear()[i]);
				tpRetencion.setSucursal(reciboDto.getRetencionSucursal()[i]);
				tpRetencion.setTipoPago(reciboDto.getRetencionTipoPago()[i]);
				
				//recibo.addRetencion(tpRetencion);
				retenciones.add(tpRetencion);
			}
			recibo.setTpRetenciones(retenciones);
		}
		
		return recibo;
	}
}
