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
