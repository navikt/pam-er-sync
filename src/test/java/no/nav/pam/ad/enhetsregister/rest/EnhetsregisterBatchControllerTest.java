package no.nav.pam.ad.enhetsregister.rest;

import no.nav.pam.ad.enhetsregister.batch.TestConfig;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.persistence.IndexClient;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestConfig.class)
class EnhetsregisterBatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IndexClient client;

    @BeforeEach
    void before() {
        assertEquals(TestConfig.IndexClient.class, client.getClass());
    }

    @AfterEach
    void after() {
        ((TestConfig.IndexClient) client).getStorage().clear();
    }

    @Test
    void triggerDownloadOfHovedenheterAndGetNotFound() throws Exception {

        // Should be disabled in test configuration, so 404 is what we want.
        mockMvc.perform(post("/internal/enhetsregister/sync/hovedenheter"))
                .andExpect(status().isNotFound());

    }

    @Test
    void triggerDownloadOfUnderenheterAndProcessBatchJob() throws Exception {

        mockMvc.perform(post("/internal/enhetsregister/sync/underenheter"))
                .andExpect(status().isOk());

        Map<String, List<Enhet>> index = ((TestConfig.IndexClient) client).getStorage();
        assertEquals(1, index.keySet().size());

        List<Enhet> entry = index.entrySet().iterator().next().getValue();
        assertTrue(index.keySet().iterator().next().startsWith("UNDER"));
        assertEquals(6, entry.size());

        SoftAssertions softAssert = new SoftAssertions();
        softAssert.assertThat(entry.getFirst().organisasjonsnummer()).isEqualTo("914541662");
        softAssert.assertThat(entry.getFirst().navn()).isEqualTo("STANETA LOGISTICS AND SERVICE STANELY OKOROAFOR");
        softAssert.assertThat(entry.getFirst().organisasjonsform()).isEqualTo("BEDR");
        softAssert.assertThat(entry.getFirst().antallAnsatte()).isEqualTo(0);
        softAssert.assertThat(entry.getFirst().overordnetEnhet()).isEqualTo("914514444");
        softAssert.assertThat(entry.getFirst().adresse()).isNotNull();
        softAssert.assertThat(entry.getFirst().adresse().adresse()).isEqualTo("Ognagata 1");
        softAssert.assertThat(entry.getFirst().adresse().postnummer()).isEqualTo("4014");
        softAssert.assertThat(entry.getFirst().adresse().poststed()).isEqualTo("STAVANGER");
        softAssert.assertThat(entry.getFirst().adresse().kommune()).isEqualTo("STAVANGER");
        softAssert.assertThat(entry.getFirst().adresse().landkode()).isEqualTo("NO");
        softAssert.assertThat(entry.getFirst().adresse().land()).isEqualTo("Norge");
        softAssert.assertThat(entry.getFirst().naringskoder().size()).isEqualTo(1);
        softAssert.assertThat(entry.getFirst().naringskoder().getFirst().kode()).isEqualTo("53.200");
        softAssert.assertThat(entry.getFirst().naringskoder().getFirst().beskrivelse()).isEqualTo("Andre post- og budtjenester");
        softAssert.assertAll();
    }

}
