package lojistik;

import lojistik.arac.Arac;
import lojistik.arac.Drone;
import lojistik.arac.Kamyon;
import lojistik.arac.Motor;
import lojistik.istisna.AgirPaketException;
import lojistik.loglama.ILogger;
import lojistik.loglama.KonsolLogger;
import lojistik.model.Konum;
import lojistik.model.Musteri;
import lojistik.model.Paket;
import lojistik.sistem.LojistikSistemi;

/**
 * Main: Programin baslangic noktasi.
 * Akilli Sehir Lojistik Simulasyonu'nun ornek senaryolarini calistirir.
 *
 * Bu sinif, projedeki NYP prensiplerinin (kalitim, polimorfizm, soyutlama,
 * kapsulleme, hata yonetimi, FIFO kuyruk) nasil calistigini gosterir.
 */
public class Main {

    public static void main(String[] args) {
        // SOYUTLAMA: Somut KonsolLogger'i ILogger arayuzu uzerinden kullaniyoruz.
        ILogger logger = new KonsolLogger();

        // Ortak bir depo (cikis) konumu
        Konum depo = new Konum(0, 0);

        // Ornek musteriler
        Musteri ayse = new Musteri(1, "Ayse", new Konum(3, 4), false);      // normal sokak
        Musteri mehmet = new Musteri(2, "Mehmet", new Konum(6, 8), true);   // DAR sokak
        Musteri zeynep = new Musteri(3, "Zeynep", new Konum(1, 1), false);  // normal sokak

        baslik("SENARYO 1: Normal kamyon teslimati");
        Kamyon kamyon = new Kamyon("34-ABC-01", 1000, 50, 90, depo, logger);
        Paket paket1 = new Paket(20, "Buzdolabi");
        kamyon.teslimatYap(ayse, paket1);

        baslik("SENARYO 2: Kamyonun dar sokaga teslimat yapamamasi");
        Paket paket2 = new Paket(15, "Camasir Makinesi");
        kamyon.teslimatYap(mehmet, paket2); // Mehmet dar sokakta -> basarisiz

        baslik("SENARYO 3: Drone'un hafif paketi teslim etmesi");
        Drone drone = new Drone("DRN-007", 5, 30, 120, depo, logger);
        Paket paket3 = new Paket(2, "Ilac Kutusu");
        try {
            drone.teslimatYap(zeynep, paket3); // 2 kg < 5 kg -> basarili
        } catch (AgirPaketException e) {
            logger.hataYaz(e.getMessage());
        }

        baslik("SENARYO 4: Drone'un agir paket icin AgirPaketException firlatmasi");
        Paket paket4 = new Paket(7, "Kitap Kolisi");
        try {
            drone.teslimatYap(ayse, paket4); // 7 kg >= 5 kg -> istisna
        } catch (AgirPaketException e) {
            // HATA YONETIMI: ozel istisna burada yakalanir.
            logger.hataYaz("Yakalanan istisna -> " + e.getMessage());
        }

        baslik("SENARYO 5: Motorun 10 kg uzeri pakette hizinin %20 azalmasi");
        Motor motor = new Motor("34-MTR-09", 50, 40, 100, depo, logger);
        Paket paket5 = new Paket(12, "Spor Malzemesi"); // 12 kg > 10 kg
        motor.teslimatYap(zeynep, paket5);

        baslik("SENARYO 6: Yakit yetersizse yakit ikmali yapilmasi");
        // Yakiti cok dusuk bir motor olusturuyoruz, uzak musteriye teslimat deneyecek.
        Motor azYakitliMotor = new Motor("34-MTR-00", 50, 2, 100, depo, logger);
        Paket paket6 = new Paket(3, "Telefon");
        azYakitliMotor.teslimatYap(ayse, paket6); // mesafe=5, yakit=2 -> ikmal gerekir

        baslik("SENARYO 7: Paketlerin FIFO sirasiyla kuyruktan cekilmesi");
        LojistikSistemi sistem = new LojistikSistemi(logger);
        sistem.musteriEkle(ayse);
        sistem.musteriEkle(zeynep);

        // Teslimati yapacak araclar (kapasitesi yuksek kamyon ilk sirada)
        Kamyon dagitimKamyonu = new Kamyon("34-XYZ-99", 1000, 100, 90, depo, logger);
        sistem.aracEkle(dagitimKamyonu);

        // Paketler kuyruga giris SIRASIYLA eklenir (1 -> 2 -> 3)
        sistem.paketiKuyrugaEkle(new Paket(4, "Paket-A", ayse));
        sistem.paketiKuyrugaEkle(new Paket(6, "Paket-B", zeynep));
        sistem.paketiKuyrugaEkle(new Paket(8, "Paket-C", ayse));

        // FIFO: ilk eklenen (Paket-A) ilk teslim edilir.
        sistem.teslimatlariBaslat();

        baslik("SIMULASYON TAMAMLANDI");
    }

    /** Konsolda senaryolari ayirmak icin basit bir baslik yazar. */
    private static void baslik(String metin) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(">> " + metin);
        System.out.println("==================================================");
    }
}
