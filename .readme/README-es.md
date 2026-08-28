<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
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

El README.md actual admite los siguientes idiomas:

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

Video Player reproduce contenido de video proporcionado por el gestor de archivos u otra aplicación Android mediante acceso temporal de solo lectura a un content URI. Usa AndroidX Media3 ExoPlayer y nunca modifica el video de origen.

******

### Funciones

******

- Compatibilidad ligera con XVID en MKV mediante el decodificador MPEG-4 Part 2 integrado del dispositivo, con recuperación explícita mediante otro reproductor cuando el decodificador del sistema no está disponible o falla.
- Registra una acción principal de Explorador de solo lectura mediante la versión 12 del protocolo compartido `org.autojs.plugin.EXPLORER_ACTION`, con capacidades opcionales y acotadas para archivos hermanos y progreso de reproducción.
- Usa AndroidX Media3 ExoPlayer y PlayerView para reproducción automática, controles estándar, enfoque de audio, gestión del cambio a salida de audio noisy e integración con los códecs del dispositivo.
- Restaura la posición y la intención de reproducción o pausa después de una recreación, y mantiene la pantalla encendida solo mientras el video se reproduce activamente.
- Proporciona una entrada `android.intent.action.VIEW` exportada e independiente para content URI de solo lectura con un tipo MIME `video/*`.
- Ofrece una acción segura Abrir con otra aplicación tras un fallo de reproducción mediante la reconstrucción de un Intent de visualización de solo lectura y la exclusión de este plugin de la lista de candidatos.
- Ofrece reproducción inmersiva a pantalla completa con gestos para brillo, volumen y búsqueda, saltos con doble toque, velocidad temporal con pulsación larga y bloqueo de controles.
- Proporciona velocidades de reproducción de 0,25× a 3× y cambio con un toque del modo de ajuste y la orientación de la pantalla.
- Recuerda localmente la posición de reanudación indexada por resúmenes del content URI, borrada al terminar el video.
- Selecciona pistas de audio y subtítulos integrados, con los subtítulos desactivados de forma predeterminada y las pistas no compatibles claramente identificadas.
- Ofrece repetición del video actual, un panel de metadatos detallado, orientación según la relación de aspecto e imagen en imagen en API 26+ con reproducción/pausa remota.
- Integra MediaSession para controles de auriculares, Bluetooth y del sistema, además de una notificación multimedia con título y progreso.
- Temporizador de 15, 30, 45 o 60 minutos y fin del video, con ajustes persistentes de sensibilidad y salto por doble toque de 5/10/30 segundos.
- Zoom por pellizco de 0,25× a 4× con restablecimiento por doble toque y tiempo de destino al arrastrar con miniaturas opcionales en memoria.
- Capturas del fotograma actual sin permisos en Android 10+, guardadas como PNG independientes mediante MediaStore sin modificar el video de origen.
- Colas de videos de la misma carpeta con orden natural mediante Explorer Action v12, con anterior / siguiente, secuencia, aleatorio, repetición de uno y reproducción automática del siguiente elemento.
- Descubre subtítulos externos .srt y .ass coincidentes, incluidos sufijos de idioma, desactivados de forma predeterminada y cargados solo tras una selección explícita.
- Historial de reanudación opcional administrado por el host, desactivado de forma predeterminada, desactivable o borrable en los ajustes de AutoJs6 y sin marcas de visto en la lista de archivos.

******

### Integración con el host

******

El host usa este complemento para la ruta principal de apertura de video y la acción Reproducir.

Después de instalar, activar, confiar y comprobar la compatibilidad del plugin, al abrir cualquier archivo que el host reconozca como video se ejecuta la acción `play-video` como visor principal del Explorador.

Si el plugin falta, está desactivado, no está autorizado, no está disponible, es incompatible o no se puede iniciar, un host compatible muestra instrucciones de recuperación. El selector de aplicaciones del sistema solo se abre cuando el usuario elige explícitamente Abrir con otras aplicaciones.

Este plugin solo coincide con archivos de video. La reproducción de audio y la visualización de imágenes siguen siendo capacidades de plugins independientes y no se incluyen en este APK.

******

### Formatos compatibles

******

La acción principal del Explorador acepta `video/*` para todos los tipos de video reconocidos por el host y conserva estos 23 comparadores exactos de extensión para mantener la compatibilidad con hosts anteriores:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Interfaz del plugin

******

El host descubre y ejecuta el complemento con las siguientes identidades:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

La versión 1.4 proporciona una acción principal de Explorador de solo lectura con protocolo v12. Un host compatible puede adjuntar capacidades por solicitud para hermanos directos y progreso de reproducción; los hosts sin estas extensiones opcionales conservan la reproducción de un único archivo. La entrada externa independiente sigue aceptando solo un content URI de lectura con un subtipo MIME `video/*` válido.

Las funciones completas de cooperación requieren AutoJs6 6.8.0 build 5276 o posterior y Explorer Action v12; este requisito no aumentará con capacidades futuras del plugin.

******

### Seguridad

******

El plugin no solicita permisos de almacenamiento ni de INTERNET. Su límite del Explorador protegido por firma valida el sobre completo del protocolo v12, el único destino seleccionado, la relación con el padre, ClipData, metadatos, compilación del host y concesiones de solo lectura. Las Host Session opcionales quedan vinculadas por el host al UID del plugin y solo permiten listar el padre directo del archivo seleccionado y abrir este o un hermano directo legible. El reproductor privado valida una cola opaca acotada y nunca recibe una ruta del sistema de archivos. El límite público ACTION_VIEW permanece independiente y de un único archivo.

******

### Límites de seguridad

******

- El Explorador parte exactamente de un content URI seleccionado; una Host Session opcional solo puede exponer hermanos directos legibles y nunca acceso recursivo a directorios.
- La ejecución desde el Explorador requiere el permiso de nivel signature `org.autojs.permission.PLUGIN`.
- Las concesiones de escritura y persistentes siempre se rechazan. El acceso prefix utilizado para validar el URI padre nunca se reenvía al reproductor.
- El límite público ACTION_VIEW rechaza concesiones de escritura, persistentes y prefix.
- Los candidatos externos se resuelven primero, se filtran para conservar otros paquetes y se inician con un Intent de solo lectura recién creado.
- El historial de reproducción del host está desactivado de forma predeterminada, solo guarda resúmenes de rutas canónicas y valores de tiempo tras la aceptación explícita, y puede desactivarse o borrarse en los ajustes de AutoJs6.
- El reconocimiento como video o una extensión heredada incluida no garantiza compatibilidad de decodificación en todos los dispositivos. Media3 y los códecs de plataforma instalados determinan la compatibilidad real de reproducción.

******

### Historial de versiones

******

# v1.4.0

###### 2026/08/28

* `Función` Colas de vídeos de la misma carpeta con orden natural mediante una Host Session de Explorer Action v12 limitada a la solicitud y vinculada al UID, con anterior / siguiente, secuencia, aleatorio, repetición individual y avance automático
* `Función` Detección de subtítulos externos .srt y .ass coincidentes, incluidas variantes con sufijo de idioma; permanecen desactivados por defecto y solo se cargan tras una selección explícita
* `Función` Historial de reanudación opcional administrado por el host: el registro está desactivado por defecto, se puede desactivar o borrar en los ajustes de AutoJs6 y no añade marcas de visto a la lista de archivos
* `Corrección` Las solicitudes de Explorer clasificadas por el host como video se rechazaban si su extensión no figuraba en la lista heredada de 23 elementos; ahora se aceptan de manera uniforme las solicitudes `video/*` de confianza
* `Mejora` El acceso se limita al archivo seleccionado y a archivos hermanos directos legibles, sin recorrido recursivo, escritura, permisos persistentes ni rutas en texto claro dentro del plugin
* `Mejora` Las colas serializadas se limitan a 128 vídeos, 8 subtítulos por vídeo y 128 asociaciones de subtítulos en total, conservando siempre el elemento seleccionado
* `Mejora` La compatibilidad permanece en AutoJs6 6.8.0 build 5276 y Explorer Action v12; los hosts sin extensiones opcionales conservan de forma segura la reproducción de un solo archivo
* `Dependencia` Se actualizó la API Explorer Action incluida del protocolo v2 a la extensión de sesión multimedia v12 retrocompatible

# v1.3.1

###### 2026/08/27

* `Corrección` Se añadió una capa ligera de compatibilidad XVID en MKV que expone las pistas XVID VFW/FourCC validadas al decodificador MPEG-4 Part 2 integrado del dispositivo, sin transcodificar ni modificar el archivo original
* `Corrección` Se detiene la reproducción de solo audio si no hay un decodificador del sistema compatible o si falla la decodificación, con una explicación específica y la opción de abrir con otra aplicación

# v1.3.0

###### 2026/08/27

* `Función` Temporizador: pausa tras 15, 30, 45 o 60 minutos o al finalizar el video actual, con gestión del conflicto con la repetición
* `Función` Interacción con la imagen: zoom por pellizco de 0,25× a 4×, restablecimiento por doble toque y coordinación con los modos de ajuste existentes
* `Función` Vista previa al arrastrar: tiempo de destino y miniaturas opcionales en memoria, con degradación silenciosa a solo el tiempo
* `Función` Capturas del fotograma actual en Android 10+ guardadas como PNG independientes mediante MediaStore, sin permiso de almacenamiento ni modificación del origen
* `Función` Ajustes persistentes de gestos para sensibilidad baja, normal o alta y saltos por doble toque de 5, 10 o 30 segundos
* `Mejora` Los plazos del temporizador usan tiempo transcurrido y sobreviven a la recreación de la página sin depender de cambios del reloj
* `Mejora` La extracción de miniaturas combina las solicitudes rápidas en un único hilo y libera cada bitmap temporal que queda obsoleto

##### Para consultar más versiones

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

Los parámetros de compilación proceden de `version.properties`. El SDK mínimo actual es 24 y el SDK de destino es 36.

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

`strings.xml` localiza los metadatos del plugin y el texto de la interfaz. `plugin_instruction.md` proporciona instrucciones de uso y seguridad. `.python/generate_markdown.py` genera archivos README y de cambios localizados a partir de fuentes JSON.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- Uso compartido seguro de archivos en Android: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
