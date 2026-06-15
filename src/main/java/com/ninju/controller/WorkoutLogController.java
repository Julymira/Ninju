package com.ninju.controller;

import com.ninju.bo.WorkoutLogBO;
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

@Path("/workout-logs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"USER", "ADMIN"})
public class WorkoutLogController {

    @Inject WorkoutLogBO workoutLogBO;

    private Long userId(SecurityContext sc) {
        JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
        return Long.parseLong(p.getClaim("userId").toString());
    }

    private String email(SecurityContext sc) {
        return sc.getUserPrincipal().getName();
    }

    @GET
    public Response list(@QueryParam("date") String date, @Context SecurityContext sc) {
        try {
            LocalDate d = (date != null && !date.isBlank()) ? LocalDate.parse(date) : LocalDate.now();
            return Response.ok(workoutLogBO.listByDate(userId(sc), d, email(sc))).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("message", e.getMessage())).build();
        }
    }

    @POST
    public Response create(Map<String, Object> body, @Context SecurityContext sc) {
        try {
            return Response.ok(workoutLogBO.create(body, userId(sc))).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400).entity(Map.of("message", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("message", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            workoutLogBO.delete(id, userId(sc), email(sc));
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(404).entity(Map.of("message", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(403).entity(Map.of("message", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("message", e.getMessage())).build();
        }
    }
}
