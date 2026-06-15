package lojistik.model;

/**
 * Paket sinifi: Tasinacak/teslim edilecek bir kargo paketini temsil eder.
 *
 * NYP Prensibi: KAPSULLEME
 * - agirlik ve icerik degismez (final), durum ise teslimat boyunca guncellenir.
 *
 * Not: "hedefMusteri" alani, paketin kime teslim edilecegini bilmek icin
 * eklenmistir. Boylece LojistikSistemi paket kuyrugundan cektigi her paketi
 * dogru musteriye teslim edebilir. (Gercek dunyada her kargonun bir alicisi vardir.)
 */
public class Paket {

    private final double agirlik;       // kg cinsinden agirlik
    private final String icerik;        // paket icerigi (aciklama)
    private PaketDurumu durum;          // paketin guncel durumu
    private Musteri hedefMusteri;       // paketin teslim edilecegi musteri (opsiyonel)

    /**
     * Hedef musteri belirtmeden paket olusturur.
     * (Araca dogrudan teslimat yaptirilan senaryolar icin uygundur.)
     */
    public Paket(double agirlik, String icerik) {
        this(agirlik, icerik, null);
    }

    /**
     * Hedef musteri ile birlikte paket olusturur.
     * (Kuyruk uzerinden otomatik teslimat senaryolari icin uygundur.)
     */
    public Paket(double agirlik, String icerik, Musteri hedefMusteri) {
        this.agirlik = agirlik;
        this.icerik = icerik;
        this.hedefMusteri = hedefMusteri;
        // Yeni olusturulan her paket "Hazirlaniyor" durumunda baslar.
        this.durum = PaketDurumu.HAZIRLANIYOR;
    }

    public double getAgirlik() {
        return agirlik;
    }

    public String getIcerik() {
        return icerik;
    }

    public PaketDurumu getDurum() {
        return durum;
    }

    public void setDurum(PaketDurumu durum) {
        this.durum = durum;
    }

    public Musteri getHedefMusteri() {
        return hedefMusteri;
    }

    public void setHedefMusteri(Musteri hedefMusteri) {
        this.hedefMusteri = hedefMusteri;
    }

    @Override
    public String toString() {
        return "Paket{icerik='" + icerik + "', agirlik=" + agirlik
                + " kg, durum=" + durum + "}";
    }
}
