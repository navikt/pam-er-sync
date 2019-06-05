package no.nav.pam.ad.enhetsregister.model.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReaderEnhet {

    public String organisasjonsnummer;
    public String navn;
    public Integer antallAnsatte;
    public String overordnetEnhet;
    public ReaderOrgform organisasjonsform;

    public ReaderNaringskode naeringskode1;
    public ReaderNaringskode naeringskode2;
    public ReaderNaringskode naeringskode3;

    //Underenhet
    public ReaderAdresse beliggenhetsadresse;

    //Enhet
    public ReaderAdresse forretningsadresse;
}
