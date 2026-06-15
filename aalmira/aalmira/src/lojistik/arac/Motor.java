package lojistik.arac;

import lojistik.model.Konum;
import lojistik.model.Musteri;
import lojistik.model.Paket;
import lojistik.model.PaketDurumu;
import lojistik.loglama.ILogger;

/**
 * Motor (motosiklet): Arac sinifindan turetilmis somut arac tipi.
 *
 * NYP Prensibi: KALITIM + POLIMORFIZM
 * - Is kurali: Paket 10 kg'dan agirsa motorun hizi %20 azalir.
 */
public class Motor extends Arac {

    public Motor(String plaka, double kapasite, double yakit, double hiz,
                 Konum konum, ILogger logger) {
        super(plaka, kapasite, yakit, hiz, konum, logger);
    }

    @Override
    public boolean teslimatYap(Musteri musteri, Paket paket) {
        try {
            logger.bilgiYaz("MOTOR " + plaka + " teslimata hazirlaniyor -> Musteri: " + musteri.getAd());

            // Kapasite kontrolu
            if (!kapasiteUygunMu(paket)) {
                return false;
            }

            // IS KURALI 3: 10 kg uzeri pakette hiz %20 azaltilir.
            double teslimatHizi = this.hiz;
            if (paket.getAgirlik() > 10) {
                teslimatHizi = this.hiz * 0.8; // %20 azalma
                logger.bilgiYaz("Paket 10 kg uzerinde! Hiz %20 azaltildi: "
                        + this.hiz + " -> " + teslimatHizi + " km/s");
            }

            double mesafe = konum.mesafeHesapla(musteri.getKonum());
            yakitKontrolEt(mesafe);

            // Once durum "Yolda"
            paket.setDurum(PaketDurumu.YOLDA);
            logger.bilgiYaz("Paket yola cikti. Mesafe: " + String.format("%.2f", mesafe)
                    + " birim, Teslimat hizi: " + teslimatHizi + " km/s");

            yakitTuket(mesafe);
            this.konum = musteri.getKonum();

            // Teslimat tamamlandi
            paket.setDurum(PaketDurumu.TESLIM_EDILDI);
            logger.bilgiYaz("Paket teslim edildi -> " + musteri.getAd() + " | Durum: " + paket.getDurum());
            return true;

        } catch (Exception e) {
            logger.hataYaz("Motor teslimati sirasinda beklenmeyen hata: " + e.getMessage());
            return false;
        }
    }
}
