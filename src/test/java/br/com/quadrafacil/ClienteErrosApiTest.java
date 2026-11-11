package br.com.quadrafacil;

import br.com.quadrafacil.cliente.repository.ClienteRepository;
import br.com.quadrafacil.reserva.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(ClienteErrosApiTest.FalhasController.class)
class ClienteErrosApiTest {
    @Autowired WebApplicationContext context;
    @Autowired ClienteRepository clientes;
    @Autowired ReservaRepository reservas;
    MockMvc mvc;

    @BeforeEach void preparar() {
        reservas.deleteAll(); clientes.deleteAll();
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test void criaConsultaListaEPreservaEmailNaoUnico() throws Exception {
        mvc.perform(get("/api/clientes")).andExpect(status().isOk()).andExpect(content().json("[]"));
        var result = mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"  João  \",\"email\":\" joao@example.com \"}"))
            .andExpect(status().isCreated()).andReturn();
        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);
        mvc.perform(get(location)).andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value("João")).andExpect(jsonPath("$.email").value("joao@example.com"));
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
            .content("{\"nome\":\"Outro\",\"email\":\"joao@example.com\"}"))
            .andExpect(status().isCreated());
        assertEquals(2, clientes.count());
        mvc.perform(get("/api/clientes")).andExpect(jsonPath("$.length()").value(2));
    }
    @Test void rejeitaEntradasInvalidasSemPersistir() throws Exception {
        for (String body : new String[]{"{}", "{", "null", "{\"nome\":\" \",\"email\":\"x\"}",
                "{\"nome\":\"João\",\"email\":\"joao@example.com\",\"senha\":\"segredo\"}",
                "{\"nome\":\""+"a".repeat(101)+"\",\"email\":\"joao@example.com\"}"}) {
            var response = mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").isString()).andExpect(jsonPath("$.campos").isArray()).andReturn();
            assertFalse(response.getResponse().getContentAsString().contains("stackTrace"));
        }
        assertEquals(0, clientes.count());
        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(jsonPath("$.campos[0].mensagem").value("O e-mail é obrigatório."));
    }
    @Test void ausenciasEFormatosUsamMesmoContrato() throws Exception {
        for (String path : new String[]{"/api/clientes/"+UUID.randomUUID(), "/api/quadras/"+UUID.randomUUID(), "/api/nao-existe"}) {
            mvc.perform(get(path)).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.campos").isArray());
        }
        mvc.perform(get("/api/clientes/invalido")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
        mvc.perform(delete("/api/clientes")).andExpect(status().isMethodNotAllowed()).andExpect(jsonPath("$.status").value(405));
        mvc.perform(post("/api/clientes").contentType(MediaType.TEXT_PLAIN).content("texto"))
            .andExpect(status().isUnsupportedMediaType()).andExpect(jsonPath("$.status").value(415));
    }
    @Test void conflitosEExcecoesNaoVazamDetalhesInternos() throws Exception {
        for (String path : new String[]{"/teste/conflito", "/teste/integridade"}) {
            var result = mvc.perform(get(path)).andExpect(status().isConflict()).andExpect(jsonPath("$.status").value(409)).andReturn();
            assertFalse(result.getResponse().getContentAsString().contains("SQL_SECRETO"));
        }
        var result = mvc.perform(get("/teste/falha")).andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.mensagem").value("Não foi possível concluir a solicitação.")).andReturn();
        assertFalse(result.getResponse().getContentAsString().contains("SQL_SECRETO"));
    }

    /** Rotas só existem neste contexto de teste; não são empacotadas na aplicação. */
    @RestController
    static class FalhasController {
        @GetMapping("/teste/conflito") void conflito() { throw new ResponseStatusException(HttpStatus.CONFLICT, "Horário ocupado."); }
        @GetMapping("/teste/integridade") void integridade() { throw new DataIntegrityViolationException("SQL_SECRETO"); }
        @GetMapping("/teste/falha") void falha() { throw new IllegalStateException("SQL_SECRETO"); }
    }
}
