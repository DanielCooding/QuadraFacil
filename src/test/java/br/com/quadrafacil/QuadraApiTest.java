package br.com.quadrafacil;

import br.com.quadrafacil.cliente.entity.Cliente;
import br.com.quadrafacil.cliente.repository.ClienteRepository;
import br.com.quadrafacil.quadra.repository.QuadraRepository;
import br.com.quadrafacil.reserva.entity.*;
import br.com.quadrafacil.reserva.repository.ReservaRepository;
import br.com.quadrafacil.funcionamento.repository.HorarioFuncionamentoRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.net.URI;
import java.net.http.*;
import java.util.UUID;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class QuadraApiTest {
    @Value("${local.server.port}") int port;
    @Autowired QuadraRepository quadras;
    @Autowired ClienteRepository clientes;
    @Autowired ReservaRepository reservas;
    @Autowired HorarioFuncionamentoRepository horarios;
    final JsonMapper json = new JsonMapper();
    final String valido = "{\"nome\":\"  Central  \",\"modalidade\":\" Futebol \",\"precoHora\":120.00}";

    @BeforeEach void limpar() { reservas.deleteAll(); horarios.deleteAll(); quadras.deleteAll(); clientes.deleteAll(); }
    HttpResponse<String> request(String method, String path, String body) throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body))
                .build(), HttpResponse.BodyHandlers.ofString());
        }
    }
    String criar() throws Exception {
        var result = request("POST", "/api/quadras", valido);
        assertEquals(201, result.statusCode(), result.body());
        return result.headers().firstValue("Location").orElseThrow();
    }
    @Test void cadastroConsultaEListaUsamBanco() throws Exception {
        assertEquals("[]", request("GET", "/api/quadras", null).body());
        String path = criar();
        var r = request("GET", path, null);
        assertEquals(200, r.statusCode());
        var node = json.readTree(r.body());
        assertEquals("Central", node.get("nome").asText());
        assertEquals("Futebol", node.get("modalidade").asText());
        assertTrue(node.get("ativa").asBoolean());
        assertFalse(node.has("proprietarioId"));
        assertEquals(1, quadras.count());
        assertEquals(1, json.readTree(request("GET", "/api/quadras", null).body()).size());
    }
    @Test void editaDesativaEReativaPreservandoHistorico() throws Exception {
        String path = criar();
        UUID id = UUID.fromString(path.substring(path.lastIndexOf('/')+1));
        var cliente = clientes.save(new Cliente("João", "joao@example.com"));
        var reserva = reservas.save(new Reserva(cliente, quadras.findById(id).orElseThrow(),
            Instant.parse("2030-01-07T21:00:00Z"), Instant.parse("2030-01-07T22:00:00Z"),
            new BigDecimal("120.00"), EstadoReserva.CONFIRMADA));
        assertEquals(200, request("PATCH", path+"/situacao", "{\"ativa\":false}").statusCode());
        assertEquals(200, request("PATCH", path+"/situacao", "{\"ativa\":false}").statusCode());
        var edit = request("PUT", path, "{\"nome\":\"Nova\",\"modalidade\":\"Vôlei\",\"precoHora\":200.00}");
        assertEquals(200, edit.statusCode());
        assertFalse(json.readTree(edit.body()).get("ativa").asBoolean());
        assertEquals("Nova", quadras.findById(id).orElseThrow().getNome());
        assertEquals(new BigDecimal("120.00"), reservas.findById(reserva.getId()).orElseThrow().getValorTotal());
        assertEquals(1, reservas.count());
        assertEquals(1, json.readTree(request("GET", "/api/quadras", null).body()).size());
        assertEquals(200, request("PATCH", path+"/situacao", "{\"ativa\":true}").statusCode());
        assertTrue(quadras.findById(id).orElseThrow().isAtiva());
    }
    @Test void dadosInvalidosNaoGravamNemAlteram() throws Exception {
        for (String body : new String[]{"{}", "{", valido.replace("120.00", "0"), valido.replace("120.00", "-1"),
                valido.replace("120.00", "1.001"), valido.replace("  Central  ", " "), valido.replace(" Futebol ", " "),
                valido.replace("}", ",\"ativa\":false}")}) {
            assertEquals(400, request("POST", "/api/quadras", body).statusCode(), body);
        }
        assertEquals(0, quadras.count());
        String path = criar();
        assertEquals(400, request("PUT", path, valido.replace("120.00", "0")).statusCode());
        assertEquals(400, request("PATCH", path+"/situacao", "{}").statusCode());
        assertEquals(400, request("PATCH", path+"/situacao", "{\"ativa\":null}").statusCode());
        assertEquals("Central", json.readTree(request("GET", path, null).body()).get("nome").asText());
    }
    @Test void identificaRegistroAusenteEUuidInvalido() throws Exception {
        String path = "/api/quadras/"+UUID.randomUUID();
        assertEquals(404, request("GET", path, null).statusCode());
        assertEquals(404, request("PUT", path, valido).statusCode());
        assertEquals(404, request("PATCH", path+"/situacao", "{\"ativa\":false}").statusCode());
        var error = request("GET", "/api/quadras/invalido", null);
        assertEquals(400, error.statusCode());
        assertTrue(json.readTree(error.body()).has("mensagem"));
        assertFalse(error.body().contains("stackTrace"));
    }
}
