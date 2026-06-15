package com.ninju.controller;

import com.ninju.bo.DailyLogBO;
import com.ninju.dto.DailyLogEntryRequestDTO;
import com.ninju.dto.DailyLogRequestDTO;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.time.LocalDate;
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
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            return Response.ok(dailyLogBO.listByUser(userId, p.getName())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @GET
    @Path("/report")
    public Response report(@QueryParam("date") String dateStr, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            LocalDate date = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
            return Response.ok(dailyLogBO.getReport(userId, date, p.getName())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/entry")
    public Response addEntry(DailyLogEntryRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            return Response.status(Response.Status.CREATED)
                    .entity(dailyLogBO.addEntry(userId, dto, p.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/entry/{id}")
    public Response removeEntry(@PathParam("id") Long entryId, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            dailyLogBO.removeEntry(userId, entryId, p.getName());
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/refeicao")
    public Response registrarRefeicao(DailyLogRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            return Response.ok(dailyLogBO.registrarRefeicao(userId, dto, p.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/treino")
    public Response registrarTreino(DailyLogRequestDTO dto, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            return Response.ok(dailyLogBO.registrarTreino(userId, dto, p.getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
