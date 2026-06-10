package com.ninju.controller;

import com.ninju.bo.AuthBO;
import com.ninju.bo.UserBO;
import com.ninju.dto.LoginRequestDTO;
import com.ninju.dto.LoginResponseDTO;
import com.ninju.dto.UserRequestDTO;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthBO authBO;

    @Inject
    UserBO userBO;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(LoginRequestDTO dto) {
        try {
            LoginResponseDTO response = authBO.login(dto);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(UserRequestDTO dto) {
        try {
            dto.role = "USER";
            return Response.status(Response.Status.CREATED)
                    .entity(userBO.create(dto, "self-register"))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        }
    }
}
