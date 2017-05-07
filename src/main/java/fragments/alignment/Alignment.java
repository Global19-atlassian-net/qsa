package fragments.alignment;

import fragments.AwpNode;
import java.util.Collection;

/**
 *
 * @author Antonin Pavelka
 */
public interface Alignment {

	public Collection<AwpNode> getNodes();

	public double getScore();
}