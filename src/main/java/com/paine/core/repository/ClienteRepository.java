package main.java.com.paine.core.repository;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Cliente;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

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
	
	public Cliente findOneByNroCte(double nro) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM clientes WHERE nro_cliente = ? ");
		Object[] params = new Object[]{nro};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			Cliente cliente = new Cliente();
			
			cliente.setId(rs.getInt("id"));
			cliente.setNumeroCliente(rs.getInt("nro_cliente"));
			cliente.setNombre(rs.getString("nombre"));
			cliente.setVendedor(rs.getString("vendedor"));
			
			return cliente;
		});
	}
	
	public boolean existeCliente(int nro) {
		try {
			
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT * FROM clientes WHERE nro_cliente = ? ");
			Object[] params = new Object[]{nro};
			
			return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {
				return true;
			});
			
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
	
	public void saveCliente(List<Cliente> clientes){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO clientes (nro_cliente, nombre, domicilio, localidad, cp, zona, vendedor, transporte, es_cliente, es_vendedor, es_transporte) ");
		 sb.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
		 
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					Cliente cte = clientes.get(index);
					
					ps.setInt(1, cte.getNumeroCliente());
					ps.setString(2, cte.getNombre());
					ps.setString(3, cte.getDomicilio());
					ps.setString(4, cte.getLocalidad());
					ps.setString(5, cte.getCp());
					ps.setString(6, cte.getZona());
					ps.setString(7, cte.getVendedor());
					ps.setString(8, cte.getTransporte());
					ps.setString(9, cte.getEsCliente());
					ps.setString(10, cte.getEsVendedor());
					ps.setString(11, cte.getEsTransporte());
				}
				
				@Override
				public int getBatchSize() {
					return clientes.size();
				}
		});
	}
}
