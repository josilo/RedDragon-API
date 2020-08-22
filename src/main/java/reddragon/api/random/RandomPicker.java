package reddragon.api.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Helper class to pick a single value from a list of possibilities. Those are
 * given in a builder-like structure, each value having its own probability.
 */
public class RandomPicker<T> {

	private class Entry {
		public double probability;
		public Supplier<T> value;

		public Entry(final double probability, final Supplier<T> value) {
			super();
			this.probability = probability;
			this.value = value;
		}

	}

	private final List<Entry> entries = new ArrayList<>();

	public RandomPicker() {

	}

	public RandomPicker<T> withChance(final double probability, final Supplier<T> action) {
		entries.add(new Entry(probability, action));

		return this;
	}

	/**
	 * Returns a randomly selected value from all provided possibilities.
	 */
	public T pick() {
		return pick(new Random());
	}

	/**
	 * Returns a randomly selected value from all provided possibilities.
	 */
	public T pick(final Random random) {
		final double randomValue = random.nextDouble() * getProbabilitySum();

		double i = 0;

		for (final Entry entry : entries) {
			i += entry.probability;

			if (i >= randomValue) {
				return entry.value.get();
			}
		}

		return null;
	}

	private double getProbabilitySum() {
		return entries.stream().mapToDouble(e -> e.probability).sum();
	}
}
