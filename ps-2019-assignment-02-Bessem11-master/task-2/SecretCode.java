import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

class LoadEvent {
	public LoadEvent() {

	}
}

class RectifyEvent {
	public RectifyEvent() {

	}
}

public class SecretCode {

	private String secretcode;
	private EventBus eventBus;
	private String secretcodelocation = "secret.code";
	private int digits = 4;
	private int Max_Retry = 10;
	private final String USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";

	public SecretCode(EventBus eventBus) {
		this.eventBus = eventBus;
		this.eventBus.register(this);

	}

	@Subscribe
	public void load(LoadEvent loadEvent) throws IOException {

		InputStream flux = new FileInputStream(this.secretcodelocation);
		InputStreamReader lecture = new InputStreamReader(flux);
		BufferedReader buff = new BufferedReader(lecture);
		String ligne;
		ligne = buff.readLine();
		this.secretcode = ligne;
		buff.close();

	}

	/*
	 * ALESSIO: Why do you need a rectify event? Could you simply rectify the code
	 * after loading it?
	 */
	@Subscribe
	public void rectify(RectifyEvent rectifyEvent) throws IOException {

		if (this.secretcode.length() < this.digits) {
			int lengthdiff = this.digits - this.secretcode.length();

			for (int i = 0; i < lengthdiff; i++) {
				this.secretcode = '0' + this.secretcode;
			}
		}
		// pick less digits of the secret_code number because the secret_code length is
		// higher then Digits
		else if ((this.secretcode.length() > this.digits))

		{
			int lengthdiff = this.secretcode.length() - this.digits;
			this.secretcode = this.secretcode.substring(lengthdiff, this.secretcode.length());
		}

	}

	@Subscribe
	public void produce_work(String guessedcode) throws IOException {

		// String guessedcode;
		// BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		// System.out.format(this.USER_MESSAGE_GUESS_CODE+ "\n",4,this.Max_Retry);

		// do
		// {
		// guessedcode=in.readLine();
		// System.out.print(guessedcode+"\n");
		System.out.print(guessedcode + "\n");
		String[] codes = { guessedcode, this.secretcode };

		/*
		 * ALESSIO: Why are you using TWO event bus. This seems a bit overkilling, and
		 * is a violation of the bulletin board style ! Additionally, you create one of
		 * such a bus at every invocation while this should only be a matter of posting
		 * events ?!
		 */
		EventBus GuessedCodeEventBus = new EventBus();
		// ALESSIO: Why this ?! You do not even use this instance outside this method ?!
		GuessedCode GuessInstance = new GuessedCode(GuessedCodeEventBus, codes);
		GuessedCodeEventBus.post(new VerifyInputEvent()); // here we will call the VerifyInput method

		// }while(true);

	}

}
