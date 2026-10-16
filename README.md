# FranekCoinFlip

![banner](assets/banner.png)

System gier CoinFlip (rzut monetą o kasę z ekonomii Vault) z pełnym GUI, animacją losowania, historią własnych gier i przełączaniem powiadomień na czacie.

Gracz otwiera `/coinflip`, klika tworzenie gry, wpisuje stawkę na czacie i gra trafia na listę aktywnych zakładów. Kiedy ktoś do niej dołączy, obaj gracze widzą animację losowania z główkami i zwycięzca zgarnia pulę pomniejszoną o prowizję serwera.

## Komendy

| Komenda | Co robi |
|---|---|
| `/coinflip` | otwiera główne menu CoinFlip |
| `/coinflip reload` | przeładowuje `config.yml` (wymaga uprawnienia `franek.coinflip` lub `op`) |

## Config

`plugins/FranekCoinFlip/config.yml`, tworzy się sam przy pierwszym uruchomieniu:

```yaml
timings:
  animation-duration-seconds: 10
  animation-interval-ticks: 5
  winner-display-duration-seconds: 3

game-settings:
  tax-percentage: 0.03
  max-bet-percentage: 0.50
```

Wszystkie nazwy przedmiotów w GUI, sloty i wiadomości na czacie można dowolnie zmienić w pliku konfiguracyjnym.

## Wymagania

- **Vault** + dowolny plugin ekonomii (np. EssentialsX)

## Instalacja

Jar z folderu [`releases/`](releases/) wrzucasz do `plugins/` na serwerze (Spigot / Paper) i restart.

## Budowanie

```bash
mvn package
```

Wychodzi `target/FranekCoinFlip-1.0.jar`. Wymaga JDK 16+ i Mavena.

## Licencja

Wszelkie prawa zastrzeżone. Autor: franekk ([GitHub](https://github.com/Blejziak)).
