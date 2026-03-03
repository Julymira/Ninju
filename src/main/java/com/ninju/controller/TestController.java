package com.ninju.controller;

import com.ninju.dao.UserDao;
import com.ninju.model.User;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/teste") // O endereço da URL
public class TestController {

    @Inject // O Quarkus injeta o DAO aqui automaticamente
    UserDao userDao;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String testarBanco() {
        try {
            // Criando um usuário de mentira para testar
            User user = new User();
            user.setName("Julyana Mira");
            user.setEmail("julyana@teste.com");
            user.setPassword("123456");
            user.setRole("ADMIN");

            // Mandando o DAO salvar no banco
            userDao.save(user);

            return "Sucesso! Usuário " + user.getName() + " salvo no banco de dados do Ninju!";
        } catch (Exception e) {
            return "Deu erro: " + e.getMessage();
        }
    }
}
