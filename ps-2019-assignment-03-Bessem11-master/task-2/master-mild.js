
//The Quarantine class
class TFQuarantine {

    constructor(func){

        this.funcs=[func];

    }
    bind(func){
        this.funcs.push(func);
        return this;
    }
    async execute()
    {
        function guard_callable(v){
            if(typeof v=="function")
                return v();
            else
                return v;
        }

        var value =() => {};

        this.funcs.forEach(func => {
            value=func(guard_callable(value));
        });

        var codesc=guard_callable(value);

        // ALESSIO: This is a violation of the style as you make you Quarantine dependent on the fact that your last
        //      function produces this message objects. Instead you should have defined an impure function that prints
        //      the messages and bound that into the quarantine monad.
        
        //here we are going to print messages
        var messages=await codesc

        for(var i=0;i<messages.length;i++)
        {
            for(var j=0;j<messages[i].length;j++)
            {
                console.log(messages[i][j])
            }
        }
    }
}

//The functions

// ALESSIO: Violations of the style, this function is PURE but you implemented it as IMPURE
var get_input=function()
{
    function f(){
    return "secret.code";
    }

    return f;
}


var extractsecretcode= function(secretCodeLocation) {
    function f(){
    try{
    var digits=4
    var fs = require('fs');
    var inputcode = fs.readFileSync(secretCodeLocation).toString();
    var secretcode= inputcode.trim();
    var lengthdiff = 0;
    }catch(err){
    console.log('No such file name or directory');
    process.exit()
    }

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
    
    return secretcode;
}
return f;

}

const readguessedcode =  function (secretcode) {
    async function f(){
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
    return codes;

}
return f

}

// ALESSIO: Violations of the style, this function is PURE but you implemented it as IMPURE
var verifyinput= function(codesc) {
    async function f(){

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
    return codes
    }
    return f
}

//this function Compute Svd and HD values
var compute=async function(codesc)
{
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
    return HdAndSvdAndcode; 
}

var messages=async function(codesc){

        codes=await codesc;

        var messages=[]
        var attempts=10;
        var digits=4;
        var secretcode=codes[codes.length-1]
        var USER_MESSAGE_INVALID_INPUT =   ">> Yikes! Invalid input";
        var USER_MESSAGE_CORRECT_GUESS =   ">> Correct - You won!";
        var USER_MESSAGE_GAME_OVER =       ">> Wrong - Game over!";
        var USER_MESSAGE_CODE =     ">> The secret code was: ";

    for(var i=0;i<codes.length-1;i++)
    {
         var message=[]
         var Hd=codes[i][0];
         var Svd=codes[i][1];
         var guessedcode=codes[i][2];
  
         
        message.push(">> Guess the "+digits+"-digit secret code ("+attempts+"):")
        message.push(guessedcode+"\n")
         if(attempts==1 && Hd!=null &&Hd!=0)
            {
                message.push(USER_MESSAGE_GAME_OVER)
                message.push(USER_MESSAGE_CODE+secretcode)
                messages.push(message)
                break;
            }
         else{

            if(Hd==null)
            {
                 message.push(USER_MESSAGE_INVALID_INPUT)
                 messages.push(message)

            }
            else if (Hd==0)
            {
                message.push(USER_MESSAGE_CORRECT_GUESS);
                messages.push(message)
                break;
            }
            else
            {
                message.push(">> Wrong! SVD="+Svd+", HD="+Hd)
                messages.push(message)
                attempts--;
            }
          }

    }
    return messages

}


//The main function
tfQuarantine=new TFQuarantine(get_input);
tfQuarantine.bind( extractsecretcode)
            .bind(readguessedcode)
            .bind(verifyinput)
            .bind(compute)
            .bind(messages)
            .execute();

