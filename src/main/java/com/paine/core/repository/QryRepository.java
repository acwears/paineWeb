package main.java.com.paine.core.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.QryVer;

@Repository
public class QryRepository extends JDBCRepository {

	public QryVer findOne(double id) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_recibo inner join tbl_factura on tbl_recibo.rbo_nro_pk = tbl_factura.fac_fk_rbo_nro where tbl_recibo.rbo_nro_pk = ? ");
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			QryVer qryVer = new QryVer();
			
			qryVer.setRbo_nro_pk(rs.getDouble("rbo_nro_pk"));
			qryVer.setRbo_fecha(rs.getString("rbo_fecha"));
			qryVer.setFac_nro_pk(rs.getDouble("fac_nro_pk"));
			qryVer.setFac_fecha(rs.getString("fac_fecha"));
			qryVer.setFac_monto(rs.getDouble("fac_monto"));
			
			return qryVer;
		});
	}
	
	public List<QryVer> findAll(double numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_recibo inner join tbl_factura on tbl_recibo.rbo_nro_pk = tbl_factura.fac_fk_rbo_nro where tbl_recibo.rbo_nro_pk = ? ");
		
		Object[] params = new Object[]{numRecibo};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			QryVer qryVer = new QryVer();
			
			qryVer.setRbo_nro_pk(rs.getDouble("rbo_nro_pk"));
			qryVer.setRbo_fecha(rs.getString("rbo_fecha"));
			qryVer.setFac_nro_pk(rs.getDouble("fac_nro_pk"));
			qryVer.setFac_fecha(rs.getString("fac_fecha"));
			qryVer.setFac_monto(rs.getDouble("fac_monto"));
			
			return qryVer;
		});
	}
}
