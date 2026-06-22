package com.ninju.controller;

import com.ninju.bo.UserPlanBO;
import io.smallrye.jwt.auth.principal.JWTCallerPrincipal;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.Map;

@Path("/user-plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
public class UserPlanController {

    @Inject UserPlanBO planBO;

    private JWTCallerPrincipal principal(SecurityContext sc) {
        return (JWTCallerPrincipal) sc.getUserPrincipal();
    }

    private Long userId(SecurityContext sc) {
        return Long.parseLong(principal(sc).getClaim("userId").toString());
    }

    @GET
    public Response listPlans(@Context SecurityContext sc) {
        try {
            return Response.ok(planBO.listPlans(userId(sc), principal(sc).getName())).build();

        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    public Response createPlan(Map<String, Object> body, @Context SecurityContext sc) {
        try {
            String name = (String) body.get("name");
            return Response.status(Response.Status.CREATED)
                    .entity(planBO.createPlan(userId(sc), name, principal(sc).getName()))
                    .build();

        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response renamePlan(@PathParam("id") Long id, Map<String, Object> body, @Context SecurityContext sc) {
        try {
            String name = (String) body.get("name");
            return Response.ok(planBO.renamePlan(id, userId(sc), name, principal(sc).getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deletePlan(@PathParam("id") Long id, @Context SecurityContext sc) {
        try {
            planBO.deletePlan(id, userId(sc), principal(sc).getName());
            return Response.noContent().build();

        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @POST
    @Path("/{id}/exercises")
    public Response addExercise(@PathParam("id") Long id, Map<String, Object> body, @Context SecurityContext sc) {
        try {
            return Response.ok(planBO.addExercise(id, userId(sc), body, principal(sc).getName())).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("erro", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }

    @DELETE
    @Path("/{id}/exercises/{exId}")
    public Response removeExercise(@PathParam("id") Long id, @PathParam("exId") Long exId, @Context SecurityContext sc) {
        try {
            return Response.ok(planBO.removeExercise(id, userId(sc), exId, principal(sc).getName())).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(Map.of("erro", e.getMessage())).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(Map.of("erro", e.getMessage())).build();
        } catch (Exception e) {
            return Response.serverError().entity(Map.of("erro", e.getMessage())).build();
        }
    }
}
