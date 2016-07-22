package main.java.com.paine.core.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Banco;

@Repository
public class BancoRepository extends JDBCRepository{
	
	public List<Banco> cargarBancos() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM bancos ");
		
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			Banco banco = new Banco();
			
			banco.setId(rs.getInt("id"));
			banco.setCodigo(rs.getInt("codigo"));
			banco.setNombre(rs.getString("nombre"));
			banco.setAbreviatura(rs.getString("abreviatura"));
			
			return banco;
		});
	}
	
	public List<Banco> cargarBancosDeposito() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM bancos WHERE paine = 'SI'");
		
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			Banco banco = new Banco();
			
			banco.setId(rs.getInt("id"));
			banco.setCodigo(rs.getInt("codigo"));
			banco.setNombre(rs.getString("nombre"));
			banco.setAbreviatura(rs.getString("abreviatura"));
			
			return banco;
		});
	}
}
