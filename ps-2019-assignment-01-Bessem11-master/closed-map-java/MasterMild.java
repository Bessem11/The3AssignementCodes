
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;


public class MasterMild {
	
	public static final String SECRET_CODE_LOCATION = "secret.code";
    public static final int DIGITS =4;
    public static final int MAX_RETRY = 10;
    
    
    /*
     * Predefined message templates.
     * Check for example:https://dzone.com/articles/java-string-format-examples
     */
    public static final String USER_MESSAGE_GUESS_CODE =    ">> Guess the %d-digit secret code (%d):";
    public static final String USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
    public static final String USER_MESSAGE_WRONG_GUESS =   ">> Wrong! SVD=%d, HD=%d";
    public static final String USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
    public static final String USER_MESSAGE_GAME_OVER =     ">> Wrong - Game over!";
    public static final String USER_MESSAGE_CODE =     ">> The secret code was: ";

    
    

	  public void extract_secretcode(Map<String, Object> SecretCode ) throws IOException {


		    //Read secret code file
	        InputStream flux=new FileInputStream(SECRET_CODE_LOCATION); 
	        InputStreamReader lecture=new InputStreamReader(flux);
	        BufferedReader buff=new BufferedReader(lecture);
	        String ligne;
	        ligne=buff.readLine();
	        SecretCode.replace("secretcode",ligne);
	   }
	  
	  public void adjustcode(Map<String, Object> SecretCode ) {
		  
		  String secretcode=(String)SecretCode.get("secretcode");

		  //Add zeros 
		  if(secretcode.length()<DIGITS)
		  {
			  int lengthdiff=DIGITS-secretcode.length();

			  for (int i=0; i<lengthdiff;i++)
			  {
				  secretcode='0'+secretcode;
			  }
		  }
		  // pick less digits of the secret_code number because the secret_code length is higher then Digits
		  else if ((secretcode.length()>DIGITS))
		  
		  {
			  int lengthdiff=secretcode.length()-DIGITS;
			  secretcode=secretcode.substring(lengthdiff, secretcode.length());
		  }
		  
	        SecretCode.replace("secretcode",secretcode);
	  }

	  
	  public void init_guessedcode(Map<String, Object> GuessCode, String guessedcode )  {

	         GuessCode.replace("guessedcode", guessedcode);
	      
	  }
	  
	  public void verifyinput(Map<String, Object> GuessCode ) {
		  
		  String guessedcode=((String) GuessCode.get("guessedcode")).trim();
		  GuessCode.replace("guessedcode",guessedcode);
		  boolean isnumber=true;

		  //verify if the guessed_code is a number
		  for (int i=0 ;i<guessedcode.length()&&isnumber;i++)
		  {
			if (!Character.isDigit(guessedcode.charAt(i)))
				isnumber=false;
		  }
		  
		  //verify if the guessed_code is valid
	      if (guessedcode.length()!=DIGITS || !isnumber)
	      {
	    	  System.out.println("\n"+USER_MESSAGE_INVALID_INPUT);
		      GuessCode.replace("guessedcode","0");
	      }
	      
	  	}
	  
	  public void computevalue(Map<String, Object> ComputeValue, Map<String, Object> SecretCode,Map<String, Object> GuessCode )  {

	     Object guessedcode= GuessCode.get("guessedcode");
		 Object secretcode=SecretCode.get("secretcode");
		 //compute the sum of digits of guessedcode numbers
		 int sumguessedcode = ((String)guessedcode).chars()
				    .map(Character::getNumericValue)
				    .sum();
		 
		 //compute the sum of digits of secretcode numbers
		 int sumsecretcode =((String)secretcode)
				    .chars()
				    .map(Character::getNumericValue)
				    .sum();
		 //compute the SVD value
		 int svd=sumsecretcode-sumguessedcode;
		 int count=0;
		 //compute the HD value
		 for (int i=0;i<DIGITS;i++)
		 {
			if(((String)guessedcode).charAt(i)!=((String)secretcode).charAt(i))
				count++;
		 }
		 
		 ComputeValue.replace("SVD", svd);
		 ComputeValue.replace("HD", count);  
	  }

	  
	  
    public static void main(String[] args) throws Exception {
    	

    	MasterMild m=new MasterMild();
    	
    	Map<String, Object> SecretCode = new HashMap<String, Object>();
        SecretCode.put("secretcode","");
        SecretCode.put("init", MasterMild.class.getMethod("extract_secretcode",Map.class));
        SecretCode.put("rectify", MasterMild.class.getMethod("adjustcode",Map.class));

        Map<String, Object> GuessedCode = new HashMap<String, Object>();
        GuessedCode.put("guessedcode","");
        GuessedCode.put("init", MasterMild.class.getMethod("init_guessedcode",Map.class,String.class));
        GuessedCode.put("isvalid", MasterMild.class.getMethod("verifyinput",Map.class));
       
        Map<String, Object> ComputeValue = new HashMap<String, Object>();
        ComputeValue.put("HD",0);
        ComputeValue.put("SVD",0);
        ComputeValue.put("compute", MasterMild.class.getMethod("computevalue",Map.class,Map.class,Map.class));

        
        ((Method)SecretCode.get("init")).invoke(m,SecretCode); 
        ((Method)SecretCode.get("rectify")).invoke(m,SecretCode);  
        
        
        int i=0;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
        String guessedcode;
        while(i<MAX_RETRY )
        {
        	//|| (guessedcode = in.readLine()) != null
        	// Create a Scanner object
             System.out.format(USER_MESSAGE_GUESS_CODE+ "\n",DIGITS,MAX_RETRY-i);
             
             guessedcode = in.readLine(); // the player enter the guessed_code
             System.out.print(guessedcode+"\n");

             ((Method)GuessedCode.get("init")).invoke(m,GuessedCode,guessedcode); 
             ((Method)GuessedCode.get("isvalid")).invoke(m,GuessedCode); // verifying if the input is valid
            
             //the while loop verify if the player is writing a valid input or not
             // every time the player write an ivalide inpute the guessed_code is reset to 0 so the condition of the while loop is true
             while(GuessedCode.get("guessedcode")=="0")
             {
                  System.out.format(USER_MESSAGE_GUESS_CODE + "\n",DIGITS,MAX_RETRY-i);
            	  guessedcode = in.readLine();
 	             System.out.print(guessedcode+"\n");
            	 ((Method)GuessedCode.get("init")).invoke(m,GuessedCode,guessedcode); 
                 ((Method)GuessedCode.get("isvalid")).invoke(m,GuessedCode); 
             }
             
             //compute the SVD, HD values
             ((Method)ComputeValue.get("compute")).invoke(m,ComputeValue,SecretCode,GuessedCode);  
             
             // if HD Value is equal to Zero then the player Win
             if((int)ComputeValue.get("HD")==0)
            	 break;
             // if HD value !=0 then display the wrong message to player ( we don't display this message at the last attempt)
             else if (i!=MAX_RETRY-1)
                 System.out.format("\n"+USER_MESSAGE_WRONG_GUESS + "\n",(int)ComputeValue.get("SVD"),(int)ComputeValue.get("HD"));

            i++;
        }

        
        //Print the final result (if the player exceeds the MAX_RETRY then it's a game over else the player win)
        if(i==MAX_RETRY)
        {
        	System.out.println("\n"+USER_MESSAGE_GAME_OVER);
    		System.out.println(USER_MESSAGE_CODE+(String)SecretCode.get("secretcode"));
        }

        else
        	System.out.println("\n"+USER_MESSAGE_CORRECT_GUESS); 

    }

 }

