# crypto market

## Backend

### Instalacja
Backend jest aplikacją napisaną w języku Scala. Na jej podstawie stworzony jest obraz dockerowy [link](https://cloud.docker.com/u/matematyk60/repository/docker/matematyk60/crypto-market-backend).
Najprostszym sposobem na uruchomienie aplikacji jest wejscie do katalogu `backend`, zmodyfikowanie pliku `docker-compose.yml`,
poprzez zamontowanie wolumenu z kluczami `public.der` oraz `private.der`, które są potrzebne do podpisywania tokenów JWT generowanych przez aplikację.
Plik ten po wpisaniu komendy `docker-compose up -d` uruchomi w tle dwa serwisy: aplikację oraz instancję MongoDB.

### Dokumentacja

#### API
Aplikacja wystawia REST'owe API na porcie `8080`. Plik swagger z dokumentacją API jest dostępny w katalogu `backend/-docs./api-documentation.yaml`,
jak również pod tym [adresem](https://app.swaggerhub.com/apis/matematyk60/crypto-market/1.0.0#/). 

#### Opis działania systemu
By użytkownik mógł korzystać z naszego systemu, musi zarejestrować się udając się pod odpowiedni endpoint. W odpowiedzi otrzymuje token JWT, który dołącza później do każdego zapytania w nagłówku HTTP `Authorization`. 
Użytkownik może dodawać środki na konto w danej walucie. Może zamieszczać oferty wymiany walut, a później z pasującymi ofertami dokonywać wymiany. System od każdej transakcji pobiera 5 % prowizji.

Opcjonalnie, oprócz standardowych parametrów transakcji takich jak kwota, kurs, minimalna wymieniona kwota, przy tworzeniu oferty użytkownik ma możliwość utworzenia warunku wykonania, np.: tylko wtedy, gdy kurs BitCoin nie będzie mniejszy niż 3500 USD. W tym celu system komunikuje się poprzez zapytanie HTTP z serwisem `blockchain.info`.

Oprócz manualnego dokonywania transakcji, system autmatycznie znajduje pasujące do siebie transakcje, oraz przeprowadza ich wymianę. 

#### Opis struktury projektu
Kod podzielony jest na trzy główne moduły:
1. `api` - jest to moduł wykorzystujący klasy domenowe, w którym definiowane są sposoby komunikacji z aplikacją
2. `domain` - moduł definiujący klasy domenowe jak i operacje na nich
3. `infrastructure` - moduł zawierający implementacje domenowych interfejsów, np. implementację interfejsu `UserRepository`, która łączy się z MongoDB i persystuje klasy domenowe.

Aplikacja do ustępniania API HTTP wykorzystuje bibliotekę Akka HTTP, a do połączenia z bazą danych MongoDB bibliotekę ReactiveMongo.

#### Testowanie
Wszystkie funkcjonalności aplikacji są testowane za pomocą biblioteki ScalaTest oraz Akka Http. Testy jednostkowe znajdują się w katalogu `/backend/src/test/scala/com/market/unit`, 
a integracyjne w `/backend/src/test/scala/com/market/e2e`. Pokrycie kodu aplikacji testami to odpowiednio:
* na poziomie klas: 67%
* na poziomie metod: 93%
* na poziomie linii kodu: 92%

(wygenerowane za pomocą narzędzia `Coverage` dostępnego w środowisku IDE `Intellij IDEA Community`

Testy integracyjne inicjalizują embedded instancję MongoDB, inicjalizują aplikację i wykonują requesty HTTP do aplikacji.

### Użyte biblioteki
* https://github.com/akka/akka
* https://github.com/Nycto/Hasher
* https://github.com/json4s/json4s
* https://github.com/ReactiveMongo/ReactiveMongo
* https://github.com/typelevel/cats
* https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo 


## Frontend
Frontend został napisany w języku React. 

### Użyte biblioteki
*npm react
*npm react-DOM
*npm react-transition-group
*npm babel
*npm backstrap
*npm react-subpage
*npm axios
