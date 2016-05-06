package main.java.com.paine.core.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.AuthenticatedUser;
import main.java.com.paine.core.model.Usuario;

@Repository
public class UsuarioRepository extends JDBCRepository {

	public List<Usuario> cargarUsuarios() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM usuario ");

		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			Usuario usuario = new Usuario();

			usuario.setId(rs.getInt("id"));
			usuario.setCodigo(rs.getInt("codigo"));
			usuario.setNombre(rs.getString("nombre"));
			//usuario.setApellido(rs.getString("apellido"));
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(rs.getString("role"));

			return usuario;
		});
	}

	/**
	 * Metodo usado para autenticacion de usuários. Retorna un usuario
	 * autenticado si existe en el banco.
	 * 
	 * @param email
	 * @return
	 */
	public AuthenticatedUser getAuthenticated(String email) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM usuario WHERE email = ? ");

		return getJdbcTemplate().queryForObject(sb.toString(), new Object[] { email }, (rs, rowNum) -> {

			AuthenticatedUser usuario = new AuthenticatedUser();

			usuario.setId(rs.getInt("id"));
			usuario.setCodigo(rs.getInt("codigo"));
			usuario.setNombre(rs.getString("nombre"));
			//usuario.setApellido(rs.getString("apellido"));
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(rs.getString("role"));

			return usuario;
		});
	}
}
