package main.java.com.paine.core.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.TpEff;

@Repository
public class TpEffRepository extends JDBCRepository {

	public TpEff findOne(double numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_tipo_de_pago_eff WHERE tp_eff_fk_rbo_nro = ? ");
		Object[] params = new Object[] { numRecibo };

		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			TpEff tpEff = new TpEff();

			tpEff.setId(rs.getInt("id"));
			// tpEff.setFecha(rs.getString("tp_eff_importe"));
			tpEff.setMonto(rs.getDouble("tp_eff_importe"));
			//// tpEff.setRecibo(rs.getInt("tp_eff_importe"));

			/*
			 * tpEff.setTp_eff_importe(rs.getDouble("tp_eff_importe"));
			 * tpEff.setTp_eff_nro_pk(rs.getDouble("tp_eff_id_pk"));
			 * tpEff.setTp_eff_fk_rbo_nro(rs.getDouble("tp_eff_fk_rbo_nro"));
			 * tpEff.setTp_eff_fecha(rs.getString("tp_eff_fecha"));
			 */

			return tpEff;
		});
	}

	public List<TpEff> find_TP_Eff(double numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(
				" SELECT tp_eff_importe FROM tbl_recibo INNER JOIN tbl_tipo_de_pago_eff ON tbl_recibo.rbo_nro_pk = tbl_tipo_de_pago_eff.tp_eff_fk_rbo_nro WHERE rbo_nro_pk = ? ");

		Object[] params = new Object[] { numRecibo };

		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			TpEff tpEff = new TpEff();

			// tpEff.setTp_eff_importe(rs.getDouble("tp_eff_importe"));

			return tpEff;
		});
	}

	public void save(TpEff tpEff) {

		StringBuilder sb = new StringBuilder();
		sb.append(" INSERT INTO tp_eff (id_recibo, importe) ");
		sb.append(" VALUES(?, ?) ");

		Object[] params = new Object[] { tpEff.getRecibo().getId(), tpEff.getMonto() };
		getJdbcTemplate().update(sb.toString(), params);
	}

}
