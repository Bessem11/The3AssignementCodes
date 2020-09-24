
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MasterMild {

	public static void main(String[] args) throws IOException {
		MasterMildCOntroller master = new MasterMildCOntroller();

		String[] messageinit = { "init" };
		String[] messagerun = { "run" };
		master.dispatch(messageinit);
		master.dispatch(messagerun);
	}

}

class SecretCode {
	private String secretcode;

	public String dispatch(String[] messages) throws IOException {
		if (messages[0] == "init") {
			init(messages[1]);
		} else if (messages[0] == "rectify") {
			rectify(messages[1]);
		} else if (messages[0] == "getsecretcode") {
			return this.secretcode;
		}

		return "";
	}

	private void init(String secretcodelocation) throws IOException {

		InputStream flux = new FileInputStream(secretcodelocation);
		InputStreamReader lecture = new InputStreamReader(flux);
		BufferedReader buff = new BufferedReader(lecture);
		String ligne;
		ligne = buff.readLine();
		this.secretcode = ligne;
		buff.close();

	}

	private void rectify(String numberofdig) {

		// Add zeros
		int digits = Integer.parseInt(numberofdig);

		if (this.secretcode.length() < digits) {
			int lengthdiff = digits - this.secretcode.length();

			for (int i = 0; i < lengthdiff; i++) {
				this.secretcode = '0' + this.secretcode;
			}
		}
		// pick less digits of the secret_code number because the secret_code length is
		// higher then Digits
		else if ((this.secretcode.length() > digits))

		{
			int lengthdiff = this.secretcode.length() - digits;
			this.secretcode = this.secretcode.substring(lengthdiff, this.secretcode.length());
		}

	}

}

class GuessedCode {

	private String guessedcode;
	public final String USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";

	public String dispatch(String[] messages) {
		if (messages[0] == "init") {
			init(messages[1]);
		} else if (messages[0] == "isvalid") {
			isvalid(messages[1]);
		} else if (messages[0] == "getguessedcode") {
			return getguessedcode();
		}

		return "";

	}

	private void init(String input) {
		this.guessedcode = input;
	}

	private void isvalid(String numberofdig) {

		int digits = Integer.parseInt(numberofdig);
		this.guessedcode = this.guessedcode.trim();
		boolean isnumber = true;

		// verify if the guessed_code is a number
		for (int i = 0; i < this.guessedcode.length() && isnumber; i++) {
			if (!Character.isDigit(this.guessedcode.charAt(i)))
				isnumber = false;
		}

		// verify if the guessed_code is valid
		if (this.guessedcode.length() != digits || !isnumber) {
			System.out.println("\n" + USER_MESSAGE_INVALID_INPUT);
			this.guessedcode = "0";
		}
	}

	private String getguessedcode() {
		return this.guessedcode;
	}

}

class ComputeValue {
	int SVD;
	int HD;

	public int dispatch(String[] messages) {
		if (messages[0] == "compute") {
			compute(messages[1], messages[2], messages[3]);
		} else if (messages[0] == "getsvd") {
			return getsvd();
		} else if (messages[0] == "gethd") {
			return gethd();
		}
		return 0;
	}

	private int getsvd() {
		return SVD;
	}

	private int gethd() {
		return HD;
	}

	private void compute(String secretcode, String guessedcode, String numberofdig) {

		int digits = Integer.parseInt(numberofdig);
		// compute the sum of digits of guessedcode numbers
		int sumguessedcode = ((String) guessedcode).chars().map(Character::getNumericValue).sum();

		// compute the sum of digits of secretcode numbers
		int sumsecretcode = ((String) secretcode).chars().map(Character::getNumericValue).sum();
		// compute the SVD value
		int svd = sumsecretcode - sumguessedcode;
		int count = 0;
		// compute the HD value
		for (int i = 0; i < digits; i++) {
			if ((guessedcode).charAt(i) != (secretcode).charAt(i))
				count++;
		}
		this.HD = count;
		this.SVD = svd;

	}
}

class MasterMildCOntroller {

	public final String SECRET_CODE_LOCATION = "secret.code";
	public final String USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
	public final String USER_MESSAGE_GAME_OVER = ">> Wrong - Game over!";
	public final String USER_MESSAGE_WRONG_GUESS = ">> Wrong! SVD=%d, HD=%d";
	public final String USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";
	public final String USER_MESSAGE_CODE = ">> The secret code was: ";
	public final int MAX_RETRY = 10;
	public final int DIGITS = 4;

	public SecretCode secret;
	private GuessedCode guess;
	private ComputeValue computeval;

	public void dispatch(String[] messages) throws IOException {
		if (messages[0] == "init") {
			init();
		} else if (messages[0] == "run") {
			run();
		}

	}

	private void init() throws IOException {
		this.secret = new SecretCode();
		this.guess = new GuessedCode();
		this.computeval = new ComputeValue();

		String[] messages0 = { "init", this.SECRET_CODE_LOCATION };
		secret.dispatch(messages0);
		String[] messages1 = { "rectify", Integer.toString(this.DIGITS) };
		secret.dispatch(messages1);
	}

	private void run() throws IOException {

		int i = 0;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String guessedcode;
		while (i < MAX_RETRY) {

			System.out.format(USER_MESSAGE_GUESS_CODE + "\n", this.DIGITS, MAX_RETRY - i);
			guessedcode = in.readLine(); // the player enter the guessed_code
			System.out.print(guessedcode + "\n");

			/*
			 * ALESSIO: this is not necessary a violation of the style, but this guess thing
			 * is implemented in a very funny way: you call init multiple times to store the
			 * code of the user, and later you call isValid passing the digits which is
			 * always the same number. A more sensible choice would have been to call init
			 * once with the digits and then call isvalid passing the guesscode every time.
			 */
			String[] message0 = { "init", guessedcode };
			this.guess.dispatch(message0);
			String[] message1 = { "isvalid", Integer.toString(this.DIGITS) };
			this.guess.dispatch(message1);// verifying if the input is valid

			String[] message2 = { "getguessedcode" };

			// the while loop verify if the player is writing a valid input or not
			// every time the player write an ivalide inpute the guessed_code is reset to 0
			// so the condition of the while loop is true
			while (guess.dispatch(message2) == "0") {
				System.out.format(USER_MESSAGE_GUESS_CODE + "\n", this.DIGITS, MAX_RETRY - i);
				guessedcode = in.readLine();
				System.out.print(guessedcode + "\n");
				/*
				 * ALESSIO: Here you are using the same message "template" but it is really hard
				 * to track since you did not give human readable names to the objects
				 */
				message0[1] = guessedcode;
				this.guess.dispatch(message0);

				this.guess.dispatch(message1);
			}

			// compute the SVD, HD values
			String[] message3 = { "getsecretcode" };

			/*
			 * ALESSIO: This is also a weird organization of the code: you first ask the
			 * thing to compute all the distances and then retrieve one by one. Instead of
			 * computing one by one, or retrieving both at once..
			 */
			String[] message4 = { "compute", this.secret.dispatch(message3), this.guess.dispatch(message2),
					Integer.toString(this.DIGITS) };
			computeval.dispatch(message4);

			String[] message5 = { "gethd" };
			String[] message6 = { "getsvd" };

			// if HD Value is equal to Zero then the player Win
			if (this.computeval.dispatch(message5) == 0)
				break;
			// if HD value !=0 then display the wrong message to player ( we don't display
			// this message at the last attempt)
			else if (i != MAX_RETRY - 1)
				System.out.format("\n" + this.USER_MESSAGE_WRONG_GUESS + "\n", this.computeval.dispatch(message6),
						this.computeval.dispatch(message5));

			i++;
		}

		String[] message3 = { "getsecretcode" };

		// Print the final result (if the player exceeds the MAX_RETRY then it's a game
		// over else the player win)
		if (i == MAX_RETRY) {
			System.out.println("\n" + this.USER_MESSAGE_GAME_OVER);
			System.out.println(this.USER_MESSAGE_CODE + this.secret.dispatch(message3));
		} else
			System.out.println("\n" + this.USER_MESSAGE_CORRECT_GUESS);

	}
}
