# Assignment 3: Function Composition, Adversity and Concurrency
----------------------

Re-Implement the **Master Mild** game using functions composition and concurrency styles. 
In none of the tasks you are allowed to use any external library/module (i.e., no additional jars, nothing that must be installed using `npm install`)

## Task 1
Re-implement Master Mild according to the **Kick Forward Style**.

> This task must be solved using only and only **JavaScript v8.12.0**

## Task 2
Re-implement Master Mild according to the **Quarantine Style**.

> This task must be solved using only and only **JavaScript v8.12.0**

## Task 3
Re-implement Master Mild according to the **The One Style**.

> This task must be solved using only and only **Java 10**.

## Task 4
Re-implement Master Mild according to the **Data Spaces Style**.

> This task must be solved using only and only **Java 10**.

## Grading and Submission
In order to pass this assignment you must correctly implement at least **two of those four tasks**. If you correctly implement all, you’ll get a full mark.

You can earn additional points by contributing public tests or anything that can be useful to the students dealing with the assignment.

### Build scripts
The automated testing infrastructure will use the `run.sh` scripts in each task folder to clean up, rebuild and execute your program. The intended usage of the scripts is the following:

> cat inputs | ./run.sh

Basic `run.sh` scripts are already available, but feel free to modify them to fit your need. Commit and push them to GitHub if you change them !

If you plan to use a different build system, e.g., make, contact Dr. Alessio Gambi and be sure all the necessary files are committed to GitHub. Be also sure that your build file can also feed inputs to your program while running it.

### Valid submissions

Be sure that whatever you submit passes **all the public tests**; otherwise, the corresponding task will not be considered correct.
Public tests can be found at: [https://github.com/se2p/ps-2019-assignment-03-public-tests](https://github.com/se2p/ps-2019-assignment-03-public-tests)


A valid submission must pass all the public tests **on the continuous testing/integration system**. That is, if your solution passes the public tests on your local installation this might not be enough!

### Code comments
Please add a reasonable number of comments to your code. Code comments help in understanding the reasoning behind your program and, more importantly,  explain (potential) style violations. The default behavior in the absence of code comments is to consider your solution poor/wrong.

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

The following examples make assumptions on the number of digits, the maximum number of attempts, and the content of the `secret.code` file. However, those assumptions might not be the same made by the private and public tests. In other words, do not give it for granted that tests will use the values that you see below !! The output that the following examples show, must be matched **literally** by the output produced by your programs, modulo new line characters (Windows uses different line endings that unix/linux/Mac Os). 

### Easy Peasy
The following example assume that 
    the number of digits is **4**, 
    the `secret.code` file contains the string 1234 (hence, `secret_code` is `1234`), and
    the maximum number of attempts is **10**.
    

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
The following example assume that 
    the number of digits is **4**, 
    the `secret.code` file contains the string 1234 (hence, `secret_code` is `1234`), and
    the maximum number of attempts  is **3**.

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
The content of the `secret.code` must be interpreted on the base of the configured number of digits: If the content of the file is longer than number of digits, you need to *clip it*; if the content of the file is smaller you need to *pad it with leading zeros*; otherwise, the content of the file should correspond to the value of the secret code.

Clipping case:

- Number of digits is 1, `secret.code` contains 1234, hence `secret_code` is `4`
- Number of digits is 2, `secret.code` contains 1234, hence `secret_code` is `34`
- Number of digits is 3, `secret.code` contains 1234, hence `secret_code` is `234`

Common case:

- Number of digits is 4, `secret.code` contains 1234, hence `secret_code` is `1234`

Padding case:

- Number of digits is 5, `secret.code` contains 1234, hence `secret_code` is `01234`
- Number of digits is 6, `secret.code` contains 1234, hence `secret_code` is `001234`

### Corner Cases (Input Validation)
Not all the inputs that a user can provide is valid, so you need to validate and possibly reject invalid inputs. Inputs that contain letters, punctuations, special characters are trivially invalid.

For example, inputs such as `12a4`, `...3`, and `1 2 3 4` are not valid.

However, pay attention that some combinations of spaces and tabs are allowed, hence produce perfectly valid inputs. In particular, sequences of spaces and tabs that appear before the first DIGIT and after the LAST digit do not invalidate the input. Inputs become invalid, only if spaces and tabs appear BETWEEN digits.

The following examples, which assume that the number of digit is 3, should clarify the concept:

- The input: `[:space:]` `[:space:]` `[:space:]` `1` `1` `1` is **valid**
- The input: `[:space:]` `1` `1` `1` `[:tab:]` is **valid**
- The input: `1` `1` `1` `[:tab:]` `[:space:]`is **valid**

But

- The input `1` `[:space:]` `1` `1` is **NOT valid**
- The input `1` `[:tab:]` `1` `1` `[:tab:]` is **NOT valid**
- The input `1` `1` `[:tab:]` `[:tab:]` `1` is **NOT valid**

## Exceptional cases
**UNLESS THE FOLLOWING IS NOT ALREADY DEALT WITH BY THE PROGRAMMING STYLE**

If your program cannot find the `secret.code` file, either because the file is missing or because your program is not looking in the right place, adopt a safe defensive style: stop the program and raise an exception so the problem becomes evident as soon as possible. 

Avoid hard-coding predefined values for the secret code in your program

## Code Conventions and File Naming
Please stick to the file names and the interfaces when provided with the assignment.
You can add more files, but you cannot remove or rename the existing ones since that will brake automated testing and eventually impact your grade !

## References
[1] [https://en.wikipedia.org/wiki/Mastermind_(board_game)](https://en.wikipedia.org/wiki/Mastermind_(board_game))

[2] [https://en.wikipedia.org/wiki/Hamming_distance](https://en.wikipedia.org/wiki/Hamming_distance)
