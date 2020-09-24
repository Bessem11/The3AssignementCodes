import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Vector;

public class MasterMild {

    public String initSecretCode(String filePath) throws IOException {

        int DIGITS = 4;
        String secret_code_value = new BufferedReader(new FileReader(filePath)).readLine();

        if (secret_code_value.length() > DIGITS) {
            secret_code_value = secret_code_value.substring(secret_code_value.length() - DIGITS,
                    secret_code_value.length());
        } else if (secret_code_value.length() < DIGITS) {
            secret_code_value = new String(new char[DIGITS - secret_code_value.length()]).replace("\0", "0")
                    + secret_code_value;
        }
        return secret_code_value;
    }

    public Entry<String, Vector<String>> readInputs(String secret_code) throws IOException {

        Vector<String> guessedCodes = new Vector<String>();
        String line;
        BufferedReader console_reader = new BufferedReader(new InputStreamReader(System.in));

        while ((line = console_reader.readLine()) != null) {
            guessedCodes.add(line);
        }
        return new SimpleEntry<String, Vector<String>>(secret_code, guessedCodes);
    }

    public Entry<String, Vector<Entry<String, Boolean>>> checkValidity(Entry<String, Vector<String>> inputs) {

        int DIGITS = 4;
        Vector<Entry<String, Boolean>> guessedCodesWithValidity = new Vector<Entry<String, Boolean>>();
        String secretCode = inputs.getKey();

        for (String guessedCode : inputs.getValue()) {

            Boolean isValide = true;
            if (guessedCode == null || guessedCode.trim().length() != DIGITS)
                isValide = false;
            if (!guessedCode.trim().chars().allMatch(Character::isDigit))
                isValide = false;

            guessedCodesWithValidity.add(new SimpleEntry<String, Boolean>(guessedCode, isValide));
        }

        return new SimpleEntry<String, Vector<Entry<String, Boolean>>>(secretCode, guessedCodesWithValidity);
    }

    public Entry<String, Vector<Vector<String>>> computation(Entry<String, Vector<Entry<String, Boolean>>> inputs) {

        Vector<Vector<String>> guessedCodesWithSVDAndHD = new Vector<Vector<String>>();
        String secretCode = inputs.getKey();
        for (Entry<String, Boolean> guessedCodeWithValidity : inputs.getValue()) {

            if (guessedCodeWithValidity.getValue() == true) // valide
            {
                // Compute SVD
                int SVD;
                int sum_secret_code = 0;
                for (char c : secretCode.toCharArray()) {
                    sum_secret_code = sum_secret_code + Character.getNumericValue(c);
                }
                int sum_guessed_code = 0;
                for (char c : guessedCodeWithValidity.getKey().trim().toCharArray()) {
                    sum_guessed_code = sum_guessed_code + Character.getNumericValue(c);
                }
                SVD = sum_secret_code - sum_guessed_code;

                // Compute HD
                int HD = 0, i = 0;
                char[] array_guessed_code = guessedCodeWithValidity.getKey().trim().toCharArray();

                for (char c : secretCode.toCharArray()) {
                    if (c != array_guessed_code[i])
                        HD++;
                    i++;
                }
                Vector<String> guessedCodeWithSVDAndHDVector = new Vector<String>();
                guessedCodeWithSVDAndHDVector.add(guessedCodeWithValidity.getKey());
                guessedCodeWithSVDAndHDVector.add(SVD + "");
                guessedCodeWithSVDAndHDVector.add(HD + "");
                guessedCodesWithSVDAndHD.add(guessedCodeWithSVDAndHDVector);
            } else {
                Vector<String> guessedCodeWithoutSVDAndHDVector = new Vector<String>();
                guessedCodeWithoutSVDAndHDVector.add(guessedCodeWithValidity.getKey());
                guessedCodesWithSVDAndHD.add(guessedCodeWithoutSVDAndHDVector);
            }
        }
        return new SimpleEntry<String, Vector<Vector<String>>>(secretCode, guessedCodesWithSVDAndHD);

        /*
         * the output will be like this: <secretCode , [guessedCode1,SVD1,HD1],
         * [guessedCode2]> the SVD and HD are only added if the guessedCode is valid
         */
    }

    public Vector<String> buildOutputMessages(Entry<String, Vector<Vector<String>>> inputs) {

        int MAX_RETRY = 10;
        int DIGITS = 4;

        String USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";
        String USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
        String USER_MESSAGE_WRONG_GUESS = ">> Wrong! SVD=%s, HD=%s";
        String USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
        String USER_MESSAGE_GAME_OVER = ">> Wrong - Game over!";
        String USER_MESSAGE_SECRET_CODE_WAS = ">> The secret code was: %s";

        int nb_retry = 0;
        Vector<String> messages = new Vector<String>();
        String secretCode = inputs.getKey();
        for (Vector<String> guessedCodeVector : inputs.getValue()) {
            String format_USER_MESSAGE_GUESS_CODE = String.format(USER_MESSAGE_GUESS_CODE, DIGITS,
                    MAX_RETRY - nb_retry);
            messages.add(format_USER_MESSAGE_GUESS_CODE);
            messages.add(guessedCodeVector.get(0) + "\n"); // guessedCode

            if (guessedCodeVector.size() > 1) {
                nb_retry++;
                if (Integer.parseInt(guessedCodeVector.get(2)) == 0) {
                    messages.add(USER_MESSAGE_CORRECT_GUESS);
                    break;
                } else {
                    if (nb_retry == MAX_RETRY) {
                        messages.add(USER_MESSAGE_GAME_OVER);
                        messages.add(String.format(USER_MESSAGE_SECRET_CODE_WAS, secretCode));
                        break;
                    }
                    String format_USER_MESSAGE_WRONG_GUESS = String.format(USER_MESSAGE_WRONG_GUESS,
                            guessedCodeVector.get(1), guessedCodeVector.get(2));
                    messages.add(format_USER_MESSAGE_WRONG_GUESS);
                }
            } else {
                messages.add(USER_MESSAGE_INVALID_INPUT);
            }

        }
        return messages;

    }

    public static void main(String[] args) throws Exception {

        String SECRET_CODE_LOCATION = "secret.code";
        new TheOne(SECRET_CODE_LOCATION)//
                .bind(MasterMild.class.getMethod("initSecretCode", String.class))//
                .bind(MasterMild.class.getMethod("readInputs", String.class))//
                .bind(MasterMild.class.getMethod("checkValidity", Entry.class))//
                .bind(MasterMild.class.getMethod("computation", Entry.class))//
                .bind(MasterMild.class.getMethod("buildOutputMessages", Entry.class))//
                .printMe();
    }

}
