package main.java.com.paine.core.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.AuthenticatedUser;
import main.java.com.paine.core.model.Role;
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
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(Role.getByValue(rs.getString("role")));

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

		return queryForObjectNull(sb.toString(), new Object[] { email }, (rs, rowNum) -> {

			AuthenticatedUser usuario = new AuthenticatedUser();

			usuario.setId(rs.getInt("id"));
			usuario.setCodigo(rs.getInt("codigo"));
			usuario.setNombre(rs.getString("nombre"));
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(Role.getByValue(rs.getString("role")));

			return usuario;
		});
	}

	public Usuario find(Integer usuarioId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM usuario WHERE id = ? ");

		return getJdbcTemplate().queryForObject(sb.toString(), new Object[] { usuarioId }, (rs, rowNum) -> {

			AuthenticatedUser usuario = new AuthenticatedUser();

			usuario.setId(rs.getInt("id"));
			usuario.setCodigo(rs.getInt("codigo"));
			usuario.setNombre(rs.getString("nombre"));
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(Role.getByValue(rs.getString("role")));

			return usuario;
		});
	}
	
	public Usuario findOne(Integer usuarioId) {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM usuario WHERE id = ? ");

		return getJdbcTemplate().queryForObject(sb.toString(), new Object[] { usuarioId }, (rs, rowNum) -> {

			Usuario usuario = new Usuario();

			usuario.setId(rs.getInt("id"));
			usuario.setCodigo(rs.getInt("codigo"));
			usuario.setNombre(rs.getString("nombre"));
			usuario.setEmail(rs.getString("email"));
			usuario.setPwd(rs.getString("pwd"));
			usuario.setRole(Role.getByValue(rs.getString("role")));

			return usuario;
		});
	}

	public void salvar(Usuario usuario) {
		 Map<String, Object> parameters = new HashMap<>();
		 parameters.put("codigo", usuario.getCodigo());
		 parameters.put("nombre", usuario.getNombre());
		 parameters.put("email", usuario.getEmail());
		 parameters.put("pwd", usuario.getPwd());
		 parameters.put("role", usuario.getRole().name());
		 
		 SimpleJdbcInsert simpleJdbcInsert = getSimpleJdbcInsert();
		 simpleJdbcInsert.withTableName("usuario");
		 simpleJdbcInsert.usingGeneratedKeyColumns("id");
		 Number reciboId = simpleJdbcInsert.executeAndReturnKey(parameters);
		 
		 usuario.setId(reciboId.intValue());		
	}

	public void actualizar(Usuario usuario) {
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE usuario SET ");
		sb.append(" nombre = ?, ");
		sb.append(" email = ?, ");
		sb.append(" pwd = ?, ");
		sb.append(" role = ? ");
		sb.append(" WHERE id = ? ");
		
		Object[] params = new Object[]{usuario.getNombre(), usuario.getEmail(), usuario.getPwd(), usuario.getRole().name(), usuario.getId()};
		getJdbcTemplate().update(sb.toString(), params);	
	}
}
