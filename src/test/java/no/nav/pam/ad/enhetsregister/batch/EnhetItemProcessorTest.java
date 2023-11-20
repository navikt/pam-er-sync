package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderAdresse;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderNaringskode;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderOrgform;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


public class EnhetItemProcessorTest {

    @Test
    public void should_map_underenhet_properly() {

        EnhetItemProcessor processor = new EnhetItemProcessor();

        ReaderEnhet input = new ReaderEnhet();
        input.navn = "Test 1";
        input.antallAnsatte = 15;
        input.organisasjonsnummer = "56789";
        input.overordnetEnhet = "12345";
        ReaderOrgform orgform = new ReaderOrgform();
        orgform.kode = "BEDR";
        input.organisasjonsform = orgform;

        ReaderNaringskode kode1 = new ReaderNaringskode();
        kode1.beskrivelse = "Butikkhandel";
        kode1.kode = "47.111";
        ReaderNaringskode kode2 = new ReaderNaringskode();
        kode2.beskrivelse = "Produksjon av ferdigmat";
        kode2.kode = "10.850";
        input.naeringskode1 = kode1;
        input.naeringskode2 = kode2;

        ReaderAdresse readerAdresse = new ReaderAdresse();
        readerAdresse.adresse = Arrays.asList("Gate 1");
        readerAdresse.postnummer = "0576";
        readerAdresse.poststed = "Oslo";
        readerAdresse.land = "Norge";
        input.beliggenhetsadresse = readerAdresse;


        Enhet jsonEnhet = processor.process(input);
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(jsonEnhet.getNavn()).isEqualTo(input.navn);
        softAssert.assertThat(jsonEnhet.getOrganisasjonsnummer()).isEqualTo(input.organisasjonsnummer);
        softAssert.assertThat(jsonEnhet.getOverordnetEnhet()).isEqualTo(input.overordnetEnhet);
        softAssert.assertThat(jsonEnhet.getAntallAnsatte()).isEqualTo(input.antallAnsatte);
        softAssert.assertThat(jsonEnhet.getOrganisasjonsform()).isEqualTo(input.organisasjonsform.kode);

        softAssert.assertThat(jsonEnhet.getAdresse().getAdresse()).isEqualTo("Gate 1");
        softAssert.assertThat(jsonEnhet.getAdresse().getPoststed()).isEqualTo(input.beliggenhetsadresse.poststed);
        softAssert.assertThat(jsonEnhet.getAdresse().getLand()).isEqualTo(input.beliggenhetsadresse.land);
        softAssert.assertThat(jsonEnhet.getAdresse().getPostnummer()).isEqualTo(input.beliggenhetsadresse.postnummer);

        softAssert.assertThat(jsonEnhet.getNaringskoder().size()).isEqualTo(2);
        softAssert.assertThat(jsonEnhet.getNaringskoder().stream().anyMatch(n -> n.getKode().equals(input.naeringskode1.kode))).isTrue();
        softAssert.assertThat(jsonEnhet.getNaringskoder().stream().anyMatch(n -> n.getKode().equals(input.naeringskode2.kode))).isTrue();
        softAssert.assertAll();
    }
}
