package com.sachetan.services;

/**
 * @author rakib (ansaryfantastic@gmail.com
 */

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sachetan.domains.User;
import com.sachetan.repositories.UserRepository;

@Service
public class SachetanUserDetailsService implements UserDetailsService {
	private final UserRepository mUserRepository;

	@Autowired
	public SachetanUserDetailsService(UserRepository userRepository) {
		this.mUserRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = mUserRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username and/or password.");
		}

		return new UserRepositoryUserDetails(user);
	}

	private final static class UserRepositoryUserDetails extends User implements UserDetails {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2779417923223338090L;

		private UserRepositoryUserDetails(User user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return getRoles();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

	}

}
