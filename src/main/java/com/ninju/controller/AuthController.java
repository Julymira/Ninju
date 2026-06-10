package com.ninju.controller;

import com.ninju.bo.AuthBO;
import com.ninju.bo.UserBO;
import com.ninju.dto.ChangePasswordDTO;
import com.ninju.dto.LoginRequestDTO;
import com.ninju.dto.LoginResponseDTO;
import com.ninju.dto.UserGoalsDTO;
import com.ninju.dto.UserRequestDTO;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
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

    @PUT
    @Path("/me")
    @RolesAllowed({"ADMIN", "USER"})
    public Response updateMe(UserRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(principal.getClaim("userId").toString());
            dto.role = null;
            return Response.ok(userBO.update(userId, dto, principal.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/me/password")
    @RolesAllowed({"ADMIN", "USER"})
    public Response changePassword(ChangePasswordDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(principal.getClaim("userId").toString());
            userBO.changePassword(userId, dto, principal.getName());
            return Response.noContent().build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("erro", e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/me/goals")
    @RolesAllowed({"ADMIN", "USER"})
    public Response updateGoals(UserGoalsDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(principal.getClaim("userId").toString());
            return Response.ok(userBO.updateGoals(userId, dto, principal.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
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
