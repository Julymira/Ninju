package com.ninju.controller;

import com.ninju.bo.FoodBO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/foods")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class FoodController {

    @Inject
    FoodBO foodBO;

    @GET
    public Response listAll(@Context SecurityContext sc) {
        try {
            return Response.ok(foodBO.listAll(sc.getUserPrincipal().getName())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            return Response.ok(foodBO.findById(id, sc.getUserPrincipal().getName())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
