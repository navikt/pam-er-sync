package no.nav.pam.ad.enhetsregister.batch.model;

public class JsonAdresse {

    private String adresse;
    private String postnummer;
    private String poststed;
    private String kommunenummer;
    private String kommune;
    private String landkode;
    private String land;

    public JsonAdresse(String adresse, String postnummer, String poststed, String kommunenummer, String kommune, String landkode, String land) {
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

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public void setPoststed(String poststed) {
        this.poststed = poststed;
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public void setKommunenummer(String kommunenummer) {
        this.kommunenummer = kommunenummer;
    }

    public String getKommune() {
        return kommune;
    }

    public void setKommune(String kommune) {
        this.kommune = kommune;
    }

    public String getLandkode() {
        return landkode;
    }

    public void setLandkode(String landkode) {
        this.landkode = landkode;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }
}
