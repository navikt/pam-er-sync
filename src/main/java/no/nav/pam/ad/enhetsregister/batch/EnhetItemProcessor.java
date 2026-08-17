package no.nav.pam.ad.enhetsregister.batch;


import no.nav.pam.ad.enhetsregister.model.Adresse;
import no.nav.pam.ad.enhetsregister.model.Enhet;
import no.nav.pam.ad.enhetsregister.model.Naringskode;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderAdresse;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderEnhet;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderNaringskode;
import no.nav.pam.ad.enhetsregister.model.reader.ReaderOrgform;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


public class EnhetItemProcessor implements ItemProcessor<ReaderEnhet, Enhet> {

    @Override
    public Enhet process(ReaderEnhet enhet) {
        var adresse = Stream.of(enhet.beliggenhetsadresse(), enhet.forretningsadresse())
                .filter(Objects::nonNull)
                .findFirst()
                .map(this::map)
                .orElse(null);

        var naringskoder = Stream.of(enhet.naeringskode1(), enhet.naeringskode2(), enhet.naeringskode3())
                .filter(Objects::nonNull)
                .map(this::map)
                .toList();

        var organisasjonsform = switch (enhet.organisasjonsform()) {
            case ReaderOrgform(var kode) -> kode;
            case null -> null;
        };

        return new Enhet(
                enhet.organisasjonsnummer(),
                enhet.navn(),
                organisasjonsform,
                enhet.antallAnsatte(),
                enhet.overordnetEnhet(),
                adresse,
                naringskoder
        );
    }

    private Naringskode map(ReaderNaringskode readerItem) {
        return new Naringskode(readerItem.kode(), readerItem.beskrivelse());
    }

    private Adresse map(ReaderAdresse readerItem) {
        var adresse = switch (readerItem.adresse()) {
            case List<String> liste -> String.join(", ", liste);
            case null -> null;
        };

        return new Adresse(adresse,
                readerItem.postnummer(),
                readerItem.poststed(),
                readerItem.kommunenummer(),
                readerItem.kommune(),
                readerItem.landkode(),
                readerItem.land());
    }

}
