package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.batch.model.CsvEnhet;
import no.nav.pam.ad.enhetsregister.batch.model.JsonAdresse;
import no.nav.pam.ad.enhetsregister.batch.model.JsonEnhet;
import no.nav.pam.ad.enhetsregister.batch.model.JsonNaringskode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;

public class EnhetItemProcessor implements ItemProcessor<CsvEnhet, JsonEnhet> {

    @Override
    public JsonEnhet process(CsvEnhet enhet) throws Exception {

        return mapToJsonEnhet(enhet);
    }

    private JsonEnhet mapToJsonEnhet(CsvEnhet csv) {
        JsonEnhet json = new JsonEnhet();

        json.setNavn(csv.getNavn());
        json.setAntallAnsatte(csv.getAntallAnsatte());
        json.setOrganisasjonsnummer(csv.getOrganisasjonsnummer());
        json.setOrganisasjonsform(csv.getOrganisasjonsform());
        json.setOverordnetEnhet(csv.getOverordnetEnhet());

        if (StringUtils.isNotBlank(csv.getAdresse())) {
            json.setPostAdresse(new JsonAdresse(csv.getAdresse(), csv.getPostnummer(), csv.getPoststed(), csv.getKommunenummer(), csv.getKommune(), csv.getLandkode(), csv.getLand()));
        }
        if (StringUtils.isNotBlank(csv.getNaeringskode1_kode())) {
            json.getNaringskoder().add(new JsonNaringskode(csv.getNaeringskode1_kode(), csv.getNaeringskode1_beskrivelse()));
        }
        if (StringUtils.isNotBlank(csv.getNaeringskode2_kode())) {
            json.getNaringskoder().add(new JsonNaringskode(csv.getNaeringskode2_kode(), csv.getNaeringskode2_beskrivelse()));
        }
        if (StringUtils.isNotBlank(csv.getNaeringskode3_kode())) {
            json.getNaringskoder().add(new JsonNaringskode(csv.getNaeringskode3_kode(), csv.getNaeringskode3_beskrivelse()));
        }

        return json;
    }
}
