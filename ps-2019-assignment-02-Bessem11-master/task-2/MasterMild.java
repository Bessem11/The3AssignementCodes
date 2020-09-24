import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

import com.google.common.eventbus.EventBus;

public class MasterMild {

	private static int Max_Retry = 10;
	private static String USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";

	/**
	 * The first argument passed to this program must be a valid FILE pointing to
	 * the .jar file containing the implementation of the client playing MasterMild
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {
		IMasterMildClient client = null;

		EventBus eventBus = new EventBus();

		SecretCode secretcode = new SecretCode(eventBus);
		
		
		
		eventBus.post(new LoadEvent()); // This post will call the Load method
		eventBus.post(new RectifyEvent()); // This post will call the Rectify method

		System.out.format(USER_MESSAGE_GUESS_CODE + "\n", 4, Max_Retry);

		if (args.length == 0) {

			client = new DefaultClient(eventBus);

		} else {

			// Load the Jar file
			File plugin = new File(args[0]);
			URL[] classLoaderUrls = new URL[] { plugin.toURI().toURL() };
			URLClassLoader urlClassLoader = new URLClassLoader(classLoaderUrls);
			/*
			 * ALESSIO: Despite this is not a violation of the style, hard-coding the name
			 * of the class is not robust. Maybe a configuration file would have been
			 * better?
			 */
			Class<?> beanClass = urlClassLoader.loadClass("DynamicClient");
			Constructor<?> constructor = beanClass.getConstructor(EventBus.class);
			Object dynamicClient = constructor.newInstance(eventBus);
			client = (IMasterMildClient) dynamicClient;
		}

		// Start the client
		/*
		 * Here is a violation of the bulletin board style but since we are combining
		 * two different styles this may be allowed in this context
		 */
		client.play();
		// ALESSIO: OK !

	}
}
