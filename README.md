# API-Score  

Este módulo se encarga de analizar la cobertura mediática de eventos deportivos y calcular un "score" que determinará descuentos en la tienda.  

## Objetivo  

La API utilizará fuentes de noticias en tiempo real para medir la repercusión de un evento (como un gol o una expulsión) y calcular un índice de impacto.  

### ¿Cómo funciona?  

- **Recopilar noticias** en tiempo real sobre un jugador o equipo después de un evento clave.  
- **Analizar la frecuencia** con la que se menciona un jugador o equipo en titulares y artículos.  
- **Calcular un score** basado en el número de menciones y relevancia de las fuentes.  
- **Determinar el porcentaje de descuento** en camisetas según la notoriedad del evento.  

## Uso en el Proyecto  

Si un jugador marca un gol decisivo, se consultará NewsAPI para analizar cuántos medios lo reportan y con qué intensidad. Cuantas más menciones tenga, mayor será el descuento aplicado a su camiseta en la tienda online.  

---
