package main.java.com.paine.core.model;

public class QryVer extends Bean{
	private double fac_nro_pk;
	private String fac_fecha;
	private double fac_monto;
	
	private double rbo_nro_pk;
	private double rbo_fk_cte_nro;
	private String rbo_fecha;
	private long rbo_descuento;
	private double rbo_importe_suma_facturas;
	private double rbo_importe_total;
	private String rbo_observaciones;
	
	public double getFac_nro_pk() {
		return fac_nro_pk;
	}
	public void setFac_nro_pk(double fac_nro_pk) {
		this.fac_nro_pk = fac_nro_pk;
	}
	public String getFac_fecha() {
		return fac_fecha;
	}
	public void setFac_fecha(String fac_fecha) {
		this.fac_fecha = fac_fecha;
	}
	public double getFac_monto() {
		return fac_monto;
	}
	public void setFac_monto(double fac_monto) {
		this.fac_monto = fac_monto;
	}
	public double getRbo_nro_pk() {
		return rbo_nro_pk;
	}
	public void setRbo_nro_pk(double rbo_nro_pk) {
		this.rbo_nro_pk = rbo_nro_pk;
	}
	public double getRbo_fk_cte_nro() {
		return rbo_fk_cte_nro;
	}
	public void setRbo_fk_cte_nro(double rbo_fk_cte_nro) {
		this.rbo_fk_cte_nro = rbo_fk_cte_nro;
	}
	public String getRbo_fecha() {
		return rbo_fecha;
	}
	public void setRbo_fecha(String rbo_fecha) {
		this.rbo_fecha = rbo_fecha;
	}
	public long getRbo_descuento() {
		return rbo_descuento;
	}
	public void setRbo_descuento(long rbo_descuento) {
		this.rbo_descuento = rbo_descuento;
	}
	public double getRbo_importe_suma_facturas() {
		return rbo_importe_suma_facturas;
	}
	public void setRbo_importe_suma_facturas(double rbo_importe_suma_facturas) {
		this.rbo_importe_suma_facturas = rbo_importe_suma_facturas;
	}
	public double getRbo_importe_total() {
		return rbo_importe_total;
	}
	public void setRbo_importe_total(double rbo_importe_total) {
		this.rbo_importe_total = rbo_importe_total;
	}
	public String getRbo_observaciones() {
		return rbo_observaciones;
	}
	public void setRbo_observaciones(String rbo_observaciones) {
		this.rbo_observaciones = rbo_observaciones;
	}
}
