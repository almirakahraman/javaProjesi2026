package lojistik.sistem;

import java.util.ArrayList;

import lojistik.arac.Arac;
import lojistik.istisna.AgirPaketException;
import lojistik.loglama.ILogger;
import lojistik.model.Musteri;
import lojistik.model.Paket;

/**
 * LojistikSistemi: Tum sistemi yoneten merkezi sinif.
 * Musterileri, araclari ve paket kuyrugunu yonetir.
 *
 * NYP Prensipleri:
 * - KAPSULLEME: Tum listeler private'tir, sadece metotlarla yonetilir.
 * - KUYRUK / FIFO: paketKuyrugu ArrayList ile FIFO (ilk giren ilk cikar) calisir.
 * - SOYUTLAMA: Loglama icin somut sinifa degil ILogger arayuzune baglidir.
 * - POLIMORFIZM: teslimatlariBaslat() icinde Arac.teslimatYap() cagrilir;
 *   hangi alt sinif oldugu fark etmeksizin dogru davranis calisir.
 */
public class LojistikSistemi {

    private final ArrayList<Musteri> musteriListesi;
    private final ArrayList<Arac> aracListesi;
    // Paket kuyrugu FIFO mantigiyla calisir: sona eklenir, bastan cekilir.
    private final ArrayList<Paket> paketKuyrugu;
    private final ILogger logger;

    public LojistikSistemi(ILogger logger) {
        this.logger = logger;
        this.musteriListesi = new ArrayList<>();
        this.aracListesi = new ArrayList<>();
        this.paketKuyrugu = new ArrayList<>();
    }

    /** Sisteme yeni musteri ekler. */
    public void musteriEkle(Musteri musteri) {
        musteriListesi.add(musteri);
        logger.bilgiYaz("Musteri eklendi: " + musteri.getAd());
    }

    /** Sisteme yeni arac ekler. */
    public void aracEkle(Arac arac) {
        aracListesi.add(arac);
        logger.bilgiYaz("Arac eklendi: " + arac.getPlaka());
    }

    /**
     * Paketi kuyrugun SONUNA ekler (FIFO: enqueue).
     */
    public void paketiKuyrugaEkle(Paket paket) {
        paketKuyrugu.add(paket);
        logger.bilgiYaz("Paket kuyruga eklendi: " + paket.getIcerik()
                + " (" + paket.getAgirlik() + " kg)");
    }

    /**
     * Kuyrugun BASINDAN paket ceker (FIFO: dequeue).
     * Ilk giren paket ilk cikar.
     *
     * @return siradaki paket, kuyruk bossa null
     */
    public Paket paketiKuyruktanCek() {
        if (paketKuyrugu.isEmpty()) {
            logger.hataYaz("Paket kuyrugu bos, cekilecek paket yok.");
            return null;
        }
        // index 0 = en eski (ilk giren) paket -> FIFO mantigi
        return paketKuyrugu.remove(0);
    }

    /** Kuyrukta bekleyen paket sayisi. */
    public int kuyruktakiPaketSayisi() {
        return paketKuyrugu.size();
    }

    /**
     * Kuyruktaki tum paketleri FIFO sirasiyla teslimata cikarir.
     * Her paket, kendi hedef musterisine ilk uygun arac ile teslim edilir.
     */
    public void teslimatlariBaslat() {
        logger.bilgiYaz("=== Teslimatlar FIFO sirasiyla baslatiliyor ===");

        while (!paketKuyrugu.isEmpty()) {
            // FIFO: en once eklenen paketi al
            Paket paket = paketiKuyruktanCek();
            Musteri hedef = paket.getHedefMusteri();

            if (hedef == null) {
                logger.hataYaz("Paketin hedef musterisi tanimsiz: " + paket.getIcerik());
                continue;
            }

            // Paketi tasiyabilecek uygun bir arac sec (POLIMORFIZM)
            Arac arac = uygunAracBul(paket, hedef);
            if (arac == null) {
                logger.hataYaz("'" + paket.getIcerik() + "' icin uygun arac bulunamadi.");
                continue;
            }

            // HATA YONETIMI: Drone agir pakette istisna firlatabilir.
            try {
                arac.teslimatYap(hedef, paket);
            } catch (AgirPaketException e) {
                logger.hataYaz("Teslimat basarisiz: " + e.getMessage());
            }
        }

        logger.bilgiYaz("=== Tum teslimatlar tamamlandi ===");
    }

    /**
     * Verilen paket ve musteri icin uygun ilk araci bulur.
     * - Kapasitesi yeterli olmali.
     * (Basit secim: kapasitesi uygun olan ilk araci doner.)
     */
    private Arac uygunAracBul(Paket paket, Musteri musteri) {
        for (Arac arac : aracListesi) {
            if (paket.getAgirlik() <= arac.getKapasite()) {
                return arac;
            }
        }
        return null;
    }
}
