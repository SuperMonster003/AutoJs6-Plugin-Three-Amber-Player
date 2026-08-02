# Reproductor de video

Video Player añade una acción principal de video de solo lectura al Explorador de AutoJs6. Usa AndroidX Media3 ExoPlayer y PlayerView, inicia la reproducción automáticamente, gestiona el enfoque de audio y los cambios a salida de audio noisy, y restaura la posición y el estado de reproducción.

El plugin requiere AutoJs6 build 5269+. Cuando está instalado, activado, es de confianza y compatible, los archivos de video coincidentes se abren en este reproductor. Si falta o no está disponible, AutoJs6 recurre a la ruta Android ACTION_VIEW. La reproducción de audio y la visualización de imágenes siguen siendo capacidades de plugins independientes.

Extensiones del Explorador:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Límites de seguridad y privacidad:

- La ejecución desde el Explorador requiere el permiso de plugin AutoJs6 de nivel signature.
- El plugin acepta content URI con acceso temporal de solo lectura y nunca escribe el origen.
- Se rechazan las concesiones de escritura y persistentes. El acceso prefix nunca se reenvía al reproductor.
- Una entrada ACTION_VIEW separada solo acepta content URI de video de solo lectura y descarta los extras y ClipData recibidos.
- Si la reproducción falla, Abrir con otra aplicación reconstruye un Intent de solo lectura y excluye este plugin.
- La decodificación real depende de los extractores de Media3 y de los códecs disponibles en el dispositivo.
