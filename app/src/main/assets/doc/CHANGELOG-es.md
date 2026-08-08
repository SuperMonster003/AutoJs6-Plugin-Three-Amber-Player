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
