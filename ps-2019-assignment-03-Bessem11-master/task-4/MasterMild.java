import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterMild implements Runnable {
    
        //game variables
        private static String secretcode;
        private int digits=4;
    
        //List that containts threads
        private static List<Thread>  workers =new ArrayList<Thread>();
    
        //Two locks to lock shared data so that we don't get incompatoble result
        //when accessing to the shared data
        private static Lock inputLock = new ReentrantLock();
        private static Lock outputLock = new ReentrantLock();
    
    
        //Two data spaces
        private static List<List<String>> Inputs=new ArrayList<List<String>>();
        private static List<List<String>> Outputs=new ArrayList<List<String>>();
    
    
        //games messages
        public static final String USER_MESSAGE_GUESS_CODE =    ">> Guess the %d-digit secret code (%d):\n";
        public static final String USER_MESSAGE_INVALID_INPUT = ">> Yikes! Invalid input";
        public static final String USER_MESSAGE_WRONG_GUESS =   ">> Wrong! SVD=%d, HD=%d\n";
        public static final String USER_MESSAGE_CORRECT_GUESS = ">> Correct - You won!";
        public static final String USER_MESSAGE_GAME_OVER =     ">> Wrong - Game over!";
        public static final String USER_MESSAGE_CODE =     ">> The secret code was: ";
    
    
        //this method merge the partial data and print the final output
    public void print() {
        
                int Attempts=10;
                List<String> codes=new ArrayList<String>();
                int i=0;
        
                while(!Outputs.isEmpty())
                    {
                
                            for(int m=0;m<Outputs.size();m++)
                                {
                                        if(Integer.parseInt(Outputs.get(m).get(2))==i)
                                            {
                                                    //codes[0] containts the HD value, codes[1] contain the SVD value
                                                    //codes[2] contain the rank of the input regarding all the other inputs,codes[3] contain the guessedcode
                                                    codes=Outputs.remove(m);
                                                    i++;
                                                    break;
                                                }
                                    }
                
                            System.out.format(USER_MESSAGE_GUESS_CODE,this.digits,Attempts);
                             System.out.println(codes.get(3)+"\n");
                
                             //for the last attemps if the user doesn't guess the right code
                             //then we didn't print the SVD AND HD
                            if(Attempts==1 &&codes.get(0)!=null&&Integer.parseInt(codes.get(0))!=0)
                                {
                                        System.out.println(USER_MESSAGE_GAME_OVER);
                                        System.out.format(USER_MESSAGE_CODE+secretcode+"\n");
                                        break;
                                    }
                            else
                                {
                        
                                      //If the guessedcode was invalid
                                     if(codes.get(0)==null)
                                        {
                                                System.out.println(USER_MESSAGE_INVALID_INPUT);
                                            }
                                     // If HD=0
                                    else if(Integer.parseInt(codes.get(0))==0)
                                        {
                                                System.out.println(USER_MESSAGE_CORRECT_GUESS);
                                                break;
                                
                                            }
                                    // If the guessedcode was valid but Hd!=0
                                    else
                                        {
                                                System.out.format(USER_MESSAGE_WRONG_GUESS,Integer.parseInt(codes.get(1)),Integer.parseInt(codes.get(0)));
                                                Attempts--;
                                
                                            }
                        
                                    }
                
                
                        }
            }
    
    
        public void extract_secretcode(String SECRET_CODE_LOCATION)  {
        
        
                //Read secret code file
                try
                {
                    InputStream flux=new FileInputStream(SECRET_CODE_LOCATION);
                    InputStreamReader lecture=new InputStreamReader(flux);
                    BufferedReader buff=new BufferedReader(lecture);
                    String ligne;
                    ligne=buff.readLine();
                    secretcode=ligne;
                    }catch(IOException e)
                {
                        System.out.println("No valid path or file not found");
                    }
           }
    
      public void adjustcode() {
        
        
                  //Add zeros
                  if(secretcode.length()<this.digits)
                      {
                              int lengthdiff=this.digits-secretcode.length();
                
                              for (int i=0; i<lengthdiff;i++)
                                  {
                                          secretcode='0'+secretcode;
                                      }
                          }
                  // pick less digits of the secret_code number because the secret_code length is higher then Digits
                  else if ((secretcode.length()>this.digits))
            
                      {
                              int lengthdiff=secretcode.length()-this.digits;
                              secretcode=secretcode.substring(lengthdiff, secretcode.length());
                          }
        
              }
    
    
    
        @Override
        public void run() {
        
                while(true)
                    {
                
                            int hd = 0;
                            int svd=0;
                            String position;
                            boolean isnumber=true;
                            String guessedcode="";
                            List<String> readinput=new ArrayList<String>();
                            List<String> codes=new ArrayList<String>();
                
                            inputLock.lock();
                            if(!Inputs.isEmpty())
                                {
                                        readinput=Inputs.remove(0);
                                        position=readinput.get(0);
                                        guessedcode  = readinput.get(1);
                                        inputLock.unlock();
                                    }
                            else
                                {
                                        inputLock.unlock();
                                        break;
                                    }
                
                
                
                
                
                
                        String noSpaceCode=guessedcode.trim();
                        //verify if the guessed_code is a number
                        for (int i=0 ;i<noSpaceCode.length()&&isnumber;i++)
                                 {
                                        if (!Character.isDigit(noSpaceCode.charAt(i)))
                                                isnumber=false;
                                     }      
                        
                        if (noSpaceCode.length()!=this.digits || !isnumber)
                              {
                                    //here w insert invalide hd and svd to indicate that the Guessecode
                                    //is not valid since we cannot have Hd=0 and SVD=0 they must be both
                                    //equal to zero
                                    codes.add(null);
                                    codes.add(null);
                                    codes.add(position);
                                    codes.add(guessedcode);
                        
                                  }
                        else
                            {
                                int sumguessedcode = (noSpaceCode).chars().map(Character::getNumericValue).sum();
                                // compute the sum of digits of secretcode numbers
                                int sumsecretcode = (secretcode).chars().map(Character::getNumericValue).sum();
                                // compute the SVD value
                                svd = sumsecretcode - sumguessedcode;
                                // compute the HD value
                                for (int i = 0; i < this.digits; i++) {
                                        if ((noSpaceCode).charAt(i) != (secretcode).charAt(i))
                                                hd++;
                                    }
                                
                                codes.add(Integer.toString(hd));
                                codes.add(Integer.toString(svd));
                                codes.add(position);
                                codes.add(guessedcode);
                        
                                }
                        
                        outputLock.lock();
                        try
                          { 
                            Outputs.add(codes);
                              }
                        finally
                        {
                                outputLock.unlock();
                    
                            }
                
                        }
                
                
            }
        
        public static void main(String[] args) throws IOException, InterruptedException {
                
                MasterMild mastermild=new MasterMild();
                //Scanner scanner = new Scanner(System.in);
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 
                mastermild.extract_secretcode("secret.code");
                mastermild.adjustcode();
                String line;
                //Read the guessedcode
                int i=0;
                while ((line = in.readLine()) != null) {
                        List<String> guessedcodes = new ArrayList<String>();
                        guessedcodes.add(Integer.toString(i));
                        guessedcodes.add(line);
                        Inputs.add(guessedcodes);
                        i++;  
                     }
                  
                for(int j=0;j<3;j++) {
                        Thread t1=new Thread(mastermild);
                         workers.add(t1);
                     }
                //Here we  launch them at their jobs
                 for(Thread worker:workers) {
                         worker.start();
                     }
                 for(Thread worker:workers) {
                         worker.join();
                     }
                 
                 
                 //Let's merge the partial results and print the output  
                 mastermild.print();
                 
            }
    
    
    
        
    
    }