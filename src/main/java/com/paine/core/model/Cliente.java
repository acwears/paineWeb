package main.java.com.paine.core.model;

public class Cliente extends Bean {
	
	private int numeroCliente;
	private String nombre;
	private String vendedor;
	
	private String domicilio;
	private String localidad;
	private String cp;
	private String zona;
	private String transporte;
	private String esCliente;
	private String esVendedor;
	private String esTransporte;
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getVendedor() {
		return vendedor;
	}

	public void setVendedor(String vendedor) {
		this.vendedor = vendedor;
	}

	public int getNumeroCliente() {
		return numeroCliente;
	}

	public void setNumeroCliente(int numeroCliente) {
		this.numeroCliente = numeroCliente;
	}

	public String getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(String domicilio) {
		this.domicilio = domicilio;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCp() {
		return cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getTransporte() {
		return transporte;
	}

	public void setTransporte(String transporte) {
		this.transporte = transporte;
	}

	public String getEsCliente() {
		return esCliente;
	}

	public void setEsCliente(String esCliente) {
		this.esCliente = esCliente;
	}

	public String getEsVendedor() {
		return esVendedor;
	}

	public void setEsVendedor(String esVendedor) {
		this.esVendedor = esVendedor;
	}

	public String getEsTransporte() {
		return esTransporte;
	}

	public void setEsTransporte(String esTransporte) {
		this.esTransporte = esTransporte;
	}
	
	
}
