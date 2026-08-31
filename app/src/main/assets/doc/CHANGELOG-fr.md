******

### Historique des versions

******

# v2.2.0

###### 2026/08/31

* `Fonctionnalité` Nouveau panneau de file Host Session : noms, durées connues ou espaces réservés, élément actuel surligné, saut au toucher et commande synchronisée séquence/aléatoire/répétition d'un élément
* `Fonctionnalité` Nouvelle action en lecture seule Lire la sélection : lit de 1 à 128 vidéos compatibles d'un même dossier dans l'ordre choisi par l'hôte, sans découverte des fichiers voisins
* `Fonctionnalité` Avant la lecture automatique de la file, conserve la dernière image et affiche pendant trois secondes une invite annulable pour l'élément suivant sans déverrouiller les commandes
* `Fonctionnalité` Nouveau réglage Mémoriser le mode de lecture, désactivé par défaut, pour conserver séquence, aléatoire ou répétition d'un élément entre les sessions
* `Amélioration` Chaque cible est contrôlée séparément avec son URI content, sa position ClipData, son MIME, ses métadonnées, sa taille et son dossier ; les ID ou URI en double rejettent tout le groupe
* `Amélioration` La sélection multiple lit uniquement les cibles explicitement autorisées via leurs routes Host Session bornées et n'analyse jamais les fichiers voisins pour rechercher vidéos ou sous-titres

# v2.1.0

###### 2026/08/31

* `Fonctionnalité` Paramètres de style des sous-titres: taille de 75% à 150%, couleur, fond opaque/semi-transparent/absent et marge basse de 0%/4%/8% avec aperçu en direct; les choix persistent en lecture et en incrustation
* `Fonctionnalité` Détection de l'encodage des sous-titres externes: BOM en priorité, puis GBK, Big5, Shift_JIS, EUC-KR, Windows-1251 et Windows-1256, avec un avertissement clair en cas d'incertitude
* `Fonctionnalité` Chargement manuel d'un sous-titre .srt ou .ass depuis le menu via le sélecteur de documents système; les fichiers de plus de 4 Mio ou d'une autre extension sont refusés
* `Fonctionnalité` Décalage du sous-titre externe sélectionné de −600,0 à +600,0 secondes par pas de 0,1 seconde avec retour immédiat à l'écran
* `Fonctionnalité` En pause, avance ou recul image par image (appui long pour répéter) et boucle d'intervalle A-B avec normalisation des limites
* `Amélioration` Le décodage, la conversion UTF-8 et le décalage temporel des sous-titres restent entièrement en mémoire, sans fichier temporaire, autorisation de stockage ni droit persistant
* `Amélioration` Le décalage revient à zéro à chaque changement de vidéo; la boucle A-B est prioritaire tant qu'elle est active, puis un mode de répétition ou un minuteur de fin explicitement choisi l'efface

# v2.0.0

###### 2026/08/29

* `Fonctionnalité` Tout nouveau système de thèmes: une seule couleur de base génère une palette claire et une palette sombre lisibles, en suivant le thème AutoJs6 par défaut, avec 19 couleurs prédéfinies et une couleur RGB personnalisée avec aperçu en direct
* `Fonctionnalité` Le plugin devient une application autonome: une entrée sur l'écran d'accueil est ajoutée, et les vidéos peuvent être ouvertes directement via le sélecteur de fichiers du système
* `Fonctionnalité` Nouvelle page de paramètres: langue, mode nuit, couleur du thème, reprise, mises à jour et historique des versions réunis au même endroit; la langue, le mode nuit et la couleur du thème suivent AutoJs6 par défaut, et ces options sont désactivées avec les valeurs par défaut de l'application quand l'hôte est indisponible
* `Fonctionnalité` Nouvelle vérification des mises à jour: vérification manuelle et vérification automatique quotidienne des versions officielles GitHub, avec possibilité d'ignorer des versions et une page d'historique des versions localisée intégrée
* `Correctif` Correction du suivi des paramètres AutoJs6 qui ne prenait pas effet dans certains scénarios (le service d'informations du plugin requis par l'hôte n'était pas exposé auparavant)
* `Amélioration` L'historique de reprise est réduit à la seule dernière vidéo non terminée; ouvrir une autre vidéo efface immédiatement l'ancien enregistrement, et les vidéos terminées ne conservent aucune position
* `Amélioration` L'application est renommée 3-Ember Player; l'ID d'application et l'ID de plugin restent inchangés, les installations existantes se mettent donc à niveau directement

# v1.4.0

###### 2026/08/28

* `Fonctionnalité` Lecture enchaînée du dossier: ouvrir une vidéo construit automatiquement une file de lecture du même dossier (tri naturel des noms de fichiers), avec précédent/suivant, les modes séquentiel, aléatoire et répétition d'un seul élément, et l'enchaînement automatique sur la vidéo suivante
* `Fonctionnalité` Sous-titres externes: les fichiers .srt / .ass du même nom et leurs variantes à suffixe de langue sont détectés automatiquement; les sous-titres restent désactivés tant qu'ils ne sont pas choisis dans le menu des sous-titres
* `Fonctionnalité` Historique de reprise facultatif géré par l'hôte: désactivé par défaut, il peut être activé, désactivé ou effacé dans les paramètres AutoJs6; les listes de fichiers n'affichent aucun marqueur de visionnage
* `Correctif` Correction du rejet de fichiers reconnus comme vidéo par l'hôte parce que leur extension manquait dans l'ancienne liste blanche; les requêtes video/* de confiance sont désormais acceptées uniformément
* `Amélioration` L'accès aux fichiers frères est strictement limité au fichier sélectionné et à ses frères directs lisibles, avec interdiction des dossiers récursifs, des écritures et des accès persistants; le plugin ne stocke aucun chemin en clair
* `Amélioration` La file de lecture est plafonnée à 128 vidéos avec jusqu'à 8 sous-titres externes chacune, et la vidéo choisie par l'utilisateur reste toujours dans la file
* `Amélioration` La base de compatibilité est fixée à AutoJs6 6.8.0 (build 5276); les hôtes plus anciens reviennent à la lecture d'un seul fichier, sans impact sur les fonctions de base
* `Dépendance` Mise à niveau de l'API Explorer Action intégrée du protocole v2 vers la v12 rétrocompatible

# v1.3.1

###### 2026/08/27

* `Correctif` Ajout d'une couche de compatibilité XVID dans MKV: ces vidéos sont désormais lues par le décodeur MPEG-4 Part 2 intégré de l'appareil, sans transcodage et sans toucher au fichier source
* `Correctif` Quand l'appareil n'a pas de décodeur compatible ou que le décodage échoue, la lecture avec le son seul ne se produit plus; un message clair s'affiche à la place, avec la possibilité d'ouvrir la vidéo avec une autre application

# v1.3.0

###### 2026/08/27

* `Fonctionnalité` Minuteur de veille: pause automatique après 15, 30, 45 ou 60 minutes, ou à la fin de la vidéo en cours
* `Fonctionnalité` Zoom par pincement (0,25× à 4×) avec réinitialisation par double appui pendant le zoom
* `Fonctionnalité` Faire glisser la barre de progression affiche une bulle avec le temps cible et un aperçu en miniature, avec repli sur le temps seul quand l'extraction échoue
* `Fonctionnalité` Capture d'image: sur Android 10+, enregistrement de l'image actuelle en PNG dans la galerie système, sans autorisation de stockage et sans modifier la vidéo source
* `Fonctionnalité` Nouveaux réglages de sensibilité des gestes (faible/standard/élevée) et de pas de saut par double appui (5/10/30 secondes), mémorisés automatiquement
* `Amélioration` Le minuteur de veille se base sur le temps de fonctionnement du système: changer l'heure de l'appareil n'affecte pas le compte à rebours, et celui-ci survit à la rotation de l'écran
* `Amélioration` L'extraction de miniatures fusionne les demandes pendant les déplacements rapides et libère rapidement les images périmées, ce qui économise la mémoire

# v1.2.0

###### 2026/08/27

* `Fonctionnalité` Sélection des pistes: changement de piste audio intégrée selon la langue et les canaux, et activation des sous-titres intégrés qui restent désactivés par défaut
* `Fonctionnalité` Incrustation vidéo: sur Android 8.0+, quitter l'application pendant la lecture ouvre automatiquement une fenêtre flottante, avec entrée manuelle possible et lecture/pause à distance
* `Fonctionnalité` Intégration multimédia système: commandes au casque et en Bluetooth, plus une notification multimédia avec titre, progression et actions de lecture
* `Fonctionnalité` Outils de lecture: répétition d'un seul élément, un panneau d'informations vidéo, et un choix automatique unique de l'orientation selon le format de l'image
* `Amélioration` Les pistes non prises en charge sont clairement signalées et non sélectionnables; les entrées se masquent automatiquement quand aucune piste sélectionnable n'existe
* `Amélioration` La taille de fichier du panneau d'informations vidéo provient de la taille déclarée validée, ce qui la rend plus fiable
* `Dépendance` Ajout d'AndroidX Media3 Session 1.10.1

# v1.1.0

###### 2026/08/27

* `Fonctionnalité` Commandes gestuelles: balayage vertical sur la moitié gauche pour la luminosité et sur la moitié droite pour le volume, balayage horizontal pour naviguer, double appui sur les côtés pour sauter de 10 secondes, double appui au centre pour lecture/pause, appui long pour une vitesse temporaire de 2×
* `Fonctionnalité` Vitesse de lecture: 9 paliers de 0,25× à 3×, avec mise en évidence de la vitesse actuelle dans la barre de commandes quand elle diffère de 1×
* `Fonctionnalité` Plein écran immersif: barres système masquées avec prise en charge des encoches, barres de titre et de commandes à masquage automatique, bascule en un appui de l'orientation et du mode d'affichage
* `Fonctionnalité` Verrouillage des commandes: blocage de tous les boutons et de tous les gestes d'un seul appui pour éviter les appuis accidentels
* `Fonctionnalité` Reprise de lecture: la position est mémorisée automatiquement et restaurée à la réouverture de la même vidéo; les vidéos terminées sont effacées, jusqu'à 200 enregistrements sont conservés, sous forme de condensés de fichiers plutôt que de chemins
* `Amélioration` La barre de commandes inférieure est reconstruite: lecture/pause, sauts de 10 secondes, barre de progression déplaçable et progression de la mise en mémoire tampon, tout y est
* `Amélioration` Le panneau d'échec de lecture gagne un bouton Réessayer tout en conservant l'ouverture avec une autre application
* `Amélioration` Les changements d'interface comme la rotation ne reconstruisent plus le lecteur, pour des transitions plus fluides

# v1.0.1

###### 2026/08/08

* `Correctif` Correction de la liaison de service invalide après l'activation du plugin dans le centre des plugins AutoJs6, qui empêchait l'hôte d'utiliser le plugin
* `Amélioration` Nom et description du plugin allégés, avec une formulation plus naturelle de la documentation dans toutes les langues

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Première version: publiée comme plugin du gestionnaire de fichiers AutoJs6; appuyer sur un fichier vidéo dans le gestionnaire de fichiers le lit directement
* `Fonctionnalité` Couvre 23 extensions vidéo courantes (MP4, MKV, AVI, MOV, FLV, WEBM et plus), en prenant en charge l'action d'ouverture vidéo du gestionnaire de fichiers en mode lecture seule
* `Fonctionnalité` Lecture basée sur AndroidX Media3 ExoPlayer: démarrage automatique, commandes standard, gestion du focus audio, pause automatique au débranchement du casque, et écran maintenu allumé pendant la lecture
* `Fonctionnalité` Validation de sécurité stricte: l'entrée de lecture est protégée par une autorisation de signature, l'origine des requêtes, les fichiers cibles et les accès en lecture seule sont vérifiés point par point, et le lecteur n'est pas exporté vers le système
* `Fonctionnalité` Une entrée système distincte pour l'ouverture depuis d'autres applications: elles peuvent appeler ce lecteur avec un content URI en lecture seule pour regarder des vidéos
* `Fonctionnalité` En cas d'échec de lecture, la vidéo peut être confiée en toute sécurité à une autre application, ce plugin étant exclu des candidats
* `Fonctionnalité` Livré en 10 langues: chinois simplifié, chinois traditionnel (Hong Kong/Taïwan), anglais, français, espagnol, japonais, coréen, russe et arabe
* `Dépendance` Introduction d'AndroidX Media3 ExoPlayer et UI 1.10.1
* `Dépendance` Introduction du runtime Kotlin Parcelize 2.2.21
