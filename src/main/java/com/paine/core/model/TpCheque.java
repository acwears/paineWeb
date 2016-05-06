package main.java.com.paine.core.model;
import java.util.Date;

public class TpCheque extends Bean{
	
	private Recibo recibo;
	private int numero;
	private double monto;
	private String cuit;
	private Date fechaDeposito;
	private Banco banco;
	
	/*private double tp_ch_nro;
	private String tp_ch_fecha_deposito;
	private double tp_ch_importe;
	private String tp_ch_cuit;
	private String tp_ch_banco;*/


	
	public Recibo getRecibo() {
		return recibo;
	}
	public void setRecibo(Recibo recibo) {
		this.recibo = recibo;
	}
	public int getNumero() {
		return numero;
	}
	public void setNumero(int numero) {
		this.numero = numero;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
	public String getCuit() {
		return cuit;
	}
	public void setCuit(String cuit) {
		this.cuit = cuit;
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
	public Date getFechaDeposito() {
		return fechaDeposito;
	}
	public void setFechaDeposito(Date fechaDeposito) {
		this.fechaDeposito = fechaDeposito;
	}
}
