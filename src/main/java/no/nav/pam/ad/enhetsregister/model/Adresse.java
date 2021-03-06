package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Adresse {

    private final String adresse;
    private final String postnummer;
    private final String poststed;
    private final String kommunenummer;
    private final String kommune;
    private final String landkode;
    private final String land;

    public Adresse(String adresse, String postnummer, String poststed, String kommunenummer, String kommune, String landkode, String land) {
        this.adresse = adresse;
        this.postnummer = postnummer;
        this.poststed = poststed;
        this.kommunenummer = kommunenummer;
        this.kommune = kommune;
        this.landkode = landkode;
        this.land = land;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public String getKommune() {
        return kommune;
    }

    public String getLandkode() {
        return landkode;
    }

    public String getLand() {
        return land;
    }
}
