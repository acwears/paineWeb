package main.java.com.paine.core.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import main.java.com.paine.core.dto.view.ReciboDto;
import main.java.com.paine.core.model.Banco;
import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.CuentaCorriente;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpRetencion;
import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.BancoRepository;
import main.java.com.paine.core.repository.ClienteRepository;
import main.java.com.paine.core.repository.CuentaCorrienteRepository;
import main.java.com.paine.core.repository.ReciboRepository;
import main.java.com.paine.core.repository.TpDepositoRepository;
import main.java.com.paine.core.service.ReciboService;

@Controller
public class ModificarReciboController {

	private Log log = LogFactory.getLog(ModificarReciboController.class);

	@Autowired
	private ReciboService reciboService;

	@Autowired
	private ReciboRepository reciboRepository;

	@Autowired
	private TpDepositoRepository tpDepositoRepository;

	@Autowired
	private BancoRepository bancoRepository;

	@Autowired
	private ClienteRepository clienteRepository;

	@Autowired
	private CuentaCorrienteRepository cuentaCorrienteRepository;

	@RequestMapping("/modificar")
	public ModelAndView modificar(String successMessage, String errorMessage) {

		int idRecibo = 75;

		ModelAndView model = new ModelAndView("reciboModificar");

		try {

			// List<TpDeposito> tpDeposito =
			// tpDepositoRepository.findAll(idRecibo);
			// model.addAttribute("tpDeposito", tpDeposito);

			List<Banco> bancos = bancoRepository.cargarBancos();
			model.addObject("bancos", bancos);

			List<Cliente> clientes = clienteRepository.cargarClientes();
			model.addObject("clientes", clientes);

			List<CuentaCorriente> cuentasCorrientes = cuentaCorrienteRepository.cargarCC();
			model.addObject("cuentasCorrientes", cuentasCorrientes);

			// model.addAttribute("reciboDto", new ReciboDto());
			// -----

			// ini
			Recibo recibo = reciboService.findOne(idRecibo);
			model.addObject("recibo", recibo);

			// SUMO EL IMPORTE TOTAL DE LAS RETENCIONES
			double sumaMontoRetenciones = 0;
			if (CollectionUtils.isNotEmpty(recibo.getTpRetenciones())) {

				for (TpRetencion ret : recibo.getTpRetenciones()) {
					sumaMontoRetenciones = sumaMontoRetenciones + ret.getMonto();
					break;
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
			// fin

			model.addObject("successMessage", successMessage);
			model.addObject("errorMessage", errorMessage);

		} catch (Exception e) {
			log.error("Error interno");
			ModelAndView errorModel = new ModelAndView("internalError");
			return errorModel;
		}

		return model;
	}

	@RequestMapping("/recibo/modificar")
	public ModelAndView salvar(@ModelAttribute("reciboDto") ReciboDto reciboDto, Model model) {
		Recibo recibo = null;

		try {

			recibo = criarRecibo(reciboDto, 75);
			reciboService.modify(recibo);

		} catch (Exception e) {
			e.printStackTrace();
			return this.modificar(null, "Se produjo un error al intentar guardar el recibo");
		}

		// Mostrar mensaje suceso al cliente
		return this.modificar("Se guardaron los datos del recibo!", null);
	}

	private Recibo criarRecibo(ReciboDto reciboDto, int idReciboModificar) throws ParseException {
		Cliente cliente = clienteRepository.findOne(reciboDto.getReciboClienteId());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		// Salvar recibo
		Recibo recibo = new Recibo();
		recibo.setId(idReciboModificar);
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

		// salvo efectivo
		/*
		 * TpEff tpEff = new TpEff(); tpEff.setRecibo(recibo);
		 * tpEff.setMonto(reciboDto.getEfectivoImporte());
		 * recibo.setTpEff(tpEff); // tpEffRepository.save(tpEff);
		 * 
		 * TpRetencion tpRetencion = new TpRetencion();
		 * tpRetencion.setRecibo(recibo); //
		 * tpRetencion.setNumero(reciboDto.getRetencionNumero()); //
		 * tpRetencion.setMonto(reciboDto.getRetencionImporte()); //
		 * tpRetencion.setAnio(reciboDto.getRetencionYear()); //
		 * tpRetencion.setSucursal(reciboDto.getRetencionSucursal()); //
		 * tpRetencionRepository.save(tpRetencion);
		 * 
		 * // Criar facturas List<Factura> facturas = new ArrayList<>(); for
		 * (int i = 0; i < reciboDto.getFacturaNro().length; i++) {
		 * 
		 * Factura factura = new Factura();
		 * 
		 * factura.setRecibo(recibo);
		 * factura.setMonto(reciboDto.getFacturaMonto()[i]);
		 * factura.setNumero(reciboDto.getFacturaNro()[i]);
		 * factura.setFecha(sdf.parse(reciboDto.getFacturaFecha()[i]));
		 * 
		 * facturas.add(factura); } // Salvar facturas //
		 * facturaRepository.save(facturas);
		 * 
		 * // Criar cheques List<TpCheque> cheques = new ArrayList<>(); for (int
		 * i = 0; i < reciboDto.getChequeBanco().length; i++) {
		 * 
		 * TpCheque cheque = new TpCheque();
		 * 
		 * cheque.setRecibo(recibo);
		 * cheque.setBanco(reciboDto.getChequeBanco()[i]);
		 * cheque.setMonto(reciboDto.getChequeImporte()[i]);
		 * cheque.setNumero(reciboDto.getChequeNro()[i]);
		 * cheque.setFechaDeposito(sdf.parse(reciboDto.getChequeFechaDeposito()[
		 * i])); cheque.setCuit(reciboDto.getChequeCuit()[i]);
		 * 
		 * cheques.add(cheque); } // Salvar cheques //
		 * tpChequeRepository.save(cheques);
		 * 
		 * // Criar depositos List<TpDeposito> depositos = new ArrayList<>();
		 * for (int i = 0; i < reciboDto.getDepositoImporte().length; i++) {
		 * TpDeposito deposito = new TpDeposito();
		 * 
		 * deposito.setRecibo(recibo);
		 * deposito.setBanco(reciboDto.getDepositoBanco()[i]);
		 * deposito.setMonto(reciboDto.getDepositoImporte()[i]);
		 * deposito.setFecha(sdf.parse(reciboDto.getDepositoFecha()[i]));
		 * 
		 * depositos.add(deposito); } // salvar depositos //
		 * tpDepositoRepository.save(depositos);
		 * 
		 * // Criar descuentos List<Descuento> descuentos = new ArrayList<>();
		 * for (int i = 0; i < reciboDto.getDescuentoPorcentaje().length; i++) {
		 * Descuento descuento = new Descuento();
		 * 
		 * descuento.setRecibo(recibo);
		 * descuento.setPorcentaje(reciboDto.getDescuentoPorcentaje()[i]);
		 * descuento.setDescripcion(reciboDto.getDescuentoDescripcion()[i]);
		 * 
		 * descuentos.add(descuento); }
		 */
		// salvar descuentos
		// descuentoRepository.save(descuentos);

		return recibo;
	}

	@RequestMapping("/save")
	public String save() {

		Recibo recibo = new Recibo();
		// recibo.setName("primero insert");

		reciboRepository.save(recibo);

		return "index";
	}

	@RequestMapping("/update")
	public String update() {

		Recibo recibo = reciboRepository.findOne(2);
		// recibo.setName("modificando...");
		reciboRepository.update(recibo);

		return "index";
	}
}
