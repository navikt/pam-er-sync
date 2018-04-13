package no.nav.pam.ad.enhetsregister.batch;

public class CsvProperties {

    private int[] includedFields;
    private String[] fieldNames;

    public CsvProperties(int[] includedFields, String[] fieldNames) {
        this.includedFields = includedFields;
        this.fieldNames = fieldNames;
    }

    public static CsvProperties buildCsvProperties(EnhetType type) {
        if (type == EnhetType.HOVEDENHET) {
            return new CsvProperties(
                    new int[]{0, 1, 4, 11, 14, 15, 16, 17, 18, 19, 27, 28, 29, 30, 31, 32, 33},
                    new String[]{
                            "organisasjonsnummer",
                            "navn",
                            "organisasjonsform",
                            "antallAnsatte",
                            "naeringskode1_kode",
                            "naeringskode1_beskrivelse",
                            "naeringskode2_kode",
                            "naeringskode2_beskrivelse",
                            "naeringskode3_kode",
                            "naeringskode3_beskrivelse",
                            "adresse",
                            "postnummer",
                            "poststed",
                            "kommunenummer",
                            "kommune",
                            "landkode",
                            "land"
                    }
            );
        } else if (type == EnhetType.UNDERENHET) {
            return new CsvProperties(
                    new int[]{0, 1, 2, 3, 9, 11, 12, 13, 14, 15, 16, 24, 25, 26, 27, 28, 29, 30},
                    new String[]{
                            "organisasjonsnummer",
                            "navn",
                            "overordnetEnhet",
                            "organisasjonsform",
                            "antallAnsatte",
                            "naeringskode1_kode",
                            "naeringskode1_beskrivelse",
                            "naeringskode2_kode",
                            "naeringskode2_beskrivelse",
                            "naeringskode3_kode",
                            "naeringskode3_beskrivelse",
                            "adresse",
                            "postnummer",
                            "poststed",
                            "kommunenummer",
                            "kommune",
                            "landkode",
                            "land"
                    }
            );
        } else {
            throw new IllegalArgumentException("Enhet type was null");
        }
    }

    public int[] getIncludedFields() {
        return includedFields;
    }

    public String[] getFieldNames() {
        return fieldNames;
    }

    public enum EnhetType {
        HOVEDENHET,
        UNDERENHET
    }
}
