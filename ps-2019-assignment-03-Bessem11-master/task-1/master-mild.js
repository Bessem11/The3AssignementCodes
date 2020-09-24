
var extractsecretcode= function(secretCodeLocation,func) {
    try{
    var fs = require('fs');
    var inputcode = fs.readFileSync(secretCodeLocation).toString();
    var secretcode= inputcode.trim();
    var lengthdiff = 0;
    }catch(err){
    console.log('No such file name or directory');
    process.exit()
    }
    var digits=4

    if(secretcode.length<digits)
    { 
        lengthdiff=digits-secretcode.length

        for (i=0;i<lengthdiff;i++)
        {
            secretcode='0'+secretcode;
        }

    }
    else if (secretcode.length>digits)
    {
        lengthdiff=secretcode.length-digits;
        secretcode=secretcode.substr(lengthdiff,secretcode.length);
    }
    // inputGuessedCode
    func(secretcode,verifyinput)
}

// ALESSIO: Name of this function is misleading? It seems that you are accumulating all the guessedCodes into
//      codes to call the next function with it
var inputGuessedCode=async function(secretcode,func)
{
    const readline = require('readline');
    codes=[];

    this.cons = readline.createInterface({
        input: process.stdin,
        prompt: ''
    });

    this.codesc = [];

    this.promise = new Promise(res => {
        this.resolve = res;
    });

    this.cons.on('line', (line) => {
        this.resolve();
        this.codesc.push(line);
        this.promise = new Promise(res => {
        this.resolve = res;
        });
    });

    if (this.codesc.length == 0) {
        await this.promise;
    }    

    codes.push(codesc);
    codes.push(secretcode);
    // the codes variable contain the guessedcodes and the secret code
    //it's format is [[guessedcode1,guessedcode2,....],secretcode]
    //here at the end of this function we call the next function

    //verifyinput
    func(codes,compute);

}

var verifyinput= async function(codesc,func){

    // ALESSIO Why do you need await here?
    var codes=await codesc
    var guessedcodes=codes[0];
    var digits=4

    for(var i=0;i<guessedcodes.length;i++)
    {
        var nospacescode = guessedcodes[i].trim(); 
        var isnumber=true;  
        
        for ( var m=0;m<nospacescode.length&&isnumber;m++)
           {
                var c= nospacescode.charAt(m);
                if (!(c >= '0' && c <= '9')) //verifying if the user input consists only of digits
                {
                     isnumber=false;
                } 
            }

        if (nospacescode.length!=digits || !isnumber)
            {
                //if the guessedcode is invalid then we replace the guessed code with [guessedcode1,null]
                guessedcodes[i]=[guessedcodes[i],null]
            }
    }
    codes[0]=guessedcodes;

    //if for exemple the guessedcode1 is invalid , codes will have this format [[[guessedcode1,null],guesssedcode2,..],secretcode]
     //here at the end of this function we call the next function

     // ALESSIO Why do not you simply generate the output as you go instead of accumulating them ?
     // compute
    func(codes,print)
}

var compute=async function(codesc,func){

    var codes=await codesc 
    var digits=4
    var guessedcodes=codes[0];
    var secretcode=codes[1];
    var HdAndSvdAndcode=[];
    for(var i=0;i<guessedcodes.length;i++)
    {
        //if guessedcode is an array then it's invalid since it's composed from the guessedcode and null value
        if(Array.isArray(guessedcodes[i]))
         {
            HdAndSvdAndcode.push([null,null,guessedcodes[i][0]])
         }
        else
        {
             sumsecret = secretcode
             .split('')
             .map(Number)
             .reduce(function (a, b) {
                 return a + b;
              }, 0);

              nospacecode=guessedcodes[i].trim();
             //calculate the sum of digits of the guessedcode
             sumguessed = nospacecode
             .split('')
             .map(Number)
             .reduce(function (a, b) {
                   return a + b;
                 }, 0);

         var count=0;
    
         for (var j=0;j<digits;j++)
                {
                 if (secretcode.charAt(j)!=nospacecode.charAt(j))
                    {
                     count++;
                    }
                }

         var SVD=sumsecret-sumguessed;
         var HD=count;
         HdAndSvdAndcode.push([HD,SVD,guessedcodes[i]])
        }
    }

    HdAndSvdAndcode.push(secretcode);
     //here at the end of this function we call the next function
     // print
    func(HdAndSvdAndcode,no_op); 
   
}

var print=async function(hdandsvdcodes,func){

        const digits = 4
        const USER_MESSAGE_GUESS_CODE = ">> Guess the %d-digit secret code (%d):";
        const USER_MESSAGE_INVALID_INPUT =   ">> Yikes! Invalid input";
        const USER_MESSAGE_WRONG_GUESS =     ">> Wrong! SVD=%d, HD=%d";
        const USER_MESSAGE_CORRECT_GUESS =   ">> Correct - You won!";
        const USER_MESSAGE_GAME_OVER =       ">> Wrong - Game over!";
        const USER_MESSAGE_CODE =     ">> The secret code was:";

        var codes=await hdandsvdcodes;
        var attempts=10;
        var secretcode=codes[codes.length-1]
        const util = require('util');
        for(var i=0;i<codes.length-1;i++)
        {
             var Hd=codes[i][0];
             var Svd=codes[i][1];
             var guessedcode=codes[i][2];

             console.log(util.format(USER_MESSAGE_GUESS_CODE,digits,attempts));
             console.log(guessedcode+"\n")
             if(attempts==1 && Hd!=null &&Hd!=0)
                {
                    console.log(USER_MESSAGE_GAME_OVER)
                    console.log(util.format(USER_MESSAGE_CODE,secretcode))
                    break;
                }
             else{

                if(Hd==null)
                    console.log(USER_MESSAGE_INVALID_INPUT)
                else if (Hd==0)
                {
                    console.log(USER_MESSAGE_CORRECT_GUESS);
                    break;
                }
                else
                {
                    console.log(util.format(USER_MESSAGE_WRONG_GUESS,Svd,Hd))
                    attempts--;
                }
            }

        }

    //here at the end of this function we call the last function that does nothing
    func(undefined)
}
// ALESSIO why not var here?
no_op=function(func){
    process.exit()
}

extractsecretcode("secret.code",inputGuessedCode);