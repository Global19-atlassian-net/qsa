package structure;

import java.io.Serializable;
import org.rcsb.mmtf.dataholders.MmtfStructure;

public class ResidueId implements /*Comparable<ResidueId>,*/ Serializable {

	private static final long serialVersionUID = 1L;
	private static final char EMPTY = ' ';
	private static final char UNAVAILABLE_CHAR_VALUE
		= MmtfStructure.UNAVAILABLE_CHAR_VALUE;
	private ChainId chain_;
	private int number_;
	private String name_;
	private char insertion_;

	/**
	 * Serial number is a string (maybe hexa number) in Gromacs outputs. This is
	 * againstPDB file format guide, but we need to read PDB files produced by
	 * Gromacs.
	 *
	 * @param chain chain ID
	 * @param number serial number
	 * @param insertionCode insertion code
	 */
	public ResidueId(ChainId chain, int number, char insertionCode, String name) {
		this.chain_ = chain;
		this.number_ = number;
		this.insertion_ = insertionCode;
		this.name_ = name;
	}

	/*public ResidueId(ChainId chain, int number) {
        chain_ = chain;
        number_ = number;
        insertion_ = EMPTY;
    }*/
	public ChainId getChain() {
		return chain_;
	}

	public void setChain(ChainId c) {
		chain_ = c;
	}

	public int getSequenceNumber() {
		return number_;
	}

	public String getName() {
		return name_;
	}

	/*
	 * See PDB file format guide, ATOM
	 * http://www.bmsc.washington.edu/CrystaLinks/man/pdb/part_62.html
	 */
	public Character getInsertionCode() {
		return insertion_;
	}

	@Override
	public String toString() {
		return chain_ + ":" + number_ + "" + (insertion_ == EMPTY ? "" : insertion_);
	}

	public String getPdbString() {
		return number_ + "" + (insertion_ == EMPTY ? "" : insertion_);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ResidueId)) {
			return false;
		}
		ResidueId other = (ResidueId) o;
		return chain_.equals(other.chain_) && number_ == other.number_ && insertion_ == other.insertion_;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + this.chain_.hashCode();
		hash = 37 * hash + this.number_;
		hash = 37 * hash + Character.hashCode(this.insertion_);
		return hash;
	}
	/*
    @Override
    public int compareTo(ResidueId other) {
        int c = chain_.compareTo(other.chain_);
        if (0 == c) {
            c = Integer.compare(number_, other.number_);
            if (0 == c) {
                c = Character.compare(insertion_, other.insertion_);
            }
        }
        return c;
    }
	 */
}
