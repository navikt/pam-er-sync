package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderAdresse(@Nullable List<String> adresse,
                            @Nullable String postnummer,
                            @Nullable String poststed,
                            @Nullable String kommunenummer,
                            @Nullable String kommune,
                            @Nullable String landkode,
                            @Nullable String land) {
}
