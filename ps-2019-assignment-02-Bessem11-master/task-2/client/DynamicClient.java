package client;

import com.google.common.eventbus.EventBus;

public class DynamicClient implements IMasterMildClient {

	private EventBus eventbus;
	private int digits = 4;

	public DynamicClient(EventBus eventbus) {
		this.eventbus = eventbus;
	}

	public void play() {

		do {
			int random_int;
			String guessedcode = "";

			for (int i = 0; i < this.digits; i++) {
				random_int = (int) (Math.random() * 9 + 0);// here I generate a number between 0 and 9
				guessedcode = guessedcode + Integer.toString(random_int); // the guessed code is the concatenation of
																			// the 4 random_int
			}

			this.eventbus.post(guessedcode);
		} while (true);

	}

}