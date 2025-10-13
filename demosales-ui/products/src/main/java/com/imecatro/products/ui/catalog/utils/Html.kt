package com.imecatro.products.ui.catalog.utils

import com.imecatro.products.ui.catalog.model.ProductCatalogModel
import java.net.URLEncoder



fun buildProductsCatalogHtml(
    title: String = "Products Catalog",
    products: Map<String?, List<ProductCatalogModel>>,
    columns: Int = 4,
    paginate: Boolean = true
): String {
    val safeCols = columns.coerceIn(1, 6)

    fun htmlEsc(s: String?): String =
        s?.replace("&", "&amp;")
            ?.replace("<", "&lt;")
            ?.replace(">", "&gt;")
            ?.replace("\"", "&quot;")
            ?.replace("'", "&#39;") ?: ""

    fun placeholderSvgDataUrl(text: String = "No Image"): String {
        val svg = """
            <svg xmlns='http://www.w3.org/2000/svg' width='600' height='400'>
              <rect width='100%' height='100%' fill='#EEE'/>
              <text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle'
                    font-family='Roboto, Helvetica, Arial, sans-serif' font-size='32' fill='#888'>$text</text>
            </svg>
        """.trimIndent()
        return "data:image/svg+xml;utf8," + URLEncoder.encode(svg, "UTF-8")
    }

    val sb = StringBuilder()
    sb.append(
        """
        <!doctype html>
        <html lang="es">
        <head>
          <meta charset="utf-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <title>${htmlEsc(title)}</title>
          <style>
            :root{
              --columns: ${safeCols};
              --gap: 4mm;
              --card-radius: 2mm;
              --card-padding: 2mm;
              --img-h: 35mm;
              --title-fs: 3.5mm;
              --price-fs: 4mm;
              --muted: #666;
              --stroke: #E5E7EB;
              --elev: 0 0.5mm 2mm rgba(0,0,0,.08);
              --page-margin-mm: 10mm;
              --footer-line-color: #000;      /* más visible */
              --footer-line-thickness: 1mm;   /* más gruesa */
            }

            @page { size: A4; margin: 0; }
            html, body { margin:0; padding:0; }
            body { font-family: Roboto, Helvetica, Arial, sans-serif; color:#111; }

            .sheet { ${if (paginate) "width:210mm; margin:0 auto;" else ""} }

            ${if (paginate) """
            .page {
              position: relative;
              width: 210mm;
              height: 297mm;
              box-sizing: border-box;
              background: white;
              overflow: hidden;
            }
            .page-inner {
              position: absolute;
              left: var(--page-margin-mm);
              right: var(--page-margin-mm);
              top: var(--page-margin-mm);
              bottom: var(--page-margin-mm);
              box-sizing: border-box;
            }
            /* línea al fondo (encima del contenido) */
            .page-inner::after {
              content: "";
              position: absolute;
              left: 0; right: 0; bottom: 0;
              border-top: var(--footer-line-thickness) solid var(--footer-line-color);
              z-index: 2;
            }
            /* el grid queda por debajo del pseudo-elemento de la línea */
            .page-inner > .catalog { position: relative; z-index: 1; }

            @media screen {
              body { background:#f6f6f6; padding: 12px; }
              .page { box-shadow: 0 0 12px rgba(0,0,0,.15); margin-bottom: 8mm; }
            }
            @media print { .sheet { width:auto; } }
            """.trimIndent() else """
            @media screen {
              body { background:#f6f6f6; padding: 12px; }
              .sheet { width:210mm; margin:0 auto; background:white; box-shadow:0 0 12px rgba(0,0,0,.15); padding: var(--page-margin-mm); }
            }
            @media print {
              .sheet { width:210mm; margin:0 auto; padding:0; box-shadow:none; }
            }
            """.trimIndent()}

            .catalog{
              display:grid;
              grid-template-columns: repeat(var(--columns), 1fr);
              gap: var(--gap);
            }

            .category{
              grid-column: 1 / -1;
              font-size: 5mm; font-weight: 600;
              margin: 4mm 0 2mm 0;
              padding-bottom: 1mm;
              border-bottom: 0.3mm solid var(--stroke);
              break-after: avoid;
            }

            .card{
              display:flex; flex-direction: column;
              border: 0.3mm solid var(--stroke);
              border-radius: var(--card-radius);
              box-shadow: var(--elev);
              overflow: hidden;
              break-inside: avoid; page-break-inside: avoid;
              background: white;
            }

            .img-wrap{
              position: relative; width: 100%; height: var(--img-h);
              background: #fafafa; overflow: hidden;
            }
            .img-wrap img{
              width: 100%; height: 100%; display: block;
              object-fit: cover; object-position: center;
              image-rendering: -webkit-optimize-contrast;
            }

            .content{ padding: var(--card-padding); }
            .name{ font-size: var(--title-fs); font-weight: 600; line-height:1.25; margin:0 0 1mm 0; }
            .price{ font-size: var(--price-fs); font-weight: 700; }
            .muted { color: var(--muted); }

            /* espaciador que ocupa el resto de página (cuando un ítem no cabe) */
            .spacer{ grid-column: 1 / -1; }
            .card, .category, .img-wrap { break-inside: avoid; page-break-inside: avoid; }
          </style>
        </head>
        <body>
          <div class="sheet">
        """.trimIndent()
    )

    if (paginate) {
        sb.append("""<div id="catalog-origin" class="catalog">""")
    } else {
        sb.append("""<div class="catalog">""")
    }

    products.forEach { (categoryKey, list) ->
        val category = list.firstOrNull()?.category ?: categoryKey ?: "Uncategorized"
        sb.append("""<h2 class="category">${htmlEsc(category)}</h2>""")

        val b64 =
            "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"
        val defImg = "data:image/jpeg;base64,$b64"

        list.forEach { p ->
            val title = buildString {
                append(htmlEsc(p.name ?: ""))
                val unit = p.unit?.trim()
                if (!unit.isNullOrEmpty()) { append(" | "); append(htmlEsc(unit)) }
            }


            val price = htmlEsc(p.price ?: "")
            val rawImg = p.imageUrl?.takeIf { it.isNotBlank() } ?: defImg
            val imgSrc = if (rawImg.startsWith("data:")) rawImg else htmlEsc(rawImg)
            val alt = htmlEsc(p.name ?: "product")

            sb.append(
                """
                <article class="card">
                  <div class="img-wrap">
                    <img src="$imgSrc" alt="$alt"/>
                  </div>
                  <div class="content">
                    <div class="name">$title</div>
                    <div class="price">$price</div>
                  </div>
                </article>
                """.trimIndent()
            )
        }
    }

    sb.append("</div>") // cierra catalog-origin o catalog

    if (paginate) {
        // Paginar con relleno (spacer) si el siguiente item no cabe
        sb.append(
            """
            <script>
            (function(){
              function mm(n){
                const d=document.createElement('div');
                d.style.height=n+'mm'; d.style.position='absolute';
                d.style.visibility='hidden'; document.body.appendChild(d);
                const h=d.getBoundingClientRect().height; d.remove(); return h;
              }
              const M = mm(10);          // margen interior
              const A4H = mm(297);       // alto A4
              const usableH = A4H - 2*M; // alto útil

              const sheet = document.querySelector('.sheet');
              const origin = document.getElementById('catalog-origin');
              if (!sheet || !origin) return;

              function newPage(){
                const page = document.createElement('div');
                page.className = 'page';
                const inner = document.createElement('div');
                inner.className = 'page-inner';
                const grid = document.createElement('div');
                grid.className = 'catalog';
                inner.appendChild(grid);
                page.appendChild(inner);
                sheet.appendChild(page);
                return { page, inner, grid, used: 0 };
              }

              function measure(node){
                const clone = node.cloneNode(true);
                clone.style.visibility = 'hidden';
                clone.style.position = 'absolute';
                clone.style.left = '-10000px';
                document.body.appendChild(clone);
                const h = clone.getBoundingClientRect().height;
                clone.remove();
                return h;
              }

              function addSpacer(grid, px){
                if (px <= 0) return;
                const s = document.createElement('div');
                s.className = 'spacer';
                s.style.height = px + 'px';
                grid.appendChild(s);
              }

              const items = Array.from(origin.children);
              let cur = newPage();

              for (let i = 0; i < items.length; i++) {
                const node = items[i];
                const h = measure(node);
                const remaining = usableH - cur.used;

                if (cur.used > 0 && h > remaining) {
                  // rellena con blanco el resto de la página actual
                  addSpacer(cur.grid, remaining);
                  // nueva página
                  cur = newPage();
                }

                cur.grid.appendChild(node);
                cur.used += h;
              }

              origin.remove();
            })();
            </script>
            """.trimIndent()
        )
    }

    sb.append(
        """
          </div>
        </body>
        </html>
        """.trimIndent()
    )

    return sb.toString()
}