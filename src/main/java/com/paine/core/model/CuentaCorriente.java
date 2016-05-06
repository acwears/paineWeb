package main.java.com.paine.core.model;
import java.util.Date;

public class CuentaCorriente extends Bean {
	private String nro_factura;
	private String nombre;
	private Date fecha_factura;
	private double monto_original;
	private double monto_adeudado;
	private double suma_deuda;
	private Date fecha_vencimiento;
	private Cliente cliente;
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNro_factura() {
		return nro_factura;
	}
	public void setNro_factura(String nro_factura) {
		this.nro_factura = nro_factura;
	}
	public double getMonto_original() {
		return monto_original;
	}
	public void setMonto_original(double monto_original) {
		this.monto_original = monto_original;
	}
	public double getMonto_adeudado() {
		return monto_adeudado;
	}
	public void setMonto_adeudado(double monto_adeudado) {
		this.monto_adeudado = monto_adeudado;
	}
	public double getSuma_deuda() {
		return suma_deuda;
	}
	public void setSuma_deuda(double suma_deuda) {
		this.suma_deuda = suma_deuda;
	}
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public Date getFecha_factura() {
		return fecha_factura;
	}
	public void setFecha_factura(Date fecha_factura) {
		this.fecha_factura = fecha_factura;
	}
	public Date getFecha_vencimiento() {
		return fecha_vencimiento;
	}
	public void setFecha_vencimiento(Date fecha_vencimiento) {
		this.fecha_vencimiento = fecha_vencimiento;
	}
	
	
}
