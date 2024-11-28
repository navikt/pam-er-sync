package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Adresse;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.Naringskode;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderAdresse;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderNaringskode;
import no.nav.pam.ad.es.IndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.stream.Collectors;


public class EnhetItemProcessor implements ItemProcessor<ReaderEnhet, Enhet> {

    private static final Logger LOG = LoggerFactory.getLogger(IndexService.class);

    @Override
    public Enhet process(ReaderEnhet readerEnhet) {
        Enhet enhet = mapToJsonEnhet(readerEnhet);
        if ("834547732".equals(enhet.getOrganisasjonsnummer())) {
            LOG.info("Processing enhet: {}", enhet);
        }
        return enhet;
    }

    private Enhet mapToJsonEnhet(ReaderEnhet readerEnhet) {

        Adresse adresse = null;
        if (readerEnhet.beliggenhetsadresse != null) {
            adresse = map(readerEnhet.beliggenhetsadresse);
        } else if (readerEnhet.forretningsadresse != null) {
            adresse = map(readerEnhet.forretningsadresse);
        }

        Enhet json = new Enhet(
                readerEnhet.organisasjonsnummer,
                readerEnhet.navn,
                readerEnhet.organisasjonsform.kode,
                readerEnhet.antallAnsatte,
                readerEnhet.overordnetEnhet,
                adresse);


        if (readerEnhet.naeringskode1 != null) {
            json.getNaringskoder().add(map(readerEnhet.naeringskode1));
        }
        if (readerEnhet.naeringskode2 != null) {
            json.getNaringskoder().add(map(readerEnhet.naeringskode2));
        }
        if (readerEnhet.naeringskode3 != null) {
            json.getNaringskoder().add(map(readerEnhet.naeringskode3));
        }

        return json;
    }

    private Naringskode map(ReaderNaringskode readerItem) {
        return new Naringskode(readerItem.kode, readerItem.beskrivelse);
    }

    private Adresse map(ReaderAdresse readerItem) {
        return new Adresse(readerItem.adresse.stream().collect(Collectors.joining(", ")),
                readerItem.postnummer,
                readerItem.poststed,
                readerItem.kommunenummer,
                readerItem.kommune,
                readerItem.landkode,
                readerItem.land);
    }

}
