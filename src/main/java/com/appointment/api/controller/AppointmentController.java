package com.appointment.api.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.appointment.api.exception.AuthException;
import com.appointment.api.model.appointment;
import com.appointment.api.model.User;
import com.appointment.api.repository.AppointmentRepository;
import com.appointment.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1")
public class AppointmentController {
	@Autowired
	private AppointmentRepository appRepository;
	@Autowired
	UserService Uservice;

	User user;

	@Operation(summary = "create a new appoint ", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "add new appointment to the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@PostMapping("/requestapp")
	public String createaAppointment(@Valid @RequestBody appointment appointment) {

		Date d = appointment.getschedule_time();
		appointment app = appRepository.findAppointmentByDate(d);

		if (app == null) {
			appRepository.save(appointment);
			return "Appointment saved waiting for approval";
		} else {
			String day = d.toString();
			String dy = "";
			for (int i = 0; i < 10; i++) {
				dy = dy + day.charAt(i);

			}

			return "Doctor is busy on this day   "+ dy;
		}

	}

	@Operation(summary = "This is to fetch all appointments from the  Database", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "fetch all appointments from the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@GetMapping("/appointment")
	public List<appointment> getAllAppointments(HttpServletRequest request)
			throws javax.security.auth.message.AuthException {
		String role = request.getAttribute("role").toString();

		int i = Integer.parseInt(role);
		if (i == 4) {

			return appRepository.findAll();
		} else {
			throw new AuthException("Only admin and  can view appointments data :: ");
		}
	}

	@Operation(summary = "This is to fetch appointment by id from the  Database", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "fetch appointment by id from the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@GetMapping("/appointment/{id}")
	public ResponseEntity<appointment> getappointmentById(HttpServletRequest request,
			@PathVariable(value = "id") Long id)
			throws Exception {
		String role = request.getAttribute("role").toString();

		int i = Integer.parseInt(role);
		if (i == 4) {
			appointment app = appRepository.findById(id).orElseThrow(() -> new Exception("Appointment not found"));

			return ResponseEntity.ok().body(app);
		} else {

			throw new AuthException("Only admin and  can view appointment data :: ");

		}

	}

	@Operation(summary = "This is to the check the status of your appoint by entering phonenumber")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Appointment status", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@GetMapping("/checkappointment/phone/{phoneNumber}")
	public String getappointmentById(
			@PathVariable(value = "phoneNumber") String phoneNumber)
			throws Exception {

		appointment app = appRepository.findAppointmentByPhone(phoneNumber);
		if (app == null) {

			return "no appointment assigned to this phone number";

		} else {

			return app.getstatus();

		}

	}

	@Operation(summary = "This is to delelte  appointment from the  Database", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "delete  appointment from the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@DeleteMapping("/appointment/{id}")
	public Map<String, Boolean> deletapp(HttpServletRequest request, @PathVariable(value = "id") Long id)
			throws Exception {
		String role = request.getAttribute("role").toString();

		int i = Integer.parseInt(role);
		if (i == 4) {
			appointment app = appRepository.findById(id).orElseThrow(() -> new Exception("Appointment not found"));

			appRepository.delete(app);
			Map<String, Boolean> response = new HashMap<>();
			response.put("deleted", Boolean.TRUE);
			String activity = "deleted appointment: " + id;

			return response;
		} else {
			throw new AuthException("Only admin and  can delete appointment data :: ");
		}
	}

	@Operation(summary = "This is to approve an appointment only done by admin", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "approve  appointment", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@PutMapping("/appointment/{id}")
	public Map<String, Boolean> approve(HttpServletRequest request, @PathVariable(value = "id") Long id)
			throws Exception {
		String role = request.getAttribute("role").toString();

		int i = Integer.parseInt(role);
		Map<String, Boolean> response = new HashMap<>();
		if (i == 4) {
			appointment app = appRepository.findById(id).orElseThrow(() -> new Exception("Appointment not found"));

			if (app.getstatus().equals("pending")) {
				appRepository.approve("approved", id);

				response.put("Appointmenr approved", Boolean.TRUE);

			} else {

				response.put("Appointment is already approved", Boolean.TRUE);
			}

			return response;
		} else {
			throw new AuthException("Only admin and  can delete appointment data :: ");
		}
	}

	@Operation(summary = "This is to fetch all appointments from the  Database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "fetch all appointments from the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@GetMapping("/booked")
	public List<String> getAllbooked()

	{
		List<appointment> app = appRepository.findAll();
		List<String> booked = new ArrayList<>();
		String temp;
		for (int b = 0; b < app.size(); b++) {

			temp = app.get(b).getschedule_time().toString();
			String dy = "";
			for (int j = 0; j < 11; j++) {
				dy = dy + temp.charAt(j);

			}
			booked.add(dy);
		}
		return booked;
	}

	@Operation(summary = "This is to fetch all pending appointments from the  Database", security = @SecurityRequirement(name = "bearerAuth"))
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "fetch all pending appointments from the  Database", content = {
					@Content(mediaType = "application/json") }),
			@ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden, Authorization token must be provided", content = @Content) })

	@GetMapping("/appointment/pending")
	public List<appointment> getAllPending(HttpServletRequest request)
			throws javax.security.auth.message.AuthException {
		String role = request.getAttribute("role").toString();

		int i = Integer.parseInt(role);
		if (i == 4) {

			return appRepository.pendingapp("pending");
		} else {
			throw new AuthException("restricted privelages,only the admin is allowed to this ,please approach the asdmin:: ");
		}
	}

}
