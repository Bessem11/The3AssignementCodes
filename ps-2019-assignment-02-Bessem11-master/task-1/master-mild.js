class MasterMildFramework {

    constructor () {  
        this.load_event_handlers = [];
        this.verify_event_handlers = [];
        this.dowork_event_handlers = [];
    }
     run (path_to_file) {  
        this.load_event_handlers.forEach(h => {
            h(path_to_file)     
         });
        this.verify_event_handlers.forEach(h => {
            h()
         });
         this.dowork_event_handlers.forEach(h => {
            h()  
         });
    }
}

class SecretStorge {

    constructor(mmf,guessedcode){
        SecretStorge.secretcode;
        SecretStorge.digits=4;
        SecretStorge.max_retry=10;
        SecretStorge.Try_number=0;
        SecretStorge.guess;
        SecretStorge.guessedcode=guessedcode;
        SecretStorge.compute_event_handlers=[];
        mmf.load_event_handlers.push(this.load);
        mmf.verify_event_handlers.push(this.adjust);
        mmf.dowork_event_handlers.push(this.Produce_Work);
    }

    load(path_to_file){
        var fs = require('fs');
        var inputcode = fs.readFileSync(path_to_file).toString();
        SecretStorge.secretcode=inputcode;     
    }

    adjust () {
        var code= SecretStorge.secretcode.trim();
        var lengthdiff = 0;
        if(code.length<SecretStorge.digits) 
        { 
            lengthdiff=SecretStorge.digits-code.length;
            var i;
            for (i=0;i<lengthdiff;i++)
             {
             code='0'+code; //Adding Zeros
             }

        }
         else if (code.length>SecretStorge.digits){
             lengthdiff=code.length-SecretStorge.digits; 
             code=code.substr(lengthdiff,code.length); //extracting numbers from secret cdoe
        }

        SecretStorge.secretcode=code;
    }

    Produce_Work()
    {
        var readline = require('readline');
        var rl = readline.createInterface({
         input: process.stdin,
         output: process.stdout,
         terminal: false
        });
        rl.on('line', function(line) {
        SecretStorge.guess=line;
        // ALESSIO: why are you accessing prototype here and not simply invoking SecretStorge.Logic_Function(); ?
        var res = SecretStorge.prototype.Logic_Function();

        if(res[4]!=null)
        {
             if(res[4][0]==0 || res[4][0]!=0 && res[1]==res[2])
             {
                // ALESSIO: Violations of the style, you are breaking the encapsulation here. You should have find a way to trigger this
                //      upon an "end_event" orsomething
                 process.exit();

             }
         }
        })
    }

    Logic_Function()
    {
        //Maybe here I violate the style because I called the verifyinput function directly because this 
        //function is used to verfiy the input , even in the exemple we made in the calss we called the 
        //is_stop word directly
        // ALESSIO: I think it debatable whether or not this is an ACTION method, however, since you commented on it
        //      I will not consider this as a violation
        var isright=SecretStorge.guessedcode.verifyinput(SecretStorge.guess);
        var HDANDSVD;
        if(isright){
            //SecretStorge.args=[SecretStorge.guess.trim(),SecretStorge.secretcode];
            SecretStorge.compute_event_handlers.forEach(func => {
                HDANDSVD=func(SecretStorge.guess.trim(),SecretStorge.secretcode)
                SecretStorge.Try_number++;
            });
        }
        // ALESSIO: However, this is a violation of the Aspects style because you are defining the function and its
        //      very peculiar return value to fit with the aspects, while the solution should be completely agnostic to it
       return [isright,SecretStorge.Try_number,SecretStorge.max_retry,SecretStorge.secretcode,HDANDSVD];
    
    }

    register_for_compute_event(handler){
        SecretStorge.compute_event_handlers.push(handler)
    }


}

class GuessedCode{

    constructor(){
        GuessedCode.digits=4;
    }

    verifyinput (guessedcode) {

        var guessedcode = guessedcode.trim(); //removing spaces from the begining and the end of the secret code
        var isnumber=true;  
        for (var i=0;i<guessedcode.length&&isnumber;i++){
                var c= guessedcode.charAt(i);
                if (!(c >= '0' && c <= '9')) //verifying if the user input consists only of digits
                {
                     isnumber=false;
                } 
            }
        if (guessedcode.length!=GuessedCode.digits || !isnumber)
                return false;
        else
                return true;
     }

}

class HdAndSvdCalculator{

    constructor(secretstorge){

        HdAndSvdCalculator.digits=4;
        // ALESSIO: There is not clear and natural reason to have the following two fields declared here
        //      Since you recompute them everytime
        HdAndSvdCalculator.SVD;
        HdAndSvdCalculator.HD;

        secretstorge.register_for_compute_event(this.Calculator);
    }

    Calculator(guessedcode,secretcode){

        var SvdAndHd=[];
        HdAndSvdCalculator.SVD=0;
        HdAndSvdCalculator.HD=0;

        var sumsecret = secretcode
        .split('')
        .map(Number)
        .reduce(function (a, b) {
            return a + b;
        }, 0);//calculate the sum of the digits of the secret code

        var sumguessed = guessedcode
        .split('')
        .map(Number)
        .reduce(function (a, b) {
            return a + b;
        }, 0); //calculate the sum of the digits of the guessed code

        var i;
        for (i=0;i<HdAndSvdCalculator.digits;i++)
        {
            if (secretcode.charAt(i)!=guessedcode.charAt(i))
                {
                    HdAndSvdCalculator.HD++; //cound represent the HD value
                }
        }

        HdAndSvdCalculator.SVD=sumsecret-sumguessed;  
        SvdAndHd=[HdAndSvdCalculator.HD, HdAndSvdCalculator.SVD];    
        return SvdAndHd;
    }

}


class AOP {

    constructor(){

    AOP.DIGITS = 4;
    AOP.MAXRETRY = 10;
    AOP.USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";
    AOP.USER_MESSAGE_INVALID_INPUT =   ">> Yikes! Invalid input";
    AOP.USER_MESSAGE_WRONG_GUESS =     ">> Wrong! SVD=%d, HD=%d";
    AOP.USER_MESSAGE_CORRECT_GUESS =   ">> Correct - You won!";
    AOP.USER_MESSAGE_GAME_OVER =       ">> Wrong - Game over!";
    AOP.USER_MESSAGE_SECRET_CODE_WAS = ">> The secret code was: %s";

    }

    InitialOutput(f){
        function wrapper(){
            // Before
            const util = require('util');
            console.log(util.format(AOP.USER_MESSAGE_GUESS_CODE,AOP.DIGITS,AOP.MAXRETRY));
            var ret_value = f(); //caling the function
            return ret_value
        }
        return wrapper;
    }

    FailureOutput(f){   
        function wrapper(){
            const util = require('util');
            // Before
            if(arguments[1]==0){
                console.log(util.format(AOP.USER_MESSAGE_GUESS_CODE,AOP.DIGITS,AOP.MAXRETRY));
            }
            console.log(SecretStorge.guess);

            var ret_value = f(); //caling the function
            
            // ALESSIO: Why this function is returning two values? or all the functions return two values?
            var Retry_Number=ret_value[2]-ret_value[1];
            // After the call
            if(ret_value[0]==false){
                console.log("\n"+AOP.USER_MESSAGE_INVALID_INPUT);
                console.log(util.format(AOP.USER_MESSAGE_GUESS_CODE,AOP.DIGITS,Retry_Number));
            } else {
                if(ret_value[1]==ret_value[2] && ret_value[4][0]!=0){

                    console.log("\n"+AOP.USER_MESSAGE_GAME_OVER);
                    console.log(util.format(AOP.USER_MESSAGE_SECRET_CODE_WAS,ret_value[3]));

                } else {
                    if(ret_value[4][0]==0){
                        console.log("\n"+AOP.USER_MESSAGE_CORRECT_GUESS);
                    } else {
                        console.log(util.format("\n"+AOP.USER_MESSAGE_WRONG_GUESS,ret_value[4][1],ret_value[4][0]));
                        console.log(util.format(AOP.USER_MESSAGE_GUESS_CODE,AOP.DIGITS,Retry_Number));
                    }
                }
            }
            return ret_value
        }
        return wrapper;
    }    

}


//wrappers
var args = process.argv.slice(2);
const ASPECT_FLAG = '--enable-aspects'

if ( args.length > 0 && args[0] == ASPECT_FLAG) {
    AOP=new AOP();
    
    SecretStorge.prototype.Logic_Function=AOP.FailureOutput(SecretStorge.prototype.Logic_Function);
    SecretStorge.prototype.Produce_Work=AOP.InitialOutput(SecretStorge.prototype.Produce_Work);

    mastermildframework=new MasterMildFramework();
    guessed=new GuessedCode();
    secret=new SecretStorge(mastermildframework,guessed);
    hdandsvdcalculator=new HdAndSvdCalculator(secret);
    mastermildframework.run("secret.code");
} else {
    mastermildframework=new MasterMildFramework();
    guessed=new GuessedCode();
    secret=new SecretStorge(mastermildframework,guessed);
    hdandsvdcalculator=new HdAndSvdCalculator(secret);
    mastermildframework.run("secret.code");
    
}
   

   
    


