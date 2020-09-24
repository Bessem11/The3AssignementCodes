
import com.google.common.eventbus.EventBus;

public class DefaultClient implements IMasterMildClient {

	private EventBus eventbus;

	public DefaultClient(EventBus eventbus) {
		this.eventbus = eventbus;
	}

	public void play() {

		do {
			String guessedcode = "0000";
			this.eventbus.post(guessedcode);
		} while (true);

	}

}
