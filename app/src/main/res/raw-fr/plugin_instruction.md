# 3-Ember Player

3-Ember Player ajoute une action vidéo principale en lecture seule au gestionnaire de fichiers. Il utilise AndroidX Media3 ExoPlayer et PlayerView, démarre automatiquement la lecture, gère le focus audio et le passage à une sortie audio noisy, puis restaure la position et l'état de lecture.

La coopération complète nécessite AutoJs6 6.8.0 build 5276 ou ultérieur et Explorer Action v12 ; les capacités futures du plugin ne relèveront pas ce seuil. Lorsqu'il est installé, activé, approuvé et compatible, tous les fichiers reconnus comme vidéo par l'hôte s'ouvrent dans ce lecteur. Un hôte sans capacités de session facultatives conserve la lecture du fichier sélectionné. Si le plugin est absent, désactivé, non autorisé, indisponible, incompatible ou impossible à lancer, un hôte compatible affiche des instructions de récupération et n'ouvre le sélecteur d'applications Android qu'après le choix explicite Ouvrir avec d'autres applications. L'audio et les images restent des capacités indépendantes.

Commandes de lecture:

- Balayez verticalement la moitié gauche ou droite de l'écran pour régler la luminosité ou le volume multimédia.
- Balayez horizontalement pour naviguer, touchez deux fois le tiers gauche ou droit pour sauter de 5, 10 ou 30 secondes selon le réglage, touchez deux fois le centre pour lire ou mettre en pause, et maintenez appuyé pour une vitesse temporaire de 2×.
- La barre inférieure offre lecture/pause, sauts de 10 secondes, barre de progression déplaçable, vitesses de 0,25× à 3×, mode d'affichage, orientation de l'écran et verrouillage contre les touches accidentelles.
- L'ouverture d'une vidéo dans l'explorateur peut former une file bornée et naturellement triée de vidéos sœurs directes lisibles. La lecture commence sur la vidéo choisie et propose précédent / suivant, séquence, aléatoire et répétition d'un élément.
- L’historique de reprise conserve exactement la dernière vidéo inachevée sous forme d’un condensé d’identité SHA-256 et de valeurs temporelles ; l’ouverture d’une autre vidéo l’efface immédiatement et une lecture terminée n’est jamais conservée.
- La barre supérieure choisit les pistes audio et texte intégrées. Les sous-titres externes .srt et .ass correspondants, avec suffixes de langue, sont découverts dans le même dossier. Tous sont désactivés par défaut et chargés seulement après une sélection explicite.
- La barre supérieure propose aussi la répétition de la vidéo actuelle, les informations vidéo, l’orientation automatique des vidéos horizontales et l’incrustation vidéo sur Android 8.0+ avec lecture/pause.
- MediaSession prend en charge les commandes des écouteurs, du Bluetooth et du système; la notification multimédia affiche le titre et la progression actuels.
- Le minuteur met la lecture en pause après 15, 30, 45 ou 60 minutes, ou à la fin de la vidéo. Les paramètres des gestes règlent la sensibilité et l’intervalle du double appui.
- Pincez l’image pour zoomer de 0,25× à 4×; touchez-la deux fois pendant le zoom pour réinitialiser. Le déplacement de la barre affiche le temps cible et, si le conteneur le permet, une miniature facultative.
- Sur Android 10 ou version ultérieure, Enregistrer l’image actuelle écrit un PNG dans Images/3-Ember Player. L’option est masquée sur les versions antérieures.
- Génère des rôles sémantiques clairs et sombres lisibles depuis une couleur HCT, suit AutoJs6 par défaut et propose 19 couleurs Material 500 localisées ainsi qu’un RGB personnalisé prévisualisé. Fournit un lanceur autonome et des paramètres de langue, nuit et couleur suivant l’hôte, reprise d’une seule vidéo, vérifications manuelles et automatiques, versions ignorées, historique et informations sur l’application et le développeur.

Extensions de l'explorateur:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Limites de sécurité et de confidentialité:

- L'exécution par le gestionnaire de fichiers nécessite l'autorisation de plugin de niveau signature.
- Le plugin accepte les content URI avec un accès temporaire en lecture seule et ne modifie jamais la source.
- Une Host Session par requête est liée par l'hôte à l'UID du plugin et permet uniquement de lister sans récursion le parent direct du fichier sélectionné et d'ouvrir celui-ci ou un frère direct lisible. Le plugin ne reçoit aucun chemin de système de fichiers.
- Les accès en écriture et persistants sont refusés. L'accès prefix n'est jamais transmis au lecteur.
- Une entrée ACTION_VIEW distincte accepte uniquement les content URI vidéo en lecture seule et abandonne les extras et ClipData entrants.
- Si la lecture échoue, Ouvrir avec une autre application reconstruit un Intent en lecture seule et exclut ce plugin.
- Les pistes XVID dans MKV validées utilisent le décodeur MPEG-4 Part 2 de l'appareil sans transcodage. S'il est indisponible ou échoue, la lecture s'arrête et propose l'ouverture dans une autre application.
- Le décodage réel dépend des extracteurs Media3 et des codecs disponibles sur l'appareil.
- L’historique de reprise conserve exactement la dernière vidéo inachevée sous forme d’un condensé d’identité SHA-256 et de valeurs temporelles ; l’ouverture d’une autre vidéo l’efface immédiatement et une lecture terminée n’est jamais conservée.
- Le panneau d’informations utilise seulement les métadonnées de la session de lecture, le nom sécurisé et la taille déclarée; il n’analyse ni ne modifie la vidéo source.
- Une capture demandée crée un PNG séparé via MediaStore et n’écrit jamais dans la vidéo source. Les miniatures restent en mémoire et sont omises silencieusement si leur extraction est impossible.
