package rcr.lge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Clase para manejar GameObjects animados
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 */
public class Sprite extends GameObject {
	HashMap<String, BufferedImage[]> surfaces;
	String iname;
	int idx;
	double elapsed = 0;

	/**
	 * Crea un GameObject animado con las secuencias de imagenes cargadas con
	 * LittleGameEngine.LoadImage()
	 *
	 * @param iname    nombre de la secuencia de imagenes a utilizar
	 * @param position posicion inicial (x, y) del GameObject
	 */
	public Sprite(String iname, Point position) {
		this(new String[] { iname }, position, null);
	}

	/**
	 * Crea un GameObject animado con las secuencias de imagenes cargadas con
	 * LittleGameEngine.LoadImage()
	 *
	 * @param iname    nombre de la secuencia de imagenes a utilizar
	 * @param position posicion inicial (x, y) del GameObject
	 * @param name     nombre a asignar a este GameObject
	 */
	public Sprite(String iname, Point position, String name) {
		this(new String[] { iname }, position, name);
	}

	/**
	 * Crea un GameObject animado con las secuencias de imagenes cargadas con
	 * LittleGameEngine.LoadImage()
	 *
	 * @param inames   los nombres de las secuencias de imagenes a utilizar
	 * @param position posicion inicial (x, y) del GameObject
	 */
	public Sprite(String[] inames, Point position) {
		this(inames, position, null);
	}

	/**
	 * Crea un GameObject animado con las secuencias de imagenes cargadas con
	 * LittleGameEngine.LoadImage()
	 *
	 * @param inames   nombres de las secuencias de imagenes a utilizar
	 * @param position posicion inicial (x, y) del GameObject
	 * @param name     nombre a asignar a este GameObject
	 */
	public Sprite(String[] inames, Point position, String name) {
		super(position, new Dimension(0, 0), name);
		surfaces = new HashMap<String, BufferedImage[]>();

		for (String iname : inames)
			surfaces.put(iname, LittleGameEngine.getInstance().getImages(iname));

		Entry<String, BufferedImage[]> elem = surfaces.entrySet().iterator().next();
		this.iname = elem.getKey();
		this.idx = 0;
		this.surface = elem.getValue()[0];
		this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
	}

	/**
	 * Retorna el nombre de la secuencia actual de imagenes que utiliza este Sprite
	 *
	 * @return el nombre de la secuencia
	 */
	public String getCurrentIName() {
		return iname;
	}

	/**
	 * Retorna el indice de la secuencia actual de imagenes que utiliza este Sprite
	 *
	 * @return el numero de la imagen dentro de la secuencia actual
	 */
	public int getCurrentIdx() {
		return idx;
	}

	/**
	 * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
	 */
	public void nextShape() {
		nextShape(0, 0);
	}

	/**
	 * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
	 *
	 * @param dt tiempo transcurrido desde la ultima invocacion a este metodo
	 */
	public void nextShape(double dt) {
		nextShape(dt, 0);
	}

	/**
	 * Avanza automaticamente a la siguiente imagen de la secuencia de este Sprite
	 *
	 * @param dt    tiempo transcurrido desde la ultima invocacion a este metodo
	 * @param delay tiempo que debe transcurrir antes de pasar a la siguiente imagen
	 *              de la secuencia
	 */
	public void nextShape(double dt, double delay) {
		elapsed = elapsed + dt;
		if (elapsed < delay)
			return;

		elapsed = 0;
		idx = idx + 1;
		if (idx >= surfaces.get(iname).length)
			idx = 0;

		surface = surfaces.get(iname)[idx];
		this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
	}

	/**
	 * Establece la secuencia de imagenes a utilizar en este Sprite
	 *
	 * @param iname el nombre de la secuencia (cargada con LoadImage y especificada
	 *              al crear este Sprite)
	 */
	public void setShape(String iname) {
		setShape(iname, 0);
	}

	/**
	 * Establece la secuencia de imagenes a utilizar en este Sprite
	 *
	 * @param iname el nombre de la secuencia (cargada con LoadImage y especificada
	 *              al crear este Sprite)
	 * @param idx   el numero de la secuencia a utilizar
	 */
	public void setShape(String iname, int idx) {
		this.iname = iname;
		if (idx >= surfaces.get(iname).length)
			idx = 0;
		this.idx = idx;
		surface = surfaces.get(iname)[idx];
		this.rect.setSize(this.surface.getWidth(), this.surface.getHeight());
	}
}
