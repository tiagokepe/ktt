package ufpr.inf.kepe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Roulette {
	@SuppressWarnings("hiding")
	public class Range<ELEMENT> {
		Object begin, end;
		ELEMENT element;
		public Range(Object begin, Object end, ELEMENT element) {
			this.begin = begin;
			this.end = end;
			this.element = element;
		}
		
		public Object getBegin() {
			return begin;
		}
		public Object getEnd() {
			return end;
		}
		public ELEMENT getElement() {
			return element;
		}
	}
	
	private int size;
	private List<Range<Individual>> ranges = new ArrayList<Range<Individual>>();

	public Roulette(int size) {
		this.size = size;
	}
	
	public List<Individual> spin() {
		List<Individual> result = new ArrayList<Individual>();
		Random rand;
		double newSorted;
		for(int i=0; i<size; i++) {
			rand = new Random();
			newSorted = rand.nextDouble();
			for(Range<Individual> range: ranges) {
				if(newSorted >= (Double)range.getBegin() && newSorted <= (Double)range.getEnd()) {
					result.add(range.getElement().clone());
					break;
				}
			}
		}
		return result;
	}
	
	public List<Range<Individual>> getRanges() {
		return ranges;
	}
	
	public void insertRange(int rangePos, double probability, Individual element) {
		if(rangePos == 0)
			ranges.add(rangePos, new Range<Individual>(0.0d, probability, element));
		else {
			double end = probability;
			for(int i=0; i < rangePos; i++)
				end += (Double)ranges.get(i).getEnd();
			
			Double begin = (Double)ranges.get(rangePos-1).getEnd();
			long l = Double.doubleToLongBits(begin);
			l += 1; //Add one bit to init the range correctly 
			begin = Double.longBitsToDouble(l);
			ranges.add(rangePos, new Range<Individual>(begin, end, element));
		}
	}
}
