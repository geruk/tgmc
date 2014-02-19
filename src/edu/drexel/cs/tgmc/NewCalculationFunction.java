package edu.drexel.cs.tgmc;

import org.encog.neural.error.ErrorFunction;

public class NewCalculationFunction implements ErrorFunction {
	@Override
	public void calculateError(final double[] ideal, final double[] actual, final double[] error)
	{
		for(int i = 0; i < actual.length; i++)
		{
			if(actual[i] == 1)
				actual[i] = 0.99;
			error[i] = ideal[i] * Math.log(actual[i])
					+ (1 - ideal[i]) * Math.log(1-actual[i]);
		}
	}
}
