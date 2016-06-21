package main.java.com.paine.core.dto.view;

import main.java.com.paine.core.model.Usuario;

public class UsuarioDto {

	private Integer id;
	private Integer codigo;
	private String nombre;
	private String email;
	private String pwd;
	private String pwdConfirmacion;
	private String role;
	
	public static UsuarioDto crearDto(Usuario usuario) {
		UsuarioDto usuarioDto = new UsuarioDto();
		usuarioDto.setId(usuario.getId());
		usuarioDto.setNombre(usuario.getNombre());
		usuarioDto.setCodigo(usuario.getCodigo());
		usuarioDto.setEmail(usuario.getEmail());
		usuarioDto.setRole(usuario.getRole().name());
		usuarioDto.setPwd(usuario.getPwd());
		return usuarioDto;		
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getPwdConfirmacion() {
		return pwdConfirmacion;
	}

	public void setPwdConfirmacion(String pwdConfirmacion) {
		this.pwdConfirmacion = pwdConfirmacion;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
