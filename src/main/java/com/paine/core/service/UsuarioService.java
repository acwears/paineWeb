package main.java.com.paine.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.com.paine.core.model.Usuario;
import main.java.com.paine.core.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	/**
	 * Retorna todos los usuários del sistema
	 * 
	 * @return
	 */
	public List<Usuario> cargarUsuarios() {
		return usuarioRepository.cargarUsuarios();
	}

	/**
	 * Busca usuario por id
	 * 
	 * @param usuarioId
	 * @return
	 */
	public Usuario find(Integer usuarioId) {
		return usuarioRepository.find(usuarioId);
	}

	public void salvar(Usuario usuario) {
		usuarioRepository.salvar(usuario);
	}

	public void actualizar(Usuario usuario) {
		usuarioRepository.actualizar(usuario);
	}

}
