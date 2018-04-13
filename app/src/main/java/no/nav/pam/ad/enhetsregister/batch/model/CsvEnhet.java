package no.nav.pam.ad.enhetsregister.batch.model;

/**
 * Represents both Hovedenhet(Org) and Underenhet(Virksomhet)
 */
public class CsvEnhet {

    private String organisasjonsnummer;
    private String navn;
    private String organisasjonsform;
    private String antallAnsatte;
    private String overordnetEnhet;

    //Hovedenhet:forretningsadresse
    //Underenhet:beliggenhetsadresse
    private String adresse;
    private String postnummer;
    private String poststed;
    private String kommunenummer;
    private String kommune;
    private String landkode;
    private String land;

    //postadresse
    private String postadresse_adresse;
    private String postadresse_postnummer;
    private String postadresse_poststed;
    private String postadresse_kommunenummer;
    private String postadresse_kommune;
    private String postadresse_landkode;
    private String postadresse_land;

    private String naeringskode1_kode;
    private String naeringskode1_beskrivelse;
    private String naeringskode2_kode;
    private String naeringskode2_beskrivelse;
    private String naeringskode3_kode;
    private String naeringskode3_beskrivelse;

    public CsvEnhet() {
    }

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

    public String getNaeringskode1_kode() {
        return naeringskode1_kode;
    }

    public void setNaeringskode1_kode(String naeringskode1_kode) {
        this.naeringskode1_kode = naeringskode1_kode;
    }

    public String getNaeringskode1_beskrivelse() {
        return naeringskode1_beskrivelse;
    }

    public void setNaeringskode1_beskrivelse(String naeringskode1_beskrivelse) {
        this.naeringskode1_beskrivelse = naeringskode1_beskrivelse;
    }

    public String getNaeringskode2_kode() {
        return naeringskode2_kode;
    }

    public void setNaeringskode2_kode(String naeringskode2_kode) {
        this.naeringskode2_kode = naeringskode2_kode;
    }

    public String getNaeringskode2_beskrivelse() {
        return naeringskode2_beskrivelse;
    }

    public void setNaeringskode2_beskrivelse(String naeringskode2_beskrivelse) {
        this.naeringskode2_beskrivelse = naeringskode2_beskrivelse;
    }

    public String getNaeringskode3_kode() {
        return naeringskode3_kode;
    }

    public void setNaeringskode3_kode(String naeringskode3_kode) {
        this.naeringskode3_kode = naeringskode3_kode;
    }

    public String getNaeringskode3_beskrivelse() {
        return naeringskode3_beskrivelse;
    }

    public void setNaeringskode3_beskrivelse(String naeringskode3_beskrivelse) {
        this.naeringskode3_beskrivelse = naeringskode3_beskrivelse;
    }
}
