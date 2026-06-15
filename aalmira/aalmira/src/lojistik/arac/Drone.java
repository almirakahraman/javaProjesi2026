package lojistik.arac;

import lojistik.model.Konum;
import lojistik.model.Musteri;
import lojistik.model.Paket;
import lojistik.model.PaketDurumu;
import lojistik.istisna.AgirPaketException;
import lojistik.loglama.ILogger;

/**
 * Drone: Arac sinifindan turetilmis somut arac tipi.
 *
 * NYP Prensibi: KALITIM + POLIMORFIZM + HATA YONETIMI
 * - Is kurali: Drone yalnizca 5 kg'dan HAFIF paketleri tasiyabilir.
 * - 5 kg ve uzeri pakette ozel AgirPaketException firlatir.
 */
public class Drone extends Arac {

    public Drone(String plaka, double kapasite, double yakit, double hiz,
                 Konum konum, ILogger logger) {
        super(plaka, kapasite, yakit, hiz, konum, logger);
    }

    @Override
    public boolean teslimatYap(Musteri musteri, Paket paket) throws AgirPaketException {
        logger.bilgiYaz("DRONE " + plaka + " teslimata hazirlaniyor -> Musteri: " + musteri.getAd());

        // IS KURALI 2: 5 kg ve uzeri paketlerde ozel istisna firlatilir.
        // Bu istisnayi cagiran taraf (LojistikSistemi veya Main) yakalamalidir.
        if (paket.getAgirlik() >= 5) {
            throw new AgirPaketException("Drone " + plaka + " 5 kg ve uzeri paket tasiyamaz! "
                    + "Paket agirligi: " + paket.getAgirlik() + " kg");
        }

        try {
            double mesafe = konum.mesafeHesapla(musteri.getKonum());
            yakitKontrolEt(mesafe);

            // Once durum "Yolda"
            paket.setDurum(PaketDurumu.YOLDA);
            logger.bilgiYaz("Paket havadan yola cikti. Mesafe: " + String.format("%.2f", mesafe)
                    + " birim, Hiz: " + hiz + " km/s");

            yakitTuket(mesafe);
            this.konum = musteri.getKonum();

            // Teslimat tamamlandi
            paket.setDurum(PaketDurumu.TESLIM_EDILDI);
            logger.bilgiYaz("Paket teslim edildi -> " + musteri.getAd() + " | Durum: " + paket.getDurum());
            return true;

        } catch (Exception e) {
            logger.hataYaz("Drone teslimati sirasinda beklenmeyen hata: " + e.getMessage());
            return false;
        }
    }
}
