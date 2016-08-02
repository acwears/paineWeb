package main.java.com.paine.core.repository;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.TipoDePago;

@Repository
public class TipoDePagoRepository extends JDBCRepository {
	
	public TipoDePago findOne(String tp) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tipo_de_pagos WHERE tipo_de_pago = ? ");
		Object[] params = new Object[]{tp};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {

			TipoDePago tipoPago = new TipoDePago();
			
			tipoPago.setId(rs.getInt("id"));
			tipoPago.setCodigo(rs.getString("codigo_de_pago"));
			tipoPago.setImputacion(rs.getString("imputacion"));
			tipoPago.setTipo(rs.getString("tipo_de_pago"));
			
			return tipoPago;
		});
	}
	
}
