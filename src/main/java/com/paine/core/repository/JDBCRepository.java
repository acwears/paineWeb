package main.java.com.paine.core.repository;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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
	
	protected <T >T queryForObjectNull(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
		List<T> resultList = getJdbcTemplate().query(sql, args, rowMapper);
		return getFirstFromList(resultList);
	}
	
	private <T> T getFirstFromList(List<T> resultList) {

		T result = null;

		if (resultList.size() == 1) {
			result = resultList.get(0);
		} else if (resultList.size() > 1) {
			throw new IllegalStateException("### Result has more than one value when expecting only one! ###");
		}

		return result;
	}	
}
