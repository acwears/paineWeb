package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.CuentaCorriente;

@Repository
public class CuentaCorrienteRepository extends JDBCRepository{
	public List<CuentaCorriente> cargarCC() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM cc ORDER BY nro_cliente ASC ");
		
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			CuentaCorriente cuentaCorriente = new CuentaCorriente();
			
			cuentaCorriente.setId(rs.getInt("nro_cliente"));
			cuentaCorriente.setNombre(rs.getString("nombre"));
			cuentaCorriente.setFecha_factura(rs.getDate("fecha_factura"));
			cuentaCorriente.setNro_factura(rs.getString("nro_factura"));
			cuentaCorriente.setMonto_original(rs.getDouble("monto_original"));
			cuentaCorriente.setMonto_adeudado(rs.getDouble("monto_adeudado"));
			cuentaCorriente.setSuma_deuda(rs.getDouble("suma_deuda"));
			cuentaCorriente.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
			
			return cuentaCorriente;
		});
	}
	
	public List<CuentaCorriente> cargarCCByVendedor(int idUserPaine) {

		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT cc.nro_factura, ");
		sb.append(" cc.nro_cliente, ");
		sb.append(" cc.nombre, ");
		sb.append(" cc.fecha_factura, ");
		sb.append(" cc.monto_original, ");
		sb.append(" cc.monto_adeudado, ");
		sb.append(" cc.suma_deuda, ");
		sb.append(" cc.fecha_vencimiento ");
		sb.append(" FROM cc INNER JOIN clientes c ");
		sb.append(" ON cc.nro_cliente = c.nro_cliente ");
		sb.append(" INNER JOIN usuario u ");
		sb.append(" ON u.codigo = c.vendedor ");
		sb.append(" WHERE u.codigo = ? ");

		Object[] params = new Object[]{idUserPaine};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			CuentaCorriente cuentaCorriente = new CuentaCorriente();
			
			cuentaCorriente.setId(rs.getInt("nro_cliente"));
			cuentaCorriente.setNombre(rs.getString("nombre"));
			cuentaCorriente.setFecha_factura(rs.getDate("fecha_factura"));
			cuentaCorriente.setNro_factura(rs.getString("nro_factura"));
			cuentaCorriente.setMonto_original(rs.getDouble("monto_original"));
			cuentaCorriente.setMonto_adeudado(rs.getDouble("monto_adeudado"));
			cuentaCorriente.setSuma_deuda(rs.getDouble("suma_deuda"));
			cuentaCorriente.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
			
			return cuentaCorriente;
		});
	}
	
	public List<CuentaCorriente> cargarCCByCustomer(int nroCliente) {

		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT cc.nro_factura, ");
		sb.append(" cc.nro_cliente, ");
		sb.append(" cc.nombre, ");
		sb.append(" cc.fecha_factura, ");
		sb.append(" cc.monto_original, ");
		sb.append(" cc.monto_adeudado, ");
		sb.append(" cc.suma_deuda, ");
		sb.append(" cc.fecha_vencimiento ");
		sb.append(" FROM cc INNER JOIN clientes c ");
		sb.append(" ON cc.nro_cliente = c.nro_cliente ");
		sb.append(" INNER JOIN usuario u ");
		sb.append(" ON u.codigo = c.vendedor ");
		sb.append(" WHERE c.nro_cliente = ? ");

		Object[] params = new Object[]{nroCliente};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			CuentaCorriente cuentaCorriente = new CuentaCorriente();
			
			cuentaCorriente.setId(rs.getInt("nro_cliente"));
			cuentaCorriente.setNombre(rs.getString("nombre"));
			cuentaCorriente.setFecha_factura(rs.getDate("fecha_factura"));
			cuentaCorriente.setNro_factura(rs.getString("nro_factura"));
			cuentaCorriente.setMonto_original(rs.getDouble("monto_original"));
			cuentaCorriente.setMonto_adeudado(rs.getDouble("monto_adeudado"));
			cuentaCorriente.setSuma_deuda(rs.getDouble("suma_deuda"));
			cuentaCorriente.setFecha_vencimiento(rs.getDate("fecha_vencimiento"));
			
			return cuentaCorriente;
		});
	}	
	
	public void saveCC(List<CuentaCorriente> CCs){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO cc (nro_cliente, nro_factura, nombre, fecha_factura, monto_original, monto_adeudado, suma_deuda, fecha_vencimiento) ");
		 sb.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?) ");
		 
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					CuentaCorriente cc = CCs.get(index);
					
					ps.setInt(1, cc.getCliente().getNumeroCliente());
					ps.setString(2, cc.getNro_factura());
					ps.setString(3, cc.getNombre());
					ps.setDate(4, new java.sql.Date(cc.getFecha_factura().getTime()));
					ps.setDouble(5, cc.getMonto_original());
					ps.setDouble(6, cc.getMonto_adeudado());
					//if(ArrayUtils.isEmpty(cc.getSuma_deuda())){}
					ps.setDouble(7, cc.getSuma_deuda());
					ps.setDate(8, new java.sql.Date(cc.getFecha_vencimiento().getTime()));
				}
				
				@Override
				public int getBatchSize() {
					return CCs.size();
				}
		});
	}
	
	 public void delete(){
		 
		  StringBuilder sb = new StringBuilder();
		  
		  sb.append(" DELETE ");
		  sb.append(" FROM cc ");
		  		  
		  //Object[] params = new Object[]{1};
		  
		  getJdbcTemplate().update(sb.toString());
	}
}
