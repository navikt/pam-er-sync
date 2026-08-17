package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderOrgform(@Nullable String kode) {
}
