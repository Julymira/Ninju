package com.ninju.controller;

import com.ninju.bo.WorkoutBO;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/workouts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class WorkoutController {

    @Inject WorkoutBO workoutBO;

    @GET
    public Response listAll(@Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            return Response.ok(workoutBO.listVisible(userId, p.getName())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            return Response.ok(workoutBO.findById(id, sc.getUserPrincipal().getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    public Response create(Map<String, Object> body, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            boolean isAdmin = sc.isUserInRole("ADMIN");
            return Response.status(Response.Status.CREATED)
                    .entity(workoutBO.create(body, userId, isAdmin, p.getName()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            JWTCallerPrincipal p = (JWTCallerPrincipal) sc.getUserPrincipal();
            Long userId = Long.parseLong(p.getClaim("userId").toString());
            boolean isAdmin = sc.isUserInRole("ADMIN");
            workoutBO.delete(id, userId, isAdmin, p.getName());
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
