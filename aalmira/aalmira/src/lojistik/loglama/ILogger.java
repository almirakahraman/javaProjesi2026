package lojistik.loglama;

/**
 * ILogger arayuzu: Sistemdeki loglama (kayit tutma) davranisini tanimlar.
 *
 * NYP Prensibi: SOYUTLAMA (interface)
 * - Sistem, somut bir loglayiciya degil bu arayuze baglidir.
 * - Yarin dosyaya yazan bir Logger yazilsa bile sistem kodu degismez.
 */
public interface ILogger {

    /** Normal bilgilendirme mesaji yazar. */
    void bilgiYaz(String mesaj);

    /** Hata mesaji yazar. */
    void hataYaz(String mesaj);
}
