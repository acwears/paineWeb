package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Factura;

@Repository
public class FacturaRepository extends JDBCRepository {
	
	public Factura findOne(int id) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_factura WHERE fac_fk_rbo_nro = ? ");
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			Factura factura = new Factura();
			
			/*factura.setFac_fk_rbo_nro(rs.getDouble("fac_fk_rbo_nro"));
			factura.setId(rs.getInt("fac_nro_pk"));
			factura.setFac_fecha(rs.getString("fac_fecha"));
			factura.setFac_monto(rs.getDouble("fac_monto"));*/
			
			return factura;
		});
	}
	
	public List<Factura> findAll(double numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_factura WHERE fac_fk_rbo_nro = ? ");
		
		Object[] params = new Object[]{numRecibo};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			Factura factura = new Factura();
			
			/*factura.setFac_fk_rbo_nro(rs.getDouble("fac_fk_rbo_nro"));
			factura.setId(rs.getInt("fac_nro_pk"));
			factura.setFac_fecha(rs.getString("fac_fecha"));
			factura.setFac_monto(rs.getDouble("fac_monto"));*/
			
			return factura;
		});
	}
	
	 public void save(List<Factura> facturas) {
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO factura (id_recibo, numero, fecha, monto) ");
		 sb.append(" VALUES(?, ?, ?, ?) ");
	  
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					Factura factura = facturas.get(index);
					
					ps.setInt(1, factura.getRecibo().getId());
					ps.setString(2, factura.getNumero());
					ps.setDate(3, new java.sql.Date(factura.getFecha().getTime()));
					ps.setDouble(4, factura.getMonto());
				}
				
				@Override
				public int getBatchSize() {
					return facturas.size();
				}
		});
	 }
}
