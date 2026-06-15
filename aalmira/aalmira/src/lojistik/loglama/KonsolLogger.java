package lojistik.loglama;

/**
 * KonsolLogger: ILogger arayuzunun konsola (ekrana) yazan somut uygulamasidir.
 *
 * NYP Prensibi: SOYUTLAMA'nin uygulanmasi (implements)
 */
public class KonsolLogger implements ILogger {

    @Override
    public void bilgiYaz(String mesaj) {
        System.out.println("[BILGI] " + mesaj);
    }

    @Override
    public void hataYaz(String mesaj) {
        System.out.println("[HATA] " + mesaj);
    }
}
