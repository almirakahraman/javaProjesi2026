# Akıllı Şehir Lojistik ve Dağıtım Simülasyonu

Java ile nesneye yönelik programlama (NYP) prensiplerini gösteren bir teslimat simülasyon motoru.

## Çalıştırma

### Kolay yol (Windows)
Proje klasöründeki `derle_ve_calistir.bat` dosyasına çift tıklayın.

### Komut satırı
```bat
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src\*.java).FullName
java -Dfile.encoding=UTF-8 -cp out lojistik.Main
```

## Klasör Yapısı
```
src/lojistik/
  Main.java                 -> Programın giriş noktası, 7 senaryo
  model/   -> Konum, Musteri, Paket, PaketDurumu
  arac/    -> Arac (soyut), Kamyon, Motor, Drone
  istisna/ -> AgirPaketException
  loglama/ -> ILogger, KonsolLogger
  sistem/  -> LojistikSistemi (FIFO kuyruk)
```

## Kullanılan NYP Prensipleri
- **Kalıtım**: Kamyon, Motor, Drone -> Arac
- **Polimorfizm**: `teslimatYap()` her araçta farklı çalışır
- **Soyutlama**: `Arac` soyut sınıfı, `ILogger` arayüzü
- **Kapsülleme**: private alanlar + getter/setter
- **Hata yönetimi**: `try-catch` + özel `AgirPaketException`
- **FIFO kuyruk**: `LojistikSistemi.paketKuyrugu`

## UML Diyagramı (Metinsel)

İlişki gösterimi:
- `<|--`  : Kalıtım (is-a) / arayüz uygulama
- `-->`   : Bağımlılık / sahiplik (has-a)

```
                          <<interface>>
                            ILogger
                  + bilgiYaz(mesaj: String)
                  + hataYaz(mesaj: String)
                              ^
                              | (implements)
                       KonsolLogger
                  + bilgiYaz(mesaj)
                  + hataYaz(mesaj)


                       <<abstract>>
                          Arac
        --------------------------------------------
        # plaka: String
        # kapasite: double
        # yakit: double
        # hiz: double
        # konum: Konum
        # logger: ILogger
        --------------------------------------------
        + teslimatYap(m: Musteri, p: Paket): boolean   <<abstract>>
        + yakitIkmal(): void
        # yakitTuket(mesafe: double): void
        # yakitKontrolEt(gereken: double): void
        # kapasiteUygunMu(p: Paket): boolean
                  ^            ^            ^
                  |            |            |   (extends)
        ----------+   ---------+   ---------+----------
        |              |                    |
     Kamyon          Motor               Drone
   (dar sokak      (>10 kg ->          (>=5 kg ->
    teslimat        hiz %20             AgirPaket-
    yapamaz)        azalir)             Exception)


        AgirPaketException  ----|>  Exception        (extends)


                    LojistikSistemi
        --------------------------------------------
        - musteriListesi: ArrayList<Musteri>
        - aracListesi: ArrayList<Arac>
        - paketKuyrugu: ArrayList<Paket>   (FIFO)
        - logger: ILogger
        --------------------------------------------
        + musteriEkle(m: Musteri)
        + aracEkle(a: Arac)
        + paketiKuyrugaEkle(p: Paket)
        + paketiKuyruktanCek(): Paket
        + teslimatlariBaslat()

  Bağımlılıklar (-->):
    LojistikSistemi --> Musteri        (musteriListesi)
    LojistikSistemi --> Arac           (aracListesi, POLIMORFIZM)
    LojistikSistemi --> Paket          (paketKuyrugu)
    LojistikSistemi --> ILogger        (loglama)
    Arac            --> Konum, Musteri, Paket, ILogger
    Arac            --> AgirPaketException (Drone fırlatır)
    Musteri         --> Konum
    Paket           --> PaketDurumu (enum), Musteri (hedefMusteri)
```



## UML Diyagramı (Görsel)

```mermaid
classDiagram
    direction TB

    %% Arayüz ve Loglama Katmanı
    class ILogger {
        <<interface>>
        +bilgiYaz(mesaj: String) void
        +hataYaz(mesaj: String) void
    }
    class KonsolLogger {
        +bilgiYaz(mesaj: String) void
        +hataYaz(mesaj: String) void
    }
    ILogger <|.. KonsolLogger : implements

    %% Veri Modelleri (Model)
    class Konum {
        -int x
        -int y
        +MesafeHesapla(Konum diger) double
    }

    class Musteri {
        -String ad
        -String id
        -Konum konum
        -boolean darSokakMi
    }
    Musteri --> Konum

    class Paket {
        -double agirlik
        -String icerik
        -PaketDurumu durum
        -Musteri hedefMusteri
    }
    
    class PaketDurumu {
        <<enumeration>>
        HAZIRLANIYOR
        YOLDA
        TESLIM_EDILDI
    }
    Paket --> PaketDurumu
    Paket --> Musteri

    %% Soyut Sınıf ve Araç Katmanı
    class Arac {
        <<abstract>>
        #plaka: String
        #kapasite: double
        #yakit: double
        #hiz: double
        #konum: Konum
        #logger: ILogger
        +teslimatYap(Musteri m, Paket p)* boolean
        +yakitIkmal() void
        #yakitTuket(mesafe: double) void
        #yakitKontrolEt(gereken: double) void
        #kapasiteUygunMu(Paket p) boolean
    }
    Arac --> Konum
    Arac --> ILogger

    class Kamyon {
        +teslimatYap(Musteri m, Paket p) boolean
    }
    class Motor {
        +teslimatYap(Musteri m, Paket p) boolean
    }
    class Drone {
        +teslimatYap(Musteri m, Paket p) boolean
    }

    Arac <|-- Kamyon : extends
    Arac <|-- Motor : extends
    Arac <|-- Drone : extends

    %% Özel İstisna ve Sistem Katmanı
    class AgirPaketException {
        <<exception>>
    }
    Exception <|-- AgirPaketException : extends
    Drone ..> AgirPaketException : throws

    class LojistikSistemi {
        -ArrayList~Musteri~ musteriListesi
        -ArrayList~Arac~ araclarListesi
        -ArrayList~Paket~ paketKuyrugu
        -ILogger logger
        +musteriEkle(Musteri m) void
        +aracEkle(Arac a) void
        +paketiKuyrugaEkle(Paket p) void
        +paketiKuyruktanCek() Paket
        +teslimatlariBaslat() void
    }
    LojistikSistemi --> Musteri : musteriListesi
    LojistikSistemi --> Arac : aracListesi (POLIMORFIZM)
    LojistikSistemi --> Paket : paketKuyrugu (FIFO)
    LojistikSistemi --> ILogger : loglama ```

## Proje Raporu Açıklama Metni

> Bu bölüm doğrudan ödev raporuna eklenebilir.

Bu projede, bir şehrin lojistik dağıtım ağını modelleyen nesneye yönelik bir
simülasyon motoru geliştirilmiştir. Sistemin temelinde, tüm araçların ortak
özelliklerini (plaka, kapasite, yakıt, hız) ve davranışlarını barındıran
soyut `Arac` sınıfı yer alır. Bu sınıf, **soyutlama** prensibi gereği teslimat
işlemini `teslimatYap()` soyut metodu ile tanımlar; ancak teslimatın nasıl
yapılacağını alt sınıflara bırakır.

`Kamyon`, `Motor` ve `Drone` sınıfları `Arac` sınıfından **kalıtım** yoluyla
türetilmiş olup, her biri `teslimatYap()` metodunu kendi iş kuralına göre
yeniden yazar. Bu durum **polimorfizm**in açık bir örneğidir: `LojistikSistemi`
bir aracın türünü bilmeden `arac.teslimatYap(...)` çağrısı yapar ve çalışma
anında doğru davranış otomatik olarak seçilir. Kamyon dar sokaklara teslimat
yapamaz, motosiklet 10 kg üzerindeki paketlerde hızını %20 düşürür, drone ise
5 kg ve üzeri paketlerde özel `AgirPaketException` istisnasını fırlatır.

**Kapsülleme** prensibi gereği `Konum`, `Musteri` ve `Paket` sınıflarındaki
tüm alanlar `private` olarak tanımlanmış, dışarıdan yalnızca getter/setter
metotları aracılığıyla erişime izin verilmiştir. Böylece nesnelerin iç durumu
korunmuştur.

Sistemde paketler, **FIFO (İlk Giren İlk Çıkar)** mantığıyla çalışan bir
kuyrukta tutulur. Kuyruk, dilin standart `ArrayList` yapısı ile gerçeklenmiş;
ekleme listenin sonuna (`add`), çekme ise listenin başından (`remove(0)`)
yapılarak giriş sırasının korunması sağlanmıştır. `teslimatlariBaslat()`
metodu paketleri bu sıraya göre tek tek işler.

**Hata yönetimi** için hem dilin `try-catch` mekanizması hem de projeye özel
`AgirPaketException` sınıfı kullanılmıştır. Ayrıca araçların yakıt yönetimi
modellenmiş; bir araç hareket ettiği mesafe kadar yakıt tüketir ve yakıtı
yetersiz kaldığında otomatik olarak `yakitIkmal()` çağrılır. Loglama işlemleri,
somut bir sınıfa değil `ILogger` arayüzüne bağlı kalınarak gerçekleştirilmiş;
bu da sistemin ileride farklı log mekanizmalarına (örneğin dosyaya yazma)
kolayca uyarlanabilmesini sağlar.

Sonuç olarak proje; kalıtım, polimorfizm, soyutlama, kapsülleme, hata yönetimi
ve kuyruk veri yapısı kavramlarını gerçek bir problem üzerinde bir arada
göstermektedir. `Main` sınıfında çalıştırılan yedi farklı test senaryosu, tüm
iş kurallarının beklendiği gibi çalıştığını doğrulamaktadır.

## Beklenen Konsol Çıktısı

Aşağıdaki çıktı, programın `lojistik.Main` sınıfı çalıştırıldığında ürettiği
gerçek çıktıdır (JDK 21 ile derlenip test edilmiştir).

```
==================================================
>> SENARYO 1: Normal kamyon teslimati
==================================================
[BILGI] KAMYON 34-ABC-01 teslimata hazirlaniyor -> Musteri: Ayse
[BILGI] Paket yola cikti. Mesafe: 5,00 birim, Hiz: 90.0 km/s
[BILGI] 34-ABC-01 arac 5,00 birim yol gitti. Kalan yakit: 45,00 birim
[BILGI] Paket teslim edildi -> Ayse | Durum: Teslim Edildi

==================================================
>> SENARYO 2: Kamyonun dar sokaga teslimat yapamamasi
==================================================
[BILGI] KAMYON 34-ABC-01 teslimata hazirlaniyor -> Musteri: Mehmet
[HATA] Kamyon 34-ABC-01 dar sokaga giremez! 'Mehmet' adli musteriye teslimat yapilamadi.

==================================================
>> SENARYO 3: Drone'un hafif paketi teslim etmesi
==================================================
[BILGI] DRONE DRN-007 teslimata hazirlaniyor -> Musteri: Zeynep
[BILGI] Paket havadan yola cikti. Mesafe: 1,41 birim, Hiz: 120.0 km/s
[BILGI] DRN-007 arac 1,41 birim yol gitti. Kalan yakit: 28,59 birim
[BILGI] Paket teslim edildi -> Zeynep | Durum: Teslim Edildi

==================================================
>> SENARYO 4: Drone'un agir paket icin AgirPaketException firlatmasi
==================================================
[BILGI] DRONE DRN-007 teslimata hazirlaniyor -> Musteri: Ayse
[HATA] Yakalanan istisna -> Drone DRN-007 5 kg ve uzeri paket tasiyamaz! Paket agirligi: 7.0 kg

==================================================
>> SENARYO 5: Motorun 10 kg uzeri pakette hizinin %20 azalmasi
==================================================
[BILGI] MOTOR 34-MTR-09 teslimata hazirlaniyor -> Musteri: Zeynep
[BILGI] Paket 10 kg uzerinde! Hiz %20 azaltildi: 100.0 -> 80.0 km/s
[BILGI] Paket yola cikti. Mesafe: 1,41 birim, Teslimat hizi: 80.0 km/s
[BILGI] 34-MTR-09 arac 1,41 birim yol gitti. Kalan yakit: 38,59 birim
[BILGI] Paket teslim edildi -> Zeynep | Durum: Teslim Edildi

==================================================
>> SENARYO 6: Yakit yetersizse yakit ikmali yapilmasi
==================================================
[BILGI] MOTOR 34-MTR-00 teslimata hazirlaniyor -> Musteri: Ayse
[HATA] 34-MTR-00 icin yakit yetersiz! (Gerekli: 5,00, Mevcut: 2,00) Yakit ikmali yapiliyor...
[BILGI] 34-MTR-00 plakali arac yakit ikmali yapti. Yeni yakit: 100.0 birim
[BILGI] Paket yola cikti. Mesafe: 5,00 birim, Teslimat hizi: 100.0 km/s
[BILGI] 34-MTR-00 arac 5,00 birim yol gitti. Kalan yakit: 95,00 birim
[BILGI] Paket teslim edildi -> Ayse | Durum: Teslim Edildi

==================================================
>> SENARYO 7: Paketlerin FIFO sirasiyla kuyruktan cekilmesi
==================================================
[BILGI] Musteri eklendi: Ayse
[BILGI] Musteri eklendi: Zeynep
[BILGI] Arac eklendi: 34-XYZ-99
[BILGI] Paket kuyruga eklendi: Paket-A (4.0 kg)
[BILGI] Paket kuyruga eklendi: Paket-B (6.0 kg)
[BILGI] Paket kuyruga eklendi: Paket-C (8.0 kg)
[BILGI] === Teslimatlar FIFO sirasiyla baslatiliyor ===
[BILGI] KAMYON 34-XYZ-99 teslimata hazirlaniyor -> Musteri: Ayse
[BILGI] Paket yola cikti. Mesafe: 5,00 birim, Hiz: 90.0 km/s
[BILGI] 34-XYZ-99 arac 5,00 birim yol gitti. Kalan yakit: 95,00 birim
[BILGI] Paket teslim edildi -> Ayse | Durum: Teslim Edildi
[BILGI] KAMYON 34-XYZ-99 teslimata hazirlaniyor -> Musteri: Zeynep
[BILGI] Paket yola cikti. Mesafe: 3,61 birim, Hiz: 90.0 km/s
[BILGI] 34-XYZ-99 arac 3,61 birim yol gitti. Kalan yakit: 91,39 birim
[BILGI] Paket teslim edildi -> Zeynep | Durum: Teslim Edildi
[BILGI] KAMYON 34-XYZ-99 teslimata hazirlaniyor -> Musteri: Ayse
[BILGI] Paket yola cikti. Mesafe: 3,61 birim, Hiz: 90.0 km/s
[BILGI] 34-XYZ-99 arac 3,61 birim yol gitti. Kalan yakit: 87,79 birim
[BILGI] Paket teslim edildi -> Ayse | Durum: Teslim Edildi
[BILGI] === Tum teslimatlar tamamlandi ===

==================================================
>> SIMULASYON TAMAMLANDI
==================================================
```

> Not: Çıktıdaki ondalık ayıraç (örn. `5,00`) sistemin Türkçe bölge ayarından
> kaynaklanır; bu, programın doğru çalışmasını etkilemez.
