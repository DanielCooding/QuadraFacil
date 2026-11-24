package br.com.quadrafacil.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ReservaRequest(@NotNull Long clienteId, @NotNull Long quadraId,
                             @NotNull OffsetDateTime inicio, @NotNull OffsetDateTime fim) {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static ReservaRequest deJson(@JsonProperty("clienteId") Long clienteId,
            @JsonProperty("quadraId") Long quadraId, @JsonProperty("inicio") String inicio,
            @JsonProperty("fim") String fim) {
        return new ReservaRequest(clienteId, quadraId, interpretar(inicio), interpretar(fim));
    }

    private static OffsetDateTime interpretar(String dataHora) {
        return dataHora == null ? null : OffsetDateTime.parse(dataHora);
    }
}
