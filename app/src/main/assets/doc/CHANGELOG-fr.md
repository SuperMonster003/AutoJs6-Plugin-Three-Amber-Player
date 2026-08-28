******

### Historique des versions

******

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

# v1.3.0

###### 2026/08/27

* `Fonctionnalité` Minuteur: pause après 15, 30, 45 ou 60 minutes ou à la fin de la vidéo, avec gestion du conflit avec la répétition
* `Fonctionnalité` Interaction avec l’image: zoom par pincement de 0,25× à 4×, réinitialisation par double appui et coordination avec les modes d’affichage existants
* `Fonctionnalité` Aperçu du déplacement: temps cible et miniatures facultatives en mémoire, avec repli silencieux vers le temps seul
* `Fonctionnalité` Captures de l’image actuelle sur Android 10+ enregistrées comme PNG séparés via MediaStore, sans autorisation de stockage ni modification de la source
* `Fonctionnalité` Paramètres persistants des gestes pour une sensibilité faible, normale ou élevée et un saut par double appui de 5, 10 ou 30 secondes
* `Amélioration` Les échéances du minuteur utilisent le temps écoulé et survivent à la recréation de la page sans dépendre des changements d’horloge
* `Amélioration` L’extraction des miniatures regroupe les demandes rapides sur un seul thread et libère chaque bitmap temporaire devenu obsolète

# v1.2.0

###### 2026/08/27

* `Fonctionnalité` Sélection des pistes: bascule entre les pistes audio intégrées avec langue et canaux, et activation à la demande des sous-titres intégrés désactivés par défaut
* `Fonctionnalité` Incrustation vidéo sur Android 8.0+: entrée automatique en quittant pendant la lecture, entrée manuelle, respect du format vidéo et lecture/pause à distance
* `Fonctionnalité` Intégration multimédia système: commandes MediaSession pour écouteurs et Bluetooth, avec notification affichant titre, actions et progression
* `Fonctionnalité` Outils de lecture: répétition de la vidéo actuelle, panneau d’informations et sélection unique de l’orientation selon le format
* `Amélioration` Les pistes non prises en charge sont clairement signalées et non sélectionnables; les commandes restent masquées sans choix réel
* `Amélioration` La requête interne stricte conserve désormais uniquement la taille déclarée validée avec le nom sûr pour le panneau d’informations
* `Dépendance` Ajout d’AndroidX Media3 Session 1.10.1

# v1.1.0

###### 2026/08/27

* `Fonctionnalité` Commandes gestuelles: balayages verticaux sur la moitié gauche ou droite pour la luminosité ou le volume multimédia, balayages horizontaux pour naviguer, double touche latérale pour sauter de 10 secondes, double touche centrale pour lecture/pause et appui long pour une vitesse temporaire de 2×
* `Fonctionnalité` Vitesse de lecture: neuf paliers de 0,25× à 3×, avec mise en évidence de la vitesse actuelle dans la barre de commandes lorsqu'elle diffère de 1×
* `Fonctionnalité` Plein écran immersif: barres système masquées avec prise en charge des encoches, barres de titre et de commandes flottantes à masquage automatique, et bascule en un geste de l'orientation de l'écran et du mode d'affichage
* `Fonctionnalité` Verrouillage des commandes: une touche désactive toutes les commandes et tous les gestes pour éviter les touches accidentelles
* `Fonctionnalité` Mémoire de reprise: la lecture reprend à la dernière position locale indexée par un condensé SHA-256 du content URI, effacée après la fin et limitée à 200 entrées
* `Amélioration` Barre de commandes inférieure reconstruite avec lecture/pause, sauts de 10 secondes, barre de progression déplaçable et affichage de la progression du tampon
* `Amélioration` Le panneau d'échec de lecture propose désormais une action Réessayer en plus de l'ouverture avec une autre application
* `Amélioration` Les changements de configuration comme la rotation ne reconstruisent plus le lecteur pour des transitions plus fluides

# v1.0.1

###### 2026/08/08

* `Correctif` Renvoyer une liaison de service Explorer Action valide lors de l'activation depuis le centre des plugins
* `Amélioration` Raccourcir le nom et la description du plugin et rendre la documentation utilisateur plus naturelle

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Plugin Video Player avec ID de plugin `video-player`, ID d'action `play-video`, moteur `explorer-action` et variante `default`
* `Fonctionnalité` Action principale de l'explorateur en lecture seule via le protocole v2, avec filtre MIME vide, correspondant uniquement aux 23 extensions vidéo actuelles de l'hôte et nécessitant la version 5269
* `Fonctionnalité` Entrée de l'explorateur protégée par signature avec validation stricte des content URI cible et parent, de ClipData, de la source, du nom affiché, de la taille, du type MIME, de l'extension et des accès, suivie d'une transmission minimale vers un lecteur non exporté
* `Fonctionnalité` Entrée ACTION_VIEW exportée et indépendante pour les content URI vidéo en lecture seule, avec abandon des extras et ClipData non fiables, refus des accès interdits et protection contre les boucles internes
* `Fonctionnalité` Lecture Media3 ExoPlayer et PlayerView avec démarrage automatique, commandes standard, focus audio, gestion du passage à une sortie audio noisy, sauvegarde de la position et de l'état de lecture, et écran allumé uniquement pendant la lecture active
* `Fonctionnalité` Récupération sécurisée Ouvrir avec une autre application après un échec de lecture, au moyen de nouveaux Intents en lecture seule qui excluent explicitement ce plugin
* `Fonctionnalité` Métadonnées, texte d'interface, instructions d'utilisation, fichiers README et historiques localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan
* `Dépendance` Ajout de AndroidX Media3 ExoPlayer et UI version 1.10.1
* `Dépendance` Ajout du runtime Kotlin Parcelize version 2.2.21
