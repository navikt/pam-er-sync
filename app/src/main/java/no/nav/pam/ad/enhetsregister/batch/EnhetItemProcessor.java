package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Adresse;
import no.nav.pam.ad.enhetsregister.model.CsvEnhet;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.Naringskode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ItemProcessor;


public class EnhetItemProcessor implements ItemProcessor<CsvEnhet, Enhet> {

    @Override
    public Enhet process(CsvEnhet enhet) throws Exception {

        return mapToJsonEnhet(enhet);
    }

    private Enhet mapToJsonEnhet(CsvEnhet csv) {

        Adresse adresse = null;
        Adresse post = null;

        if (StringUtils.isNotBlank(csv.getAdresse())) {
            adresse = new Adresse(
                    csv.getAdresse(),
                    csv.getPostnummer(),
                    csv.getPoststed(),
                    csv.getKommunenummer(),
                    csv.getKommune(),
                    csv.getLandkode(),
                    csv.getLand());
        }

        if (StringUtils.isNotBlank(csv.getAdresse())) {
            post = new Adresse(
                    csv.getPostadresse_adresse(),
                    csv.getPostadresse_postnummer(),
                    csv.getPostadresse_poststed(),
                    csv.getPostadresse_kommunenummer(),
                    csv.getPostadresse_kommune(),
                    csv.getPostadresse_landkode(),
                    csv.getPostadresse_land());
        }

        Integer antallAnsatte = null;
        try {
            antallAnsatte = Integer.parseInt(csv.getAntallAnsatte());
        } catch (NumberFormatException e) {
            antallAnsatte = 0;
        }

        Enhet json = new Enhet(
                csv.getOrganisasjonsnummer(),
                csv.getNavn(),
                csv.getOrganisasjonsform(),
                antallAnsatte,
                csv.getOverordnetEnhet(),
                adresse,
                post);

        if (StringUtils.isNotBlank(csv.getNaeringskode1_kode())) {
            json.getNaringskoder().add(new Naringskode(csv.getNaeringskode1_kode(), csv.getNaeringskode1_beskrivelse()));
        }
        if (StringUtils.isNotBlank(csv.getNaeringskode2_kode())) {
            json.getNaringskoder().add(new Naringskode(csv.getNaeringskode2_kode(), csv.getNaeringskode2_beskrivelse()));
        }
        if (StringUtils.isNotBlank(csv.getNaeringskode3_kode())) {
            json.getNaringskoder().add(new Naringskode(csv.getNaeringskode3_kode(), csv.getNaeringskode3_beskrivelse()));
        }

        return json;
    }
}
