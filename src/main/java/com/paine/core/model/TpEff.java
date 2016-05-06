package main.java.com.paine.core.model;

public class TpEff extends Bean{
	/*private double tp_eff_nro_pk;
	private double tp_eff_fk_rbo_nro;
	 
	private double tp_eff_importe;
	private String tp_eff_fecha;*/
	
	private Recibo recibo;
	private double monto;
	
	public Recibo getRecibo() {
		return recibo;
	}
	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
}
