# 3-Ember Player

3-Ember Player ajoute une action vidéo principale en lecture seule au gestionnaire de fichiers. Il utilise AndroidX Media3 ExoPlayer et PlayerView, démarre automatiquement la lecture, gère le focus audio et le passage à une sortie audio noisy, puis restaure la position et l'état de lecture.

La coopération complète nécessite AutoJs6 6.8.0 build 5276 ou ultérieur et Explorer Action v12 ; les capacités futures du plugin ne relèveront pas ce seuil. Lorsqu'il est installé, activé, approuvé et compatible, tous les fichiers reconnus comme vidéo par l'hôte s'ouvrent dans ce lecteur. Un hôte sans capacités de session facultatives conserve la lecture du fichier sélectionné. Si le plugin est absent, désactivé, non autorisé, indisponible, incompatible ou impossible à lancer, un hôte compatible affiche des instructions de récupération et n'ouvre le sélecteur d'applications Android qu'après le choix explicite Ouvrir avec d'autres applications. L'audio et les images restent des capacités indépendantes.

Commandes de lecture:

- Balayez verticalement la moitié gauche ou droite de l'écran pour régler la luminosité ou le volume multimédia.
- Balayez horizontalement pour naviguer, touchez deux fois le tiers gauche ou droit pour sauter de 5, 10 ou 30 secondes selon le réglage, touchez deux fois le centre pour lire ou mettre en pause, et maintenez appuyé pour une vitesse temporaire de 2×.
- La barre inférieure offre lecture/pause, sauts de 10 secondes, barre de progression déplaçable, vitesses de 0,25× à 3×, mode d'affichage, orientation de l'écran et verrouillage contre les touches accidentelles.
- L'ouverture d'une vidéo dans l'explorateur peut former une file bornée et naturellement triée de vidéos sœurs directes lisibles. La lecture commence sur la vidéo choisie et propose précédent / suivant, séquence, aléatoire et répétition d'un élément.
- Les hôtes compatibles affichent aussi l’action en lecture seule Lire la sélection pour 1 à 128 vidéos d’un même dossier dans l’ordre de l’hôte, sans découverte voisine. Les files de l’hôte offrent un panneau élément/durée et le saut au toucher, gardent la dernière image pendant une invite annulable de trois secondes et peuvent mémoriser le mode.
- L’historique de reprise conserve exactement la dernière vidéo inachevée sous forme d’un condensé d’identité SHA-256 et de valeurs temporelles ; l’ouverture d’une autre vidéo l’efface immédiatement et une lecture terminée n’est jamais conservée.
- La barre supérieure choisit les pistes audio et texte intégrées. Les sous-titres externes .srt et .ass correspondants, avec suffixes de langue, sont découverts dans le même dossier. Tous sont désactivés par défaut et chargés seulement après une sélection explicite.
- La barre supérieure propose aussi la répétition de la vidéo actuelle, les informations vidéo, l’orientation automatique des vidéos horizontales et l’incrustation vidéo sur Android 8.0+ avec lecture/pause.
- MediaSession prend en charge les commandes des écouteurs, du Bluetooth et du système; la notification multimédia affiche le titre et la progression actuels.
- Le minuteur met la lecture en pause après 15, 30, 45 ou 60 minutes, ou à la fin de la vidéo. Les paramètres des gestes règlent la sensibilité et l’intervalle du double appui.
- Pincez l’image pour zoomer de 0,25× à 4×; touchez-la deux fois pendant le zoom pour réinitialiser. Le déplacement de la barre affiche le temps cible et, si le conteneur le permet, une miniature facultative.
- Sur Android 10 ou version ultérieure, Enregistrer l’image actuelle écrit un PNG dans Images/3-Ember Player. L’option est masquée sur les versions antérieures.
- Génère des rôles sémantiques clairs et sombres lisibles depuis une couleur HCT, suit AutoJs6 par défaut et propose 19 couleurs Material 500 localisées ainsi qu’un RGB personnalisé prévisualisé. Fournit un lanceur autonome et des paramètres de langue, nuit et couleur suivant l’hôte, reprise d’une seule vidéo, vérifications manuelles et automatiques, versions ignorées, historique et informations sur l’application et le développeur.

Outils de sous-titres et de lecture précise:

- Les paramètres de sous-titres offrent un aperçu en direct pour une taille de texte de 75 % à 150 %, quatre couleurs de premier plan, un arrière-plan opaque, translucide ou absent, et une marge inférieure de 0 %, 4 % ou 8 % ; ces choix s'appliquent aussi en incrustation vidéo.
- Le menu des sous-titres peut charger temporairement un fichier .srt ou .ass de 4 Mio maximum, détecter en mémoire BOM/GBK/Big5/Shift_JIS/EUC-KR/Windows-1251/Windows-1256 et décaler un sous-titre externe de ±600,0 secondes par pas de 0,1 seconde. Aucune autorisation persistante n'est conservée.
- Mettez la lecture en pause pour afficher les boutons image précédente/suivante (maintenez-les pour répéter) et la boucle A–B. Une boucle A–B active empêche le passage à l'élément suivant ; choisir ensuite un mode de répétition ou le minuteur de fin de vidéo l'efface.

Affichage, accessibilité et lecture en arrière-plan:

- Le panneau d'informations identifie HDR10, HLG ou SDR et les données colorimétriques disponibles. Les captures peuvent inclure les sous-titres et être partagées; le miroir et l'amplification avertie de +3 à +15 dB restent limités à la session.
- TalkBack garde les commandes nommées visibles et chaque geste possède un équivalent par bouton ou menu. Le lecteur tolère une échelle texte/affichage de 200 % et prend en charge le focus visible clavier/DPAD, Espace/Entrée, la recherche gauche/droite selon le pas réglé et les touches MediaSession.
- Media3 gère le rythme des appuis du casque: un appui bascule lecture/pause, le double appui d'un appareil externe avance si la file contient un élément suivant et une commande Previous explicite revient en arrière. Aucun détecteur de triple appui non standard n'est ajouté.
- Continuer le son en arrière-plan est désactivé par défaut et exige l'autorisation des notifications. Si le PiP est indisponible, le lecteur existant passe à un service privé de premier plan de lecture multimédia avec commandes de notification; le PiP est prioritaire, et désactiver le réglage, terminer, rencontrer une erreur ou quitter explicitement arrête le service. FOREGROUND_SERVICE et FOREGROUND_SERVICE_MEDIA_PLAYBACK servent uniquement à ce cycle activé volontairement, et l'accès URI reste temporaire.

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
