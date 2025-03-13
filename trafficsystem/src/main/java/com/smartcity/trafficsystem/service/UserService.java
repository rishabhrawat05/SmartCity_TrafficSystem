package com.smartcity.trafficsystem.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.smartcity.trafficsystem.config.PasswordEncoder;
import com.smartcity.trafficsystem.dto.LoginRequest;
import com.smartcity.trafficsystem.dto.LoginResponse;
import com.smartcity.trafficsystem.dto.ResendOtpRequest;
import com.smartcity.trafficsystem.dto.UserDto;
import com.smartcity.trafficsystem.dto.VerifyRequest;
import com.smartcity.trafficsystem.email.EmailUtility;
import com.smartcity.trafficsystem.exception.EmailNotVerifiedException;
import com.smartcity.trafficsystem.exception.UserAlreadyExistsException;
import com.smartcity.trafficsystem.exception.UserNotFoundException;
import com.smartcity.trafficsystem.jwtAuth.JwtUtility;
import com.smartcity.trafficsystem.model.Role;
import com.smartcity.trafficsystem.model.User;
import com.smartcity.trafficsystem.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;

	private JwtUtility jwtUtility;

	private PasswordEncoder passwordEncoder;

	private EmailUtility emailUtility;

	private AuthenticationManager authenticationManager;
	
	private CustomUserDetailsService userDetailsService;

	public UserService(UserRepository userRepository, JwtUtility jwtUtility, PasswordEncoder passwordEncoder,
			EmailUtility emailUtility, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService) {
		this.userRepository = userRepository;
		this.jwtUtility = jwtUtility;
		this.passwordEncoder = passwordEncoder;
		this.emailUtility = emailUtility;
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}

	public String signUp(UserDto userDto) {
		Optional<User> optUser = userRepository.findByEmail(userDto.getEmail());
		if (optUser.isPresent()) {
			throw new UserAlreadyExistsException("Sorry User Already Exists with the email" + userDto.getEmail());
		}
		User user = new User();
		user.setEmail(userDto.getEmail());
		String encodedPassword = passwordEncoder.bCryptPasswordEncoder().encode(userDto.getPassword());
		String otp = generateOtp();
		Set<Role> roleSet = new HashSet<>();
		Role role = new Role();
		role.setName("USER");
		roleSet.add(role);
		user.setOtp(otp);
		user.setPassword(encodedPassword);
		user.setRouteSuggestions(new ArrayList<>());
		user.setUserBehaviours(new ArrayList<>());
		user.setOtpGeneratedTime(LocalDateTime.now());
		user.setVerified(false);
		user.setRoleSet(roleSet);
		userRepository.save(user);
		emailUtility.sendEmail(user.getEmail(), "Email Verification", "Your OTP is:" + otp);
		return new String("User Signed Up Successfully!! Please Verify Your Email");
	}

	public LoginResponse login(LoginRequest loginRequest) {
		authenticate(loginRequest.getEmail(), loginRequest.getPassword());
		User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UserNotFoundException("User Not Found Exception with Email:" + loginRequest.getEmail()));
		if(!user.isVerified()) {
			throw new EmailNotVerifiedException("Email Not Verified!! Please Verify Your Email");
		}
		UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());		
		String token = jwtUtility.generateToken(userDetails);
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(token);
		return loginResponse;
	}

	public void authenticate(String email, String password) {
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
				email, password);
		try {
			authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Invalid Username or Password");
		}
	}

	public String verifyOtp(VerifyRequest verifyRequest) {
		Optional<User> optUser = userRepository.findByEmail(verifyRequest.getEmail());
		if (optUser.isEmpty()) {
			throw new UserNotFoundException("User Not found with email:" + verifyRequest.getEmail());
		}
		User user = optUser.get();
		if (user.getOtp().equals(verifyRequest.getOtp())
				&& user.getOtpGeneratedTime().plusMinutes(10).isAfter(LocalDateTime.now())) {
			user.setVerified(true);
			user.setOtp(null);
			userRepository.save(user);
			return new String("Email Verified Successfully");
		} else {
			throw new EmailNotVerifiedException("Email cannot be verified with otp:" + verifyRequest.getOtp());
		}
	}

	public String resendOtp(ResendOtpRequest resendOtpRequest) {
		Optional<User> optUser = userRepository.findByEmail(resendOtpRequest.getEmail());
		if (optUser.isEmpty()) {
			throw new UserNotFoundException("User Not Found with email:" + resendOtpRequest.getEmail());
		}
		User user = optUser.get();
		user.setOtp(generateOtp());
		user.setOtpGeneratedTime(LocalDateTime.now());
		userRepository.save(user);
		emailUtility.sendEmail(user.getEmail(), "Email Verification", "Your OTP is:" + user.getOtp());
		return new String("A new OTP has been sent to your email.");
	}

	public String generateOtp() {
		return String.valueOf(1000 + new Random().nextInt(9000));
	}

	public Page<User> getAllUser(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
}
