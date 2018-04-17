package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.CsvEnhet;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;


public class EnhetItemProcessorTest {

    @Test
    public void should_map_underenhet_properly() throws Exception {

        EnhetItemProcessor processor = new EnhetItemProcessor();

        CsvEnhet u = new CsvEnhet();
        u.setNavn("Test 1");
        u.setAntallAnsatte("15");
        u.setOrganisasjonsform("BEDR");
        u.setOrganisasjonsnummer("56789");
        u.setOverordnetEnhet("12345");

        u.setAdresse("Gate 1");
        u.setPostnummer("0576");
        u.setPoststed("Oslo");
        u.setLand("Norge");

        u.setNaeringskode1_kode("47.111");
        u.setNaeringskode1_beskrivelse("Butikkhandel");
        u.setNaeringskode2_kode("10.850");
        u.setNaeringskode2_beskrivelse("Produksjon av ferdigmat");

        Enhet jsonEnhet = processor.process(u);
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(jsonEnhet.getNavn()).isEqualTo(u.getNavn());
        softAssert.assertThat(jsonEnhet.getOrganisasjonsnummer()).isEqualTo(u.getOrganisasjonsnummer());
        softAssert.assertThat(jsonEnhet.getOverordnetEnhet()).isEqualTo(u.getOverordnetEnhet());
        softAssert.assertThat(jsonEnhet.getAntallAnsatte()).isEqualTo(Integer.parseInt(u.getAntallAnsatte()));
        softAssert.assertThat(jsonEnhet.getOrganisasjonsform()).isEqualTo(u.getOrganisasjonsform());

        softAssert.assertThat(jsonEnhet.getAdresse().getAdresse()).isEqualTo(u.getAdresse());
        softAssert.assertThat(jsonEnhet.getAdresse().getPoststed()).isEqualTo(u.getPoststed());
        softAssert.assertThat(jsonEnhet.getAdresse().getLand()).isEqualTo(u.getLand());
        softAssert.assertThat(jsonEnhet.getAdresse().getPostnummer()).isEqualTo(u.getPostnummer());

        softAssert.assertThat(jsonEnhet.getNaringskoder().size()).isEqualTo(2);
        softAssert.assertThat(jsonEnhet.getNaringskoder().stream().anyMatch(n -> n.getKode().equals(u.getNaeringskode1_kode()))).isTrue();
        softAssert.assertThat(jsonEnhet.getNaringskoder().stream().anyMatch(n -> n.getKode().equals(u.getNaeringskode2_kode()))).isTrue();
        softAssert.assertAll();
    }
}
