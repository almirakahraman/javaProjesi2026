package lojistik.arac;

import lojistik.model.Konum;
import lojistik.model.Musteri;
import lojistik.model.Paket;
import lojistik.istisna.AgirPaketException;
import lojistik.loglama.ILogger;

/**
 * Arac: Tum araclarin ortak ozelliklerini ve davranislarini tanimlayan SOYUT sinif.
 *
 * NYP Prensipleri:
 * - SOYUTLAMA: "teslimatYap" soyut metottur; her arac kendine ozgu sekilde uygular.
 * - KALITIM: Kamyon, Motor ve Drone bu siniftan turer.
 * - KAPSULLEME: Alanlar 'protected' olup ortak yardimci metotlarla yonetilir.
 */
public abstract class Arac {

    protected final String plaka;
    protected final double kapasite;   // tasiyabilecegi maksimum agirlik (kg)
    protected double yakit;            // mevcut yakit miktari (birim)
    protected final double hiz;        // aracin temel hizi (km/saat)
    protected Konum konum;             // aracin guncel konumu
    protected final ILogger logger;    // loglama icin (SOYUTLAMA: arayuze baglilik)

    public Arac(String plaka, double kapasite, double yakit, double hiz,
                Konum konum, ILogger logger) {
        this.plaka = plaka;
        this.kapasite = kapasite;
        this.yakit = yakit;
        this.hiz = hiz;
        this.konum = konum;
        this.logger = logger;
    }

    /**
     * SOYUT METOT: Her arac tipi teslimati kendi kurallarina gore yapar.
     * (POLIMORFIZM'in temelidir.)
     *
     * @return teslimat basarili ise true, aksi halde false
     * @throws AgirPaketException bazi araclar (Drone) agir pakette istisna firlatabilir
     */
    public abstract boolean teslimatYap(Musteri musteri, Paket paket) throws AgirPaketException;

    /**
     * Aracin yakit deposunu doldurur.
     */
    public void yakitIkmal() {
        this.yakit = 100.0;
        logger.bilgiYaz(plaka + " plakali arac yakit ikmali yapti. Yeni yakit: " + yakit + " birim");
    }

    /**
     * Hareket edilen mesafe kadar yakit tuketir. (Her birim mesafe = 1 birim yakit)
     */
    protected void yakitTuket(double mesafe) {
        this.yakit -= mesafe;
        if (this.yakit < 0) {
            this.yakit = 0;
        }
        logger.bilgiYaz(plaka + " arac " + String.format("%.2f", mesafe)
                + " birim yol gitti. Kalan yakit: " + String.format("%.2f", this.yakit) + " birim");
    }

    /**
     * Teslimat oncesi yakit kontrolu yapar. Yakit yetersizse otomatik ikmal cagirir.
     * (Is kurali: "Yakit yetersizse yakitIkmal() cagrilmali.")
     */
    protected void yakitKontrolEt(double gerekenYakit) {
        if (this.yakit < gerekenYakit) {
            logger.hataYaz(plaka + " icin yakit yetersiz! (Gerekli: "
                    + String.format("%.2f", gerekenYakit) + ", Mevcut: "
                    + String.format("%.2f", this.yakit) + ") Yakit ikmali yapiliyor...");
            yakitIkmal();
        }
    }

    /**
     * Paketin bu aracin kapasitesine uygun olup olmadigini kontrol eder.
     */
    protected boolean kapasiteUygunMu(Paket paket) {
        if (paket.getAgirlik() > kapasite) {
            logger.hataYaz(plaka + " kapasitesi yetersiz. Paket: " + paket.getAgirlik()
                    + " kg, Arac kapasitesi: " + kapasite + " kg");
            return false;
        }
        return true;
    }

    // --- Getter metotlari (kapsulleme) ---
    public String getPlaka() {
        return plaka;
    }

    public double getKapasite() {
        return kapasite;
    }

    public double getYakit() {
        return yakit;
    }

    public double getHiz() {
        return hiz;
    }

    public Konum getKonum() {
        return konum;
    }
}
