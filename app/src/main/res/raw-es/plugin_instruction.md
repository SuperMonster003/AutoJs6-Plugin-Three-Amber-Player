# 3-Ember Player

3-Ember Player añade una acción principal de video de solo lectura al gestor de archivos. Usa AndroidX Media3 ExoPlayer y PlayerView, inicia la reproducción automáticamente, gestiona el enfoque de audio y los cambios a salida de audio noisy, y restaura la posición y el estado de reproducción.

La cooperación completa requiere AutoJs6 6.8.0 build 5276 o posterior y Explorer Action v12; las capacidades futuras del plugin no elevarán este requisito. Cuando está instalado, activado, es de confianza y compatible, todos los archivos que el host reconoce como video se abren en este reproductor. Un host sin las capacidades de sesión opcionales conserva la reproducción del archivo seleccionado. Si el plugin falta, está desactivado, no está autorizado, no está disponible, es incompatible o no se puede iniciar, un host compatible muestra instrucciones de recuperación y abre el selector de aplicaciones de Android solo cuando el usuario elige explícitamente Abrir con otras aplicaciones. El audio y las imágenes siguen siendo capacidades independientes.

Controles de reproducción:

- Desliza verticalmente en la mitad izquierda o derecha de la pantalla para ajustar el brillo o el volumen multimedia.
- Desliza horizontalmente para buscar, toca dos veces el tercio izquierdo o derecho para saltar los 5, 10 o 30 segundos configurados, toca dos veces el centro para reproducir o pausar, y mantén pulsado para una velocidad temporal de 2×.
- La barra inferior ofrece reproducir/pausar, saltos de 10 segundos, barra de progreso arrastrable, velocidades de 0,25× a 3×, modo de ajuste, orientación de la pantalla y bloqueo contra toques accidentales.
- Al abrir un video desde el Explorador se puede formar una cola acotada y ordenada de forma natural con videos hermanos directos legibles. Comienza en el video seleccionado y ofrece anterior / siguiente, secuencia, aleatorio y repetición de uno.
- El historial de reanudación conserva exactamente el último vídeo sin terminar como un resumen SHA-256 de identidad y valores de tiempo; abrir otro vídeo lo borra de inmediato y nunca se conserva una reproducción terminada.
- La barra superior permite elegir pistas integradas de audio y texto. Descubre en la misma carpeta subtítulos externos .srt y .ass coincidentes, incluidos sufijos de idioma. Todos están desactivados de forma predeterminada y solo se cargan tras una selección explícita.
- La barra superior también ofrece repetición del video actual, información del video, orientación automática para videos horizontales e imagen en imagen en Android 8.0+ con acciones de reproducción y pausa.
- MediaSession admite controles de auriculares, Bluetooth y del sistema; la notificación multimedia muestra el título y el progreso actuales.
- El temporizador pausa la reproducción tras 15, 30, 45 o 60 minutos, o al final del video. Los ajustes de gestos permiten elegir la sensibilidad y el intervalo del doble toque.
- Pellizca la imagen para ampliarla entre 0,25× y 4×; tócala dos veces mientras está ampliada para restablecerla. Al arrastrar la barra aparece el tiempo de destino y, si el contenedor lo permite, una miniatura opcional.
- En Android 10 o posterior, Guardar fotograma actual escribe un PNG en Imágenes/3-Ember Player. La opción está oculta en versiones anteriores.
- Genera roles semánticos accesibles claros y oscuros desde un color HCT, sigue AutoJs6 de forma predeterminada y ofrece 19 preajustes Material 500 localizados y RGB personalizado con vista previa. Ofrece un lanzador independiente y ajustes de idioma, noche y color que siguen al host, reanudación de un único vídeo, comprobaciones manuales y automáticas, versiones ignoradas, historial e información de la aplicación y el desarrollador.

Extensiones del Explorador:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Límites de seguridad y privacidad:

- La ejecución desde el gestor de archivos requiere el permiso de complemento de nivel signature.
- El plugin acepta content URI con acceso temporal de solo lectura y nunca escribe el origen.
- Una Host Session por solicitud queda vinculada por el host al UID del plugin y solo permite listar sin recursión el padre directo del archivo seleccionado y abrir este o un hermano directo legible. El plugin no recibe rutas del sistema de archivos.
- Se rechazan las concesiones de escritura y persistentes. El acceso prefix nunca se reenvía al reproductor.
- Una entrada ACTION_VIEW separada solo acepta content URI de video de solo lectura y descarta los extras y ClipData recibidos.
- Si la reproducción falla, Abrir con otra aplicación reconstruye un Intent de solo lectura y excluye este plugin.
- Las pistas XVID en MKV validadas usan el decodificador MPEG-4 Part 2 del dispositivo sin transcodificación. Si no está disponible o falla, la reproducción se detiene y ofrece Abrir con otra aplicación.
- La decodificación real depende de los extractores de Media3 y de los códecs disponibles en el dispositivo.
- El historial de reanudación conserva exactamente el último vídeo sin terminar como un resumen SHA-256 de identidad y valores de tiempo; abrir otro vídeo lo borra de inmediato y nunca se conserva una reproducción terminada.
- El panel de información solo usa metadatos de la sesión de reproducción, el nombre seguro y el tamaño declarado; no examina ni modifica el video de origen.
- Una captura solicitada crea un PNG independiente mediante MediaStore y nunca escribe en el video de origen. Las miniaturas solo permanecen en memoria y se omiten silenciosamente si no se pueden extraer.
