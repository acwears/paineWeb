package main.java.com.paine.core.repository;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

public abstract class JDBCRepository {

	private DataSource dataSource;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	protected JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(this.dataSource);
	}
	
	protected SimpleJdbcInsert getSimpleJdbcInsert() {
		return new SimpleJdbcInsert(this.dataSource);
	}
}
