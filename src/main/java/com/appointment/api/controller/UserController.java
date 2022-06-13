package com.appointment.api.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import com.appointment.api.constants.Constants;
import com.appointment.api.exception.AuthException;
import com.appointment.api.exception.ResourceNotFoundException;
import com.appointment.api.repository.UserRepository;
import com.appointment.api.model.User;
import com.appointment.api.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/User")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserService userService;

	@Autowired

	@Operation(summary = "This is to fetch all users from the  Database", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "fetch  all users from database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })
	@SecurityRequirement(name = "bearerAuth")
	@GetMapping("/all")
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	User user;

	@Operation(summary = "This is to  login to system")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "login to the system", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody Map<String, Object> UserMap)
			throws javax.security.auth.message.AuthException {
		String email = (String) UserMap.get("email");
		String password = (String) UserMap.get("password");
		user = userService.validateUser(email, password);
		Map<String, Object> data = new HashMap<String, Object>();

		
		data.putAll(generateJWTToken(user));
	

		userRepository.setStatusForUser(1, user.getEmail());
		
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@Operation(summary = "This is to  logou from the system", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "logout from the system", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })
	@SecurityRequirement(name = "bearerAuth")
	@PostMapping("/logout")

	public String logout(HttpServletRequest request)
			throws javax.security.auth.message.AuthException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("User", user);
		
		String useremail = request.getAttribute("email").toString();
		Integer stat = userRepository.findstatusbyemail(useremail);
		if (stat == 1) {

			User userr = userRepository.findByEmailAddress(useremail);
			userRepository.setStatusForUser(0, userr.getEmail());

			data.put("User", userr);
			
			data.putAll(generateJWTToken(userr));
			return "Hello  " + userr.getFirstName() + " You Logged out successfully";
		} else {
			return "No user logged in";
		}
		
	}

	@Operation(summary = "This is to  signup  to  the system for new users")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "successfully", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@RequestBody User User) throws javax.security.auth.message.AuthException {
		User UserCreated = userService.registerUser(User);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("User", UserCreated);

		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	public String token;

	private Map<String, String> generateJWTToken(User user) {
		long timestamp = System.currentTimeMillis();

		token = Jwts.builder().signWith(SignatureAlgorithm.HS256,
				Constants.API_SECRET_KEY)
				.setIssuedAt(new Date(timestamp))
				.setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
				.claim("userId", user.getId())
				.claim("email", user.getEmail())
				.claim("firstName", user.getFirstName())
				.claim("lastName", user.getLastName())
				.claim("role", user.getRole())
			
				.compact();
		Map<String, String> map = new HashMap<>();
		map.put("token", token);
		return map;
	}


	@Operation(summary = "This is to  set role to  system  user", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Set role to a user by role id as follows, [0:client,1:agents,3:staff,4:admin]", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })
	@SecurityRequirement(name = "bearerAuth")
	@PutMapping("/setrole/{email}")
	public String updateuser(HttpServletRequest request,
			@PathVariable(value = "email") String email,
			@Valid @RequestBody User userDetails) {

		String role = request.getAttribute("role").toString();
		System.out.println("role: --------  " + role);
		int i = Integer.parseInt(role);
		if (i == 4) {

			User user = userRepository.findByEmailAddress(email);
			if (user == null) {
				throw new ResourceNotFoundException("User not found :: " + email);
			}

			user.setFirstName(userDetails.getFirstName() != null ? userDetails.getFirstName() : user.getFirstName());
			user.setLastName(userDetails.getLastName() != null ? userDetails.getLastName() : user.getLastName());
			user.setEmail(userDetails.getEmail() != null ? userDetails.getEmail() : user.getEmail());
			user.setPassword(userDetails.getPassword() != null ? userDetails.getPassword() : user.getPassword());
			user.setRole(userDetails.getRole());
			user.setstatus(userDetails.getstatus() != null ? userDetails.getstatus() : user.getstatus());
			final User updateappointmentser = userRepository.save(user);
			String activity = "Updated " + email + "'s role";
			//String useremail = request.getAttribute("email").toString();
		
			return "User role updated successfully";

		} else {

			throw new AuthException("Only system administrator can set user role :: ");
		}

	}

	@Operation(summary = "This is to update user", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "update user", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })
	@SecurityRequirement(name = "bearerAuth")

	@PutMapping("update/{email}")
	public String updateuserdata(HttpServletRequest request,
			@PathVariable(value = "email") String email,
			@Valid @RequestBody User userDetails) {

		String role = request.getAttribute("role").toString();
		
		int i = Integer.parseInt(role);
		if (i == 4) {

			User user = userRepository.findByEmailAddress(email);
			if (user == null) {
				throw new ResourceNotFoundException("User not found :: " + email);
			}

			user.setFirstName(userDetails.getFirstName() != null ? userDetails.getFirstName() : user.getFirstName());
			user.setLastName(userDetails.getLastName() != null ? userDetails.getLastName() : user.getLastName());
			user.setEmail(userDetails.getEmail() != null ? userDetails.getEmail() : user.getEmail());
			user.setPassword(userDetails.getPassword() != null ? userDetails.getPassword() : user.getPassword());
			
			user.setstatus(userDetails.getstatus() != null ? userDetails.getstatus() : user.getstatus());
			final User updateappointmentser = userRepository.save(user);


			return "User updated successfully";

		} else {

			throw new AuthException("Only system administrator can set user role :: ");
		}

	}

}
