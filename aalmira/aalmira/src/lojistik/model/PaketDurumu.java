package lojistik.model;

/**
 * PaketDurumu enum'i: Bir paketin teslimat surecindeki durumunu temsil eder.
 *
 * NYP Prensibi: SOYUTLAMA / sabit deger yonetimi
 * - Durum bilgisini "magic string" yerine tip guvenli enum ile tutariz.
 */
public enum PaketDurumu {

    HAZIRLANIYOR("Hazirlaniyor"),
    YOLDA("Yolda"),
    TESLIM_EDILDI("Teslim Edildi");

    // Her durumun ekrana yazdirilacak okunabilir aciklamasi.
    private final String aciklama;

    PaketDurumu(String aciklama) {
        this.aciklama = aciklama;
    }

    public String getAciklama() {
        return aciklama;
    }

    @Override
    public String toString() {
        return aciklama;
    }
}
