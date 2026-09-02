# Event Booking Application

Spring Boot öğrenme yolculuğumdaki **5. projem**. Önceki projelerde CRUD, DTO, validasyon, global hata yönetimi, pagination ve JWT güvenliği gibi konuları ayrı ayrı ele almıştım. Bu projede bu konuları etkinlik ve rezervasyon domain'i üzerinde bir araya getirerek; rol tabanlı yetkilendirme, refresh token akışı, migration, cache, rate limiting ve eşzamanlı rezervasyon işlemleri gibi konuları daha derinlemesine çalıştım.

Bu proje production için değil, **öğrenme amaçlı** geliştirilmiştir. Amaç, gerçek bir etkinlik rezervasyon akışının temel parçalarını tek bir Spring Boot uygulamasında görmek ve önceki projelerde öğrenilen konuları daha tutarlı bir mimari içinde uygulamaktır.

## Önceki Projelere Göre Neler Değişti / Neler Öğrenildi

- **Etkinlik ve rezervasyon domain'i:** Kullanıcılar etkinlik oluşturabilir, etkinliklere koltuk rezervasyonu yapabilir ve rezervasyonlarını onaylayabilir.
- **Rol tabanlı yetkilendirme:** `USER` ve `ADMIN` rolleri kullanılıyor. Etkinlik oluşturma, yayınlama ve iptal etme işlemleri yalnızca `ADMIN` rolüne açık.
- **JWT access ve refresh token:** Girişte access token ve refresh token üretiliyor. Refresh token rotation ve logout sırasında token'ların toplu iptali uygulanıyor.
- **Pessimistic locking:** Aynı etkinlik için eşzamanlı rezervasyonlarda kapasitenin yanlış hesaplanmasını önlemek amacıyla `PESSIMISTIC_WRITE` kullanılıyor.
- **Flyway migration'ları:** Veritabanı şeması `V1`-`V4` migration'larıyla oluşturuluyor; Hibernate yalnızca şemayı doğruluyor (`ddl-auto=validate`).
- **MapStruct:** Entity ve DTO dönüşümleri MapStruct mapper'larıyla yapılıyor.
- **Cache:** Etkinlik listeleme ve detay sorgularında Caffeine tabanlı Spring Cache kullanılıyor; yazma işlemlerinde ilgili cache kayıtları temizleniyor veya güncelleniyor.
- **Rate limiting:** Bucket4j ile login, rezervasyon ve genel public GET istekleri için IP/kullanıcı bazlı limitler uygulanıyor.
- **Standart hata gövdeleri:** Hatalar Spring'in `ProblemDetail` yapısı üzerinden anlamlı HTTP durum kodlarıyla dönüyor.
- **Testcontainers:** Entegrasyon testi altyapısı PostgreSQL Testcontainer kullanacak şekilde hazırlanmış durumda.

## Kullanılan Teknolojiler

- Java 25
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- PostgreSQL
- Flyway
- JJWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)
- MapStruct
- Caffeine Cache
- Bucket4j
- spring-dotenv
- Springdoc OpenAPI
- Lombok
- JUnit 5 + Testcontainers
- Maven

## Mimari

Paket-by-feature yaklaşımı kullanılıyor:

```
identity/      → Kullanıcılar, roller, authentication ve JWT
event/         → Etkinlik entity'si, CRUD işlemleri ve etkinlik durumları
reservation/   → Rezervasyon, kapasite kontrolü ve rezervasyon durumları
common/        → Exception, rate limiting ve ortak yapılar
config/        → Cache ve MVC yapılandırması
```

İstek akışı genel olarak şöyledir:

```
İstek → JwtAuthenticationFilter → Controller → Service → Repository → PostgreSQL
```

- `AuthController` — kayıt, giriş, refresh token, logout ve kullanıcı listeleme
- `EventController` — etkinlik oluşturma, listeleme, güncelleme, yayınlama ve iptal etme
- `ReservationController` — rezervasyon oluşturma, kullanıcının rezervasyonlarını listeleme ve onaylama
- `JwtService` / `JwtAuthenticationFilter` — JWT üretimi ve isteklerin doğrulanması
- `RefreshTokenService` — refresh token oluşturma, doğrulama, rotation ve iptal
- `EventMapper` / `ReservationMapper` — entity ↔ DTO dönüşümleri
- `GlobalExceptionHandler` — domain ve güvenlik hatalarını `ProblemDetail` olarak döndürme

## API Endpoint'leri

### Auth — Base path: `/api/v1/auth`

| Metod | Endpoint         | Açıklama                                                 | Yetki         |
| ----- | ---------------- | -------------------------------------------------------- | ------------- |
| POST  | `/register`      | Yeni kullanıcı kaydı oluşturur                           | Herkese açık  |
| POST  | `/login`         | E-posta ve şifre ile giriş yapar, token çifti döner      | Herkese açık  |
| POST  | `/refresh-token` | Refresh token'ı doğrular, yeni token çifti üretir        | Herkese açık  |
| POST  | `/logout`        | Giriş yapan kullanıcının refresh token'larını iptal eder | Giriş gerekli |
| GET   | `/users`         | Kullanıcıları listeler                                   | Giriş gerekli |

### Event — Base path: `/api/v1/events`

| Metod | Endpoint        | Açıklama                    | Yetki                      |
| ----- | --------------- | --------------------------- | -------------------------- |
| POST  | `/`             | Yeni etkinlik oluşturur     | `ADMIN`                    |
| GET   | `/`             | Tüm etkinlikleri listeler   | Giriş gerekli              |
| GET   | `/{id}`         | ID'ye göre etkinlik getirir | Giriş gerekli              |
| PUT   | `/{id}`         | Etkinliği günceller         | Etkinlik sahibi            |
| PATCH | `/{id}/publish` | Etkinliği yayınlar          | `ADMIN` ve etkinlik sahibi |
| PATCH | `/{id}/cancel`  | Etkinliği iptal eder        | `ADMIN` ve etkinlik sahibi |

Etkinlik durumları: `DRAFT`, `PUBLISHED`, `CANCELLED`.

### Reservation — Base path: `/api/v1/reservations`

| Metod | Endpoint        | Açıklama                                                    | Yetki              |
| ----- | --------------- | ----------------------------------------------------------- | ------------------ |
| POST  | `/`             | Bir etkinlik için koltuk rezervasyonu oluşturur             | Giriş gerekli      |
| GET   | `/`             | Giriş yapan kullanıcının rezervasyonlarını sayfalı listeler | Giriş gerekli      |
| POST  | `/{id}/confirm` | Kullanıcının rezervasyonunu onaylar                         | Rezervasyon sahibi |

Yeni rezervasyonlar `PENDING` durumunda oluşturulur ve 15 dakika içinde onaylanmalıdır. Rezervasyon durumları: `PENDING`, `CONFIRMED`, `CANCELLED`, `EXPIRED`.

## Örnek İstekler

Önce `/api/v1/auth/register` ve `/api/v1/auth/login` ile token alınmalıdır. Korumalı endpoint'lerde aşağıdaki header kullanılır:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

```bash
# Kayıt ol
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","fullname":"Görkem Uysal","password":"sifre123"}'

# Giriş yap ve token al
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"sifre123"}'

# Etkinlik oluştur
curl -X POST http://localhost:8080/api/v1/events \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"title":"Spring Boot Workshop","description":"Spring Boot öğrenme etkinliği","vanue":"İstanbul","startTime":"2026-10-10T14:00:00","capacity":100,"price":250.00,"status":"DRAFT"}'

# Etkinlikleri listele
curl http://localhost:8080/api/v1/events \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Etkinliği yayınla
curl -X PATCH http://localhost:8080/api/v1/events/1/publish \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Rezervasyon oluştur
curl -X POST http://localhost:8080/api/v1/reservations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ACCESS_TOKEN>" \
  -d '{"eventId":1,"seatCount":2}'

# Rezervasyonları sayfalı listele
curl "http://localhost:8080/api/v1/reservations?page=0&size=20" \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Rezervasyonu onayla
curl -X POST http://localhost:8080/api/v1/reservations/1/confirm \
  -H "Authorization: Bearer <ACCESS_TOKEN>"

# Yeni access ve refresh token al
curl -X POST http://localhost:8080/api/v1/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'

# Çıkış yap
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

> Not: Etkinlik request alanı mevcut modelde `vanue` olarak adlandırılmıştır.

### Model / DTO

```json
// EventRequest
{
  "title": "Spring Boot Workshop",
  "description": "Spring Boot öğrenme etkinliği",
  "vanue": "İstanbul",
  "startTime": "2026-10-10T14:00:00",
  "capacity": 100,
  "price": 250.00,
  "status": "DRAFT"
}

// ReservationRequest
{
  "eventId": 1,
  "seatCount": 2
}
```

## Veritabanı Kurulumu

Proje PostgreSQL bekliyor. `compose.yaml` üzerinden PostgreSQL 17 Alpine konteyneri kullanılabilir. Veritabanı bilgileri yerel geliştirme içindir:

- Veritabanı: `eventdb`
- Kullanıcı: `event_app`
- Şifre: `local_dev_only`
- Host portu: `5433`

Şema Flyway migration'larıyla otomatik oluşturulur. `spring.jpa.hibernate.ddl-auto=validate` olduğu için Hibernate veritabanını değiştirmez.

## Kurulum ve Çalıştırma

### Gereksinimler

- JDK 25
- Docker Desktop
- Maven Wrapper (`mvnw` / `mvnw.cmd` proje ile birlikte gelir)

Proje kökünde `.env` dosyası oluşturun:

```env
JWT_SECRET=uzun-ve-guvenli-bir-gizli-anahtar
JWT_EXPIRATION=15m
```

Ardından uygulamayı çalıştırın:

```bash
git clone https://github.com/grkmuysal/event-booking-application.git
cd event-booking-application
./mvnw spring-boot:run
```

Windows üzerinde:

```powershell
.\mvnw.cmd spring-boot:run
```

Spring Boot Docker Compose entegrasyonu sayesinde uygulama başlarken proje kökündeki PostgreSQL servisi otomatik olarak başlatılır. Uygulama varsayılan olarak `http://localhost:8080` adresinde çalışır.

## Testleri Çalıştırma

Testler Maven ile çalıştırılabilir. Entegrasyon testleri için Docker Desktop'ın çalışıyor olması gerekir:

```bash
./mvnw test
```

## API Dokümantasyonu

Springdoc OpenAPI kullanıldığı için uygulama çalışırken Swagger UI'a şu adresten erişilebilir:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Hâlâ Eksik Olanlar

Bu proje öğrenme amaçlıdır ve production-ready değildir. Şu an bilinçli ya da geliştirme sırasında fark edilen bazı eksikler:

- **Rezervasyon yaşam döngüsü tamamlanmadı:** `CANCELLED` ve `EXPIRED` durumları modellenmiş olsa da bunları yöneten iptal ve zamanlanmış expiration akışları henüz REST API'ye eklenmedi.
- **Event endpoint'leri sınırlı:** Etkinlik silme endpoint'i yok; arama, filtreleme ve gelişmiş pagination henüz eklenmedi.
- **Kullanıcı yönetimi sınırlı:** Kullanıcı listeleme endpoint'i giriş yapmış tüm kullanıcılara açık; admin-only kullanıcı yönetimi ve rol değiştirme akışı yok.
- **Test kapsamı genişletilmeli:** Auth, refresh token, rezervasyon yarış koşulları, rate limiting ve controller hata senaryoları için daha kapsamlı testler yazılmalı.
- **Sır yönetimi geliştirme seviyesinde:** `.env` dosyası gitignore'da tutuluyor; production ortamında secret manager kullanılmalı.

Bu maddeler sonraki projelerde veya bu projenin devamında ele alınacaktır.

## Lisans

Bu proje kişisel öğrenme amaçlı geliştirilmiştir.
