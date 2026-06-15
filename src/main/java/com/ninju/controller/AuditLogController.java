package com.ninju.controller;

import com.ninju.dao.AuditLogDao;
import com.ninju.dto.AuditLogDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/audit-logs")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AuditLogController {

    @Inject
    AuditLogDao auditLogDao;

    @GET
    public Response list() {
        List<AuditLogDTO> logs = auditLogDao.findAll().stream()
            .map(a -> new AuditLogDTO(a.getId(), a.getActionExecuted(), a.getExecutedBy(), a.getExecutionTime()))
            .collect(Collectors.toList());
        return Response.ok(logs).build();
    }
}
