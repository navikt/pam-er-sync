package no.nav.pam.ad.enhetsregister.batch.model;

import java.util.ArrayList;
import java.util.List;

public class JsonEnhet {

    private String organisasjonsnummer;
    private String navn;
    private String organisasjonsform;
    private String antallAnsatte;
    private String overordnetEnhet;
    private JsonAdresse postAdresse;
    private List<JsonNaringskode> naringskoder = new ArrayList<>();

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public void setOrganisasjonsnummer(String organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getOrganisasjonsform() {
        return organisasjonsform;
    }

    public void setOrganisasjonsform(String organisasjonsform) {
        this.organisasjonsform = organisasjonsform;
    }

    public String getAntallAnsatte() {
        return antallAnsatte;
    }

    public void setAntallAnsatte(String antallAnsatte) {
        this.antallAnsatte = antallAnsatte;
    }

    public String getOverordnetEnhet() {
        return overordnetEnhet;
    }

    public void setOverordnetEnhet(String overordnetEnhet) {
        this.overordnetEnhet = overordnetEnhet;
    }

    public JsonAdresse getPostAdresse() {
        return postAdresse;
    }

    public void setPostAdresse(JsonAdresse postAdresse) {
        this.postAdresse = postAdresse;
    }

    public List<JsonNaringskode> getNaringskoder() {
        return naringskoder;
    }

    public void setNaringskoder(List<JsonNaringskode> naringskoder) {
        this.naringskoder = naringskoder;
    }
}
