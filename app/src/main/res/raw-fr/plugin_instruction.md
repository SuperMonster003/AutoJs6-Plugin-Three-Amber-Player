3-Amber Player est le plugin de lecture vidéo de l'explorateur de fichiers d'AutoJs6 et fonctionne aussi comme lecteur vidéo local autonome. Touchez une vidéo dans l'explorateur de fichiers pour la lire en plein écran, avec lecture continue du même dossier, listes de lecture locales, sous-titres externes, incrustation vidéo et son en arrière-plan, sans jamais modifier la vidéo source.

### Prise en main

1. Vérifiez que ce plugin est activé dans le "Centre des plugins" d'AutoJs6.
2. Touchez une vidéo dans l'explorateur de fichiers d'AutoJs6; le lecteur s'ouvre en plein écran et lance la lecture.
3. Touchez l'image pour afficher les commandes; balayez verticalement la moitié gauche ou droite pour régler la luminosité ou le volume, et touchez deux fois le centre pour lire ou mettre en pause.
4. Pour l'utiliser sans AutoJs6, ouvrez ce plugin depuis son icône et touchez "Ouvrir un fichier vidéo".

### Fonctionnalités principales

- **Lecture continue**: Après l'ouverture d'une vidéo, les autres vidéos du même dossier forment une file; naviguez-y depuis "Liste de lecture vidéo" et choisissez la lecture dans l'ordre, aléatoire ou la répétition de la vidéo actuelle.
- **Lire la sélection**: Sélectionnez plusieurs vidéos dans l'explorateur de fichiers et touchez "Lire la sélection" pour en lire jusqu'à 128 dans l'ordre de sélection.
- **Listes de lecture locales**: Ouvrez des fichiers de liste tels que M3U/M3U8, PLS ou XSPF, jusqu'à 128 éléments; si une liste renvoie à des vidéos d'un autre dossier, ouvrez-la depuis l'icône du plugin et choisissez ce dossier lorsque cela est demandé.
- **Gestes et vitesse**: Balayez horizontalement pour avancer ou reculer, et appuyez longuement pour passer temporairement en vitesse 2x; réglez le pas du double toucher dans "Paramètres des gestes", et "Verrouiller les commandes" évite les touches accidentelles.
- **Sous-titres et pistes audio**: Changez de piste intégrée dans "Sous-titres" et "Piste audio", utilisez "Charger depuis un fichier" pour un sous-titre .srt ou .ass, et ajustez "Style des sous-titres" et "Décalage des sous-titres".
- **Reprise de la lecture**: Une vidéo non terminée reprend là où elle s'est arrêtée quand vous la rouvrez; désactivez "Reprendre la lecture" dans les paramètres pour l'éviter.
- **Incrustation vidéo et son en arrière-plan**: Sur Android 8.0 ou version ultérieure, utilisez "Incrustation vidéo"; avec "Continuer le son en arrière-plan" activé, le son continue après avoir quitté le lecteur.
- **Minuteur de veille et image par image**: "Minuteur de veille" arrête la lecture à l'échéance; en pause, avancez image par image et définissez une boucle A-B.
- **Captures et informations sur la vidéo**: Sur Android 10 ou version ultérieure, "Enregistrer l'image actuelle" enregistre un PNG dans `Pictures/3-Amber Player`; "Informations sur la vidéo" affiche la résolution, la fréquence d'images et plus encore.
- **Apparence et paramètres**: Les couleurs, le mode nuit et la langue suivent AutoJs6 par défaut; dans "Paramètres", vous pouvez aussi "Rechercher des mises à jour" et consulter "Historique des versions".

### Prérequis

- Android 7.0 ou version ultérieure.
- L'ouverture de vidéos depuis l'explorateur de fichiers nécessite AutoJs6 6.8.0 ou version ultérieure; le lecteur autonome ouvert depuis son icône n'a pas besoin d'AutoJs6.
- "Continuer le son en arrière-plan" nécessite l'autorisation des notifications pour afficher la notification de contrôle de lecture.

### Questions fréquentes

**Raisons pour lesquelles toucher une vidéo n'ouvre pas ce lecteur**

Vérifiez qu'AutoJs6 respecte la version requise et que ce plugin est activé dans le "Centre des plugins". Si le plugin est indisponible, AutoJs6 affiche une aide à la récupération, et "Ouvrir par d'autres apps" confie la vidéo à un autre lecteur.

**Cas où une vidéo ne peut pas être lue**

Le décodage dépend des décodeurs de l'appareil. Si la lecture échoue, touchez "Ouvrir avec une autre application" pour utiliser un autre lecteur.

**Contenu de l'historique de reprise**

Seuls une empreinte d'identité et la position de lecture de la dernière vidéo non terminée sont conservées, uniquement sur cet appareil; elles sont effacées à l'ouverture d'une autre vidéo ou à la fin de la lecture.

Plus de détails sur la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player).
