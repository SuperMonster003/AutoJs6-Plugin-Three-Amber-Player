<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
    </picture>
  </p>

  <p>Lecture vidéo avec listes, sous-titres et audio en arrière-plan</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md est actuellement disponible dans les langues suivantes:

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

3-Ember Player est le plugin de lecture vidéo du gestionnaire de fichiers AutoJs6, et en même temps un lecteur vidéo local autonome. Appuyez sur un fichier vidéo dans le gestionnaire de fichiers et la lecture démarre en plein écran; gestes, vitesses de lecture, sous-titres externes, lecture enchaînée d'un dossier et incrustation vidéo couvrent l'essentiel de l'expérience des lecteurs les plus répandus. La lecture repose sur AndroidX Media3 ExoPlayer.

Le plugin applique un modèle de sécurité en lecture seule: les vidéos arrivent dans le lecteur via des accès temporaires en lecture seule, aucune autorisation de stockage n'est jamais demandée, et les fichiers sources ne sont jamais modifiés ni déplacés; le réseau sert uniquement à vérifier les mises à jour du plugin.

******

### Points forts

******

- Lecture en un appui: appuyez sur un fichier vidéo dans le gestionnaire de fichiers AutoJs6 et il s'ouvre directement en plein écran immersif, sans aucune configuration.
- Des gestes bien pensés: balayez la moitié gauche pour la luminosité et la moitié droite pour le volume, balayez horizontalement pour naviguer dans la vidéo, faites un double appui sur les côtés pour sauter, un double appui au centre pour lecture/pause, un appui long pour une vitesse temporaire de 2×, et verrouillez toutes les commandes d'un seul appui pour éviter les appuis accidentels.
- Vitesse et image sous contrôle: 9 vitesses de lecture de 0,25× à 3×, zoom par pincement (0,25× à 4×), modes d'affichage adapter/remplir/rogner, et bascule de l'orientation en un appui avec suggestion automatique selon le format de l'image.
- Lecture enchaînée du dossier: ouvrir une vidéo construit une file au tri naturel; son panneau surligne l'élément actuel, affiche les durées connues, permet de sauter et de choisir séquence/aléatoire/répétition d'un élément, avec une invite annulable de trois secondes avant l'enchaînement.
- Sélections ordonnées de l'hôte: l'action en lecture seule Lire la sélection transforme 1 à 128 vidéos d'un dossier en file dans l'ordre exact choisi par l'hôte, sans découverte voisine; un réglage peut mémoriser le mode entre les sessions.
- Sous-titres externes et intégrés: détectez automatiquement les .srt / .ass correspondants ou chargez-en un manuellement, reconnaissez les anciens encodages courants, réglez le style et le décalage externe sur ±600 secondes, puis changez les pistes intégrées; les sous-titres restent désactivés jusqu'à votre activation explicite.
- Lecture de précision: en pause, avancez ou reculez image par image avec répétition par appui long, puis définissez ou effacez une boucle A-B pour examiner les détails.
- Reprise de lecture: la position de la dernière vidéo non terminée est mémorisée et la lecture reprend à la réouverture; les vidéos terminées sont effacées immédiatement, sans laisser de trace de visionnage.
- Incrustation vidéo et intégration système: passage automatique en fenêtre flottante (PiP) sur Android 8.0+ quand vous quittez l'application pendant la lecture, commandes au casque et en Bluetooth, et une notification multimédia avec titre et progression.
- Minuteur de veille: mise en pause automatique après 15/30/45/60 minutes ou à la fin de la vidéo en cours.
- Aperçu du déplacement: faire glisser la barre de progression affiche une bulle avec le temps cible et, quand c'est possible, un aperçu en miniature.
- Capture d'image: sur Android 10+, enregistrez l'image actuelle en PNG dans la galerie système d'un seul appui, sans autorisation de stockage et sans toucher à la vidéo source.
- Filet de secours pour formats difficiles: une couche légère de compatibilité XVID dans MKV est intégrée; quand l'appareil ne peut pas décoder une vidéo, un message clair s'affiche et la lecture peut être confiée à un autre lecteur.
- Des thèmes à votre goût: une seule couleur de base génère une palette claire et une palette sombre lisibles, en suivant AutoJs6 par défaut, avec 19 couleurs prédéfinies et une couleur RGB personnalisée avec aperçu en direct.
- Utilisable en autonomie: livré avec une icône sur l'écran d'accueil et une page de paramètres, il ouvre les vidéos via le sélecteur de fichiers du système et peut servir de lecteur vidéo dans le menu "Ouvrir avec" du système.
- Affichage et sortie: inspectez le HDR10/HLG/SDR et les informations colorimétriques disponibles, copiez le panneau, incluez facultativement les sous-titres dans une capture partageable, inversez l'image de la session ou utilisez une amplification avertie jusqu'à +15 dB.
- Accessible sans gestes: TalkBack conserve des commandes nommées, chaque geste possède un équivalent par bouton ou menu, les dispositions tolèrent une échelle de texte et d'affichage de 200 %, et le clavier/DPAD, Espace/Entrée, la recherche et les touches MediaSession sont pris en charge.
- Audio facultatif en arrière-plan: désactivé par défaut et activable uniquement avec l'autorisation des notifications; si le PiP est indisponible, le lecteur existant passe à un service multimédia de premier plan avec commandes de notification, puis s'arrête à la fin ou à la désactivation. Le PiP reste prioritaire.
- Lecture seule par conception: aucune autorisation de stockage, les vidéos sources ne sont jamais modifiées; le réseau ne sert qu'à vérifier les mises à jour.

******

### Installation et utilisation

******

Avant de commencer, vérifiez que l'environnement remplit les conditions suivantes:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

De l'installation à la première vidéo en 4 étapes:

1. Téléchargez et installez l'APK du plugin. Une icône 3-Ember Player apparaît sur l'écran d'accueil, tandis que les capacités du plugin sont gérées par AutoJs6.
2. Ouvrez AutoJs6, entrez dans le `Centre des plugins`, repérez `3-Ember Player` et activez-le.
3. Repérez n'importe quel fichier vidéo (par exemple `movie.mp4`) dans le gestionnaire de fichiers AutoJs6.
4. Appuyez sur le fichier et la vidéo démarre en plein écran.

Utilisation sans l'hôte: ouvrez 3-Ember Player depuis l'écran d'accueil, appuyez sur `Ouvrir une vidéo` et choisissez une vidéo via le sélecteur de fichiers du système; les demandes de lecture vidéo venant d'autres applications peuvent aussi être prises en charge par ce lecteur. L'exigence de version de l'hôte ci-dessus ne concerne que l'entrée du gestionnaire de fichiers; la lecture autonome n'est pas affectée.

Mémo des gestes du lecteur:

- Appui simple: afficher ou masquer la barre de commandes.
- Double appui au centre: lecture/pause; double appui sur le côté gauche/droit: reculer/avancer de 10 secondes (réglable sur 5/10/30 secondes dans les paramètres).
- Balayage vertical sur la moitié gauche: régler la luminosité; sur la moitié droite: régler le volume.
- Balayage horizontal: prévisualiser la position cible, relâcher pour l'appliquer.
- Appui long: vitesse temporaire de 2×, relâcher pour retrouver la vitesse d'origine.
- Pincement à deux doigts: zoomer sur l'image (0,25× à 4×); un double appui pendant le zoom réinitialise l'image.
- Bouton de verrouillage: bloque tous les gestes et toutes les commandes contre les appuis accidentels; appuyez ensuite sur l'écran pour faire apparaître le bouton de déverrouillage.

******

### Formats pris en charge

******

L'action de lecture du gestionnaire de fichiers correspond exactement aux 23 extensions suivantes:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

La liste ci-dessus est une liste blanche d'extensions exactes conservée pour les anciens hôtes; sur les hôtes récents, tout fichier reconnu comme vidéo est confié à ce plugin via `video/*`. Figurer dans la liste ne garantit pas le décodage sur chaque appareil; la capacité réelle de lecture dépend de Media3 et des décodeurs de la plateforme. L'entrée autonome et les appels d'autres applications sont eux aussi acceptés selon le type MIME `video/*`.

******

### FAQ

******

**Appuyer sur un fichier vidéo n'ouvre pas ce lecteur?**

Vérifiez dans l'ordre: le code de version d'AutoJs6 est au moins 5276 (soit la version 6.8.0 et au-delà); le plugin est activé dans le `Centre des plugins`; le fichier est reconnu comme vidéo par l'hôte. Si l'une de ces trois conditions n'est pas remplie, l'appui n'est pas pris en charge par ce plugin.

**Que se passe-t-il si le plugin est absent ou désactivé?**

Un hôte compatible affiche un guide de récupération qui propose d'installer ou d'activer le plugin; le sélecteur d'applications du système n'apparaît que lorsque l'utilisateur choisit explicitement `Ouvrir avec une autre application`, ce qui confie la vidéo aux autres lecteurs installés sur l'appareil.

**Le son joue mais l'écran reste noir, ou la lecture échoue?**

Le décodage d'une vidéo dépend de la plateforme de l'appareil et de Media3; figurer dans la liste d'extensions ne garantit pas la lecture. Pour l'ancien format courant XVID dans MKV, une couche de compatibilité intégrée confie la piste au décodeur MPEG-4 Part 2 du système; si le décodage échoue malgré tout, un message clair s'affiche et `Ouvrir avec une autre application` transmet la vidéo à un autre lecteur.

**Comment lire toutes les vidéos d'un dossier à la suite?**

Avec un hôte remplissant l'exigence de version, ouvrir n'importe quelle vidéo depuis le gestionnaire de fichiers construit automatiquement une file de lecture du même dossier: tri naturel des noms de fichiers, départ à partir de la vidéo actuelle, enchaînement automatique sur la suivante à la fin de chacune, avec les modes séquentiel, aléatoire et répétition d'un seul élément. Sur les hôtes plus anciens ou via l'entrée autonome, la lecture reste limitée à un seul fichier.

**Comment charger des sous-titres externes?**

Placez un fichier `.srt` ou `.ass` du même nom à côté de la vidéo (les suffixes de langue comme `movie.fr.srt` sont acceptés), ouvrez la vidéo depuis le gestionnaire de fichiers, puis choisissez le sous-titre dans le menu des sous-titres. Les sous-titres sont désactivés par défaut et ne s'activent jamais tout seuls.

**Que mémorise la reprise de lecture? Des données sont-elles envoyées?**

Seule la position de la dernière vidéo non terminée est conservée, sous forme d'un condensé SHA-256 du fichier et de valeurs temporelles, sans nom de fichier ni chemin; ouvrir une autre vidéo ou terminer la lecture l'efface immédiatement. Tout reste sur l'appareil et rien n'est envoyé.

**De quelles autorisations le plugin a-t-il besoin?**

Aucune autorisation sensible d'exécution: ni stockage, ni caméra, ni microphone. Il déclare seulement l'autorisation réseau pour vérifier les mises à jour (déclenchées par l'utilisateur ou au plus une fois par jour), plus l'autorisation de plugin protégée par signature pour l'entrée du gestionnaire de fichiers.

**Peut-on l'utiliser indépendamment d'AutoJs6?**

Oui. Depuis la v2.0.0, le plugin dispose d'une entrée sur l'écran d'accueil: choisissez une vidéo via le sélecteur de fichiers du système et lisez-la, ou sélectionnez ce lecteur dans le menu `Ouvrir avec` d'une autre application. La lecture enchaînée d'un dossier, la détection des sous-titres externes et le suivi des paramètres de l'hôte nécessitent toujours AutoJs6.

******

### Sécurité

******

Le plugin est construit sur un principe de refus par défaut; toutes les mesures ci-dessous sont toujours actives et ne peuvent pas être désactivées:

- Zéro autorisation sensible: aucune autorisation de stockage ni autre autorisation d'exécution; l'accès réseau ne sert qu'aux vérifications des versions GitHub, déclenchées par l'utilisateur ou au plus une fois par jour.
- Aucune écriture: la lecture, la capture d'image et l'extraction de miniatures sont en lecture seule de bout en bout; les vidéos sources ne sont jamais modifiées, déplacées ni supprimées.
- Validation entrée par entrée: l'entrée du gestionnaire de fichiers est protégée par une autorisation de plugin de niveau signature, et chaque requête voit sa version de protocole, son URI cible, son ClipData, ses métadonnées, sa version d'hôte et ses accès en lecture seule vérifiés point par point; le moindre écart rejette la requête.
- Accès borné aux fichiers frères: la mise en file et la détection des sous-titres passent par une session éphémère gérée par l'hôte, qui ne peut énumérer que les frères directs du fichier sélectionné, avec interdiction des dossiers récursifs, des écritures et des accès persistants; le lecteur interne ne reçoit qu'une file bornée et validée et ne touche jamais aux chemins du système de fichiers.
- Deux entrées isolées: l'entrée `ACTION_VIEW` tournée vers le système n'accepte que des requêtes `video/*` avec content URI en lecture seule, rejette les accès en écriture, persistants et prefix, et reste indépendante de l'entrée du gestionnaire de fichiers.
- Données de reprise minimales: l'historique de reprise ne conserve que le dernier enregistrement non terminé, sous forme d'un condensé SHA-256 et de valeurs temporelles, effacé dès que la lecture se termine.
- Transfert sécurisé: `Ouvrir avec une autre application` reconstruit un Intent en lecture seule et exclut ce plugin des candidats, ce qui empêche la propagation des accès et les boucles sur lui-même.

******

### Interface du plugin (pour les développeurs)

******

L'hôte découvre et appelle le plugin via les identifiants suivants:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
engine: explorer-action
variant: default
protocol version: v12
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

L'implémentation repose sur explorer-action v12 : le plugin enregistre l'action principale en lecture seule `play-video` et l'action de barre de sélection `play-video-selection`, accepte les types reconnus via `video/*` et conserve 23 extensions exactes pour les anciens hôtes. L'action principale peut lire les voisins de façon bornée pour la file et les sous-titres; l'action de sélection conserve 1 à 128 cibles de l'hôte et n'active jamais cette lecture. Les sessions compatibles peuvent aussi porter la progression, et toute capacité facultative absente se replie sans risque. Audio et images restent des plugins distincts de cet APK.

La coopération complète avec l'hôte nécessite AutoJs6 6.8.0 (build 5276) ou ultérieur avec Explorer Action v12; les futures mises à jour du plugin ne relèveront pas cette exigence.

******

### Feuille de route

******

Les capacités livrées et les projets à venir sont tenus à jour sous forme de liste à cocher dans Roadmap.md. Les éléments non cochés expriment une intention et ne décrivent pas les capacités de la version actuelle.

- [Ouvrir Roadmap.md et sa liste à cocher](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### Historique des versions

******

#### v3.0.0

###### 2026/09/11

* `Fonctionnalité` Audit complet de l'accessibilité du lecteur: commandes nommées avec état dynamique, commandes persistantes pendant l'exploration tactile TalkBack, équivalent par bouton ou menu pour chaque geste, ordre de focus déterministe et validation à 200 % pour le texte et l'affichage
* `Fonctionnalité` Prise en charge du clavier et de la télécommande: anneaux de focus visibles, navigation DPAD, lecture/pause avec Espace/Entrée, recherche gauche/droite selon le pas configuré de 5/10/30 secondes et touches multimédias via MediaSession
* `Fonctionnalité` Audio facultatif en arrière-plan, désactivé par défaut: après autorisation des notifications, la lecture active peut passer sans coupure à un service multimédia de premier plan avec commandes de notification lorsque le PiP est indisponible
* `Amélioration` Le PiP reste toujours prioritaire; désactiver le réglage, terminer la lecture, rencontrer une erreur ou quitter volontairement arrête le service, tandis qu'un appui sur la notification récupère le même lecteur à la position continue
* `Amélioration` Le comportement du casque suit Media3: un appui bascule lecture/pause, le double appui du périphérique avance si la file contient un élément suivant et une commande Previous explicite revient en arrière; aucun délai de triple appui non standard n'est imposé
* `Amélioration` Uniformiser la mise en page du README et la gestion des versions de la plateforme Gradle
* `Amélioration` Simplifier la description du plugin et normaliser la ponctuation des ressources multilingues
* `Amélioration` Renommer l'entrée de visualisation externe en External Viewer pour unifier la sémantique de la visionneuse
* `Amélioration` Ouvrir la page intégrée de l'historique des versions depuis le bouton correspondant de la boîte de dialogue de mise à jour
* `Amélioration` La vérification de compilation rejette les dépendances natives involontaires et produit un rapport JSON

#### v2.3.0

###### 2026/08/31

* `Fonctionnalité` Les informations vidéo identifient désormais HDR10, HLG ou SDR et affichent, si disponibles, l'espace colorimétrique, la plage et la profondeur; tous les champs se copient en une fois et un avertissement unique apparaît si l'écran ne signale pas la prise en charge du format HDR source
* `Fonctionnalité` Les captures peuvent inclure les sous-titres visibles via un réglage désactivé par défaut, puis l'action Partager envoie directement le nouveau PNG vers une autre application
* `Fonctionnalité` Un nouveau miroir vidéo limité à la session inverse gauche/droite, haut/bas ou les deux axes et se combine de façon cohérente avec le zoom par pincement et la rotation de l'écran
* `Fonctionnalité` Une amplification limitée à la session propose de +3 dB à +15 dB, reste désactivée par défaut, avertit des risques de distorsion et d'audition avant la première utilisation et revient à l'arrêt si elle n'est pas prise en charge
* `Amélioration` Les captures restent des PNG MediaStore indépendants sans permission de stockage; le partage n'accorde qu'un accès en lecture à l'image choisie
* `Amélioration` LoudnessEnhancer a été retenu après évaluation car il applique un gain borné à la lecture sans modifier les médias; tout échec de l'effet libère immédiatement les ressources et revient à un état sûr

#### v2.2.0

###### 2026/08/31

* `Fonctionnalité` Nouveau panneau de file Host Session : noms, durées connues ou espaces réservés, élément actuel surligné, saut au toucher et commande synchronisée séquence/aléatoire/répétition d'un élément
* `Fonctionnalité` Nouvelle action en lecture seule Lire la sélection : lit de 1 à 128 vidéos compatibles d'un même dossier dans l'ordre choisi par l'hôte, sans découverte des fichiers voisins
* `Fonctionnalité` Avant la lecture automatique de la file, conserve la dernière image et affiche pendant trois secondes une invite annulable pour l'élément suivant sans déverrouiller les commandes
* `Fonctionnalité` Nouveau réglage Mémoriser le mode de lecture, désactivé par défaut, pour conserver séquence, aléatoire ou répétition d'un élément entre les sessions
* `Amélioration` Chaque cible est contrôlée séparément avec son URI content, sa position ClipData, son MIME, ses métadonnées, sa taille et son dossier ; les ID ou URI en double rejettent tout le groupe
* `Amélioration` La sélection multiple lit uniquement les cibles explicitement autorisées via leurs routes Host Session bornées et n'analyse jamais les fichiers voisins pour rechercher vidéos ou sous-titres

##### Historique complet

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

Les paramètres de compilation proviennent de `version.properties`; le SDK minimal actuel est 24 et le SDK cible est 36.

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

`strings.xml` localise les informations du plugin et l'interface du lecteur, et `plugin_instruction.md` fournit les notes d'utilisation affichées côté hôte. Tous les fichiers README et CHANGELOG sont générés à partir des sources JSON par `.python/generate_markdown.py`: pour modifier la documentation, éditez les fichiers `lang_*.json` sous `.readme` et `.changelog` puis relancez le script, au lieu d'éditer les fichiers Markdown générés.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (moteur de lecture): https://developer.android.com/media/media3/exoplayer
- Partage sécurisé de fichiers Android: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Ember-Player/blob/master/docs/16kb.md)
