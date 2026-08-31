<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
  </p>

  <p>Complemento del gestor de archivos. Reproducir archivos de video directamente</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md está disponible actualmente en los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### Introducción

******

3-Ember Player es el complemento de reproducción de video del gestor de archivos de AutoJs6 y, al mismo tiempo, un reproductor de video local que funciona por sí solo. Toca un archivo de video en el gestor de archivos y empieza a reproducirse a pantalla completa; los gestos, la velocidad de reproducción, los subtítulos externos, la reproducción continua por carpeta y la imagen en imagen cubren la experiencia esencial de los reproductores más populares. La reproducción se basa en AndroidX Media3 ExoPlayer.

El complemento se mantiene fiel a un modelo de seguridad de solo lectura: los videos entran al reproductor mediante concesiones temporales de solo lectura, nunca se solicita permiso de almacenamiento y los archivos de origen jamás se modifican ni se mueven; el acceso a la red se usa únicamente para comprobar actualizaciones del complemento.

******

### Funciones destacadas

******

- Tocar y reproducir: toca un archivo de video en el gestor de archivos de AutoJs6 y se abre directamente en pantalla completa inmersiva, sin configurar nada.
- Gestos prácticos: desliza en la mitad izquierda para ajustar el brillo y en la derecha el volumen, desliza en horizontal para avanzar o retroceder, doble toque en los laterales para saltar, doble toque en el centro para reproducir/pausar, pulsación larga para una velocidad temporal de 2×, y bloquea todos los controles con un solo toque para evitar toques accidentales.
- Velocidad e imagen bajo control: 9 velocidades de reproducción de 0,25× a 3×, zoom por pellizco (de 0,25× a 4×), modos de escalado adaptar/rellenar/recortar, y cambio de orientación con un solo toque, con sugerencia automática según la relación de aspecto.
- Reproducción continua por carpeta: al abrir un video se crea una cola en orden natural; su panel resalta el actual, muestra la duración conocida, permite saltar y cambiar entre secuencial, aleatorio y repetición individual, y ofrece un aviso cancelable de tres segundos antes de continuar.
- Selecciones ordenadas del host: la acción de solo lectura Reproducir seleccionados convierte entre 1 y 128 videos de una carpeta en una cola con el orden exacto del host, sin descubrir archivos vecinos; una opción puede recordar el modo entre sesiones.
- Subtítulos externos e integrados: detecta automáticamente los .srt / .ass coincidentes o carga uno manualmente, reconoce codificaciones antiguas comunes, ajusta el estilo y un desfase externo de ±600 segundos, y cambia pistas integradas; los subtítulos siguen apagados hasta que los actives expresamente.
- Reproducción precisa: en pausa, avanza o retrocede fotograma a fotograma con repetición por pulsación larga, y define o borra un bucle A-B para revisar detalles.
- Reanudación de la reproducción: recuerda la posición del último video sin terminar y la retoma al volver a abrirlo; los videos terminados se borran de inmediato, sin dejar rastro de visualización.
- Imagen en imagen e integración con el sistema: imagen en imagen automática en Android 8.0+ al salir de la aplicación durante la reproducción, controles de auriculares y Bluetooth, y una notificación multimedia con título y progreso.
- Temporizador de apagado: pausa automática tras 15/30/45/60 minutos o al terminar el video actual.
- Vista previa al arrastrar: al arrastrar la barra de progreso se muestra una burbuja con el tiempo de destino y, cuando es posible, una miniatura de la escena.
- Captura de fotogramas: en Android 10+ guarda el fotograma actual como PNG en la galería del sistema con un toque, sin permiso de almacenamiento y sin tocar el video de origen.
- Respaldo para formatos difíciles: una capa ligera integrada de compatibilidad para XVID en MKV; cuando el dispositivo no puede decodificar un video, se muestra un aviso claro y la reproducción puede pasarse a otro reproductor.
- Temas a tu gusto: un solo color semilla genera paletas claras y oscuras legibles, siguiendo AutoJs6 de forma predeterminada, con 19 colores predefinidos y un color RGB personalizado con vista previa en vivo.
- Funciona por su cuenta: incluye un icono en la pantalla de inicio y una página de ajustes, abre videos mediante el selector de archivos del sistema y puede actuar como reproductor de video en el menú "Abrir con" del sistema.
- Solo lectura por diseño: sin permiso de almacenamiento, los videos de origen nunca se escriben; la red se usa únicamente para comprobar actualizaciones.

******

### Instalación y uso

******

Antes de empezar, asegúrate de que el entorno cumple los siguientes requisitos:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

De la instalación al primer video en 4 pasos:

1. Descarga e instala el APK del complemento. Aparecerá un icono de 3-Ember Player en la pantalla de inicio, mientras que las capacidades del complemento las gestiona AutoJs6.
2. Abre AutoJs6, entra en el `Centro de complementos`, busca `3-Ember Player` y actívalo.
3. Localiza cualquier archivo de video (como `movie.mp4`) en el gestor de archivos de AutoJs6.
4. Toca el archivo y el video empezará a reproducirse a pantalla completa.

Uso sin el host: abre 3-Ember Player desde la pantalla de inicio, toca `Abrir video` y elige un video con el selector de archivos del sistema; este reproductor también puede atender las solicitudes de visualización de video de otras aplicaciones. El requisito de versión del host indicado arriba solo afecta a la entrada del gestor de archivos; la reproducción independiente no se ve afectada.

Guía rápida de gestos del reproductor:

- Un toque: muestra u oculta la barra de control.
- Doble toque en el centro: reproducir/pausar; doble toque en el lado izquierdo/derecho: retroceder/avanzar 10 segundos (ajustable a 5/10/30 segundos en los ajustes).
- Deslizar en vertical en la mitad izquierda: ajustar el brillo; en la mitad derecha: ajustar el volumen.
- Deslizar en horizontal: previsualiza el punto de destino y suelta para aplicar el salto.
- Pulsación larga: velocidad temporal de 2×; suelta para recuperar la velocidad anterior.
- Pellizcar con dos dedos: acerca o aleja la imagen (de 0,25× a 4×); con zoom activo, doble toque para restablecer.
- Botón de bloqueo: bloquea todos los gestos y controles contra toques accidentales; después, toca la pantalla para mostrar el botón de desbloqueo.

******

### Formatos compatibles

******

La acción de reproducción en el gestor de archivos coincide exactamente con las siguientes 23 extensiones:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

La lista anterior es una lista de extensiones exactas permitidas que se conserva para hosts antiguos; en los hosts más recientes, cualquier archivo reconocido como video se entrega a este complemento mediante `video/*`. Figurar en la lista no garantiza la decodificación en todos los dispositivos; la reproducción real depende de Media3 y de los decodificadores de la plataforma. La entrada independiente y las llamadas de otras aplicaciones se aceptan igualmente por el tipo MIME `video/*`.

******

### Preguntas frecuentes

******

**¿Tocas un archivo de video y no se abre este reproductor?**

Comprueba en este orden: que el código de versión de AutoJs6 sea al menos 5276 (versión 6.8.0 o superior); que el complemento esté activado en el `Centro de complementos`; y que el host reconozca el archivo como video. Si falla cualquiera de las tres condiciones, este complemento no atenderá el toque.

**¿Qué ocurre si el complemento no está instalado o está desactivado?**

Un host compatible muestra una guía de recuperación que sugiere instalar o activar el complemento; el selector de aplicaciones del sistema solo aparece cuando el usuario elige expresamente `Abrir con otra aplicación`, entregando el video a otros reproductores del dispositivo.

**¿Se oye el audio pero la pantalla queda en negro, o la reproducción falla?**

Que un video se decodifique depende de la plataforma del dispositivo y de Media3; figurar en la lista de extensiones no garantiza la reproducción. Para el habitual formato antiguo XVID en MKV, una capa de compatibilidad integrada entrega la pista al decodificador MPEG-4 Part 2 del sistema; si aun así no se puede decodificar, se muestra un mensaje claro y `Abrir con otra aplicación` pasa el video a otro reproductor.

**¿Cómo reproduzco seguidos todos los videos de una carpeta?**

Con un host que cumpla el requisito de versión, abrir cualquier video desde el gestor de archivos crea automáticamente una cola de la misma carpeta: orden natural de nombre de archivo, empezando por el video actual, con reproducción automática del siguiente al terminar y modos secuencial, aleatorio y repetición individual. En hosts antiguos o desde la entrada independiente se mantiene la reproducción de un solo archivo.

**¿Cómo se cargan los subtítulos externos?**

Coloca un archivo `.srt` o `.ass` con el mismo nombre junto al video (se admiten sufijos de idioma como `movie.es.srt`), abre el video desde el gestor de archivos y elige el subtítulo en el menú de subtítulos. Los subtítulos están desactivados de forma predeterminada y nunca se activan solos.

**¿Qué registra la reanudación? ¿Se sube algo?**

Solo se guarda la posición del último video sin terminar, almacenada como un resumen SHA-256 del archivo más valores de tiempo, sin nombre de archivo ni ruta; abrir otro video o terminar la reproducción la borra de inmediato. Todo permanece en el dispositivo y no se sube nada.

**¿Qué permisos necesita el complemento?**

No necesita permisos de almacenamiento, cámara, micrófono ni ningún otro permiso sensible en tiempo de ejecución. Solo declara el permiso de red para comprobar actualizaciones (iniciadas por el usuario o como mucho una vez al día), más el permiso de complemento protegido por firma para la entrada del gestor de archivos.

**¿Se puede usar de forma independiente de AutoJs6?**

Sí. Desde la v2.0.0 el complemento tiene entrada propia en la pantalla de inicio: elige un video con el selector de archivos del sistema y reprodúcelo, o selecciona este reproductor en el menú `Abrir con` de otra aplicación. La reproducción por carpeta, la detección de subtítulos externos y seguir los ajustes del host siguen requiriendo AutoJs6.

******

### Seguridad

******

El complemento está construido sobre el principio de denegación por defecto; todas las medidas siguientes están siempre activas y no se pueden desactivar:

- Cero permisos sensibles: sin permisos de almacenamiento ni otros permisos en tiempo de ejecución; el acceso a la red se usa solo para comprobar versiones en GitHub, a petición del usuario o una vez al día.
- Nunca escribe: la reproducción, la captura de fotogramas y la extracción de miniaturas son de solo lectura de principio a fin; los videos de origen nunca se modifican, se mueven ni se eliminan.
- Validación entrada por entrada: la entrada del gestor de archivos está protegida por un permiso de complemento de nivel de firma, y en cada solicitud se verifican uno a uno la versión del protocolo, el URI de destino, ClipData, los metadatos, la compilación del host y las concesiones de solo lectura; cualquier discrepancia rechaza la solicitud.
- Acceso acotado a archivos hermanos: la cola y la detección de subtítulos pasan por una sesión efímera gestionada por el host que solo puede enumerar los hermanos directos del archivo seleccionado, con los directorios recursivos, la escritura y las concesiones persistentes prohibidos; el reproductor interno recibe únicamente una cola acotada y validada y nunca toca rutas del sistema de archivos.
- Entradas dobles aisladas: la entrada `ACTION_VIEW` orientada al sistema solo acepta solicitudes `video/*` con content URI de solo lectura, rechaza las concesiones de escritura, persistentes y prefix, y se mantiene independiente de la entrada del gestor de archivos.
- Datos de reanudación mínimos: el historial de reanudación conserva solo el último registro sin terminar como un resumen SHA-256 más valores de tiempo, y se borra al terminar la reproducción.
- Entrega segura: `Abrir con otra aplicación` reconstruye un intent de solo lectura y excluye este complemento de los candidatos, evitando la propagación de concesiones y los bucles propios.

******

### Interfaz del complemento (para desarrolladores)

******

El host descubre e invoca el complemento mediante los siguientes identificadores:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
engine: explorer-action
variant: default
protocol version: v12
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

La implementación actual se basa en explorer-action v12: registra la acción principal de solo lectura `play-video` y la acción de barra de selección `play-video-selection`, acepta los tipos reconocidos mediante `video/*` y conserva 23 extensiones exactas para hosts antiguos. La acción principal puede leer hermanos de forma limitada para la cola y subtítulos; la acción de selección conserva entre 1 y 128 objetivos del host y nunca habilita esa lectura. Las sesiones compatibles también pueden llevar progreso y toda capacidad opcional ausente retrocede de forma segura. Audio e imágenes son complementos independientes y no forman parte de este APK.

La cooperación completa con el host requiere AutoJs6 6.8.0 (build 5276) o posterior con Explorer Action v12; las próximas actualizaciones del complemento no elevarán este requisito.

******

### Hoja de ruta

******

Las capacidades ya publicadas y los planes futuros se mantienen en Roadmap.md como una lista de casillas marcables. Los elementos sin marcar expresan una intención y no describen capacidades de la versión actual.

- [Abrir el Roadmap.md con casillas marcables](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### Historial de versiones

******

#### v2.2.0

###### 2026/08/31

* `Función` Nuevo panel de cola de Host Session: nombres, duraciones conocidas o marcadores, elemento actual resaltado, salto con un toque y control sincronizado de secuencia, aleatorio y repetición de uno
* `Función` Nueva acción de solo lectura Reproducir seleccionados: reproduce de 1 a 128 vídeos compatibles del mismo directorio en el orden elegido por el host, sin descubrir archivos hermanos
* `Función` Antes de la reproducción automática de la cola, conserva el último fotograma y muestra durante tres segundos un aviso cancelable para el siguiente elemento sin desbloquear los controles
* `Función` Nueva opción Recordar modo de reproducción, desactivada de forma predeterminada, para conservar secuencia, aleatorio o repetición de uno entre sesiones
* `Mejora` Cada objetivo se comprueba por separado con su URI content, posición en ClipData, MIME, metadatos, tamaño y directorio; los ID o URI duplicados rechazan todo el grupo
* `Mejora` La selección múltiple solo lee objetivos autorizados explícitamente mediante rutas limitadas de Host Session y nunca examina archivos hermanos para buscar vídeos o subtítulos

#### v2.1.0

###### 2026/08/31

* `Función` Ajustes de estilo de subtítulos: tamaño del 75% al 150%, color, fondo opaco/semitransparente/sin fondo y margen inferior del 0%/4%/8% con vista previa en directo; las opciones se conservan en reproducción e imagen en imagen
* `Función` Detección de codificación para subtítulos externos: primero BOM y después GBK, Big5, Shift_JIS, EUC-KR, Windows-1251 y Windows-1256, con un aviso claro cuando el resultado no es seguro
* `Función` Carga manual de un subtítulo .srt o .ass desde el menú mediante el selector de documentos del sistema; se rechazan archivos mayores de 4 MiB o con otra extensión
* `Función` Desfase del subtítulo externo seleccionado entre −600,0 y +600,0 segundos en pasos de 0,1 segundos con información inmediata en pantalla
* `Función` En pausa, avance o retroceso fotograma a fotograma (mantener pulsado para repetir) y bucle A-B con límites normalizados
* `Mejora` La decodificación, conversión a UTF-8 y traslación temporal se realizan por completo en memoria, sin archivos temporales, permiso de almacenamiento ni autorización persistente
* `Mejora` El desfase vuelve a cero al cambiar de vídeo; el bucle A-B tiene prioridad mientras está activo y una selección posterior de repetición o temporizador hasta el final lo borra

#### v2.0.0

###### 2026/08/29

* `Función` Sistema de temas totalmente nuevo: un solo color semilla genera paletas claras y oscuras legibles, siguiendo el tema de AutoJs6 de forma predeterminada, con 19 colores predefinidos y un color RGB personalizado con vista previa en vivo
* `Función` El complemento se convierte en una aplicación independiente: se añade una entrada en la pantalla de inicio y los videos pueden abrirse directamente con el selector de archivos del sistema
* `Función` Nueva página de ajustes: idioma, modo nocturno, color del tema, reanudación, actualizaciones e historial de versiones en un solo lugar; el idioma, el modo nocturno y el color del tema siguen AutoJs6 de forma predeterminada, y cuando el host no está disponible las opciones se deshabilitan y se usan los valores predeterminados de la aplicación
* `Función` Nueva comprobación de actualizaciones: comprobaciones manuales y automáticas una vez al día contra las versiones oficiales de GitHub, con versiones ignorables y una página integrada de historial de versiones localizada
* `Corrección` Se corrigió que seguir los ajustes de AutoJs6 no tuviera efecto en algunos escenarios (el servicio de información del complemento que el host requiere no se exponía antes)
* `Mejora` El historial de reanudación se reduce al único video más reciente sin terminar; abrir otro video borra de inmediato el registro anterior, y los videos terminados no conservan posición
* `Mejora` La aplicación pasa a llamarse 3-Ember Player; el ID de aplicación y el ID de complemento no cambian, por lo que las instalaciones existentes se actualizan sin reinstalar

##### Historial completo

* [CHANGELOG-es.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilación Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Los parámetros de compilación proceden de `version.properties`; el SDK mínimo actual es 24 y el SDK de destino es 36.

******

### Estructura de recursos

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza la información del complemento y la interfaz del reproductor, y `plugin_instruction.md` aporta las notas de uso que se muestran en el lado del host. Todos los archivos README y CHANGELOG se generan a partir de fuentes JSON con `.python/generate_markdown.py`: para modificar la documentación, edita los archivos `lang_*.json` de `.readme` y `.changelog` y vuelve a ejecutar el script, en lugar de editar los archivos Markdown generados.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (motor de reproducción): https://developer.android.com/media/media3/exoplayer
- Uso compartido seguro de archivos en Android: https://developer.android.com/training/secure-file-sharing
