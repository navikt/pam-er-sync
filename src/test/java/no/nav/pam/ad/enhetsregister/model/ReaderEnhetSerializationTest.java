package no.nav.pam.ad.enhetsregister.model;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;


import java.io.IOException;

public class ReaderEnhetSerializationTest {

    private ObjectMapper objectMapper = new JsonMapper();

    @Test
    public void underenhet_should_deserialize_properly() throws IOException {
        String underenhetJson = """
                {
                   "organisasjonsnummer":"714541662",
                   "navn":"STANETA LOGISTICS",
                   "organisasjonsform":{
                      "kode":"BEDR",
                      "beskrivelse":"Bedrift",
                      "links":[
                
                      ]
                   },
                   "registreringsdatoEnhetsregisteret":"2014-11-29",
                   "registrertIMvaregisteret":false,
                   "naeringskode1":{
                      "beskrivelse":"Andre post- og budtjenester",
                      "kode":"53.200"
                   },
                   "antallAnsatte":5,
                   "overordnetEnhet":"714514444",
                   "oppstartsdato":"2014-11-26",
                   "beliggenhetsadresse":{
                      "land":"Norge",
                      "landkode":"NO",
                      "postnummer":"4014",
                      "poststed":"STAVANGER",
                      "adresse":[
                         "Ognagata 1"
                      ],
                      "kommune":"STAVANGER",
                      "kommunenummer":"1103"
                   },
                   "links":[
                
                   ]
                }\
                """;

        ReaderEnhet enhet = objectMapper.readValue(underenhetJson, ReaderEnhet.class);

        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(enhet.organisasjonsnummer()).isEqualTo("714541662");
        softAssert.assertThat(enhet.antallAnsatte()).isEqualTo(5);
        softAssert.assertThat(enhet.navn()).isEqualTo("STANETA LOGISTICS");
        softAssert.assertThat(enhet.organisasjonsform().kode()).isEqualTo("BEDR");
        softAssert.assertThat(enhet.overordnetEnhet()).isEqualTo("714514444");

        softAssert.assertThat(enhet.naeringskode1().beskrivelse()).isEqualTo("Andre post- og budtjenester");
        softAssert.assertThat(enhet.naeringskode1().kode()).isEqualTo("53.200");
        softAssert.assertThat(enhet.naeringskode2()).isNull();

        softAssert.assertThat(enhet.forretningsadresse()).isNull();
        softAssert.assertThat(enhet.beliggenhetsadresse().postnummer()).isEqualTo("4014");
        softAssert.assertThat(enhet.beliggenhetsadresse().poststed()).isEqualTo("STAVANGER");
        softAssert.assertThat(enhet.beliggenhetsadresse().adresse().size()).isEqualTo(1);

        softAssert.assertAll();
    }
}
