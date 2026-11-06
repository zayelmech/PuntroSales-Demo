# Carga de productos: paginado y caché

Al abrir el inventario, la implementación anterior leía, ordenaba y transformaba todo el catálogo. La nueva prepara una primera página de 40 productos y carga más al desplazarse.

## Métricas medidas

Milisegundos, mediana / P95. La referencia anterior se capturó **antes de modificar producción**, en el commit de pruebas `4d75962` sobre producción `69e235a`, con Room v14. La implementación final de producción es `153f133`, con Room v15.

| Productos en BD | Antes: catálogo completo | Después: primera generación de Paging | Mejora de mediana |
|---:|---:|---:|---:|
| 1,000 | 38.36 / 44.21 | 16.54 / 22.71 | 2.3× |
| 10,000 | 70.19 / 83.37 | 11.79 / 13.81 | 6.0× |
| 50,000 | 645.54 / 726.88 | 11.35 / 13.02 | 56.9× |

La medición nueva incluye la colección del Pager, la transformación a modelos de UI y el presentador de `paging-testing.asSnapshot()`. No representa tiempo de arranque, primer frame, FPS ni tiempo para recorrer/cargar todos los productos. El trabajo cambia de preparar N modelos a preparar 40 para poder mostrar la primera página.

También se volvió a ejecutar el flujo legado, sin cambiar su comportamiento, en el mismo proceso de instrumentación de la versión final. Esto permite ver la variación entre ejecuciones y comparar ambos caminos en el mismo entorno:

| Productos | Control legado en versión final, mediana | Página cercana al final, mediana | Recolección de caché, mediana |
|---:|---:|---:|---:|
| 1,000 | 44.17 | 1.54 | 6.15 |
| 10,000 | 78.56 | 1.07 | 5.54 |
| 50,000 | 924.42 | 1.10 | 5.59 |

La recolección de caché reutilizó 40 modelos y realizó **0 consultas adicionales de filas a Room** en las nueve muestras; el test lo comprueba. El tiempo de esa operación incluye el presentador de pruebas. La carga de la página profunda parte del cursor situado 80 productos antes del final, sin recorrer las páginas anteriores.

Resultados crudos: [referencia inicial](products-baseline.json) y [medición final y control](products-results.json).

## Condiciones y límites

- Emulador `Medium_Phone_API_35`, arm64, Android API 35, variante `huaweiDebug`, red desactivada después de completar el arranque.
- Base Room respaldada por archivo, independiente y desechable, con 1,000, 10,000 y 50,000 productos, categorías, stock positivo/negativo y descripciones. Dos calentamientos y nueve muestras por tamaño y operación. Inserciones y apertura inicial de BD fuera del cronómetro.
- P95 por rango más cercano: con nueve muestras corresponde al máximo observado. Se conservan todas las muestras para permitir revisar dispersión; son mediciones de debug en emulador.
- Sin imágenes ni composición de tarjetas dentro del cronómetro. Las miniaturas se validan por dimensiones, reutilización, versionado y conservación del original; no se atribuye a ellas una mejora de latencia medida.
- La migración inicial 14→15 y la primera generación de miniaturas no están incluidas. No se midieron heap, GC, arranque en frío ni fluidez en un dispositivo físico.
- El tamaño de ventana configurado en Paging es 200 productos; selección masiva conserva IDs para mantener los contratos actuales de CSV y navegación a catálogo. Las combinaciones de búsqueda, múltiples categorías y orden pueden tener un costo distinto del listado inicial por ID.

## Implementación y compatibilidad

- Se mantienen `getAllProducts()`, `getProductsWithIds()` y sus contratos existentes. El inventario usa el nuevo `ProductsPagingRepository`; los demás consumidores conservan sus APIs.
- Paginado bidireccional por cursor, con ID como desempate para nombre, precio y stock. La actualización recupera el ancla por ID; las mutaciones de productos/categorías invalidan la generación mediante la API pública `InvalidationTracker.createFlow()`. Se liberan observadores al cambiar consulta o destruir el ViewModel.
- Consultas de listado ligeras e índices para los órdenes y prefijo. El prefijo indexado usa el mismo plegado simple de puntos de código de ICU que Android SQLite; `LIKE` sobre el nombre original conserva la autoridad sobre comodines y coincidencias Unicode. Se verifican los planes en SQLite del emulador.
- La migración preserva productos, categorías, precios, cantidades, imágenes y códigos, y rellena las claves de búsqueda en lotes de 500 filas.
- Se conservan el alcance/orden previos de la búsqueda por prefijo y el significado de los órdenes existentes; “fecha” continúa usando ID. Los cambios de búsqueda cancelan el paginador anterior.
- `cachedIn(viewModelScope)` conserva páginas y modelos preparados. La selección usa pertenencia a un Set para las filas visibles, sin transformar de nuevo todo el catálogo. “Seleccionar todos” consulta IDs del listado filtrado completo, incluidas páginas todavía no cargadas.
- Compose conserva tarjetas, navegación y banner. Carga inicial, paginado, errores/reintento y vacío se muestran dentro del listado. Claves y filas se leen de la misma instantánea para evitar carreras al reducir resultados de una búsqueda.
- Miniaturas según densidad de pantalla, dos decodificaciones simultáneas y presupuesto de disco de 64 MiB con limpieza cada 32 escrituras. Se usa el ImageLoader compartido de Coil, se versiona por archivo/tamaño/fecha/dimensiones y se conserva la foto original. Proveedores externos sin versión de archivo usan su URI original.
- Onboarding cuenta con SQL; exportación y publicación consultan datos por lotes de hasta 500 IDs y conservan orden y campos. El selector propio de catálogo mantiene su flujo existente; esta mejora pagina el inventario.

## Validación

- 25 pruebas instrumentadas de app, 0 fallos: órdenes, categorías y sin categoría, paginado en ambos sentidos, búsqueda y Unicode, planes SQL, migración, stock, exportación, selección de productos no cargados, caché al volver, búsqueda/vacío/limpieza, scroll, miniaturas y onboarding existente.
- 66 pruebas unitarias, 0 fallos: app Huawei (45), ventas UI (17) y dominio de productos (4).
- Compilaciones Google debug y Huawei release y lint de app Huawei, datos de productos y UI de productos.
- Los generadores nuevos y repositorios dummy viven exclusivamente en `src/androidTest` o `src/test`; las bases se eliminan al terminar. Se inspeccionaron los DEX del APK Huawei release: no contienen las clases ni los marcadores de fixtures nuevos.
- Se repararon fixtures antiguos: repositorio dummy ausente, lista de monedas desactualizada y comprobación del package que ignoraba el flavor Huawei. No se modificaron sus reglas de producción.

## Reproducción

Con un emulador conectado, detener descargas de fondo antes de comparar:

```sh
adb shell svc wifi disable
adb shell svc data disable
./gradlew :app:connectedHuaweiDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.imecatro.demosales.products.ProductsLoadingBaselineTest,com.imecatro.demosales.products.ProductsPagingPerformanceTest --console=plain
adb logcat -d -s ProductsPerformance:I
```

La referencia original se reproduce desde `4d75962` ejecutando solo `ProductsLoadingBaselineTest`. En la rama final se puede ejecutar el paquete completo de pruebas de productos o la suite instrumentada completa:

```sh
./gradlew :app:connectedHuaweiDebugAndroidTest :app:testHuaweiDebugUnitTest :demosales-ui:sales:testDebugUnitTest :demosales-domain:products:test --console=plain
./gradlew :app:lintHuaweiDebug :demosales-data:products:lintDebug :demosales-ui:products:lintDebug --console=plain
./gradlew :app:assembleHuaweiRelease --console=plain
./gradlew :app:assembleGoogleDebug --console=plain
```

Los comandos de variantes y lint se validaron por separado.
