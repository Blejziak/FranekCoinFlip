# FranekCoinFlip

![banner](assets/banner.png)

Prosty plugin na coinflipa w GUI pod Vaulta. Gracz wpisuje `/coinflip`, klika tworzenie gry, podaje kwote na czacie i zaklad wpada na liste. Jak ktos dolaczy to odpala sie losowanie w GUI i wygrany dostaje hajs.

Mozna tez przegladac swoje aktywne gry i wylaczyc powiadomienia o coinflipach na czacie.

## Komendy

| Komenda | Co robi |
|---|---|
| `/coinflip` | otwiera GUI coinflipa |
| `/coinflip reload` | przeladowuje `config.yml` (`franek.coinflip` lub `op`) |

## Config

`plugins/FranekCoinFlip/config.yml`, tworzy sie sam przy starcie:

```yaml
timings:
  animation-duration-seconds: 10
  animation-interval-ticks: 5
  winner-display-duration-seconds: 3

game-settings:
  tax-percentage: 0.03
  max-bet-percentage: 0.50
```

Wszystkie itemy w GUI, sloty i wiadomosci mozna ustawic pod siebie w configu. Wymaga pluginu **Vault** i jakiejkolwiek ekonomii (np. EssentialsX).

## Instalacja

Jar z folderu [`releases/`](releases/) wrzucasz do `plugins/` na serwerze (Spigot / Paper 1.20.x+) i restart.

## Budowanie

```bash
mvn package
```

Wychodzi `target/FranekCoinFlip-1.0.jar`.

## Licencja

Wszelkie prawa zastrzezone. Autor: franekk ([GitHub](https://github.com/Blejziak)).
