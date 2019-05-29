package no.nav.pam.ad.enhetsregister.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.io.IOException;

public class ReaderEnhetSerializationTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void underenhet_should_deserialize_properly() throws IOException {
        String underenhetJson = "{\n" +
                "   \"organisasjonsnummer\":\"714541662\",\n" +
                "   \"navn\":\"STANETA LOGISTICS\",\n" +
                "   \"organisasjonsform\":{\n" +
                "      \"kode\":\"BEDR\",\n" +
                "      \"beskrivelse\":\"Bedrift\",\n" +
                "      \"links\":[\n" +
                "\n" +
                "      ]\n" +
                "   },\n" +
                "   \"registreringsdatoEnhetsregisteret\":\"2014-11-29\",\n" +
                "   \"registrertIMvaregisteret\":false,\n" +
                "   \"naeringskode1\":{\n" +
                "      \"beskrivelse\":\"Andre post- og budtjenester\",\n" +
                "      \"kode\":\"53.200\"\n" +
                "   },\n" +
                "   \"antallAnsatte\":5,\n" +
                "   \"overordnetEnhet\":\"714514444\",\n" +
                "   \"oppstartsdato\":\"2014-11-26\",\n" +
                "   \"beliggenhetsadresse\":{\n" +
                "      \"land\":\"Norge\",\n" +
                "      \"landkode\":\"NO\",\n" +
                "      \"postnummer\":\"4014\",\n" +
                "      \"poststed\":\"STAVANGER\",\n" +
                "      \"adresse\":[\n" +
                "         \"Ognagata 1\"\n" +
                "      ],\n" +
                "      \"kommune\":\"STAVANGER\",\n" +
                "      \"kommunenummer\":\"1103\"\n" +
                "   },\n" +
                "   \"links\":[\n" +
                "\n" +
                "   ]\n" +
                "}";

        ReaderEnhet enhet = objectMapper.readValue(underenhetJson, ReaderEnhet.class);

        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(enhet.organisasjonsnummer).isEqualTo("714541662");
        softAssert.assertThat(enhet.antallAnsatte).isEqualTo(5);
        softAssert.assertThat(enhet.navn).isEqualTo("STANETA LOGISTICS");
        softAssert.assertThat(enhet.organisasjonsform.kode).isEqualTo("BEDR");
        softAssert.assertThat(enhet.overordnetEnhet).isEqualTo("714514444");

        softAssert.assertThat(enhet.naeringskode1.beskrivelse).isEqualTo("Andre post- og budtjenester");
        softAssert.assertThat(enhet.naeringskode1.kode).isEqualTo("53.200");
        softAssert.assertThat(enhet.naeringskode2).isNull();

        softAssert.assertThat(enhet.forretningsadresse).isNull();
        softAssert.assertThat(enhet.beliggenhetsadresse.postnummer).isEqualTo("4014");
        softAssert.assertThat(enhet.beliggenhetsadresse.poststed).isEqualTo("STAVANGER");
        softAssert.assertThat(enhet.beliggenhetsadresse.adresse.size()).isEqualTo(1);

        softAssert.assertAll();
    }
}
