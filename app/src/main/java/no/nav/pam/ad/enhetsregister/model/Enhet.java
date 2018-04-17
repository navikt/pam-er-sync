package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enhet {

    private String organisasjonsnummer;
    private String navn;
    private String organisasjonsform;
    private Integer antallAnsatte;
    private String overordnetEnhet;

    private Adresse adresse;
    private Adresse postAdresse;
    private List<Naringskode> naringskoder = new ArrayList<>();


    public Enhet(String organisasjonsnummer, String navn, String organisasjonsform, Integer antallAnsatte, String overordnetEnhet, Adresse adresse, Adresse postAdresse) {
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

    public Adresse getPostAdresse() {
        return postAdresse;
    }

    public List<Naringskode> getNaringskoder() {
        return naringskoder;
    }
}
