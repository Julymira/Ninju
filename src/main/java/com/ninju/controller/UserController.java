package com.ninju.controller;

import com.ninju.bo.UserBO;
import com.ninju.dto.UserRequestDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class UserController {

    @Inject
    UserBO userBO;

    @GET
    public Response listAll(@Context SecurityContext sc) {
        try {
            return Response.ok(userBO.listAll(sc.getUserPrincipal().getName())).build();
            
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            return Response.ok(userBO.findById(id, sc.getUserPrincipal().getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    public Response create(UserRequestDTO dto, @Context SecurityContext sc) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(userBO.create(dto, sc.getUserPrincipal().getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, UserRequestDTO dto, @Context SecurityContext sc) {
        try {
            return Response.ok(userBO.update(id, dto, sc.getUserPrincipal().getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            userBO.delete(id, sc.getUserPrincipal().getName());
            return Response.noContent().build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
