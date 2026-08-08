# Reproductor de video

Video Player añade una acción principal de video de solo lectura al gestor de archivos. Usa AndroidX Media3 ExoPlayer y PlayerView, inicia la reproducción automáticamente, gestiona el enfoque de audio y los cambios a salida de audio noisy, y restaura la posición y el estado de reproducción.

El complemento requiere la compilación 5269 o posterior del host. Cuando está instalado, activado, es de confianza y compatible, los archivos de video coincidentes se abren en este reproductor. Si falta o no está disponible, el host recurre a la ruta Android ACTION_VIEW. La reproducción de audio y la visualización de imágenes siguen siendo capacidades de complementos independientes.

Extensiones del Explorador:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Límites de seguridad y privacidad:

- La ejecución desde el gestor de archivos requiere el permiso de complemento de nivel signature.
- El plugin acepta content URI con acceso temporal de solo lectura y nunca escribe el origen.
- Se rechazan las concesiones de escritura y persistentes. El acceso prefix nunca se reenvía al reproductor.
- Una entrada ACTION_VIEW separada solo acepta content URI de video de solo lectura y descarta los extras y ClipData recibidos.
- Si la reproducción falla, Abrir con otra aplicación reconstruye un Intent de solo lectura y excluye este plugin.
- La decodificación real depende de los extractores de Media3 y de los códecs disponibles en el dispositivo.
