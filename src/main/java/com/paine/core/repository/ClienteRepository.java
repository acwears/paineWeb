package main.java.com.paine.core.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Cliente;

@Repository
public class ClienteRepository extends JDBCRepository{
	
	public List<Cliente> cargarClientes() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM clientes ");
		
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			Cliente cliente = new Cliente();
				
			cliente.setId(rs.getInt("id"));
			cliente.setNumeroCliente(rs.getInt("nro_cliente"));
			cliente.setNombre(rs.getString("nombre"));
			cliente.setVendedor(rs.getString("vendedor"));
			
			return cliente;
		});
	}
	
	public List<Cliente> cargarClientesByVendedor(int idUserPaine) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM clientes WHERE vendedor = ? ");
		Object[] params = new Object[]{idUserPaine};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			Cliente cliente = new Cliente();
				
			cliente.setId(rs.getInt("id"));
			cliente.setNumeroCliente(rs.getInt("nro_cliente"));
			cliente.setNombre(rs.getString("nombre"));
			cliente.setVendedor(rs.getString("vendedor"));
			
			return cliente;
		});
	}
	
	public Cliente findOne(double id) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM clientes WHERE id = ? ");
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			Cliente cliente = new Cliente();
			
			cliente.setId(rs.getInt("id"));
			cliente.setNombre(rs.getString("nombre"));
			cliente.setVendedor(rs.getString("vendedor"));
			
			return cliente;
		});
	}	
}
