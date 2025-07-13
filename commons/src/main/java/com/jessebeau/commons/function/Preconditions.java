package com.jessebeau.commons.function;

import org.jetbrains.annotations.Contract;

public final class Preconditions {
	private Preconditions() { }

	/**
	 * Ensures that the given number is strictly between min and max.
	 * @param number the value to check
	 * @param min the lower bound
	 * @param max the upper bound
	 * @throws NumberFormatException if number is not in range (min, max)
	 */
	@Contract(pure = true)
	public static int requireExclusiveRange(int number, int min, int max) {
		if (number <= min || number >= max) {
			throw new NumberFormatException(
				String.format("Number cannot exceed range (%d, %d), but was %d", min, max, number)
			);
		}
		return  number;
	}
}
