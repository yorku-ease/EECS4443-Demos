package ca.yorku.eecs.mack.demoquotation;

import java.util.Random;

/*
 * A class to hold the information for a quiz question. The quotation for the question (quoteIdx) is
 * selected at random from the quotation array. Of course, we only need to store the index of
 * the selected quotation. We also need an answer array for the possible answers (answerArray).
 * The entries in this array are also randomly selected indices from the quotation array. One
 * tricky detail is to ensure that the answer array does not have any repeated indices. Of
 * course, one entry in the answer array must be the index of the correct answer, and we need a
 * place-holder for that (answerArrayCorrectIdx).
 */
public class Question
{
	int quoteIdx; // index in quotation array of the quote for the question.
	int[] answerArray; // the array of possible answers (also indices)
	int answerArrayCorrectIdx; // the index in the answer array of the correct answer
	Random r;
	int range, numberOfAnswers;

	Question(int quoteIdxArg, int rangeArg, int numberOfAnswersArg)
	{
		quoteIdx = quoteIdxArg;
		range = rangeArg;
		numberOfAnswers = numberOfAnswersArg;

		r = new Random();

		// create an array of answers (indices into the quotation array)
		answerArray = new int[numberOfAnswers];

		// fill the answer array with unique and wrong random indices
		for (int n : answerArray)
		{
			do
			{
				fillRandom();
			} while (repeats()); 
		}

		// replace one of the entries with the idx of the correct answer
		answerArrayCorrectIdx = r.nextInt(numberOfAnswers);
		answerArray[answerArrayCorrectIdx] = quoteIdx;
	}

	// fill the answer array with indices drawn at random for the array of quotations.
	private void fillRandom()
	{
		for (int i = 0; i < answerArray.length; ++i)
		{
			do
			{
				answerArray[i] = r.nextInt(range);
			} while (answerArray[i] == quoteIdx); // exclude the index of the answer
		}
	}

	// returns true if there are any repeated values in the answer array
	private boolean repeats()
	{
		for (int i = 0; i < answerArray.length; ++i)
			for (int j = i + 1; j < answerArray.length; ++j)
				if (answerArray[i] == answerArray[j])
					return true;
		return false;
	}

}