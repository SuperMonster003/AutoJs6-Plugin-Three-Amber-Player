******

### Historial de versiones

******

# v3.1.2

###### 2026/09/19

* `Corrección` Pérdida del estado de servicio en primer plano al transferir el audio al segundo plano cuando la actividad cancela la notificación compartida en Android 17
* `Corrección` El servicio de reproducción en segundo plano permanece activo después de detener la reproducción con un controlador multimedia
* `Corrección` Advertencias de lectura de SDK XML v4 con AGP 9.1 y comprobaciones de alineación nativa de APK activadas por error al ensamblar pruebas unitarias JVM, mediante los plugins de compilación compartidos 1.8.3
* `Mejora` Compatibilidad con Android 17 (SDK 37) y el servicio en primer plano para reproducir audio en segundo plano

# v3.1.1

###### 2026/09/15

* `Mejora` compileSdk sube a 37 (Android 17); targetSdk se mantiene en 36 hasta verificar el comportamiento que depende del objetivo

# v3.1.0

###### 2026/09/13

* `Función` Listas locales: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL conservan orden, títulos y duplicados. AutoJs6 lee archivos de la misma carpeta; el reproductor independiente pide la carpeta para rutas relativas. Una lista a la vez, hasta 128 elementos. No se admiten URL de red, HLS ni listas anidadas
* `Corrección` Mantener la fecha de versión del complemento en inglés sin depender del idioma del equipo de compilación
* `Mejora` Recursos traducidos coherentes, activación explícita del complemento y validación de los paquetes de publicación

# v3.0.0

###### 2026/09/12

* `Función` Revisión completa de accesibilidad del reproductor: controles con nombre y estado dinámico, controles persistentes durante la exploración táctil de TalkBack, equivalente de botón o menú para cada gesto, orden de foco determinista y diseños verificados con texto y pantalla al 200 %
* `Función` Compatibilidad con teclado y mando: anillos de foco visibles, navegación DPAD, reproducción/pausa con Espacio/Intro, búsqueda izquierda/derecha según el paso configurado de 5/10/30 segundos y teclas multimedia mediante MediaSession
* `Función` Audio opcional en segundo plano, desactivado de forma predeterminada: tras conceder permiso de notificaciones, la reproducción activa puede pasar sin cortes a un servicio multimedia en primer plano con controles de notificación cuando PiP no está disponible
* `Mejora` PiP siempre tiene prioridad; desactivar el ajuste, finalizar la reproducción, un error o salir deliberadamente detiene el servicio, mientras que tocar la notificación recupera el mismo reproductor y la posición continua
* `Mejora` El comportamiento de los auriculares sigue Media3: una pulsación alterna reproducción/pausa, la doble pulsación del dispositivo avanza si existe un siguiente elemento y un comando Previous explícito retrocede; no se impone un temporizador de triple pulsación no estándar
* `Mejora` Unificar el diseño del README y la gestión de versiones de la plataforma Gradle
* `Mejora` Simplificar la descripción del complemento y normalizar la puntuación de los recursos multilingües
* `Mejora` Renombrar la entrada de visualización externa como External Viewer para unificar la semántica del visor
* `Mejora` Abrir la página integrada del historial de versiones desde el botón correspondiente del diálogo de actualización
* `Mejora` La verificación de compilación rechaza dependencias nativas accidentales y genera un informe JSON

# v2.3.0

###### 2026/08/31

* `Función` La información del video ahora identifica HDR10, HLG o SDR y muestra el espacio, rango y profundidad de color disponibles; permite copiar todos los campos y avisa una sola vez si la pantalla no declara compatibilidad con el formato HDR de origen
* `Función` Las capturas pueden incluir los subtítulos visibles mediante un ajuste desactivado de forma predeterminada, y la acción Compartir envía el PNG recién guardado directamente a otra aplicación
* `Función` El nuevo reflejo de video, limitado a la sesión, voltea izquierda/derecha, arriba/abajo o ambos ejes y se combina de forma coherente con el zoom de pellizco y la rotación de pantalla
* `Función` La nueva amplificación limitada a la sesión ofrece de +3 dB a +15 dB, permanece desactivada de forma predeterminada, avisa de distorsión y riesgo auditivo antes del primer uso y vuelve a Desactivado si no es compatible
* `Mejora` Las capturas siguen siendo PNG independientes de MediaStore sin permiso de almacenamiento; al compartir solo se concede acceso de lectura a la imagen elegida
* `Mejora` Tras evaluarlo se adoptó LoudnessEnhancer porque aplica una ganancia limitada solo durante la reproducción sin modificar archivos; cualquier fallo libera el efecto inmediatamente y vuelve a un estado seguro

# v2.2.0

###### 2026/08/31

* `Función` Nuevo panel de cola de Host Session: nombres, duraciones conocidas o marcadores, elemento actual resaltado, salto con un toque y control sincronizado de secuencia, aleatorio y repetición de uno
* `Función` Nueva acción de solo lectura Reproducir seleccionados: reproduce de 1 a 128 vídeos compatibles del mismo directorio en el orden elegido por el host, sin descubrir archivos hermanos
* `Función` Antes de la reproducción automática de la cola, conserva el último fotograma y muestra durante tres segundos un aviso cancelable para el siguiente elemento sin desbloquear los controles
* `Función` Nueva opción Recordar modo de reproducción, desactivada de forma predeterminada, para conservar secuencia, aleatorio o repetición de uno entre sesiones
* `Mejora` Cada objetivo se comprueba por separado con su URI content, posición en ClipData, MIME, metadatos, tamaño y directorio; los ID o URI duplicados rechazan todo el grupo
* `Mejora` La selección múltiple solo lee objetivos autorizados explícitamente mediante rutas limitadas de Host Session y nunca examina archivos hermanos para buscar vídeos o subtítulos

# v2.1.0

###### 2026/08/31

* `Función` Ajustes de estilo de subtítulos: tamaño del 75% al 150%, color, fondo opaco/semitransparente/sin fondo y margen inferior del 0%/4%/8% con vista previa en directo; las opciones se conservan en reproducción e imagen en imagen
* `Función` Detección de codificación para subtítulos externos: primero BOM y después GBK, Big5, Shift_JIS, EUC-KR, Windows-1251 y Windows-1256, con un aviso claro cuando el resultado no es seguro
* `Función` Carga manual de un subtítulo .srt o .ass desde el menú mediante el selector de documentos del sistema; se rechazan archivos mayores de 4 MiB o con otra extensión
* `Función` Desfase del subtítulo externo seleccionado entre −600,0 y +600,0 segundos en pasos de 0,1 segundos con información inmediata en pantalla
* `Función` En pausa, avance o retroceso fotograma a fotograma (mantener pulsado para repetir) y bucle A-B con límites normalizados
* `Mejora` La decodificación, conversión a UTF-8 y traslación temporal se realizan por completo en memoria, sin archivos temporales, permiso de almacenamiento ni autorización persistente
* `Mejora` El desfase vuelve a cero al cambiar de vídeo; el bucle A-B tiene prioridad mientras está activo y una selección posterior de repetición o temporizador hasta el final lo borra

# v2.0.0

###### 2026/08/29

* `Función` Sistema de temas totalmente nuevo: un solo color semilla genera paletas claras y oscuras legibles, siguiendo el tema de AutoJs6 de forma predeterminada, con 19 colores predefinidos y un color RGB personalizado con vista previa en vivo
* `Función` El complemento se convierte en una aplicación independiente: se añade una entrada en la pantalla de inicio y los videos pueden abrirse directamente con el selector de archivos del sistema
* `Función` Nueva página de ajustes: idioma, modo nocturno, color del tema, reanudación, actualizaciones e historial de versiones en un solo lugar; el idioma, el modo nocturno y el color del tema siguen AutoJs6 de forma predeterminada, y cuando el host no está disponible las opciones se deshabilitan y se usan los valores predeterminados de la aplicación
* `Función` Nueva comprobación de actualizaciones: comprobaciones manuales y automáticas una vez al día contra las versiones oficiales de GitHub, con versiones ignorables y una página integrada de historial de versiones localizada
* `Corrección` Se corrigió que seguir los ajustes de AutoJs6 no tuviera efecto en algunos escenarios (el servicio de información del complemento que el host requiere no se exponía antes)
* `Mejora` El historial de reanudación se reduce al único video más reciente sin terminar; abrir otro video borra de inmediato el registro anterior, y los videos terminados no conservan posición
* `Mejora` La aplicación pasa a llamarse 3-Ember Player; el ID de aplicación y el ID de complemento no cambian, por lo que las instalaciones existentes se actualizan sin reinstalar

# v1.4.0

###### 2026/08/28

* `Función` Reproducción por carpeta: abrir un video crea automáticamente una cola de la misma carpeta (orden natural de nombre de archivo) con anterior/siguiente, modos secuencial, aleatorio, repetición individual y reproducción automática del siguiente
* `Función` Subtítulos externos: los archivos .srt / .ass con el mismo nombre y sus variantes con sufijo de idioma se detectan automáticamente; los subtítulos permanecen desactivados hasta elegirlos en el menú de subtítulos
* `Función` Historial de reanudación opcional gestionado por el host: desactivado de forma predeterminada, puede activarse, desactivarse o borrarse en los ajustes de AutoJs6; las listas de archivos no muestran marcas de visto
* `Corrección` Se corrigió que archivos reconocidos como video por el host se rechazaran porque su extensión no figuraba en la antigua lista de permitidos; ahora las solicitudes video/* de confianza se aceptan de manera uniforme
* `Mejora` El acceso a hermanos se limita estrictamente al archivo seleccionado y a sus hermanos directos legibles, con los directorios recursivos, la escritura y las concesiones persistentes prohibidos; el complemento no guarda rutas en texto claro
* `Mejora` La cola de reproducción se limita a 128 videos con hasta 8 subtítulos externos cada uno, y el video elegido por el usuario permanece siempre en la cola
* `Mejora` La base de compatibilidad queda fijada en AutoJs6 6.8.0 (build 5276); los hosts antiguos recurren a la reproducción de un solo archivo sin afectar las funciones básicas
* `Dependencia` Se actualizó la API Explorer Action incluida del protocolo v2 a la v12 retrocompatible

# v1.3.1

###### 2026/08/27

* `Corrección` Se añadió una capa de compatibilidad XVID en MKV: estos videos ahora se reproducen con el decodificador MPEG-4 Part 2 integrado del dispositivo, sin transcodificar ni tocar el archivo de origen
* `Corrección` Cuando el dispositivo no tiene un decodificador compatible o la decodificación falla, ya no se produce la reproducción solo de audio; en su lugar se muestra un mensaje claro con la opción de abrir con otra aplicación

# v1.3.0

###### 2026/08/27

* `Función` Temporizador de apagado: pausa automática tras 15, 30, 45 o 60 minutos, o al terminar el video actual
* `Función` Zoom por pellizco (de 0,25× a 4×) con restablecimiento por doble toque mientras hay zoom
* `Función` Arrastrar la barra de progreso muestra una burbuja con el tiempo de destino y una vista previa en miniatura; si la extracción falla, se muestra solo el tiempo
* `Función` Captura de fotogramas: en Android 10+ guarda el fotograma actual como PNG en la galería del sistema, sin permiso de almacenamiento y sin modificar el video de origen
* `Función` Nuevos ajustes de sensibilidad de gestos (baja/estándar/alta) y de paso de salto por doble toque (5/10/30 segundos), recordados automáticamente
* `Mejora` El temporizador de apagado cuenta según el tiempo de actividad del sistema, por lo que cambiar la hora del reloj no afecta la cuenta atrás, y esta sobrevive a la rotación de la pantalla
* `Mejora` La extracción de miniaturas combina las solicitudes durante arrastres rápidos y libera pronto las imágenes obsoletas, ahorrando memoria

# v1.2.0

###### 2026/08/27

* `Función` Selección de pistas: cambia entre pistas de audio integradas por idioma y canales, y activa los subtítulos integrados, que permanecen desactivados de forma predeterminada
* `Función` Imagen en imagen: en Android 8.0+, salir durante la reproducción entra automáticamente en una ventana flotante, con entrada manual y reproducir/pausar a distancia
* `Función` Integración multimedia con el sistema: controles de auriculares y Bluetooth, más una notificación multimedia con título, progreso y acciones de reproducción
* `Función` Herramientas de reproducción: repetición individual, un panel de información del video y orientación automática única según la relación de aspecto
* `Mejora` Las pistas no compatibles se marcan claramente y no se pueden seleccionar; las entradas se ocultan automáticamente cuando no existe ninguna pista seleccionable
* `Mejora` El tamaño de archivo del panel de información del video procede del tamaño declarado y validado, lo que lo hace más fiable
* `Dependencia` Se añadió AndroidX Media3 Session 1.10.1

# v1.1.0

###### 2026/08/27

* `Función` Controles por gestos: deslizar en vertical en la mitad izquierda para el brillo y en la derecha para el volumen, deslizar en horizontal para avanzar o retroceder, doble toque en los laterales para saltar 10 segundos, doble toque en el centro para reproducir/pausar y pulsación larga para una velocidad temporal de 2×
* `Función` Velocidad de reproducción: 9 niveles de 0,25× a 3×, con la velocidad actual resaltada en la barra de control cuando no es 1×
* `Función` Pantalla completa inmersiva: barras del sistema ocultas con soporte para muescas de pantalla, barras de título y de control con ocultación automática, y cambio con un toque de la orientación y del modo de escalado
* `Función` Bloqueo de controles: bloquea todos los botones y gestos con un toque para evitar toques accidentales
* `Función` Reanudación de la reproducción: la posición se recuerda automáticamente y se restaura al reabrir el mismo video; los videos terminados se borran, se conservan hasta 200 registros y se almacenan resúmenes de archivo en lugar de rutas
* `Mejora` Barra de control inferior reconstruida: reproducir/pausar, saltos de 10 segundos, barra de progreso arrastrable y progreso de búfer, todo incluido
* `Mejora` El panel de fallo de reproducción gana un botón de reintento y conserva abrir con otra aplicación
* `Mejora` Los cambios de interfaz como la rotación ya no reconstruyen el reproductor, haciendo las transiciones más fluidas

# v1.0.1

###### 2026/08/08

* `Corrección` Se corrigió que el enlace del servicio quedara inválido tras activar el complemento en el centro de complementos de AutoJs6, lo que impedía al host usar el complemento
* `Mejora` Se simplificaron el nombre y la descripción del complemento, con una redacción más natural de la documentación en todos los idiomas

# v1.0.0

###### 2026/08/02

* `Función` Primera versión: publicada como complemento del gestor de archivos de AutoJs6; tocar un archivo de video en el gestor de archivos lo reproduce directamente
* `Función` Cubre 23 extensiones de video comunes (MP4, MKV, AVI, MOV, FLV, WEBM y más), asumiendo en modo de solo lectura la acción de abrir videos del gestor de archivos
* `Función` Reproducción con AndroidX Media3 ExoPlayer: reproducción automática, controles estándar, gestión del enfoque de audio, pausa automática al desconectar los auriculares y pantalla encendida mientras se reproduce
* `Función` Validación de seguridad estricta: la entrada de reproducción está protegida por un permiso de firma, los orígenes de las solicitudes, los archivos de destino y las concesiones de solo lectura se verifican uno a uno, y el reproductor no se exporta al sistema
* `Función` Entrada independiente "Abrir con" del sistema: otras aplicaciones pueden invocar este reproductor con un content URI de solo lectura para ver videos
* `Función` Cuando la reproducción falla, el video puede entregarse con seguridad a otra aplicación, con este complemento excluido de los candidatos
* `Función` Incluye 10 idiomas: chino simplificado, chino tradicional (Hong Kong/Taiwán), inglés, francés, español, japonés, coreano, ruso y árabe
* `Dependencia` Se incorporaron AndroidX Media3 ExoPlayer y UI 1.10.1
* `Dependencia` Se incorporó el runtime de Kotlin Parcelize 2.2.21
