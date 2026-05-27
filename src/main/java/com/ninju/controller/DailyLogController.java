package com.ninju.controller;

import com.ninju.bo.DailyLogBO;
import com.ninju.dto.DailyLogRequestDTO;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/daily-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class DailyLogController {

    @Inject
    DailyLogBO dailyLogBO;

    @GET
    public Response listMine(@Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = principal.getClaim("userId");
            return Response.ok(dailyLogBO.listByUser(userId, principal.getName())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/refeicao")
    public Response registrarRefeicao(DailyLogRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = principal.getClaim("userId");
            return Response.ok(dailyLogBO.registrarRefeicao(userId, dto, principal.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/treino")
    public Response registrarTreino(DailyLogRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal principal = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = principal.getClaim("userId");
            return Response.ok(dailyLogBO.registrarTreino(userId, dto, principal.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
