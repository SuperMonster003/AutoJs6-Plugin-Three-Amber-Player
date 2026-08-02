<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Lecture vidéo en lecture seule pour l'explorateur AutoJs6 avec gestion sécurisée des content URI</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

Le plugin AutoJs6 Video Player lit le contenu vidéo fourni par l'explorateur AutoJs6 ou une autre application Android au moyen d'un accès temporaire en lecture seule à un content URI. Il utilise AndroidX Media3 ExoPlayer et ne modifie jamais la vidéo source.

******

### Fonctionnalités

******

- Enregistre une action principale de l'explorateur en lecture seule pour un seul fichier via la version 2 du protocole partagé `org.autojs.plugin.EXPLORER_ACTION`.
- Utilise AndroidX Media3 ExoPlayer et PlayerView pour la lecture automatique, les commandes standard, le focus audio, la gestion du passage à une sortie audio noisy et l'intégration aux codecs de l'appareil.
- Restaure la position et l'intention de lecture ou de pause après une recréation, et maintient l'écran allumé uniquement pendant la lecture active de la vidéo.
- Fournit une entrée `android.intent.action.VIEW` exportée distincte pour les content URI en lecture seule avec un type MIME `video/*`.
- Propose une action sécurisée Ouvrir avec une autre application après un échec de lecture en reconstruisant un Intent de visualisation en lecture seule et en excluant ce plugin de la liste des candidats.

******

### Intégration à l'hôte

******

AutoJs6 intègre ce plugin au chemin principal d'ouverture vidéo dans `app/src/main/java/org/autojs/autojs/ui/explorer/ExplorerView.kt` et à l'action Lire dans `app/src/main/java/org/autojs/autojs/ui/main/scripts/MediaInfoDialogManager.kt`.

Une fois le plugin installé, activé, approuvé et compatible, l'ouverture d'une vidéo correspondante lance l'action `play-video` comme visionneuse principale de l'explorateur.

Si le plugin est absent, désactivé, indisponible, incompatible ou impossible à lancer, AutoJs6 revient à sa route système `android.intent.action.VIEW` afin qu'une autre application vidéo installée puisse traiter le fichier.

Ce plugin reconnaît uniquement les fichiers vidéo. La lecture audio et l'affichage des images restent des capacités de plugins indépendantes et ne sont pas intégrés à cet APK.

******

### Formats pris en charge

******

L'action principale de l'explorateur utilise un filtre MIME vide et correspond exactement à ces 23 extensions vidéo de l'hôte uniquement:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Interface du plugin

******

AutoJs6 découvre et exécute le plugin avec les identités suivantes:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: empty
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5269
supported ABIs: unrestricted (supportedAbis = emptyArray())
```

La version 1 fournit une action principale de l'explorateur en lecture seule pour les extensions vidéo indiquées. L'entrée externe indépendante accepte un content URI en lecture seule avec tout sous-type MIME `video/*` valide. Le décodage réel dépend des extracteurs Media3 et des codecs disponibles sur l'appareil.

Le plugin est entièrement implémenté sur la JVM et ne contient aucune bibliothèque native. Il déclare `supportedAbis = emptyArray()` et est publié sous la forme d'un APK unique indépendant de l'ABI. La version 5269 ou ultérieure de l'hôte AutoJs6 est requise.

******

### Sécurité

******

Le plugin ne demande aucune autorisation de stockage ou INTERNET. Sa frontière de l'explorateur protégée par signature valide la version 2 du protocole, la surface source, la version de l'hôte, les content URI cible et parent, l'ordre exact de ClipData, le nom affiché, la taille déclarée, le type MIME, l'extension et les indicateurs d'accès en lecture seule. Elle crée ensuite un nouvel Intent explicite pour le lecteur non exporté contenant uniquement l'URI cible, le type MIME, un nom affiché sûr, un élément ClipData cible et l'accès en lecture. La frontière publique ACTION_VIEW accepte séparément uniquement un content URI, un type MIME vidéo et l'accès en lecture exact, ignore tous les extras et ClipData entrants, puis reconstruit la même requête interne minimale.

******

### Limites de sécurité

******

- Un content URI cible par demande de lecture.
- L'exécution par l'explorateur nécessite l'autorisation de niveau signature `org.autojs.permission.PLUGIN`.
- Les accès en écriture et persistants sont toujours refusés. L'accès prefix utilisé pour valider l'URI parent n'est jamais transmis au lecteur.
- La frontière publique ACTION_VIEW refuse les accès en écriture, persistants et prefix.
- Les candidats externes sont d'abord résolus, filtrés pour conserver les autres paquets, puis lancés avec un nouvel Intent en lecture seule.
- Une extension répertoriée ne garantit pas la prise en charge du décodage sur tous les appareils. Media3 et les codecs de plateforme installés déterminent la compatibilité réelle de lecture.

******

### Historique des versions

******

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Plugin Video Player avec ID de plugin `video-player`, ID d'action `play-video`, moteur `explorer-action` et variante `default`
* `Fonctionnalité` Action principale de l'explorateur en lecture seule via le protocole v2, avec filtre MIME vide, correspondant uniquement aux 23 extensions vidéo actuelles de l'hôte et nécessitant la version 5269 de l'hôte AutoJs6
* `Fonctionnalité` Entrée de l'explorateur protégée par signature avec validation stricte des content URI cible et parent, de ClipData, de la source, du nom affiché, de la taille, du type MIME, de l'extension et des accès, suivie d'une transmission minimale vers un lecteur non exporté
* `Fonctionnalité` Entrée ACTION_VIEW exportée et indépendante pour les content URI vidéo en lecture seule, avec abandon des extras et ClipData non fiables, refus des accès interdits et protection contre les boucles internes
* `Fonctionnalité` Lecture Media3 ExoPlayer et PlayerView avec démarrage automatique, commandes standard, focus audio, gestion du passage à une sortie audio noisy, sauvegarde de la position et de l'état de lecture, et écran allumé uniquement pendant la lecture active
* `Fonctionnalité` Récupération sécurisée Ouvrir avec une autre application après un échec de lecture, au moyen de nouveaux Intents en lecture seule qui excluent explicitement ce plugin
* `Fonctionnalité` Implémentation JVM pure sans bibliothèque native, ABI sans restriction déclarés par `supportedAbis = emptyArray()` et un APK indépendant de l'ABI
* `Fonctionnalité` Métadonnées, texte d'interface, instructions d'utilisation, fichiers README et historiques localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan
* `Dépendance` Ajout de AndroidX Media3 ExoPlayer et UI version 1.10.1
* `Dépendance` Ajout du runtime Kotlin Parcelize version 2.2.21

##### Pour consulter davantage de versions

* [CHANGELOG-fr.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilation Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Les paramètres de compilation proviennent de `version.properties`. Le SDK minimal actuel est 24 et le SDK cible est 36.

******

### Structure des ressources

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localise les métadonnées du plugin et le texte de l'interface. `plugin_instruction.md` fournit les instructions d'utilisation et de sécurité. `.python/generate_markdown.py` génère les fichiers README et les historiques localisés depuis les sources JSON.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- Partage sécurisé de fichiers Android: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
