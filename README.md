# ShottyLobby

Un plugin Minecraft completo e altamente configurabile per la gestione di lobby server.

## 📋 Compatibilità

- **Versione Minecraft:** `1.20.x` - `1.21.8`
- **Server:** Paper, Spigot, Purpur
- **Java:** 17+

## ✨ Funzionalità

### 🎮 Sistema di Spawn
- Spawn point configurabile con coordinate precise (X, Y, Z, Yaw, Pitch)
- Teleport automatico dei giocatori allo spawn al join
- Teleport automatico quando si cade sotto una certa altezza (void protection)
- Tutto configurabile da `config.yml`

### 🎒 Inventory Manager
Sistema completamente configurabile per dare item personalizzati ai giocatori:
- Item custom con materiale, nome, lore e azioni configurabili
- Slot inventory configurabili
- Supporto per tutti i color code Minecraft (`&6`, `&f`, ecc.)

### 🔧 Funzioni Disponibili
Il plugin offre diverse funzioni native utilizzabili per qualsiasi item:

#### `[MESSAGE] <testo>`
Invia un messaggio al player con **link cliccabili automatici**

#### `[HIDEPLAYERS]`
Toggle per nascondere/mostrare gli altri giocatori
- Materiale e nome item cambiano in base allo stato
- Completamente configurabile (material-visible, material-hidden, name-visible, name-hidden)

#### `[PARKOUR]`
Sistema flessibile con due modalità:
- **TELEPORT:** Teleporta a coordinate specifiche
- **COMMAND:** Esegue un comando personalizzato

#### `[OPENMENU] <nome>`
Apre GUI personalizzate configurabili

#### `[SOCIAL]`
Mostra messaggi social con link cliccabili automatici

#### `[COMMAND] <comando>`
Esegue un comando come player

#### `[CONSOLE] <comando>`
Esegue un comando dalla console

### 📱 Sistema Social
Sezione dedicata per mostrare i link social:
```yaml
social-messages:
  - '&6| &fDiscord &8» ds.example.it'
  - '&6| &fStore &8» store.example.it'
  - '&6| &fSito Web &8» www.example.it'
```
- **Link automaticamente cliccabili** in tutti i messaggi
- Hover text che mostra "Clicca per aprire: [url]"
- Supporto per color code

### 🖱️ Sistema GUI/Menu
Crea menu completamente personalizzabili:
- Titolo configurabile
- Dimensione configurabile (9, 18, 27, 36, 45, 54 slot)
- Item con materiale, nome, lore e comando configurabili
- Item posizionabili in qualsiasi slot

Esempio:
```yaml
menus:
  navigator:
    title: '&6&lNavigatore'
    size: 9
    items:
      survival:
        slot: 4
        material: GRASS_BLOCK
        name: '&a&lSurvival'
        lore:
          - '&7Modalita survival classica'
        command: 'queue survival'
```

### 🛡️ Protezioni
- **No Damage:** I giocatori non possono subire danni
- **No Hunger:** La fame è disabilitata
- **World Protection:** Sistema completo di protezione del mondo
  - Solo player con permesso `world.interaction` possono:
    - Rompere/piazzare blocchi
    - Interagire con porte, chest, lever, ecc.
    - Droppare/raccogliere oggetti
    - Attaccare mob
- **Inventory Lock:** I player non possono muovere item nell'inventario (senza permesso `world.interaction`)

### 📢 Sistema Annunci
Annunci automatici configurabili in `messages.yml`:
- Intervallo personalizzabile (secondi)
- Messaggi con color code
- **Link automaticamente cliccabili**
- Rotazione automatica dei messaggi

### 🎨 Join Actions
Sistema flessibile per azioni automatiche al join:
```yaml
join-actions:
  - '[MESSAGE] &6Benvenuto %player_name%!'
  - '[GAMEMODE] ADVENTURE'
```

### 🎯 Gamemode Personalizzabile
Configura in quali modalità di gioco i player possono usare gli item:
```yaml
item-click-gamemodes:
  - ADVENTURE
  - CREATIVE
  - SURVIVAL
```

## 📦 Installazione

1. Scarica l'ultimo `.jar` dalla sezione [Releases](https://github.com/24Shotty/ShottyLobby/releases)
2. Posiziona il file nella cartella `plugins/` del tuo server
3. Riavvia il server
4. Configura il plugin modificando `plugins/ShottyLobby/config.yml`

## ⚙️ Configurazione

### File Principali

- **`config.yml`** - Configurazione principale del plugin
  - Spawn point
  - Custom items inventory
  - Menu GUI
  - Messaggi social
  - Parkour settings
  - Item click gamemodes

- **`messages.yml`** - Annunci automatici
  - Intervallo messaggi
  - Lista messaggi broadcast

### Placeholder Disponibili

- `%player_name%` - Nome del giocatore
- `%player_displayname%` - Display name del giocatore
- `%player_uuid%` - UUID del giocatore
- `%online_players%` - Numero di giocatori online
- `%max_players%` - Numero massimo di giocatori

## 🔗 Link Cliccabili

Il plugin supporta **link automaticamente cliccabili** in TUTTI i messaggi:
- Messaggi social
- Annunci automatici
- Join messages
- Qualsiasi `[MESSAGE]`

Formati supportati:
- `example.it`
- `www.example.it`
- `https://example.com`
- `http://example.com`

Il plugin aggiunge automaticamente `https://` se necessario.

## 🎨 Color Codes

Tutti i messaggi supportano i color code Minecraft standard usando `&`:
- `&0` - `&9` (colori)
- `&a` - `&f` (colori)
- `&l` (grassetto)
- `&o` (corsivo)
- `&n` (sottolineato)
- `&m` (barrato)
- `&r` (reset)

## 🔑 Permessi

- `world.interaction` - Permette di interagire con il mondo (rompere blocchi, aprire chest, muovere item, ecc.)

## 📝 Esempio Configurazione

```yaml
spawnpoint:
  enable: true
  world: world
  x: 0.0
  y: 64.0
  z: 0.0
  yaw: 180.0
  pitch: 0.0

custom-items:
  enable: true
  items:
    navigator:
      material: NETHER_STAR
      slot: 4
      name: '&6&lNavigatore'
      lore:
        - '&7Apri il menu server'
      actions:
        - '[OPENMENU] navigator'
```

## 🐛 Bug Report

Hai trovato un bug? [Apri una issue](https://github.com/24Shotty/ShottyLobby/issues)

## 💡 Feature Request

Hai un'idea per migliorare il plugin? [Suggeriscila qui](https://github.com/24Shotty/ShottyLobby/issues)

## 👨‍💻 Sviluppatore

Sviluppato da **24Shotty Development Team**

---

⭐ Se ti piace questo plugin, lascia una stella su GitHub!

