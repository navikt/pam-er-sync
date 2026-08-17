package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Enhet(String organisasjonsnummer,
                    String navn,
                    @Nullable String organisasjonsform,
                    @Nullable Integer antallAnsatte,
                    @Nullable String overordnetEnhet,
                    @Nullable Adresse adresse,
                    List<Naringskode> naringskoder) {
}
