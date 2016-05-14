package main.java.com.paine.core.repository;

import java.util.List;

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
}
