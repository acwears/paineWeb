package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Descuento;

@Repository
public class DescuentoRepository extends JDBCRepository {

	 public void save(List<Descuento> descuentos) {
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO descuento (id_recibo, porcentaje, descripcion) ");
		 sb.append(" VALUES(?, ?, ?) ");
	  
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					Descuento descuento = descuentos.get(index);
					
					ps.setInt(1, descuento.getRecibo().getId());
					ps.setDouble(2, descuento.getPorcentaje());
					ps.setString(3, descuento.getDescripcion());
				}
				
				@Override
				public int getBatchSize() {
					return descuentos.size();
				}
		});
	 }
	
	 public void delete(int idReciboDelete){
		 
		  StringBuilder sb = new StringBuilder();
		  
		  sb.append(" DELETE ");
		  sb.append(" FROM descuento ");
		  sb.append(" WHERE id_recibo = ? ");
		  		  
		  Object[] params = new Object[]{idReciboDelete};
		  
		  getJdbcTemplate().update(sb.toString(), params);
	}
}
