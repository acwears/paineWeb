package main.java.com.paine.core.model;

public enum Role {

	ROLE_ADMIN("Administrador"), 
	ROLE_VENDEDOR("Vendedor"), 
	ROLE_VISITANTE("Visitante");
	
	private String descripcion;
	
	private Role(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getDescripcion() {
		return this.descripcion;
	}
	
	public static Role getByValue(String value) {
		
		for (Role role : values()) {
			if (role.name().equals(value)) {
				return role;
			}
		}
		
		return ROLE_VISITANTE;
	}
}
