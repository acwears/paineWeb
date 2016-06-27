package main.java.com.paine.core.repository;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import main.java.com.paine.core.model.Cliente;
import main.java.com.paine.core.model.Descuento;
import main.java.com.paine.core.model.Factura;
import main.java.com.paine.core.model.Recibo;
import main.java.com.paine.core.model.TpCheque;
import main.java.com.paine.core.model.TpDeposito;
import main.java.com.paine.core.model.TpEff;
import main.java.com.paine.core.model.TpRetencion;

@Repository
public class ReciboRepository extends JDBCRepository {
	
	//****************************************************************************************
	//******************* traigo datos del RECIBO ********************************************
	
	public Recibo findOne(int id) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM recibo  ");
		sb.append(" WHERE recibo.id = ? ");
			
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {
			
			Recibo recibo = new Recibo();
				
			recibo.setId(rs.getInt("id"));
			recibo.setNumero(rs.getInt("nro_recibo"));
			recibo.setDescuento(rs.getDouble("descuento"));
			recibo.setFecha(rs.getDate("fecha"));
			recibo.setFechaProceso(rs.getDate("fecha_proceso"));
			recibo.setImporteSumaFacturas(rs.getDouble("importe_suma_facturas"));
			recibo.setImporteTotal(rs.getDouble("importe_total"));
			recibo.setObservaciones(rs.getString("observaciones"));
			recibo.setFechaProceso(rs.getDate("fecha_proceso"));
			
			return recibo;
		});
	}
	
	
	//********************************************************************************
	//******************* Agrego FACTURA al RECIBO
	
	public List<Factura> agregoFacturas(int id) {
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM factura  ");
		sb.append(" where id_recibo = ? ");
		
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
			
			Factura factura = new Factura();
			
			factura.setId(rs.getInt("id"));
			factura.setFecha(rs.getDate("fecha"));
			factura.setMonto(rs.getDouble("monto"));
			factura.setNumero(rs.getString("numero"));
			
			return factura;
		});
		
	}
	
	//**********************************************************************************
	//******************* Agrego tpCheque al RECIBO
	
	public List<TpCheque> agregoCheques(int id) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tp_cheque  ");
		sb.append(" where id_recibo = ? ");
		
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
			
			try {
			
			TpCheque tpCheque = new TpCheque();
			
			tpCheque.setId(rs.getInt("id"));
			tpCheque.setBanco(rs.getInt("id_banco"));
			tpCheque.setCuit(rs.getString("cuit"));
			tpCheque.setFechaDeposito(rs.getDate("fecha_deposito"));
			tpCheque.setMonto(rs.getDouble("importe"));
			tpCheque.setNumero(rs.getInt("numero"));
			
			return tpCheque;
			
			} catch (EmptyResultDataAccessException e) {
				 return null;
			}
		});
	}
	
	
	
	//*******************************************************************************************
	//******************* Agrego tpDeposito al RECIBO
	
	public List<TpDeposito> agregoDepositos(int id) {
		//Recibo recibo = new Recibo();
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tp_deposito  ");
		sb.append(" where id_recibo = ? ");
		
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
					
			TpDeposito tpDeposito = new TpDeposito();
			
			tpDeposito.setId(rs.getInt("id"));
			tpDeposito.setBanco(rs.getInt("id_banco"));
			tpDeposito.setMonto(rs.getDouble("importe"));
			tpDeposito.setFecha(rs.getDate("fecha"));
			
			return tpDeposito;
		});
	}

	
	
	//**************************************************************************************
	//******************* Agrego tpRetencion al RECIBO
	
	public List<TpRetencion> agregoRetencion(int id) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT * FROM tp_retencion  ");
			sb.append(" where id_recibo = ? ");
			
			Object[] params = new Object[]{id};
	
			return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
						
				TpRetencion tpRetencion = new TpRetencion();
				
				tpRetencion.setId(rs.getInt("id"));
				tpRetencion.setNumero(rs.getString("numero"));
				tpRetencion.setSucursal(rs.getString("sucursal"));
				tpRetencion.setMonto(rs.getDouble("importe"));
				tpRetencion.setAnio(rs.getInt("anio"));
				
				return tpRetencion;
			});
		} catch (EmptyResultDataAccessException e) {
			 return null;
		}
	}
	
	
	
	//************************************************************************************
	//******************* Agrego tpEff al RECIBO
	
	public TpEff agregoEff(int id) {
		try {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tp_eff  ");
		sb.append(" where id_recibo = ? ");
		
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {
			
			
			
			TpEff tpEff = new TpEff();
			
			tpEff.setId(rs.getInt("id"));
			tpEff.setMonto(rs.getDouble("importe"));
						
			return tpEff;
			
			
		});
		} catch (EmptyResultDataAccessException e) {
			 return null;
		}
	}
	
	
	//****************************************************************************************
	//******************* Agrego CLIENTE al RECIBO
	public Cliente agregoCliente(int id_cliente) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(" SELECT cl.* FROM clientes cl ");
		sb.append(" inner join recibo re on re.id_cliente = cl.id ");
		sb.append(" where re.id = ? ");
		
		Object[] params = new Object[]{id_cliente};
		
		return getJdbcTemplate().queryForObject(sb.toString(), params, (rs, rowNum) -> {
					
			Cliente cliente = new Cliente();
			
			cliente.setId(rs.getInt("id"));
			cliente.setNumeroCliente(rs.getInt("nro_cliente"));
			cliente.setNombre(rs.getString("nombre"));
			
			return cliente;
		});	
	}
	
	//****************************************************************************************
	//******************* Agrego DESCUENTO al RECIBO	
	public List<Descuento> agregoDescuento(int id) {
	
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM descuento  ");
		sb.append(" where id_recibo = ? ");
		
		Object[] params = new Object[]{id};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
					
			Descuento descuento = new Descuento();
			
			descuento.setId(rs.getInt("id"));
			descuento.setPorcentaje(rs.getDouble("porcentaje"));
			descuento.setDescripcion(rs.getString("descripcion"));
			
			return descuento;
		});
	}
	

	
	//------------------------------------------------------------------------------------------------------
	public List<Recibo> findAll() {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_recibo ");
		
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {

			Recibo recibo = new Recibo();
			recibo.setId(rs.getInt("id"));
			recibo.setFecha(rs.getDate("name"));
			
			return recibo;
		});
	}	
	
	public List<Recibo> findAll(String numRecibo) {

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM tbl_recibo WHERE rbo_nro_pk = ? ");
		
		Object[] params = new Object[]{numRecibo};
		
		return getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {

			Recibo recibo = new Recibo();
			recibo.setId(rs.getInt("id"));
			//recibo.setName(rs.getString("name"));
			
			return recibo;
		});
	}		
	
	public void delete(int id) {
		  
		  StringBuilder sb = new StringBuilder();
		  sb.append(" DELETE FROM recibo_store WHERE id = ? ");
		  
		  Object[] params = new Object[]{id};
		  
		  getJdbcTemplate().update(sb.toString(), params);
		 }
		 
	 public void update(Recibo recibo) {
	  
	  StringBuilder sb = new StringBuilder();
	  sb.append(" UPDATE recibo_store ");
	  sb.append(" SET name = ?, ");
	  sb.append(" age = ?, ");
	  sb.append(" description = ? ");
	  sb.append(" WHERE id = ? ");
	  
	  Object[] params = new Object[]{recibo.getFecha(), 14, "cabeza", recibo.getId()};
	  
	  getJdbcTemplate().update(sb.toString(), params);
	 } 
	
	 public void modify(Recibo recibo) {
		 //java.util.Date fech;
		 //fech = new java.sql.Date(recibo.getFechaProceso().getTime());
		 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		 
		  StringBuilder sb = new StringBuilder();
		  sb.append(" UPDATE recibo ");
		  sb.append(" SET nro_recibo = ?, ");
		  sb.append(" id_cliente = ?, ");
		  sb.append(" fecha = ?, ");
		  sb.append(" descuento = ?, ");
		  sb.append(" importe_suma_facturas = ?, ");
		  sb.append(" importe_total = ?, ");
		  sb.append(" observaciones = ?, ");
		  sb.append(" id_usuario = ?, ");
		  sb.append(" fecha_proceso = ? ");
		  sb.append(" WHERE id = ? ");
		  
		  //recibo.getFecha()
		  
		  Object[] params = new Object[]{recibo.getNumero(), recibo.getCliente().getId(), recibo.getFecha(), recibo.getDescuento(), recibo.getImporteSumaFacturas(), recibo.getImporteTotal(), recibo.getObservaciones(), 26561556, recibo.getFechaProceso(), recibo.getId()};
		  
		  getJdbcTemplate().update(sb.toString(), params);
	 }
	 
	 public void modifyExportados(List<Recibo> recibo) {
		 
		  StringBuilder sb = new StringBuilder();
		  sb.append(" UPDATE recibo ");
		  sb.append(" SET exportado = ? ");
		  sb.append(" WHERE id = ? ");
		  
		  Object[] params = new Object[]{"SI", recibo.get(0).getId()}; //esto esta mal
		  
		  getJdbcTemplate().update(sb.toString(), params);
	 }
	 
	 public List<Recibo> recibosParaExportar(){
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * FROM recibo ");
		sb.append(" WHERE exportado = 'NO' ");
				
		return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {
			
			Recibo recibos = new Recibo();
			
			recibos.setId(rs.getInt("id"));
			recibos.setNumero(rs.getInt("nro_recibo"));
			recibos.setDescuento(rs.getDouble("descuento"));
			recibos.setFecha(rs.getDate("fecha"));
			recibos.setFechaProceso(rs.getDate("fecha_proceso"));
			recibos.setImporteSumaFacturas(rs.getDouble("importe_suma_facturas"));
			recibos.setImporteTotal(rs.getDouble("importe_total"));
			recibos.setObservaciones(rs.getString("observaciones"));
			recibos.setFechaProceso(rs.getDate("fecha_proceso"));
			
			Cliente cliente = new Cliente();
			cliente.setId(rs.getInt("id"));
			cliente.setNumeroCliente(rs.getInt("nro_cliente"));
			cliente.setNombre(rs.getString("nombre"));
			
			recibos.setCliente(cliente);
			
			return recibos;
		});
	}
	 
	 public void save(Recibo recibo) {
		 
		 Map<String, Object> parameters = new HashMap<>();
		 parameters.put("nro_recibo", recibo.getNumero());
		 parameters.put("id_cliente", recibo.getCliente().getId());
		 parameters.put("fecha",new java.sql.Date(recibo.getFecha().getTime()));
		 parameters.put("descuento", recibo.getDescuento());
		 parameters.put("importe_suma_facturas", recibo.getImporteSumaFacturas());
		 parameters.put("importe_total", recibo.getImporteTotal());
		 parameters.put("observaciones", recibo.getObservaciones());
		 //parameters.put("id_usuario", recibo.getUsuarioCriador().getId());
		 parameters.put("id_usuario", recibo.getUsuario().getId());
		 parameters.put("fecha_proceso", new java.sql.Date(recibo.getFechaProceso().getTime()));
		 parameters.put("exportado", "NO");
		 
		 SimpleJdbcInsert simpleJdbcInsert = getSimpleJdbcInsert();
		 simpleJdbcInsert.withTableName("recibo");
		 simpleJdbcInsert.usingGeneratedKeyColumns("id");
		 Number reciboId = simpleJdbcInsert.executeAndReturnKey(parameters);
		 
		 recibo.setId(reciboId.intValue());
	 } 
	 
	 //LISTADO DE RECIBOS PARA SELECCIONAR EN EL MENU 'VER'
		public List<Recibo> listarRecibos() {

			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT re.*, cl.id as clienteId, cl.nro_cliente, cl.nombre  FROM clientes cl ");
			sb.append(" inner join recibo re on re.id_cliente = cl.id ");
					
			return getJdbcTemplate().query(sb.toString(), (rs, rowNum) -> {
				
				Recibo recibos = new Recibo();
				
				recibos.setId(rs.getInt("id"));
				recibos.setNumero(rs.getInt("nro_recibo"));
				recibos.setDescuento(rs.getDouble("descuento"));
				recibos.setFecha(rs.getDate("fecha"));
				recibos.setFechaProceso(rs.getDate("fecha_proceso"));
				recibos.setImporteSumaFacturas(rs.getDouble("importe_suma_facturas"));
				recibos.setImporteTotal(rs.getDouble("importe_total"));
				recibos.setObservaciones(rs.getString("observaciones"));
				recibos.setFechaProceso(rs.getDate("fecha_proceso"));
				
				Cliente cliente = new Cliente();
				cliente.setId(rs.getInt("id"));
				cliente.setNumeroCliente(rs.getInt("nro_cliente"));
				cliente.setNombre(rs.getString("nombre"));
				
				recibos.setCliente(cliente);
			
				return recibos;
			});
			
		}
	 
	 
	 
	 
		//******************************** esta funcion ya no se usa **************************************
		//******************* traigo datos del RECIBO y Agrego factura al RECIBO **************************
		/*public Recibo findOneOld(int id) {
		
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT * FROM recibo  ");
			sb.append(" INNER JOIN factura ON recibo.id = factura.id_recibo ");
			sb.append(" WHERE factura.id_recibo = ? ");
			
			Object[] params = new Object[]{id};
			
			getJdbcTemplate().query(sb.toString(), params, (rs, rowNum) -> {
				
				Recibo recibo = null;
				
				if (recibos.size() == 0){
					recibo = new Recibo();
					
					recibo.setId(rs.getInt("id"));
					recibo.setNumero(rs.getInt("nro_recibo"));
					recibo.setDescuento(rs.getDouble("descuento"));
					recibo.setFecha(rs.getDate("fecha"));
					recibo.setFechaProceso(rs.getDate("fecha_proceso"));
					recibo.setImporteSumaFacturas(rs.getDouble("importe_suma_facturas"));
					recibo.setImporteTotal(rs.getDouble("importe_total"));
					recibo.setObservaciones(rs.getString("observaciones"));
					recibo.setFechaProceso(rs.getDate("fecha_proceso"));
					
					idCliente = rs.getInt("id_cliente");
					
					recibos.add(recibo);

				}
				
				return recibo;
			});
			
			return recibos.get(0);
		}*/
		//******************* END traigo datos del RECIBO y Agrego factura al RECIBO
		//**********************************************************************
		//***********************************************************************************************************************************
	 
}
