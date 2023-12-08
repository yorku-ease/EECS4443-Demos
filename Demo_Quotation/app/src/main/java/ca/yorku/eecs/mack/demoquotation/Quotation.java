package ca.yorku.eecs.mack.demoquotation;

/* A simple class to hold the information for a quotation.  The information needed is 
 * 
 *    - the name of the person
 *    - the life dates
 *    - the quotation
 *    - a brief biography
 *    - the resource Id for an image of the person
 * 
 */
public class Quotation
{
	String famousPerson, dates, quote, biography;
	int imageId;

	Quotation(String famousPersonArg, String datesArg, String quoteArg, String bioArg, int imageIdArg)
	{
		famousPerson = famousPersonArg;
		dates = datesArg;
		quote = quoteArg;
		biography = bioArg;
		imageId = imageIdArg;
	}
}