package lojistik.model;

/**
 * Musteri sinifi: Sisteme kayitli bir musteriyi temsil eder.
 *
 * NYP Prensibi: KAPSULLEME
 * - Tum alanlar private'tir; disaridan sadece getter/setter ile erisilir.
 */
public class Musteri {

    private final int id;
    private final String ad;
    private final Konum konum;
    // Musteri dar bir sokakta mi yasiyor? (Kamyon teslimati icin onemli)
    private final boolean darSokakMi;

    public Musteri(int id, String ad, Konum konum, boolean darSokakMi) {
        this.id = id;
        this.ad = ad;
        this.konum = konum;
        this.darSokakMi = darSokakMi;
    }

    public int getId() {
        return id;
    }

    public String getAd() {
        return ad;
    }

    public Konum getKonum() {
        return konum;
    }

    public boolean isDarSokakMi() {
        return darSokakMi;
    }

    @Override
    public String toString() {
        return "Musteri{id=" + id + ", ad='" + ad + "', konum=" + konum
                + ", darSokak=" + darSokakMi + "}";
    }
}
