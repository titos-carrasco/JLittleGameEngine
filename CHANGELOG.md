# Changelog

## 2022-05-15
- Se modifican los demos para no uilizar onCollision() ya que ralentiza demasiado el mainloop

## v0.5.6 2022-05-10
- GameObject.py
    - Agrega método getLayer()
- Sprite.py
    - Corrige método SetImage
- LittleGameEngine.py
    - Agrega método findGObjectsByTag()
- Agrega demo "Cementerio"

## v0.5.5.1 2022-04-05
- Corrige error al cargar imagenes con flipping

## v0.5.5 2022-04-05
- GameObject.py
    - Permite múltiples rectángulos como colisionador
    - Renombra useColliders() a enableCollider()
    - Agrega setCollider() para establecer varios rectángulos como parte del colisionador
    - Agrega getCollider() para retornar los rectángulos ajustados a las coordenadas
    - Agrega collidesWith() para determinar su colisiona con un GameObject dado
- Sprite.py
    - Ajusta uso de colisiones
- LittleGameEngine.py
    - Se modifica para manejar múltiples rectángulos en las colisiones
- Se ajustan los demos acorde a los cambios

## v0.5.4 2022-04-25
- Sprite.py:
  - Modifica constructor para recibir una única referencia a una secuencia de imágenes
  - Cambia de nombre método getCurrentIName() a getImagesName()
  - Cambia de nombre método getCurrentIdx() a getImagesIndex()
  - Cambia de nombre método nextShape() a nextImage()
  - Cambia de nombre y modifica método setShape() por setImage()
- Se ajustan los demos acorde a los cambios

## v0.5.3 2022-04-25
- Cambia coordenadas a la clásica 2D coincidiendo así con las coordenadas de pantalla
- Modifica los demos acorde a lo anterior

## v0.5.2.1 2022-04-21
- Agrega documentación e imágenes

## v0.5.2 2022-04-20
- Agrega CHANGELOG
- Agrega documentación
- LittleGameEngine.java: modifica repaint()
