package main.java.com.paine.core.dto.view;

public class ReciboDto {

	private Integer reciboNumero;

	private Integer reciboClienteId;
	private String reciboFecha;
	private double reciboImporteSumaFacturas;
	private double reciboImporteTotal;
	private String reciboUsuario;
	private String reciboFechaProceso;
	private String reciboObservaciones;
	private double reciboTotalDescuento; 	
	
	//Variables para el pago en efectivo
	private double efectivoImporte;
	
	//variables tabla factura
	private String[] facturaNro;
	private String[] facturaFecha;
	private double[] facturaMonto;
	
	//Variables para el listado de cheuqes
	private int[] chequeBanco;
	private double[] chequeImporte;
	private int[] chequeNro;
	private String[] chequeCuit;
	private String[] chequeFechaDeposito;
	
	//Variables para el listado de depositos
	private String[] depositoFecha;
	private int[] depositoBanco;
	private double[] depositoImporte;

	//Variables para el listado de descuentos
	private String[] descuentoDescripcion;
	private double[] descuentoPorcentaje;	
	
	//Variables del tipo de pago RETENCION
	private String[] retencionNumero;
	private double[] retencionImporte;
	private String[] retencionSucursal;
	private int[] retencionYear;

	public Integer getReciboNumero() {
		return reciboNumero;
	}

	public void setReciboNumero(Integer reciboNumero) {
		this.reciboNumero = reciboNumero;
	}
	public Integer getReciboClienteId() {
		return reciboClienteId;
	}

	public void setReciboClienteId(Integer reciboClienteId) {
		this.reciboClienteId = reciboClienteId;
	}

	public String getReciboUsuario() {
		return reciboUsuario;
	}

	public void setReciboUsuario(String reciboUsuario) {
		this.reciboUsuario = reciboUsuario;
	}

	public String getReciboObservaciones() {
		return reciboObservaciones;
	}

	public void setReciboObservaciones(String reciboObservaciones) {
		this.reciboObservaciones = reciboObservaciones;
	}

	public int[] getChequeBanco() {
		return chequeBanco;
	}

	public void setChequeBanco(int[] chequeBanco) {
		this.chequeBanco = chequeBanco;
	}

	public int[] getChequeNro() {
		return chequeNro;
	}

	public void setChequeNro(int[] chequeNro) {
		this.chequeNro = chequeNro;
	}

	public String[] getChequeCuit() {
		return chequeCuit;
	}

	public void setChequeCuit(String[] chequeCuit) {
		this.chequeCuit = chequeCuit;
	}

	public String[] getChequeFechaDeposito() {
		return chequeFechaDeposito;
	}

	public void setChequeFechaDeposito(String[] chequeFechaDeposito) {
		this.chequeFechaDeposito = chequeFechaDeposito;
	}

	public String[] getFacturaNro() {
		return facturaNro;
	}

	public void setFacturaNro(String[] facturaNro) {
		this.facturaNro = facturaNro;
	}

	public String[] getFacturaFecha() {
		return facturaFecha;
	}

	public void setFacturaFecha(String[] facturaFecha) {
		this.facturaFecha = facturaFecha;
	}

	public String[] getDepositoFecha() {
		return depositoFecha;
	}

	public void setDepositoFecha(String[] depositoFecha) {
		this.depositoFecha = depositoFecha;
	}

	public int[] getDepositoBanco() {
		return depositoBanco;
	}

	public void setDepositoBanco(int[] depositoBanco) {
		this.depositoBanco = depositoBanco;
	}

	public String[] getDescuentoDescripcion() {
		return descuentoDescripcion;
	}

	public void setDescuentoDescripcion(String[] descuentoDescripcion) {
		this.descuentoDescripcion = descuentoDescripcion;
	}

	public double getReciboImporteSumaFacturas() {
		return reciboImporteSumaFacturas;
	}

	public void setReciboImporteSumaFacturas(double reciboImporteSumaFacturas) {
		this.reciboImporteSumaFacturas = reciboImporteSumaFacturas;
	}

	public double getReciboImporteTotal() {
		return reciboImporteTotal;
	}

	public void setReciboImporteTotal(double reciboImporteTotal) {
		this.reciboImporteTotal = reciboImporteTotal;
	}

	public double getReciboTotalDescuento() {
		return reciboTotalDescuento;
	}

	public void setReciboTotalDescuento(double reciboTotalDescuento) {
		this.reciboTotalDescuento = reciboTotalDescuento;
	}

	public double getEfectivoImporte() {
		return efectivoImporte;
	}

	public void setEfectivoImporte(double efectivoImporte) {
		this.efectivoImporte = efectivoImporte;
	}

	public double[] getFacturaMonto() {
		return facturaMonto;
	}

	public void setFacturaMonto(double[] facturaMonto) {
		this.facturaMonto = facturaMonto;
	}

	public double[] getChequeImporte() {
		return chequeImporte;
	}

	public void setChequeImporte(double[] chequeImporte) {
		this.chequeImporte = chequeImporte;
	}

	public double[] getDepositoImporte() {
		return depositoImporte;
	}

	public void setDepositoImporte(double[] depositoImporte) {
		this.depositoImporte = depositoImporte;
	}

	public double[] getDescuentoPorcentaje() {
		return descuentoPorcentaje;
	}

	public void setDescuentoPorcentaje(double[] descuentoPorcentaje) {
		this.descuentoPorcentaje = descuentoPorcentaje;
	}

	public String getReciboFecha() {
		return reciboFecha;
	}

	public void setReciboFecha(String reciboFecha) {
		this.reciboFecha = reciboFecha;
	}

	public String getReciboFechaProceso() {
		return reciboFechaProceso;
	}

	public void setReciboFechaProceso(String reciboFechaProceso) {
		this.reciboFechaProceso = reciboFechaProceso;
	}

	public String[] getRetencionNumero() {
		return retencionNumero;
	}

	public void setRetencionNumero(String[] retencionNumero) {
		this.retencionNumero = retencionNumero;
	}

	public double[] getRetencionImporte() {
		return retencionImporte;
	}

	public void setRetencionImporte(double[] retencionImporte) {
		this.retencionImporte = retencionImporte;
	}

	public String[] getRetencionSucursal() {
		return retencionSucursal;
	}

	public void setRetencionSucursal(String[] retencionSucursal) {
		this.retencionSucursal = retencionSucursal;
	}

	public int[] getRetencionYear() {
		return retencionYear;
	}

	public void setRetencionYear(int[] retencionYear) {
		this.retencionYear = retencionYear;
	}
}
