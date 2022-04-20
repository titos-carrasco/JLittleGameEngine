package rcr.lge;

/**
 * Interfaz para implementar el evento onMainUpdate y que se ejecuta justo
 * despues de invocar al evento onUpdate() de los GameObjects
 *
 * @author Roberto carrasco (titos.carrasco@gmail.com)
 *
 */
public interface IEvents {
    public void onMainUpdate(double dt);
}
