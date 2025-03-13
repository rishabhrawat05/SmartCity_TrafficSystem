package com.smartcity.trafficsystem.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.smartcity.trafficsystem.dto.LoginRequest;
import com.smartcity.trafficsystem.dto.ResendOtpRequest;
import com.smartcity.trafficsystem.dto.UserDto;
import com.smartcity.trafficsystem.dto.VerifyRequest;
import com.smartcity.trafficsystem.model.User;
import com.smartcity.trafficsystem.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody UserDto userDto){
		return ResponseEntity.ok(userService.signUp(userDto));
	}
	
	@PostMapping("/verify/email")
	public ResponseEntity<?> verifyOtp(@RequestBody VerifyRequest verifyRequest){
		return ResponseEntity.ok(userService.verifyOtp(verifyRequest));
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
		return ResponseEntity.ok(userService.login(loginRequest));
	}
	@PostMapping("/resend/otp")
	public ResponseEntity<?> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest){
		return ResponseEntity.ok(userService.resendOtp(resendOtpRequest));
	}
	
	@GetMapping("/getAllUser")
	@PreAuthorize("hasRole('ADMIN')")
	public Page<User> getAllUser(@RequestParam (defaultValue = "0") int page, @RequestParam (defaultValue = "10") int size){
		Pageable pageable = PageRequest.of(page, size);
		return userService.getAllUser(pageable);
	}
}
