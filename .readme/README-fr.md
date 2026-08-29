<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
  </p>

  <p>Plugin de gestionnaire de fichiers. Lire directement les fichiers vidéo</p>

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

3-Ember Player est à la fois un plugin du gestionnaire de fichiers AutoJs6 et un lecteur vidéo autonome simple. Il accepte des content URI temporaires en lecture seule, utilise AndroidX Media3 ExoPlayer et ne modifie jamais la vidéo source.

******

### Fonctionnalités

******

- Compatibilité légère de XVID dans MKV via le décodeur MPEG-4 Part 2 intégré de l’appareil, avec transfert explicite vers un autre lecteur lorsque le décodeur système est indisponible ou échoue.
- Enregistre une action principale de l'explorateur en lecture seule via la version 12 du protocole partagé `org.autojs.plugin.EXPLORER_ACTION`, avec des capacités facultatives et bornées pour les fichiers frères et la progression.
- Utilise AndroidX Media3 ExoPlayer et PlayerView pour la lecture automatique, les commandes standard, le focus audio, la gestion du passage à une sortie audio noisy et l'intégration aux codecs de l'appareil.
- Restaure la position et l'intention de lecture ou de pause après une recréation, et maintient l'écran allumé uniquement pendant la lecture active de la vidéo.
- Fournit une entrée `android.intent.action.VIEW` exportée distincte pour les content URI en lecture seule avec un type MIME `video/*`.
- Propose une action sécurisée Ouvrir avec une autre application après un échec de lecture en reconstruisant un Intent de visualisation en lecture seule et en excluant ce plugin de la liste des candidats.
- Offre une lecture immersive en plein écran avec gestes pour la luminosité, le volume et la navigation, sauts par double touche, vitesse temporaire par appui long et verrouillage des commandes.
- Propose des vitesses de lecture de 0,25× à 3× ainsi que la bascule en un geste du mode d'affichage et de l'orientation de l'écran.
- L’historique de reprise conserve exactement la dernière vidéo inachevée sous forme d’un condensé d’identité SHA-256 et de valeurs temporelles ; l’ouverture d’une autre vidéo l’efface immédiatement et une lecture terminée n’est jamais conservée.
- Permet de sélectionner les pistes audio et les sous-titres intégrés, désactive les sous-titres par défaut et signale clairement les pistes non prises en charge.
- Propose la répétition de la vidéo actuelle, un panneau de métadonnées détaillé, l’orientation selon le format et l’incrustation vidéo sur API 26+ avec lecture/pause à distance.
- Intègre MediaSession pour les commandes des écouteurs, du Bluetooth et du système, avec une notification multimédia affichant le titre et la progression.
- Minuteur de 15, 30, 45 ou 60 minutes et de fin de vidéo, avec sensibilité persistante et saut par double appui de 5/10/30 secondes.
- Zoom par pincement de 0,25× à 4× avec réinitialisation par double appui, temps cible lors du déplacement et miniatures facultatives en mémoire.
- Captures de l’image actuelle sans autorisation sur Android 10+, enregistrées comme PNG séparés via MediaStore sans modifier la vidéo source.
- Files d'attente vidéo du même dossier triées naturellement via Explorer Action v12, avec précédent / suivant, séquence, aléatoire, répétition d'un élément et lecture automatique du suivant.
- Découverte des sous-titres externes .srt et .ass correspondants, y compris les suffixes de langue, désactivés par défaut et chargés uniquement après une sélection explicite.
- Génère des rôles sémantiques clairs et sombres lisibles depuis une couleur HCT, suit AutoJs6 par défaut et propose 19 couleurs Material 500 localisées ainsi qu’un RGB personnalisé prévisualisé.
- Fournit un lanceur autonome et des paramètres de langue, nuit et couleur suivant l’hôte, reprise d’une seule vidéo, vérifications manuelles et automatiques, versions ignorées, historique et informations sur l’application et le développeur.

******

### Intégration à l'hôte

******

L'hôte utilise ce plugin pour le chemin principal d'ouverture des vidéos et l'action Lire.

Une fois le plugin installé, activé, approuvé et compatible, l'ouverture de tout fichier reconnu comme vidéo par l'hôte lance l'action `play-video` comme visionneuse principale de l'explorateur.

Si le plugin est absent, désactivé, non autorisé, indisponible, incompatible ou impossible à lancer, un hôte compatible affiche des instructions de récupération. Le sélecteur d'applications système ne s'ouvre qu'après le choix explicite Ouvrir avec d'autres applications.

Ce plugin reconnaît uniquement les fichiers vidéo. La lecture audio et l'affichage des images restent des capacités de plugins indépendantes et ne sont pas intégrés à cet APK.

******

### Formats pris en charge

******

L'action principale de l'explorateur accepte `video/*` pour tous les types vidéo reconnus par l'hôte et conserve ces 23 filtres d'extension exacts pour la compatibilité avec les anciens hôtes:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Interface du plugin

******

L'hôte découvre et exécute le plugin avec les identités suivantes:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

La version 2.0 fournit une action principale de l'explorateur en lecture seule selon le protocole v12. Un hôte compatible peut joindre des capacités par requête pour les frères directs et la progression de lecture ; les hôtes sans ces extensions facultatives conservent la lecture d'un seul fichier. L'entrée externe indépendante reste limitée à un content URI en lecture seule avec un sous-type MIME `video/*` valide.

Les fonctions complètes de coopération nécessitent AutoJs6 6.8.0 build 5276 ou ultérieur et Explorer Action v12 ; cette exigence ne sera pas relevée par les capacités futures du plugin.

******

### Sécurité

******

L’application ne demande aucune autorisation de stockage et n’écrit jamais les vidéos sources. Internet sert uniquement aux vérifications des versions GitHub lancées par l’utilisateur ou quotidiennes. Sa frontière de l'explorateur protégée par signature valide l'enveloppe complète du protocole v12, l'unique cible sélectionnée, la relation au parent, ClipData, les métadonnées, la version de l'hôte et les accès en lecture seule. Les Host Sessions facultatives sont liées par l'hôte à l'UID du plugin et permettent seulement de lister le parent direct du fichier sélectionné et d'ouvrir celui-ci ou un frère direct lisible. Le lecteur privé valide une file opaque bornée et ne reçoit jamais de chemin de système de fichiers. La frontière publique ACTION_VIEW reste indépendante et limitée à un fichier.

******

### Limites de sécurité

******

- L'explorateur part exactement d'un content URI sélectionné ; une Host Session facultative ne peut exposer que les frères directs lisibles et jamais un accès récursif aux dossiers.
- L'exécution par l'explorateur nécessite l'autorisation de niveau signature `org.autojs.permission.PLUGIN`.
- Les accès en écriture et persistants sont toujours refusés. L'accès prefix utilisé pour valider l'URI parent n'est jamais transmis au lecteur.
- La frontière publique ACTION_VIEW refuse les accès en écriture, persistants et prefix.
- Les candidats externes sont d'abord résolus, filtrés pour conserver les autres paquets, puis lancés avec un nouvel Intent en lecture seule.
- L’historique de reprise conserve exactement la dernière vidéo inachevée sous forme d’un condensé d’identité SHA-256 et de valeurs temporelles ; l’ouverture d’une autre vidéo l’efface immédiatement et une lecture terminée n’est jamais conservée.
- La reconnaissance comme vidéo ou une extension héritée répertoriée ne garantit pas le décodage sur tous les appareils. Media3 et les codecs de plateforme installés déterminent la compatibilité réelle de lecture.

******

### Historique des versions

******

# v2.0.0

###### 2026/08/29

* `Fonctionnalité` Ajout d’un système de couleurs HCT détaillé qui génère depuis une seule couleur des rôles sémantiques lisibles en modes clair et sombre pour barres, commandes, surfaces, contours et erreurs, avec 19 couleurs Material 500 localisées et un RGB personnalisé prévisualisé
* `Fonctionnalité` Ajout d’un écran de lancement et d’un lecteur autonome pour un fichier, ainsi que de paramètres dédiés à la langue, au mode nuit, à la couleur, à la reprise, aux mises à jour, à l’historique et aux informations sur l’application et le développeur
* `Fonctionnalité` La langue, le mode nuit et la couleur suivent AutoJs6 par défaut via son contrat officiel en lecture seule ; si l’hôte est indisponible, les choix restent visibles mais désactivés et reviennent aux valeurs par défaut
* `Fonctionnalité` Ajout de la vérification manuelle et automatique quotidienne, de la gestion des versions ignorées et d’un historique localisé intégré
* `Correctif` Le suivi d’AutoJs6 est désormais fiable grâce à l’entrée protégée d’informations du plugin requise par le fournisseur de paramètres de l’hôte
* `Amélioration` La reprise mémorise exactement la dernière vidéo ouverte, efface immédiatement l’ancienne à l’ouverture d’une autre et ne conserve jamais une lecture terminée, dans l’historique local comme celui de l’hôte
* `Amélioration` Le nom fixe de l’application et du plugin devient 3-Ember Player et l’espace de noms source threeemberplayer, tout en conservant les identifiants publiés pour les mises à niveau

# v1.4.0

###### 2026/08/28

* `Fonctionnalité` Création de files vidéo du même dossier triées naturellement via une Host Session Explorer Action v12 limitée à la requête et liée à l'UID, avec précédent / suivant, séquence, lecture aléatoire, répétition d'un élément et passage automatique au suivant
* `Fonctionnalité` Détection des sous-titres externes .srt et .ass correspondants, y compris les variantes à suffixe de langue ; ils restent désactivés par défaut et ne sont chargés qu'après une sélection explicite
* `Fonctionnalité` Historique de reprise facultatif géré par l'hôte : l'enregistrement est désactivé par défaut, peut être désactivé ou effacé dans les paramètres AutoJs6 et n'ajoute aucun indicateur vu à la liste des fichiers
* `Correctif` Les requêtes Explorer classées comme vidéo par l'hôte étaient rejetées si leur extension ne figurait pas dans l'ancienne liste de 23 éléments ; les requêtes `video/*` approuvées sont désormais acceptées uniformément
* `Amélioration` L'accès est limité au fichier sélectionné et aux fichiers frères directs lisibles, sans parcours récursif, écriture, autorisation persistante ni chemin en clair dans le plugin
* `Amélioration` Les files sérialisées sont limitées à 128 vidéos, 8 sous-titres par vidéo et 128 associations de sous-titres au total, tout en conservant toujours l'élément sélectionné
* `Amélioration` La compatibilité reste fixée à AutoJs6 6.8.0 build 5276 et Explorer Action v12 ; les hôtes sans extensions facultatives conservent en toute sécurité la lecture d'un seul fichier
* `Dépendance` Mise à niveau de l'API Explorer Action intégrée du protocole v2 vers l'extension de session multimédia v12 rétrocompatible

# v1.3.1

###### 2026/08/27

* `Correctif` Ajout d’une couche légère de compatibilité XVID dans MKV qui confie les pistes XVID VFW/FourCC validées au décodeur MPEG-4 Part 2 intégré de l’appareil, sans transcodage ni modification de la source
* `Correctif` Arrêt de la lecture audio seule lorsqu’aucun décodeur système compatible n’est disponible ou que le décodage échoue, avec une explication dédiée et l’ouverture dans une autre application

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
