package br.com.quadrafacil;

import static org.junit.jupiter.api.Assertions.*;

import br.com.quadrafacil.entity.Cliente;
import br.com.quadrafacil.entity.EstadoReserva;
import br.com.quadrafacil.entity.HorarioFuncionamento;
import br.com.quadrafacil.entity.Quadra;
import br.com.quadrafacil.entity.Reserva;
import br.com.quadrafacil.repository.ClienteRepository;
import br.com.quadrafacil.repository.HorarioFuncionamentoRepository;
import br.com.quadrafacil.repository.QuadraRepository;
import br.com.quadrafacil.repository.ReservaRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservaApiTest {
    private static final String RESERVAS = "/api/reservas";
    private static final ZoneId BRASILIA = ZoneId.of("America/Sao_Paulo");

    @Value("${local.server.port}") int porta;
    @Autowired ReservaRepository reservas;
    @Autowired QuadraRepository quadras;
    @Autowired ClienteRepository clientes;
    @Autowired HorarioFuncionamentoRepository horarios;

    private final JsonMapper json = new JsonMapper();
    private final LocalDate data = LocalDate.now(BRASILIA).plusDays(7);
    private Cliente cliente;
    private Quadra quadra;

    @BeforeEach
    void preparar() {
        reservas.deleteAll();
        horarios.deleteAll();
        quadras.deleteAll();
        clientes.deleteAll();
        cliente = clientes.save(new Cliente("Cliente Teste", "cliente@example.com"));
        quadra = criarQuadra("Central", "99.99");
    }

    @Test
    void criaEConsultaReservaComPrecoContratado() throws Exception {
        var criada = enviar("POST", RESERVAS, pedido("18:00:00-03:00", "19:30:00-03:00"), 201);
        var resposta = json.readTree(criada.body());
        String local = criada.headers().firstValue("Location").orElseThrow();
        assertEquals(RESERVAS + "/" + resposta.get("id").asLong(), local);
        assertEquals(data + "T21:00:00Z", resposta.get("inicio").asText());
        assertEquals("CONFIRMADA", resposta.get("estado").asText());
        assertEquals(0, new BigDecimal("149.99").compareTo(new BigDecimal(resposta.get("valorTotal").asText())));

        enviar("PUT", "/api/quadras/" + quadra.getId(),
            """
            {"nome":"Central","modalidade":"Futebol","precoHora":200.00}
            """, 200);
        assertEquals(resposta, json.readTree(enviar("GET", local, null, 200).body()));
        assertEquals(new BigDecimal("149.99"), reservas.findById(resposta.get("id").asLong()).orElseThrow().getValorTotal());
    }

    @Test
    void recusaSobreposicoesEPermiteHorariosConsecutivosEOuOutraQuadra() throws Exception {
        reservar("18:00", "19:30");
        enviar("POST", RESERVAS, pedido(quadra.getId(), "17:30", "18:30"), 409);
        enviar("POST", RESERVAS, pedido(quadra.getId(), "19:00", "20:00"), 409);
        enviar("POST", RESERVAS, pedido(quadra.getId(), "17:30", "20:00"), 409);
        enviar("POST", RESERVAS, pedido(quadra.getId(), "18:00", "19:00"), 409);
        assertEquals(1, reservas.count());

        reservar("19:30", "20:30");
        var outra = criarQuadra("Outra", "120.00");
        enviar("POST", RESERVAS, pedido(outra.getId(), "18:00", "19:30"), 201);
        assertEquals(3, reservas.count());
    }

    @Test
    void somenteUmaDasTentativasSimultaneasPersiste() throws Exception {
        String corpo = pedido(quadra.getId(), "18:00", "19:00");
        var prontas = new CountDownLatch(2);
        var largada = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            Callable<Integer> tentativa = () -> {
                prontas.countDown();
                assertTrue(largada.await(5, TimeUnit.SECONDS));
                return requisicao("POST", RESERVAS, corpo).statusCode();
            };
            var primeira = executor.submit(tentativa);
            var segunda = executor.submit(tentativa);
            assertTrue(prontas.await(5, TimeUnit.SECONDS));
            largada.countDown();
            var codigos = List.of(primeira.get(20, TimeUnit.SECONDS), segunda.get(20, TimeUnit.SECONDS));
            assertEquals(List.of(201, 409), codigos.stream().sorted().toList());
        }
        assertEquals(1, reservas.count());
        assertEquals(EstadoReserva.CONFIRMADA, reservas.findAll().getFirst().getEstado());
    }

    @Test
    void cancelaSemExcluirEAgendaOrdenaEFiltra() throws Exception {
        String maisTarde = reservar("20:00", "21:00");
        String anterior = reservar("18:00", "19:00");
        var outra = criarQuadra("Outra", "120.00");
        enviar("POST", RESERVAS, pedido(outra.getId(), "18:00", "19:00"), 201);

        var agenda = json.readTree(enviar("GET", agenda(data), null, 200).body());
        assertEquals("America/Sao_Paulo", agenda.get("fuso").asText());
        assertEquals("08:00", agenda.get("funcionamento").get("abertura").asText());
        assertEquals(2, agenda.get("reservas").size());
        assertEquals(data + "T21:00:00Z", agenda.get("reservas").get(0).get("inicio").asText());
        assertEquals(cliente.getId().longValue(), agenda.get("reservas").get(0).get("clienteId").asLong());
        var diaFechado = json.readTree(enviar("GET", agenda(data.plusDays(1)), null, 200).body());
        assertTrue(diaFechado.get("funcionamento").isNull());
        assertEquals(0, diaFechado.get("reservas").size());

        var cancelada = json.readTree(enviar("POST", anterior + "/cancelamento", null, 200).body());
        assertEquals("CANCELADA", cancelada.get("estado").asText());
        assertEquals(cancelada, json.readTree(enviar("POST", anterior + "/cancelamento", null, 200).body()));
        assertEquals(cancelada, json.readTree(enviar("GET", anterior, null, 200).body()));
        assertEquals("CONFIRMADA", json.readTree(enviar("GET", maisTarde, null, 200).body()).get("estado").asText());
        var aposCancelar = json.readTree(enviar("GET", agenda(data), null, 200).body());
        assertEquals(1, aposCancelar.get("reservas").size());
        assertEquals(data + "T23:00:00Z", aposCancelar.get("reservas").get(0).get("inicio").asText());
        reservar("18:00", "19:00");
        assertEquals(4, reservas.count());
        assertEquals(EstadoReserva.CANCELADA, reservas.findById(cancelada.get("id").asLong()).orElseThrow().getEstado());
    }

    @Test
    void filtraPeloDiaDeBrasiliaQuandoOInicioEmUtcCaiNoDiaSeguinte() throws Exception {
        enviar("PUT", "/api/quadras/" + quadra.getId() + "/funcionamento",
            """
            [{"diaSemana":"%s","abertura":"08:00","fechamento":"23:30"}]
            """.formatted(data.getDayOfWeek()), 200);
        reservar("22:00", "23:00");

        var diaLocal = json.readTree(enviar("GET", agenda(data), null, 200).body());
        assertEquals(1, diaLocal.get("reservas").size());
        assertEquals(data.plusDays(1) + "T01:00:00Z", diaLocal.get("reservas").get(0).get("inicio").asText());
        var diaSeguinte = json.readTree(enviar("GET", agenda(data.plusDays(1)), null, 200).body());
        assertEquals(0, diaSeguinte.get("reservas").size());
    }

    @Test
    void recusaCancelarReservaJaIniciadaERepeteCancelamentoAntigo() throws Exception {
        Instant inicio = Instant.now().minusSeconds(60);
        Instant fim = inicio.plusSeconds(3600);
        var existente = reservas.save(new Reserva(cliente, quadra, inicio, fim, BigDecimal.ONE, EstadoReserva.CONFIRMADA));
        var cancelada = reservas.save(new Reserva(cliente, quadra, inicio, fim, BigDecimal.ONE, EstadoReserva.CANCELADA));
        enviar("POST", RESERVAS + "/" + existente.getId() + "/cancelamento", null, 409);
        var resposta = enviar("POST", RESERVAS + "/" + cancelada.getId() + "/cancelamento", null, 200);
        assertEquals("CANCELADA", json.readTree(resposta.body()).get("estado").asText());
        assertEquals(EstadoReserva.CONFIRMADA, reservas.findById(existente.getId()).orElseThrow().getEstado());
    }

    @Test
    void rejeitaEntradasInvalidasSemGravar() throws Exception {
        String valido = pedido(quadra.getId(), "18:00", "19:00");
        String inicioIso = data + "T18:00:00-03:00";
        long inicioEpoch = OffsetDateTime.parse(inicioIso).toEpochSecond();
        for (String invalido : List.of("{}", "{", valido.replace("-03:00", ""),
                valido.replace("\"" + inicioIso + "\"", Long.toString(inicioEpoch)),
                valido.replace("18:00:00", "18:00:01"), valido.replace("18:00:00", "18:15:00"),
                valido.replace("19:00:00", "18:30:00"), valido.replace("19:00:00", "17:00:00"),
                valido.replace("18:00:00", "07:30:00"), valido.replace(data.toString(), "2020-01-01"),
                valido.replace(data.toString(), data.plusDays(1).toString()),
                valido.replace("}", ",\"valorTotal\":1}"))) {
            enviar("POST", RESERVAS, invalido, 400);
        }
        enviar("POST", RESERVAS, valido.replace("\"clienteId\":" + cliente.getId(), "\"clienteId\":-1"), 404);
        enviar("POST", RESERVAS, pedido(-1L, "18:00", "19:00"), 404);

        var cara = criarQuadra("Cara", "9999999999.99");
        enviar("POST", RESERVAS, pedido(cara.getId(), "18:00", "19:30"), 400);
        enviar("PATCH", "/api/quadras/" + quadra.getId() + "/situacao", "{\"ativa\":false}", 200);
        enviar("POST", RESERVAS, valido, 409);
        enviar("GET", agenda(data), null, 200);
        enviar("GET", "/api/quadras/-1/agenda?data=" + data, null, 404);
        enviar("GET", "/api/quadras/" + quadra.getId() + "/agenda", null, 400);
        enviar("GET", "/api/quadras/" + quadra.getId() + "/agenda?data=2027-02-30", null, 400);
        enviar("GET", RESERVAS + "/invalido", null, 400);
        enviar("GET", RESERVAS + "/-1", null, 404);
        enviar("POST", RESERVAS + "/-1/cancelamento", null, 404);
        assertEquals(0, reservas.count());
    }

    private Quadra criarQuadra(String nome, String preco) {
        var cadastrada = quadras.save(new Quadra(nome, "Futebol", new BigDecimal(preco), true));
        horarios.save(new HorarioFuncionamento(cadastrada, data.getDayOfWeek(), LocalTime.of(8, 0), LocalTime.of(22, 0)));
        return cadastrada;
    }

    private String agenda(LocalDate dia) {
        return "/api/quadras/" + quadra.getId() + "/agenda?data=" + dia;
    }

    private String reservar(String inicio, String fim) throws Exception {
        return enviar("POST", RESERVAS, pedido(quadra.getId(), inicio, fim), 201)
            .headers().firstValue("Location").orElseThrow();
    }

    private String pedido(Long idQuadra, String inicio, String fim) {
        return pedido(idQuadra, inicio + ":00-03:00", fim + ":00-03:00", data);
    }

    private String pedido(String inicio, String fim) {
        return pedido(quadra.getId(), inicio, fim, data);
    }

    private String pedido(Long idQuadra, String inicio, String fim, LocalDate dia) {
        return """
            {"clienteId":%d,"quadraId":%d,"inicio":"%sT%s","fim":"%sT%s"}
            """.formatted(cliente.getId(), idQuadra, dia, inicio, dia, fim);
    }

    private HttpResponse<String> enviar(String metodo, String rota, String corpo, int status) throws Exception {
        var resposta = requisicao(metodo, rota, corpo);
        assertEquals(status, resposta.statusCode(), () -> metodo + " " + rota + " " + corpo + " -> " + resposta.body());
        return resposta;
    }

    private HttpResponse<String> requisicao(String metodo, String rota, String corpo) throws Exception {
        try (var client = HttpClient.newHttpClient()) {
            var body = corpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(corpo);
            var pedido = HttpRequest.newBuilder(URI.create("http://localhost:" + porta + rota))
                .timeout(Duration.ofSeconds(12)).header("Content-Type", "application/json").method(metodo, body).build();
            return client.send(pedido, HttpResponse.BodyHandlers.ofString());
        }
    }
}
