package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.TpDeposito;

@Repository
public class TpDepositoRepository extends JDBCRepository{
	public List<TpDeposito> findAll(int numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tp_deposito WHERE id_recibo = ? ");
		
		Object[] params = new Object[]{numRecibo};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			TpDeposito tpDeposito = new TpDeposito();
			
			tpDeposito.setBanco(rs.getInt("banco"));
			tpDeposito.setMonto(rs.getDouble("importe"));
			//tpDeposito.setNumero(rs.getInt("numero"));
			tpDeposito.setFecha(rs.getDate("fecha"));
			tpDeposito.setId(rs.getInt("id"));
			////tpDeposito.setRecibo(rs.getInt("id_recibo"));
			
			/*tpDeposito.setTp_dep_id_pk(rs.getDouble("tp_dep_id_pk"));
			tpDeposito.setTp_dep_fk_rbo_nro(rs.getDouble("tp_dep_fk_rbo_nro"));
			tpDeposito.setTp_dep_nro(rs.getDouble("tp_dep_nro"));
			tpDeposito.setTp_dep_banco(rs.getString("tp_dep_banco"));
			tpDeposito.setTp_dep_importe(rs.getDouble("tp_importe"));*/
			
			return tpDeposito;
		});
	}
	
	 public void save(List<TpDeposito> tpDepositos) {	 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO tp_deposito (id_recibo, id_banco, importe, fecha) ");
		 sb.append(" VALUES(?, ?, ?, ?) ");
	  
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					TpDeposito tpDeposito = tpDepositos.get(index);
					
					ps.setInt(1, tpDeposito.getRecibo().getId());
					ps.setInt(2, tpDeposito.getBanco().getId());
					ps.setDouble(3, tpDeposito.getMonto());
					ps.setDate(4, new java.sql.Date(tpDeposito.getFecha().getTime()));
				}
				
				@Override
				public int getBatchSize() {
					return tpDepositos.size();
				}
		});
	 }	
}
