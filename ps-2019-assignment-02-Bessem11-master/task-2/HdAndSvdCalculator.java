import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

class IncrementEvent {

	public IncrementEvent() {

	}
}

class CalculatorEvent {
	public CalculatorEvent() {

	}
}

class PrintEvent {
	public PrintEvent() {

	}
}

public class HdAndSvdCalculator {

	private EventBus eventBus;
	private int digits = 4;
	private static int Try_Number = 10;
	private String guessedcode;
	private String secretcode;
	private int hd = 10;
	private int svd;
	public final String USER_MESSAGE_WRONG_GUESS = ">> Wrong! SVD=%d, HD=%d";
	public final String USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
	public final String USER_MESSAGE_GAME_OVER = ">> Wrong - Game over!";
	public final String USER_MESSAGE_CODE = ">> The secret code was: ";
	public final String USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
	public final String USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";

	HdAndSvdCalculator(EventBus eventBus, String[] codes) {

		this.secretcode = codes[0];
		this.guessedcode = codes[1];
		this.eventBus = eventBus;
		this.eventBus.register(this);
	}

	@Subscribe
	public void increment(IncrementEvent incrementEvent) {
		/*
		 * ALESSIO: This is a bad design ! Since you create over and over instances of
		 * this class, and you need a state, you had to resort to use static !
		 */
		Try_Number--;
	}

	@Subscribe
	public void Calculator(CalculatorEvent calculatorEvent) {

		this.hd = 0;
		int sumguessedcode = ((String) this.guessedcode).chars().map(Character::getNumericValue).sum();

		// compute the sum of digits of secretcode numbers
		int sumsecretcode = ((String) this.secretcode).chars().map(Character::getNumericValue).sum();
		// compute the SVD value
		this.svd = sumsecretcode - sumguessedcode;
		// compute the HD value
		for (int i = 0; i < this.digits; i++) {
			if ((this.guessedcode).charAt(i) != (this.secretcode).charAt(i))
				this.hd++;
		}

	}

	/*
	 * ALESSIO: This is a weird encapsulation of the problem. This components, which
	 * has the responsibility to compute distances, also decides whether or not the
	 * game is over by brutally kill everything. A better solution would have been
	 * to publish a GAME_OVER or CORRECT_GUESS event to which the client,
	 * MasterMild, and possibly other components registered to.
	 */
	@Subscribe
	public void print(PrintEvent printEvent) {
		if (this.hd == 0) {
			System.out.println("\n" + this.USER_MESSAGE_CORRECT_GUESS);
			System.exit(0);
		} else if (this.hd == 10) {
			System.out.println("\n" + this.USER_MESSAGE_INVALID_INPUT);
			System.out.format(this.USER_MESSAGE_GUESS_CODE + "\n", this.digits, Try_Number);

		} else if (this.hd != 0 && Try_Number != 0) {
			System.out.format("\n" + this.USER_MESSAGE_WRONG_GUESS + "\n", this.svd, this.hd);
			System.out.format(this.USER_MESSAGE_GUESS_CODE + "\n", this.digits, Try_Number);

		}

		else {

			System.out.println("\n" + this.USER_MESSAGE_GAME_OVER);
			System.out.println(this.USER_MESSAGE_CODE + this.secretcode);
			System.exit(0);
		}
	}

}
