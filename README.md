# GA experiment

## Goal
Compare GA with brute-force for cracking

## Involve crypto algorithms
 - [x] Shift
 - [x] Affine
 - [x] Substitution
 - [ ] ~~Playfair~~
 - [ ] ~~ADFGVX~~
 - [ ] ~~Transposition~~

## Test Scope
 - English plaintext (A-Z)
 - Alphabet + number
 - Raw Data (0..255) (rgb)

## Test Setting
 1. 10 keys is generated randomly.
 2. For each key, generate 10 pairs of plaintext and cipher.
 3. Crack the key, both plaintext and cipher can be utilized.

## Remark
This project is deprecated, moving to [GA Art (Image generation)](https://github.com/beenotung/ga-art)
