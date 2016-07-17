package main.java.com.paine.core.model;

public class TpRetencion extends Bean{
	/*private double tp_ret_id_pk;
	private double tp_ret_fk_rbo_nro;
	 
	private String tp_ret_sucursal;
	private double tp_ret_importe;*/
	
	private Recibo recibo;
	private String numero;
	private String sucursal;
	private double monto;
	private int anio;
	private String tipoPago;
	
	public Recibo getRecibo() {
		return recibo;
	}
	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(String tipoPago) {
		this.tipoPago = tipoPago;
	}	
}
