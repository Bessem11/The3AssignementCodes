import java.util.HashMap;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

class VerifyInputEvent {
	public VerifyInputEvent() {

	}
}

public class GuessedCode {

	private EventBus eventBus;

	private int digits = 4;
	private String guessedcode;
	private String secretcode;

	public GuessedCode(EventBus eventBus, String[] codes) {

		this.secretcode = codes[1];
		this.guessedcode = codes[0];
		this.eventBus = eventBus;
		this.eventBus.register(this);

	}

	@Subscribe
	private void verifyinput(VerifyInputEvent verifyInputEvent) {

		this.guessedcode = this.guessedcode.trim(); // removing spaces from the begining and the end of the secret code
		String[] codes = { this.secretcode, this.guessedcode };
		/*
		 * ALESSIO: This is another violation of the style !
		 */
		EventBus CalculatorBus = new EventBus();
		HdAndSvdCalculator hdandsvdcalculator = new HdAndSvdCalculator(CalculatorBus, codes);
		boolean isnumber = true;

		for (int i = 0; i < this.guessedcode.length() && isnumber; i++) {
			if (!Character.isDigit(this.guessedcode.charAt(i)))
				isnumber = false;
		}

		if (this.guessedcode.length() == this.digits && isnumber) {
			CalculatorBus.post(new CalculatorEvent()); // here we will call the Calculator method
			CalculatorBus.post(new IncrementEvent());
		}

		CalculatorBus.post(new PrintEvent());

	}

}
