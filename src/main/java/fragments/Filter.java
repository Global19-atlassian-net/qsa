package fragments;

/**
 *
 * @author Antonin Pavelka
 */
public interface Filter<T> {

	public boolean include(T t);

}
