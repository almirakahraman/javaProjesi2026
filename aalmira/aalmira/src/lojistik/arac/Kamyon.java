package lojistik.arac;

import lojistik.model.Konum;
import lojistik.model.Musteri;
import lojistik.model.Paket;
import lojistik.model.PaketDurumu;
import lojistik.loglama.ILogger;

/**
 * Kamyon: Arac sinifindan turetilmis somut arac tipi.
 *
 * NYP Prensibi: KALITIM + POLIMORFIZM
 * - Is kurali: Kamyon, dar sokakta bulunan musteriye teslimat YAPAMAZ.
 */
public class Kamyon extends Arac {

    public Kamyon(String plaka, double kapasite, double yakit, double hiz,
                  Konum konum, ILogger logger) {
        super(plaka, kapasite, yakit, hiz, konum, logger);
    }

    @Override
    public boolean teslimatYap(Musteri musteri, Paket paket) {
        // try-catch ile beklenmedik hatalara karsi guvence sagliyoruz.
        try {
            logger.bilgiYaz("KAMYON " + plaka + " teslimata hazirlaniyor -> Musteri: " + musteri.getAd());

            // IS KURALI 1: Dar sokak kontrolu
            if (musteri.isDarSokakMi()) {
                logger.hataYaz("Kamyon " + plaka + " dar sokaga giremez! '"
                        + musteri.getAd() + "' adli musteriye teslimat yapilamadi.");
                return false;
            }

            // Kapasite kontrolu
            if (!kapasiteUygunMu(paket)) {
                return false;
            }

            // Mesafe ve yakit hesabi
            double mesafe = konum.mesafeHesapla(musteri.getKonum());
            yakitKontrolEt(mesafe);

            // IS KURALI: Once durum "Yolda" yapilir
            paket.setDurum(PaketDurumu.YOLDA);
            logger.bilgiYaz("Paket yola cikti. Mesafe: " + String.format("%.2f", mesafe)
                    + " birim, Hiz: " + hiz + " km/s");

            // Yol gidilir, yakit tuketilir ve arac musteriye ulasir
            yakitTuket(mesafe);
            this.konum = musteri.getKonum();

            // Teslimat tamamlandi, durum guncellenir
            paket.setDurum(PaketDurumu.TESLIM_EDILDI);
            logger.bilgiYaz("Paket teslim edildi -> " + musteri.getAd() + " | Durum: " + paket.getDurum());
            return true;

        } catch (Exception e) {
            // Beklenmeyen herhangi bir hatayi yakalayip anlamli sekilde raporlariz.
            logger.hataYaz("Kamyon teslimati sirasinda beklenmeyen hata: " + e.getMessage());
            return false;
        }
    }
}
