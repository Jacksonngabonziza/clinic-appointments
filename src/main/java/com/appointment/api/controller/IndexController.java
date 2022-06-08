package com.appointment.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Hidden
@RestController
@RequestMapping("/")
public class IndexController {

    @Operation(summary = "This is the root end point")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Home", content = {
                    @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "404", description = "NOt Available", content = @Content),
    // @ApiResponse(responseCode = "403", description = "Forbidden, Authorization
    // token must be provided", content = @Content)
    })

    @GetMapping("/")
    public String index() {
        Logger log = LoggerFactory.getLogger(IndexController.class);
        log.debug("main class running");

        return "<body style=text-align:center><h1 >Welcome to the clinic appointment api</h1>";

    }
}
