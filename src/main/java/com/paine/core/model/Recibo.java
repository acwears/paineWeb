package main.java.com.paine.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recibo extends Bean {

	private int numero;
	private Date fecha;
	private double descuento;
	private String observaciones;
	private Date fechaProceso;
	private double importeSumaFacturas;
	private double importeTotal;
	
	
	private Cliente cliente;
	private List<Factura> facturas; /* rbo_fk_fac_nro; */
	private List<TpCheque> tpCheques;
	private List<TpDeposito> tpDepositos;
	private TpEff tpEff;
	private List<TpRetencion> tpRetenciones;
	private List<Descuento> descuentos;
	
	private Vendedor vendedor;
	private Usuario usuario;
	
	public List<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(List<Factura> facturas) {
		this.facturas = facturas;
	}

	public List<TpCheque> getTpCheques() {
		return tpCheques;
	}

	public void setTpCheques(List<TpCheque> tpCheques) {
		this.tpCheques = tpCheques;
	}

	public List<TpDeposito> getTpDepositos() {
		return tpDepositos;
	}

	public void setTpDepositos(List<TpDeposito> tpDepositos) {
		this.tpDepositos = tpDepositos;
	}

	public TpEff getTpEff() {
		return tpEff;
	}

	public void setTpEff(TpEff tpEff) {
		this.tpEff = tpEff;
	}

	public List<TpRetencion> getTpRetenciones() {
		return tpRetenciones;
	}

	public void setTpRetenciones(List<TpRetencion> tpRetenciones) {
		this.tpRetenciones = tpRetenciones;
	}
	
	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public double getImporteSumaFacturas() {
		return importeSumaFacturas;
	}

	public void setImporteSumaFacturas(double importeSumaFacturas) {
		this.importeSumaFacturas = importeSumaFacturas;
	}

	public double getImporteTotal() {
		return importeTotal;
	}

	public void setImporteTotal(double importeTotal) {
		this.importeTotal = importeTotal;
	}
	
	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
	
	
	
	//*************************************************************************************
	//****** FUNCIONES PARA CARGAR LAS LISTAS DE "FACTURAS, CHEUQES, DEPOSITOS Y RETENCION"
	public void addFactura(Factura factura) {

		if(this.facturas == null) {
			this.facturas = new ArrayList<>();
		}
		
		this.facturas.add(factura);
	}
	
	public void addCheque(TpCheque tpCheque) {

		if(this.tpCheques == null) {
			this.tpCheques = new ArrayList<>();
		}
		
		this.tpCheques.add(tpCheque);
	}

	public void addDeposito(TpDeposito tpDeposito) {

		if(this.tpDepositos == null) {
			this.tpDepositos = new ArrayList<>();
		}
		
		this.tpDepositos.add(tpDeposito);
	}
	
	public void addRetencion(TpRetencion tpRetencion) {

		if(this.tpRetenciones == null) {
			this.tpRetenciones = new ArrayList<>();
		}
		
		this.tpRetenciones.add(tpRetencion);
	}
	
	public void addDescuento(Descuento descuento){
		if(this.descuentos == null){
			this.descuentos = new ArrayList<>();
		}
	}
	//******************************************************* FIN FUNCIONES DE CARGAR LISTAS
	//**********************************************************************************************
	
	

	public long getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Descuento> getDescuentos() {
		return descuentos;
	}

	public void setDescuentos(List<Descuento> descuentos) {
		this.descuentos = descuentos;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getFechaProceso() {
		return fechaProceso;
	}

	public void setFechaProceso(Date fechaProceso) {
		this.fechaProceso = fechaProceso;
	}
}