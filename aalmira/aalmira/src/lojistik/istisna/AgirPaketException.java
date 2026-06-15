package lojistik.istisna;

/**
 * AgirPaketException: Drone'un tasiyamayacagi kadar agir bir paket verildiginde
 * firlatilan ozel (custom) bir istisna sinifidir.
 *
 * NYP Prensibi: HATA YONETIMI + KALITIM
 * - Exception sinifindan turetilmistir (checked exception).
 * - Boylece programa ozgu anlamli hata mesajlari uretebiliriz.
 */
public class AgirPaketException extends Exception {

    public AgirPaketException(String mesaj) {
        super(mesaj);
    }
}
