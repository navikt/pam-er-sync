package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReaderAdresse {

    public List<String> adresse;
    public String postnummer;
    public String poststed;
    public String kommunenummer;
    public String kommune;
    public String landkode;
    public String land;
}
