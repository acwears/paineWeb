package main.java.com.paine.core.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;

@Repository
public class TpChequeRepository extends JDBCRepository{

	public List<TpCheque> findAll(double numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_tipo_de_pago_cheque WHERE tp_ch_fk_rbo_nro = ? ");
		
		Object[] params = new Object[]{numRecibo};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			
			TpCheque tpCheque = new TpCheque();			
			int id = rs.getInt("tp_ch_id_pk");
			tpCheque.setId(id);
			
			/*tpCheque.setTp_ch_id_pk(rs.getDouble("tp_ch_id_pk"));*/
			
			int reciboID = rs.getInt("tp_ch_fk_rbo_nro");
			Recibo recibo = new Recibo();
			recibo.setId(reciboID);
			
			tpCheque.setRecibo(recibo);
			
			tpCheque.setId(rs.getInt("id"));
			tpCheque.setBanco(rs.getInt("id_banco"));
			tpCheque.setCuit(rs.getString("cuit"));
			tpCheque.setFechaDeposito(rs.getDate("fecha_deposito"));
			tpCheque.setMonto(rs.getDouble("importe"));
			tpCheque.setNumero(rs.getInt("numero"));
			////tpCheque.setRecibo(rs.getInt("id_recibo"));
			
			/*tpCheque.setTp_ch_nro(rs.getDouble("tp_ch_nro"));			
			tpCheque.setTp_ch_fecha_deposito(rs.getString("tp_ch_fecha_deposito"));
			tpCheque.setTp_ch_importe(rs.getDouble("tp_ch_importe"));
			tpCheque.setTp_ch_cuit(rs.getString("tp_ch_cuit"));
			tpCheque.setTp_ch_banco(rs.getString("tp_ch_banco"));*/
			
			return tpCheque;
		});
	}
	
	 public void save(List<TpCheque> tpCheques) {
		 
		 StringBuilder sb = new StringBuilder();
		 sb.append(" INSERT INTO tp_cheque (id_recibo, numero, importe, cuit, fecha_deposito, id_banco) ");
		 sb.append(" VALUES(?, ?, ?, ?, ?, ?) ");
	  
		 getJdbcTemplate().batchUpdate(sb.toString(), new BatchPreparedStatementSetter() {
			
				@Override
				public void setValues(PreparedStatement ps, int index) throws SQLException {
					
					TpCheque tpCheque = tpCheques.get(index);	
					
					ps.setInt(1, tpCheque.getRecibo().getId());
					ps.setInt(2, tpCheque.getNumero());
					ps.setDouble(3, tpCheque.getMonto());
					ps.setString(4, tpCheque.getCuit());
					ps.setDate(5, new java.sql.Date(tpCheque.getFechaDeposito().getTime()) );
					ps.setInt(6, tpCheque.getBanco().getId());
				}
				
				@Override
				public int getBatchSize() {
					return tpCheques.size();
				}
		});
	 }
	
}
