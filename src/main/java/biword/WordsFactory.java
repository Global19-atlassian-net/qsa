package biword;

import global.Parameters;
import structure.Residue;
import structure.SimpleChain;
import structure.SimpleStructure;
import util.Counter;

public class WordsFactory {

	private final Parameters parameters;
	private final Counter id = new Counter();
	private int sparsity = 1;
	private final SimpleStructure ss;

	public WordsFactory(Parameters parameters, SimpleStructure ss) {
		this.parameters = parameters;
		this.ss = ss;
	}

	public void setSparsity(int sparsity) {
		this.sparsity = sparsity;
	}

	public Words create() {
		Words words = new Words();
		for (SimpleChain c : ss.getChains()) {
			addWords(c, parameters.getScorePars().wordLength, words);
		}
		return words;
	}

	private void addWords(SimpleChain c, int wordLength, Words words) {
		double seqLim = parameters.getSequenceNeighborLimit();
		for (int i = 0; i < c.size() - wordLength; i++) {
			if (i % sparsity == 0) {
				Residue[] residues = new Residue[wordLength];
				System.arraycopy(c.getResidues(), i, residues, 0, wordLength);
				boolean unbroken = true;
				for (int k = 0; k < residues.length - 1; k++) {
					Residue a = residues[k];
					Residue b = residues[k + 1];
					double distance = a.getPosition().distance(b.getPosition());
					if (distance > seqLim || distance < 3) {
						unbroken = false;
						break;
					}
				}
				if (unbroken) {
					Word w = new Word(id.value(), residues);
					words.add(w);
					id.inc();
				}
			}
		}
	}
}
