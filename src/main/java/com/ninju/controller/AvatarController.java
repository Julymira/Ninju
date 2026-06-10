package com.ninju.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("/avatars")
@PermitAll
public class AvatarController {

    private static final String AVATARS_PATH = "META-INF/resources/images/avatars";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource(AVATARS_PATH);
            if (url == null) return Response.ok(Collections.emptyList()).build();

            File dir = new File(url.toURI());
            if (!dir.isDirectory()) return Response.ok(Collections.emptyList()).build();

            List<String> files = Arrays.stream(dir.listFiles())
                    .filter(f -> f.isFile() && !f.getName().startsWith("."))
                    .map(File::getName)
                    .sorted()
                    .collect(Collectors.toList());

            return Response.ok(files).build();
        } catch (Exception e) {
            return Response.ok(Collections.emptyList()).build();
        }
    }
}
