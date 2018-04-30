package no.nav.pam.ad.enhetsregister.model;

public class Naringskode {

    private final String kode;
    private final String beskrivelse;

    public Naringskode(String kode, String beskrivelse) {
        this.kode = kode;
        this.beskrivelse = beskrivelse;
    }

    public String getKode() {
        return kode;
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }
}
