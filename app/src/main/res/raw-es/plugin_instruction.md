3-Amber Player es el plugin de reproducción de vídeo del explorador de archivos de AutoJs6 y también funciona como reproductor de vídeo local independiente. Toca un vídeo en el explorador de archivos para reproducirlo a pantalla completa, con reproducción continua de la misma carpeta, listas de reproducción locales, subtítulos externos, imagen en imagen y audio en segundo plano, sin modificar nunca el vídeo original.

### Primeros pasos

1. Comprueba que este plugin esté activado en el "Centro de plugins" de AutoJs6.
2. Toca un vídeo en el explorador de archivos de AutoJs6; el reproductor se abre a pantalla completa y empieza a reproducir.
3. Toca la imagen para mostrar los controles; desliza hacia arriba o abajo en la mitad izquierda o derecha para ajustar el brillo o el volumen, y toca dos veces el centro para reproducir o pausar.
4. Para usarlo sin AutoJs6, abre este plugin desde su icono y toca "Abrir un archivo de vídeo".

### Funciones principales

- **Reproducción continua**: Al abrir un vídeo, los demás vídeos de la misma carpeta forman una cola; salta entre ellos desde "Lista de reproducción de vídeos" y elige reproducir en orden, de forma aleatoria o repetir el video actual.
- **Reproducir seleccionados**: Selecciona varios vídeos en el explorador de archivos y toca "Reproducir seleccionados" para reproducir hasta 128 en el orden de selección.
- **Listas de reproducción locales**: Abre archivos de lista como M3U/M3U8, PLS o XSPF, con hasta 128 elementos; si una lista hace referencia a vídeos de otra carpeta, ábrela desde el icono del plugin y elige esa carpeta cuando se solicite.
- **Gestos y velocidad**: Desliza en horizontal para avanzar o retroceder y mantén pulsado para reproducir temporalmente a 2x; ajusta el salto del doble toque en "Ajustes de gestos", y "Bloquear controles" evita toques accidentales.
- **Subtítulos y pistas de audio**: Cambia las pistas integradas en "Subtítulos" y "Pista de audio", usa "Cargar desde archivo" para un subtítulo .srt o .ass, y ajusta "Estilo de subtítulos" y "Desfase de subtítulos".
- **Reanudar reproducción**: Un vídeo sin terminar continúa donde se detuvo al volver a abrirlo; desactiva "Reanudar reproducción" en los ajustes para evitarlo.
- **Imagen en imagen y audio en segundo plano**: En Android 8.0 o posterior puedes usar "Imagen en imagen"; con "Continuar el audio en segundo plano" activado, el sonido sigue al salir del reproductor.
- **Temporizador y fotograma a fotograma**: "Temporizador de apagado" detiene la reproducción al terminar el tiempo; en pausa, avanza fotograma a fotograma y define un bucle A-B.
- **Capturas e información del vídeo**: En Android 10 o posterior, "Guardar fotograma actual" guarda un PNG en `Pictures/3-Amber Player`; "Información del video" muestra la resolución, la velocidad de fotogramas y más.
- **Apariencia y ajustes**: Los colores, el modo nocturno y el idioma siguen a AutoJs6 de forma predeterminada; en "Ajustes" también puedes "Buscar actualizaciones" y ver "Historial de versiones".

### Requisitos

- Android 7.0 o posterior.
- Abrir vídeos desde el explorador de archivos requiere AutoJs6 6.8.0 o posterior; el reproductor independiente abierto desde su icono no necesita AutoJs6.
- "Continuar el audio en segundo plano" necesita el permiso de notificaciones para mostrar la notificación con los controles de reproducción.

### Preguntas frecuentes

**Motivos por los que tocar un vídeo no abre este reproductor**

Comprueba que AutoJs6 cumple la versión requerida y que este plugin está activado en el "Centro de plugins". Si el plugin no está disponible, AutoJs6 muestra una guía de recuperación, y "Abierto por otras aplicaciones" entrega el vídeo a otro reproductor.

**Casos en que un vídeo no se puede reproducir**

La decodificación depende de los decodificadores del dispositivo. Si la reproducción falla, toca "Abrir con otra aplicación" para usar otro reproductor.

**Contenido del registro de reanudación**

Solo se guardan una huella de identidad y la posición de reproducción del último vídeo sin terminar, únicamente en este dispositivo; se borran al abrir otro vídeo o al terminar la reproducción.

Más detalles en la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player).
