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

        ReaderOrgform orgform = new ReaderOrgform("BEDR");
        ReaderNaringskode kode1 = new ReaderNaringskode("47.111", "Butikkhandel");
        ReaderNaringskode kode2 = new ReaderNaringskode("10.850", "Produksjon av ferdigmat");
        ReaderAdresse readerAdresse = new ReaderAdresse(
                Arrays.asList("Gate 1"),
                "0576",
                "Oslo",
                null,
                null,
                null,
                "Norge"
        );
        ReaderEnhet input = new ReaderEnhet(
                "56789",
                "Test 1",
                15,
                "12345",
                orgform,
                kode1,
                kode2,
                null,
                readerAdresse,
                null
        );


        Enhet jsonEnhet = processor.process(input);
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(jsonEnhet.navn()).isEqualTo(input.navn());
        softAssert.assertThat(jsonEnhet.organisasjonsnummer()).isEqualTo(input.organisasjonsnummer());
        softAssert.assertThat(jsonEnhet.overordnetEnhet()).isEqualTo(input.overordnetEnhet());
        softAssert.assertThat(jsonEnhet.antallAnsatte()).isEqualTo(input.antallAnsatte());
        softAssert.assertThat(jsonEnhet.organisasjonsform()).isEqualTo(input.organisasjonsform().kode());

        softAssert.assertThat(jsonEnhet.adresse().adresse()).isEqualTo("Gate 1");
        softAssert.assertThat(jsonEnhet.adresse().poststed()).isEqualTo(input.beliggenhetsadresse().poststed());
        softAssert.assertThat(jsonEnhet.adresse().land()).isEqualTo(input.beliggenhetsadresse().land());
        softAssert.assertThat(jsonEnhet.adresse().postnummer()).isEqualTo(input.beliggenhetsadresse().postnummer());

        softAssert.assertThat(jsonEnhet.naringskoder().size()).isEqualTo(2);
        softAssert.assertThat(jsonEnhet.naringskoder().stream().anyMatch(n -> n.kode().equals(input.naeringskode1().kode()))).isTrue();
        softAssert.assertThat(jsonEnhet.naringskoder().stream().anyMatch(n -> n.kode().equals(input.naeringskode2().kode()))).isTrue();
        softAssert.assertAll();
    }
}
