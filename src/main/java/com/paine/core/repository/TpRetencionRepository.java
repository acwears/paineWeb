package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.TpRetencion;

@Repository
public class TpRetencionRepository extends JDBCRepository{
	
	 public void save(TpRetencion tpRetencion) {
		 
		 Map<String, Object> parameters = new HashMap<>();
		 parameters.put("id_recibo", tpRetencion.getRecibo().getId());
		 parameters.put("numero", tpRetencion.getNumero());
		 parameters.put("sucursal", tpRetencion.getSucursal());
		 parameters.put("importe", tpRetencion.getMonto());
		 parameters.put("anio", tpRetencion.getAnio());
		 
		 SimpleJdbcInsert simpleJdbcInsert = getSimpleJdbcInsert();
		 simpleJdbcInsert.withTableName("tp_retencion");
		 
		 simpleJdbcInsert.usingGeneratedKeyColumns("id");
		 Number reciboId = simpleJdbcInsert.executeAndReturnKey(parameters);
	 }
	 
	 public void save(List<TpRetencion> tpRetenciones) {
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO tp_retencion (id_recibo, numero, sucursal, importe, anio) ");
		 sb.append(" VALUES(?, ?, ?, ?, ?) ");
	  
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					TpRetencion tpRetencion = tpRetenciones.get(index);	
					
					ps.setInt(1, tpRetencion.getRecibo().getId());
					ps.setString(2, tpRetencion.getNumero());
					ps.setString(3, tpRetencion.getSucursal());
					ps.setDouble(4, tpRetencion.getMonto());
					ps.setInt(5, tpRetencion.getAnio());
				}
				
				@Override
				public int getBatchSize() {
					return tpRetenciones.size();
				}
		});		 
	 }
	 
	 public void delete(int idReciboDelete){
		 
		  StringBuilder sb = new StringBuilder();
		  
		  sb.append(" DELETE ");
		  sb.append(" FROM tp_retencion ");
		  sb.append(" WHERE id_recibo = ? ");
		  		  
		  Object[] params = new Object[]{idReciboDelete};
		  
		  getJdbcTemplate().update(sb.toString(), params);
	}
}
