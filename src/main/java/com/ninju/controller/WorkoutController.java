package com.ninju.controller;

import com.ninju.bo.WorkoutBO;
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

    @Inject
    WorkoutBO workoutBO;

    @GET
    public Response listAll(@Context SecurityContext sc) {
        try {
            return Response.ok(workoutBO.listAll(sc.getUserPrincipal().getName())).build();
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
}
