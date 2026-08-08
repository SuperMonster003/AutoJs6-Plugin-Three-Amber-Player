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

- Registra una acción principal de Explorador de solo lectura para un único archivo mediante la versión 2 del protocolo compartido `org.autojs.plugin.EXPLORER_ACTION`.
- Usa AndroidX Media3 ExoPlayer y PlayerView para reproducción automática, controles estándar, enfoque de audio, gestión del cambio a salida de audio noisy e integración con los códecs del dispositivo.
- Restaura la posición y la intención de reproducción o pausa después de una recreación, y mantiene la pantalla encendida solo mientras el video se reproduce activamente.
- Proporciona una entrada `android.intent.action.VIEW` exportada e independiente para content URI de solo lectura con un tipo MIME `video/*`.
- Ofrece una acción segura Abrir con otra aplicación tras un fallo de reproducción mediante la reconstrucción de un Intent de visualización de solo lectura y la exclusión de este plugin de la lista de candidatos.

******

### Integración con el host

******

El host usa este complemento para la ruta principal de apertura de video y la acción Reproducir.

Después de instalar, activar, confiar y comprobar la compatibilidad del plugin, al abrir un video coincidente se ejecuta la acción `play-video` como visor principal del Explorador.

Si el complemento falta, está desactivado, no está disponible, es incompatible o no se puede iniciar, el host recurre a su ruta de sistema `android.intent.action.VIEW` para que otra aplicación de video instalada pueda gestionar el archivo.

Este plugin solo coincide con archivos de video. La reproducción de audio y la visualización de imágenes siguen siendo capacidades de plugins independientes y no se incluyen en este APK.

******

### Formatos compatibles

******

La acción principal del Explorador tiene un comparador MIME vacío y solo coincide exactamente con estas 23 extensiones de video del host:

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
Explorer MIME types: empty
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5269
```

La versión 1 proporciona una acción principal de Explorador de solo lectura para las extensiones de video indicadas. La entrada externa independiente acepta un content URI de solo lectura con cualquier subtipo MIME `video/*` válido. La decodificación real depende de los extractores de Media3 y de los códecs disponibles en el dispositivo.

Se requiere la compilación 5269 o posterior del host.

******

### Seguridad

******

El plugin no solicita permisos de almacenamiento ni de INTERNET. Su límite del Explorador protegido por firma valida la versión 2 del protocolo, la superficie de origen, la compilación del host, los content URI de destino y padre, el orden exacto de ClipData, el nombre para mostrar, el tamaño declarado, el tipo MIME, la extensión y los indicadores de concesión de solo lectura. Después crea un Intent explícito nuevo para el reproductor no exportado que solo contiene el URI de destino, el tipo MIME, un nombre para mostrar seguro, un elemento ClipData de destino y la concesión de lectura. El límite público ACTION_VIEW acepta por separado solo un content URI, un tipo MIME de video y la concesión de lectura exacta, ignora todos los extras y ClipData recibidos y reconstruye la misma solicitud interna mínima.

******

### Límites de seguridad

******

- Un content URI de destino por solicitud de reproducción.
- La ejecución desde el Explorador requiere el permiso de nivel signature `org.autojs.permission.PLUGIN`.
- Las concesiones de escritura y persistentes siempre se rechazan. El acceso prefix utilizado para validar el URI padre nunca se reenvía al reproductor.
- El límite público ACTION_VIEW rechaza concesiones de escritura, persistentes y prefix.
- Los candidatos externos se resuelven primero, se filtran para conservar otros paquetes y se inician con un Intent de solo lectura recién creado.
- Una extensión incluida no garantiza compatibilidad de decodificación en todos los dispositivos. Media3 y los códecs de plataforma instalados determinan la compatibilidad real de reproducción.

******

### Historial de versiones

******

# v1.0.1

###### 2026/08/08

* `Corrección` Devolver un enlace válido al servicio Explorer Action al activarlo desde el centro de complementos
* `Mejora` Acortar el nombre y la descripción del complemento y hacer más natural la documentación de usuario

# v1.0.0

###### 2026/08/02

* `Función` Plugin Video Player con ID de plugin `video-player`, ID de acción `play-video`, motor `explorer-action` y variante `default`
* `Función` Acción principal de Explorador de solo lectura mediante el protocolo v2, con un comparador MIME vacío, que solo coincide con las 23 extensiones de video actuales del host y requiere la compilación 5269
* `Función` Entrada de Explorador protegida por firma con validación estricta de content URI de destino y padre, ClipData, origen, nombre para mostrar, tamaño, tipo MIME, extensión y concesiones, seguida de un reenvío mínimo a un reproductor no exportado
* `Función` Entrada ACTION_VIEW exportada e independiente para content URI de video de solo lectura, con descarte de extras y ClipData no confiables, rechazo de concesiones prohibidas y protección contra bucles propios
* `Función` Reproducción mediante Media3 ExoPlayer y PlayerView con inicio automático, controles estándar, enfoque de audio, gestión del cambio a salida de audio noisy, posición y estado de reproducción guardados, y pantalla encendida solo durante la reproducción activa
* `Función` Recuperación segura Abrir con otra aplicación después de un fallo de reproducción, mediante Intents de solo lectura recién creados que excluyen explícitamente este plugin
* `Función` Metadatos, texto de interfaz, instrucciones de uso, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Media3 ExoPlayer y UI versión 1.10.1
* `Dependencia` Añadido runtime de Kotlin Parcelize versión 2.2.21

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
