package structure;

import java.io.Serializable;

/**
 *
 * @author Antonin Pavelka
 */
public class ChainId implements Comparable<ChainId>, Serializable {

	private static final long serialVersionUID = 1L;
	String c_;
	String name_;

	public ChainId(char c) {
		c_ = Character.toString(c).toUpperCase();
	}

	public ChainId(String c) {
		c_ = c.toUpperCase();
	}

	public static ChainId createEmpty() {
		return new ChainId("_");
	}

	public ChainId(String c, String name) {
		c_ = c.toUpperCase();
		name_ = name.toUpperCase();
	}

	public String getId() {
		return c_;
	}

	public String getName() {
		return name_.toUpperCase();
	}

	@Override
	public boolean equals(Object o) {
		ChainId other = (ChainId) o;
		return c_.equals(other.c_);
	}
	
	@Override
	public int hashCode() {
		return c_.hashCode();
	}

	@Override
	public int compareTo(ChainId other) {
		return c_.compareTo(other.c_);
	}

	public String toString() {
		return c_;
	}
}
