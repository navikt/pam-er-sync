package no.nav.pam.ad.enhetsregister.batch;


import org.junit.Test;


public class EnhetItemProcessorTest {

    @Test
    public void should_map_underenhet_properly() throws Exception {

        EnhetItemProcessor processor = new EnhetItemProcessor();

//        Enhet u = new Enhet();
//        u.setNavn("Test 1");
//        u.setAntallAnsatte("15");
//        u.setOrganisasjonsform("BEDR");
//        u.setOrganisasjonsnummer("56789");
//        u.setOverordnetEnhet("12345");
//
//        u.setAdresse("Gate 1");
//        u.setPostnummer("0576");
//        u.setPoststed("Oslo");
//        u.setLand("Norge");
//
//        u.setNaeringskode1_kode("47.111");
//        u.setNaeringskode1_beskrivelse("Butikkhandel");
//        u.setNaeringskode2_kode("10.850");
//        u.setNaeringskode2_beskrivelse("Produksjon av ferdigmat");
//
//        Company company = processor.process(u);
//        SoftAssertions softAssert = new SoftAssertions();
//        softAssert.assertThat(company.getName()).isEqualTo("Test 1");
//        softAssert.assertThat(company.getOrgnr()).isEqualTo("56789");
//        softAssert.assertThat(company.getParentOrgnr()).isEqualTo("12345");
//        softAssert.assertThat(company.getEmployees()).isEqualTo(new Integer(15));
//        softAssert.assertThat(company.getOrgform()).isEqualTo("BEDR");
//
//        softAssert.assertThat(company.getLocation().get().getAddress()).isEqualTo("Gate 1");
//        softAssert.assertThat(company.getLocation().get().getCity()).isEqualTo("Oslo");
//        softAssert.assertThat(company.getLocation().get().getCountry()).isEqualTo("Norge");
//        softAssert.assertThat(company.getLocation().get().getPostalCode()).isEqualTo("0576");
//
//
//        softAssert.assertThat(company.getPropertiesImmutable().get(PropertyNames.nace2)).isNotNull();
//        softAssert.assertThat(company.getPropertiesImmutable().get(PropertyNames.nace2)).contains("\"code\":\"47.111\",\"name\":\"Butikkhandel\"");
//        softAssert.assertThat(company.getPropertiesImmutable().get(PropertyNames.nace2)).contains("\"code\":\"10.850\",\"name\":\"Produksjon av ferdigmat\"");
//        softAssert.assertAll();
    }
}
