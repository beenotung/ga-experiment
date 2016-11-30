# GA experiment
Group 11

## Goal
Prove the pros and cons of using GA to crack crypto system.
Find the turning point when GA is better traditional cryptanalysis.

## Involve crypto algorithms
 - Shift
 - Affine
 - Substitution
 - Playfair
 - ADFGVX
 - Transposition

## Test Scope
 - English plaintext (A-Z)
 - Alphabet + number
 - Raw Data (0..255)

## Test Setting
 1. 10 keys is generated randomly.
 2. For each key, generate 10 pairs of plaintext and cipher.
 3. Crack the key, both plaintext and cipher can be utilized.

## Todo
 1. [x] Implement GA bare-bone
 2. [ ] Single algorithm
    1. [ ] Use GA to crack the key, measure time used
    2. [ ] Use traditional cryptanalysis to crack the keyl, stop when excess 1.5 times time used by GA
 3. [ ] Combination of two to three algorithms
    1. [ ] Use GA to crack the key, measure time used
    2. [ ] Use traditional cryptanalysis to crack the key, stop when excess 1.5 times time used by GA
 4. [ ] Adjust test size to find the breaking point of GA
