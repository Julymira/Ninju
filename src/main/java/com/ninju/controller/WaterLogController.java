package com.ninju.controller;

import com.ninju.bo.WaterLogBO;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.time.LocalDate;
import java.util.Map;

@Path("/water")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class WaterLogController {

    @Inject WaterLogBO waterLogBO;
    
    // GET /water — retorna o log do dia do usuário logado
    @GET
    public Response getTodayLog(@QueryParam("date") String dateStr, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            LocalDate date = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
            return Response.ok(waterLogBO.getTodayLog(userId, date, p.getName())).build();

        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }

    }

    // POST /water/add — adiciona uma quantidade (ex: 200ml)
    @POST
    @Path("/add")
    public Response addWater(@QueryParam("date") String dateStr, @QueryParam("amount") int amount, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            LocalDate date = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
            return Response.ok(waterLogBO.addWater(userId, date, amount, p.getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    // PUT /water/goal — atualiza a meta diária
    @PUT
    @Path("/goal")
    public Response updateGoal(@QueryParam("date") String dateStr, @QueryParam("goal") int goal, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            LocalDate date = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
            return Response.ok(waterLogBO.updateGoal(userId, date, goal, p.getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }
}

