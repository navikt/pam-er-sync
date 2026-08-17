package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jspecify.annotations.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReaderEnhet(String organisasjonsnummer,
                          String navn,
                          @Nullable Integer antallAnsatte,
                          @Nullable String overordnetEnhet,
                          @Nullable ReaderOrgform organisasjonsform,
                          @Nullable ReaderNaringskode naeringskode1,
                          @Nullable ReaderNaringskode naeringskode2,
                          @Nullable ReaderNaringskode naeringskode3,
                          @Nullable ReaderAdresse beliggenhetsadresse,
                          @Nullable ReaderAdresse forretningsadresse) {
}
