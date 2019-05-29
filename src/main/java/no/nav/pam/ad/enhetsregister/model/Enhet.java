package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enhet {

    private final String organisasjonsnummer;
    private final String navn;
    private final String organisasjonsform;
    private final Integer antallAnsatte;
    private final String overordnetEnhet;

    private final Adresse adresse;
    private final List<Naringskode> naringskoder = new ArrayList<>();


    public Enhet(String organisasjonsnummer, String navn, String organisasjonsform, Integer antallAnsatte, String overordnetEnhet, Adresse adresse) {
        this.organisasjonsnummer = organisasjonsnummer;
        this.navn = navn;
        this.organisasjonsform = organisasjonsform;
        this.antallAnsatte = antallAnsatte;
        this.overordnetEnhet = overordnetEnhet;
        this.adresse = adresse;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public String getOrganisasjonsform() {
        return organisasjonsform;
    }

    public Integer getAntallAnsatte() {
        return antallAnsatte;
    }

    public String getOverordnetEnhet() {
        return overordnetEnhet;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public List<Naringskode> getNaringskoder() {
        return naringskoder;
    }
}
