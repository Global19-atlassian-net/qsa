package fragment.word;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Words implements Iterable<Word> {

	private List<Word> words = new ArrayList<>();

	public void add(Word w) {
		words.add(w);
	}

	@Override
	public Iterator<Word> iterator() {
		return words.iterator();
	}

	public List<Word> get() {
		return words;
	}

	public Word[] toArray() {
		Word[] a = new Word[words.size()];
		words.toArray(a);
		return a;
	}

	public int size() {
		return words.size();
	}
}
