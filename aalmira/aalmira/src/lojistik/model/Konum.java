package lojistik.model;

/**
 * Konum sinifi: Iki boyutlu duzlemde (x, y) bir noktayi temsil eder.
 * Musterilerin ve araclarin konumlarini tutmak icin kullanilir.
 *
 * NYP Prensibi: KAPSULLEME (encapsulation)
 * - x ve y alanlari private'tir, sadece getter metotlari ile erisilir.
 */
public class Konum {

    // Koordinat bilgileri disaridan dogrudan degistirilemez (kapsulleme).
    private final double x;
    private final double y;

    public Konum(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Bu konum ile verilen hedef konum arasindaki Oklid (duz cizgi) mesafesini hesaplar.
     * Formul: karekok( (x2-x1)^2 + (y2-y1)^2 )
     *
     * @param hedefKonum mesafenin olculecegi hedef nokta
     * @return iki nokta arasindaki mesafe
     */
    public double mesafeHesapla(Konum hedefKonum) {
        // null kontrolu ile hatali kullanimi engelliyoruz.
        if (hedefKonum == null) {
            throw new IllegalArgumentException("Hedef konum bos (null) olamaz.");
        }
        double farkX = this.x - hedefKonum.x;
        double farkY = this.y - hedefKonum.y;
        return Math.sqrt(farkX * farkX + farkY * farkY);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
