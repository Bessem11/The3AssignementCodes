/**
 * ALESSIO: you could have better encapsulate the things using classes instead of plain objects
 */

// SecretCode class definition

var SecretCode = (function () {

    // Constructor
    function SecretCode() {
        var secretcode = "";
    }

    //function to extract Secret code from file
    function extractsecretcode(secretCodeLocation) {
        var fs = require('fs');
        var inputcode = fs.readFileSync(secretCodeLocation).toString();
        secretcode = inputcode;
    }

    //function to rectify the code by adding zeros or removing number this will be done regarding to the digits number
    function adjustcode(digits) {

        var code = secretcode.trim();
        var lengthdiff = 0;

        if (code.length < digits) {
            lengthdiff = digits - code.length
            for (i = 0; i < lengthdiff; i++) {
                code = '0' + code; //Adding Zeros
            }

        }
        else if (code.length > digits) {
            lengthdiff = code.length - digits;
            code = code.substr(lengthdiff, lengthdiff); //extracting numbers from secret cdoe
        }

        secretcode = code;
    }

    //this function is responsable for dispatching messages
    SecretCode.prototype.dispatch = function (messages) {

        if (messages[0] == "init") {
            return extractsecretcode.call(this, messages[1]);
        }
        else if (messages[0] == "rectify") {
            return adjustcode.call(this, messages[1]);
        }
        else if (messages[0] == "getsecretcode") {
            return secretcode;
        }
    }

    return SecretCode;

})();


//GuessedCode class defintion

var GuessedCode = (function () {

    // Constructor
    function GuessedCode() {

        var guessedcode;
        var USER_MESSAGE_INVALID_INPUT;
    }
    // initialize variables
    function init_guessedcode(input) {
        guessedcode = "";
        USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
        guessedcode = input;
    }

    //verify if the input of user is valid
    function verifyinput(digits) {

        var nospacescode = guessedcode.trim(); //removing spaces from the begining and the end of the secret code
        guessedcode = nospacescode;
        var isnumber = true;

        for (i = 0; i < nospacescode.length && isnumber; i++) {
            var c = nospacescode.charAt(i);
            if (!(c >= '0' && c <= '9')) //verifying if the user input consists only of digits
            {
                isnumber = false;
            }
        }

        if (nospacescode.length != digits || !isnumber) {
            console.log("\n" + USER_MESSAGE_INVALID_INPUT);
            guessedcode = "0";
        }

    }
    //this function is responsable for dispatching messages
    GuessedCode.prototype.dispatch = function (messages) {

        if (messages[0] == "init") {
            return init_guessedcode.call(this, messages[1]);
        }
        else if (messages[0] == "isvalid") {
            return verifyinput.call(this, messages[1]);
        }
        else if (messages[0] == "getguessedcode") {
            return guessedcode;
        }
    }

    return GuessedCode;

})();


// Compute class definition
var Compute = (function () {

    // Constructor
    function Compute() {
        var HD = "";
        var SVD = "";
    }
    //Calculate the SVD and HD values
    function computevalue(secretcode, guessedcode, digits) {

        sumsecret = secretcode
            .split('')
            .map(Number)
            .reduce(function (a, b) {
                return a + b;
            }, 0);//calculate the sum of the digits of the secret code

        sumguessed = guessedcode
            .split('')
            .map(Number)
            .reduce(function (a, b) {
                return a + b;
            }, 0); //calculate the sum of the digits of the guessed code

        var count = 0;
        for (i = 0; i < digits; i++) {
            if (secretcode.charAt(i) != guessedcode.charAt(i)) {
                count++; //cound represent the HD value
            }
        }

        SVD = sumsecret - sumguessed;
        HD = count;
    }

    //The compute dispatcher of messages
    Compute.prototype.dispatch = function (messages) {

        if (messages[0] == "compute") {
            return computevalue(messages[1], messages[2], messages[3]);
        }
        else if (messages[0] == "getsvd") {
            return SVD;
        }
        else if (messages[0] == "gethd") {
            return HD;
        }
    }

    return Compute;

})();

//MasterMildCOntroller class definition
var MasterMildCOntroller = (function () {

    // Constructor
    function MasterMildCOntroller() {
        /**
         * ALESSIO: What's the point here ? Those are local variables...
         */
        var SECRET_CODE_LOCATION;
        var USER_MESSAGE_CORRECT_GUESS;
        var USER_MESSAGE_GAME_OVER;
        var MAX_RETRY;
        var DIGITS;
        var secret;
        var guess;
        var compute;

    }
    //intilize values
    function init() {

        secret = new SecretCode();
        guess = new GuessedCode();
        compute = new Compute();

        SECRET_CODE_LOCATION = "secret.code";
        USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!"
        USER_MESSAGE_GAME_OVER = ">> Wrong - Game over!"
        MAX_RETRY = 10;
        DIGITS = 4;

        secret.dispatch(["init", SECRET_CODE_LOCATION]);
        secret.dispatch(["rectify", DIGITS]);
    }


    function run() {


        var k = 0;
        var x1 = MAX_RETRY - k;
        var readline = require('readline');
        var rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout,
            terminal: false
        });

        console.log(">> Guess the " + DIGITS + "-digit secret code (" + x1 + "):");
        //Get the user input
        rl.on('line', function (line) {




            console.log(line);
            guess.dispatch(["init", line]);
            guess.dispatch(["isvalid", DIGITS]);

            // if the input is valid done compute HD and SVD values
            if (guess.dispatch(["getguessedcode"]) != "0") {
                //Compute HD and SVD values
                compute.dispatch(["compute", secret.dispatch(["getsecretcode"]), guess.dispatch(["getguessedcode"]), DIGITS]);
                if (compute.dispatch(["gethd"]) == 0) {
                    console.log("\n" + USER_MESSAGE_CORRECT_GUESS);
                    process.exit(); //if the HD value is equal to zero
                }
                else if (k != MAX_RETRY - 1) {
                    console.log("\n" + ">> Wrong! SVD=" + compute.dispatch(["getsvd"]) + ", HD=" + compute.dispatch(["gethd"]));
                }
                k++;
                x1 = MAX_RETRY - k

            }

            if (k == MAX_RETRY) {
                // Program exit.
                console.log("\n" + USER_MESSAGE_GAME_OVER);
                console.log(">> The secret code was: " + secret.dispatch(["getsecretcode"]))
                process.exit();
            }
            else {
                console.log(">> Guess the " + DIGITS + "-digit secret code (" + x1 + "):");
            }


        });


    }

    //this function is responsable for dispatching messages
    MasterMildCOntroller.prototype.dispatch = function (messages) {
        if (messages[0] == "init") {
            return init.call(this);
        }
        else if (messages[0] == "run") {
            return run.call(this);
        }
    }

    return MasterMildCOntroller;

})();

//Main function
var mastermildcontroller = new MasterMildCOntroller();
mastermildcontroller.dispatch(["init"]);
mastermildcontroller.dispatch(["run"]);      
