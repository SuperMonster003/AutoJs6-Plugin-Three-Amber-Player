# Lecteur vidéo

Video Player ajoute une action vidéo principale en lecture seule au gestionnaire de fichiers. Il utilise AndroidX Media3 ExoPlayer et PlayerView, démarre automatiquement la lecture, gère le focus audio et le passage à une sortie audio noisy, puis restaure la position et l'état de lecture.

Le plugin nécessite la version 5269 ou ultérieure de l'hôte. Lorsqu'il est installé, activé, approuvé et compatible, les fichiers vidéo correspondants s'ouvrent dans ce lecteur. S'il est absent ou indisponible, l'hôte revient à la route Android ACTION_VIEW. La lecture audio et l'affichage des images restent des capacités de plugins indépendantes.

Extensions de l'explorateur:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Limites de sécurité et de confidentialité:

- L'exécution par le gestionnaire de fichiers nécessite l'autorisation de plugin de niveau signature.
- Le plugin accepte les content URI avec un accès temporaire en lecture seule et ne modifie jamais la source.
- Les accès en écriture et persistants sont refusés. L'accès prefix n'est jamais transmis au lecteur.
- Une entrée ACTION_VIEW distincte accepte uniquement les content URI vidéo en lecture seule et abandonne les extras et ClipData entrants.
- Si la lecture échoue, Ouvrir avec une autre application reconstruit un Intent en lecture seule et exclut ce plugin.
- Le décodage réel dépend des extracteurs Media3 et des codecs disponibles sur l'appareil.
