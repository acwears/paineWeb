package main.java.com.paine.core.model;
import java.util.Date;

public class TpDeposito extends Bean{
	/*private double tp_dep_id_pk;
	private double tp_dep_fk_rbo_nro;
	
	private double tp_dep_nro;
	private String tp_dep_banco;
	private double tp_dep_importe;*/
	
	private Recibo recibo;
	//private int numero;
	private double monto;
	//private int banco;
	private Banco banco;
	private Date fecha;
	
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
	
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	public void setBanco(int id) {
		
		if(id == 0) {
			return;
		}
		
		this.banco = new Banco();
		this.banco.setId(id);
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
