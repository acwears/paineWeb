package main.java.com.paine.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import main.java.com.paine.core.model.AuthenticatedUser;
import main.java.com.paine.core.repository.UsuarioRepository;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	//
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AuthenticatedUser authenticatedUser = usuarioRepository.getAuthenticated(email);

		if (authenticatedUser == null) {
			throw new UsernameNotFoundException("No user found with username: " + email);
		}

		List<GrantedAuthority> authorities = getAuthorities(authenticatedUser);
		authenticatedUser.setAuthorities(authorities);

		return authenticatedUser;
	}

	private List<GrantedAuthority> getAuthorities(AuthenticatedUser authenticatedUser) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(authenticatedUser.getRole()));
		return authorities;	
	}
}