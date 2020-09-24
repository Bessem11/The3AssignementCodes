global.secretCodeLocation = "secret.code"
global.DIGITS = 4
global.MAX_RETRY = 10
global.USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
global.USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
global.USER_MESSAGE_GAME_OVER = ">> Wrong - Game over!";

var extractsecretcode = function (secret) {
    /**
     * ALESSIO: where secretCodeLocation comes from? You should have added a comment here
     */
    var fs = require('fs');
    var inputcode = fs.readFileSync(secretCodeLocation).toString();
    /**
     * ALESSIO Objects are key-value maps, only by chance javascript objects are implemented the same.
     * The correct form is: secret["secretcode"]
     *  I will not repeat this commen in all the places
     */

    secret.secretcode = inputcode;
}

var adjustcode = function (secret) {
    var code = secret.secretcode.trim();
    var lengthdiff = 0;

    if (code.length < DIGITS) {
        lengthdiff = DIGITS - code.length

        for (i = 0; i < lengthdiff; i++) {
            code = '0' + code;
        }

    }
    else if (code.length > DIGITS) {
        lengthdiff = code.length - DIGITS;
        code = code.substr(lengthdiff, lengthdiff);
    }

    secret.secretcode = code;
}

// ALESSIO: It is not clear to whom this function belongs to code or gus?
var init_guessedcode = function (code, gus) {
    gus.guessedcode = code;
}

var verifyinput = function (gus) {

    var nospacescode = gus.guessedcode.trim();
    gus.guessedcode = nospacescode;
    var isnumber = true;
    for (i = 0; i < nospacescode.length && isnumber; i++) {
        var c = nospacescode.charAt(i);
        if (!(c >= '0' && c <= '9')) {
            isnumber = false;
        }
    }
    if (nospacescode.length != DIGITS || !isnumber) {
        console.log("\n" + USER_MESSAGE_INVALID_INPUT);
        gus.guessedcode = "0";

    }

}

var computevalue = function (sec, gus, comp) {

    var secret = sec.secretcode;
    var guessed = gus.guessedcode;
    sumsecret = secret
        .split('')
        .map(Number)
        .reduce(function (a, b) {
            return a + b;
        }, 0); //calculate the sum of the digits of the secret code

    sumguessed = guessed
        .split('')
        .map(Number)
        .reduce(function (a, b) {
            return a + b;
        }, 0); //calculate the sum of the digits of the guessed code

    var count = 0;

    for (i = 0; i < DIGITS; i++) {
        if (secret.charAt(i) != guessed.charAt(i)) {
            count++; //cound represent the HD value
        }

    }

    comp.SVD = sumsecret - sumguessed;
    comp.HD = count;



}

//Map for Secret code
var SecretCode = {
    secretcode: "",
    init: function () { extractsecretcode(SecretCode) },//initilize secret code
    rectify: function () { adjustcode(SecretCode) } //rectify the secret code by adding zeros or extracting digits
};

//Map for Guessed code
var GuessedCode = {
    guessedcode: "",
    init: function (input) { init_guessedcode(input, GuessedCode) },//function to initilize values
    isvalid: function () { verifyinput(GuessedCode) }//function to verfiy user input

};

// ALESSIO: compute accesses things that are not inside its own encapsulated state. How is this possible? According to the style this is a violation
//Map for Compute
var Compute = {
    HD: 0,
    SVD: 0,
    compute: function () { computevalue(SecretCode, GuessedCode, Compute) }//function to calculate SVD and HD values
}

// ALESSIO: one again this should read like SecretCode["init"](); to match better the constraints of the style
SecretCode.init();
SecretCode.rectify();

/**
 * ALESSIO The following violates the encapsulation principle of the style. You cannot simply put the implementatino of the
 * main logic here, you should have defined proper things instead
 */
var k = 0;
var x1 = MAX_RETRY - k;
var readline = require('readline');
var rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout,
    terminal: false
});
// Prompt user to input data in console.
// When user input data and click enter key.
console.log(">> Guess the " + DIGITS + "-digit secret code (" + x1 + "):");

rl.on('line', function (line) {

    console.log(line);
    // Print user input in console.
    GuessedCode.init(line);
    GuessedCode.isvalid();

    // if the input is valid done compute HD and SVD values
    if (GuessedCode.guessedcode != "0") {
        Compute.compute();
        if (Compute.HD == 0) {
            console.log("\n" + USER_MESSAGE_CORRECT_GUESS);
            process.exit();
        }
        else if (k != MAX_RETRY - 1) {
            console.log("\n" + ">> Wrong! SVD=" + Compute.SVD + ", HD=" + Compute.HD);
        }
        k++;
        x1 = MAX_RETRY - k

    }

    if (k == MAX_RETRY) {
        console.log("\n" + USER_MESSAGE_GAME_OVER);
        console.log(">> The secret code was: " + SecretCode.secretcode)
        process.exit();
    }
    else {
        console.log(">> Guess the " + DIGITS + "-digit secret code (" + x1 + "):");
    }



});




