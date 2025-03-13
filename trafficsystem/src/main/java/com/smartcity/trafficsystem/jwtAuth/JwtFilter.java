package com.smartcity.trafficsystem.jwtAuth;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.smartcity.trafficsystem.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private JwtUtility jwtUtility;

	private CustomUserDetailsService userDetailsService;

	public JwtFilter(JwtUtility jwtUtility, CustomUserDetailsService userDetailsService) {
		this.jwtUtility = jwtUtility;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		String header = request.getHeader("Authorization");
		String email = null;
		String token = null;
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
			email = jwtUtility.getEmailFromToken(token);
			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				if (!(jwtUtility.isTokenExpired(token))) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							token, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				} else {
					System.out.println("Token is expired or user details not found.");
				}
			}
		}

		filterChain.doFilter(request, response);

	}
}
