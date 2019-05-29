package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReaderNaringskode {

    public String kode;
    public String beskrivelse;
}
