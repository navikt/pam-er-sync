package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderNaringskode(@Nullable String kode,
                                @Nullable String beskrivelse) {
}
