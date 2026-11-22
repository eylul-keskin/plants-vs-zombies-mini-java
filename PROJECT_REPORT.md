# Proje notları

## Oyun hakkında

Java Swing ile hazırlanmış küçük bir masaüstü savunma oyunu. Oyun alanı 5 satır ve 9 sütundan oluşuyor. Amaç, kaynakları kullanarak bitki yerleştirmek ve zombileri sol sınıra ulaşmadan durdurmak.

Oyunda beş bitki ve dört zombi türü bulunuyor. Bitkilerin maliyetleri ve saldırı/üretim davranışları, zombilerin ise hız ve can değerleri farklı. Oyun iki dalga sonunda tamamlanıyor.

## Kodun yapısı

Oyun mantığı ile Swing arayüzü ayrı sınıflarda tutulmuş:

| Dosya / sınıf | Görevi |
| --- | --- |
| `GameEngine` | Oyun döngüsü, güneş miktarı, saldırılar, çarpışmalar ve oyun durumları |
| `Board`, `Cell`, `Plant`, `Zombie` | Oyun tahtası ve tahtadaki nesneler |
| `WaveSpawner` | Ayrı bir iş parçacığında zombi dalgalarını zamanlayıp istekleri kuyruğa ekler |
| `SaveLoadManager` | Oyun durumunu `saves/savegame.txt` dosyasına yazıp geri okur |
| `GameFrame`, `StartMenuPanel` | Menü ve oyun ekranı arasında geçiş |
| `GamePanel`, `PlantSelectionPanel` | Oyun alanını çizme, fare etkileşimleri ve bitki seçimi |
| `UiTheme`, `UiButton` | Arayüzde ortak kullanılan görünüm ayarları |

`GameEngine`, bitki ve zombi güncellemelerini yürütürken arayüzü de yeniler. `WaveSpawner` doğrudan oyun tahtasını değiştirmek yerine bir kuyruğa zombi üretme isteği gönderir; istekler oyun güncellemesi sırasında işlenir.

## Arayüz

Üst panelden bitki seçiliyor, ardından uygun bir hücreye tıklanarak yerleştirme yapılıyor. Seçilen bitkinin önizlemesi hücre üzerinde gösteriliyor. Güneş miktarı yeterli değilse yerleştirme gerçekleşmiyor. Oyun sırasında duraklatma, devam etme ve kayıt alma seçenekleri var.

![Duraklatma ekranı](pause-preview.png)

## Bilinen sınırlar

- İki sabit zombi dalgası var; sonsuz veya rastgele oluşturulan seviyeler bulunmuyor.
- Karakter görselleri animasyonlu değil.
- Kürek görseli mevcut olsa da bitki kaldırma mekaniği uygulanmamış.
- Kayıt sistemi temel oyun durumunu saklıyor ancak rastgele sayı üreticisinin durumunu ve dalgada kalan hassas bekleme süresini saklamıyor.
- Otomatik test dosyaları bu repoya eklenmemiş. Derleme ve oynanış, yeni değişikliklerden sonra ayrıca kontrol edilmeli.

Karakterler, araç simgeleri ve arka planlar `GardenArt` sınıfında Java 2D ile çiziliyor.
