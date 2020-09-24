# Assignment 2: Objects Communication and Reflection
----------------------

Re-Implement the **Master Mild** game using a combination of Objects Communication and Reflection-based Styles.

## Task 1
Re-implement Master Mild according to the **Hollywood Style**; however, instead of directly writing the output to the console use the **Aspects Style** to capture the relevant informations and output them to the console.
That is, if run alone the base program implemented according to the Hollywood Style, it should produce no outputs, while when the aspects are enabled, you should see the output printed to the console. 

> This task must be solved using only and only **JavaScript v8.12.0**

## Task 2
Re-implement Master Mild according to the **Bulletin Board Style**; however, instead of directly interacting with your program using the command line, use the **Plugins Style** to dynamically load a client that plays the game according to a logic coded by you. Also provide a default client to be used when no dynamically loaded clients are available.
The default client is NOT loaded dynamically, always generates valid inputs composed only by zeros, and can always finish the game (either winning or loosing).

> This task must be solved using only and only **Java 10**.

## Grading and Submission
In order to pass this assignment you must correctly implement at least one of the two tasks. If you correctly implement both, you’ll get a full mark.

Be sure that whatever you submit passes **at least all the provided tests**; otherwise, the corresponding task will not be considered correct.

Initially, the repo contains one test for each task. Other tests will be added at later stages, but feel free to propose your own tests: commit the tests to your private repo and ask Alessio to make them available to everybody.



## Master Mild
Master Mild is a variation of the well known game Master Mind [1] in which the secret code is made out of *only* digits [0-9] and the program outputs the sum value distance and the Hamming distance [2] between the secret code and the guessed one instead of the number of pegs correctly places and colored and the number of pegs correctly colored but wrongly placed.

- The `secret_code` have a fixed number of digits (e.g., 4) which is more than 0.
- The `guessed_code` must have the same number of digits of the `secret_code`
- Repetitions in the codes are allowed. Assuming 4-digit codes, `0000` and `9999` are as valid as `1234`.
- The *Sum Value Distance (SVD)* is computed as difference between the sum of the digits of the codes; hence it can be positive, negative, or zero. For example, if secret code is `1234` and guessed code is `4321`, the distance between them is `(1+2+3+4)-(4+3+2+1) = 0`.
- The *Hamming Distance (HD)* is the amount of changes that one should perform on `guessed_code` in order to obtain the `secret_code`. For example, the distance between `0010` and `0000` is 1, and the distance between `9000` and `0000` is also 1.
Hence, the distance can vary between 0 and number of digits. 
- Each game runs for a maximum predefined number of iterations (e.g., 10), but can end as soon as the `secret_code` is found.
- The value of the secret code is read from a `secret.code` file. If the value in `secret.code` does not match the fixed number of digits of the program either truncate the value or pad it with heading zeros (see examples/tests below)
- The system should reject wrong inputs (codes with more or less digits than expected, non numerical chars, punctuations, etc...). Invalid inputs must not count as valid game moves.

## Basic Examples and Corner Cases

### Easy Peasy
Number of digits is **4** and `secret.code` contains 1234, hence the `secret_code` is `1234`.

Assuming number of tries is **10**.

```
>> Guess the 4-digit secret code (10):
0000

>> Wrong! SVD=10, HD=4
>> Guess the 4-digit secret code (9):
1000

>> Wrong! SVD=9, HD=3
>> Guess the 4-digit secret code (8):
8765

>> Wrong! SVD=-16, HD=4
>> Guess the 4-digit secret code (7):
3214

>> Wrong! SVD=0, HD=2
>> Guess the 4-digit secret code (6):
1234

>> Correct - You won!
```

### Game Over
Number of digits is **4** and `secret.code` contains 1234, the `secret_code` is `1234`.

Assuming number of tries is **3**.

```
>> Guess the 4-digit secret code (3):
0000

>> Wrong! SVD=10, HD=4
>> Guess the 4-digit secret code (2):
1000

>> Wrong! SVD=9, HD=3
>> Guess the 4-digit secret code (1):
8765

>> Wrong - Game over!
>> The secret code was: 1234
```

### Wrong Inputs Do Not Count
Number of digits is **4** and `secret.code` contains 1234, the `secret_code` is `1234`.

Assuming number of tries is **3**.

```
>> Guess the 4-digit secret code (3):
e3f

>> Yikes! Invalid input
>> Guess the 4-digit secret code (3):
01111111

>> Yikes! Invalid input
>> Guess the 4-digit secret code (3)
```

### Corner Cases (Configuration)
- Number of digits is 1, `secret.code` contains 1234, hence `secret_code` is `4`
- Number of digits is 2, `secret.code` contains 1234, hence `secret_code` is `34`
- Number of digits is 3, `secret.code` contains 1234, hence `secret_code` is `234`
- Number of digits is 5, `secret.code` contains 1234, hence `secret_code` is `01234`
- Number of digits is 6, `secret.code` contains 1234, hence `secret_code` is `001234`

#### Corner Cases (Input Validation)
- Number of digits is 3, input is `1` ` ` `1` `1` ` ` ` `, input is **invalid**
- Number of digits is 3, input is ` ` ` ` `1` `1` `1` ` `, input is **valid**
- Number of digits is 3, input is ` ` `1` `1` `1` ` ` ` `, input is **valid**

## Code Conventions and File Naming
Please stick to the file names and the interfaces provided with the assignment.
You can add more files, but you cannot remove or rename the existing ones since that will brake automated testing and eventually impact your grade !

## References
[1] [https://en.wikipedia.org/wiki/Mastermind_(board_game)](https://en.wikipedia.org/wiki/Mastermind_(board_game))

[2] [https://en.wikipedia.org/wiki/Hamming_distance](https://en.wikipedia.org/wiki/Hamming_distance)
