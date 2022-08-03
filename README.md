# GA experiment
Group 11

## Goal
Prove the pros and cons of using GA to crack crypto system.
Find the turning point when GA is better traditional cryptanalysis.

## Involve crypto algorithms
 - [x] Shift
 - [x] Affine
 - [x] Substitution
 - [ ] Playfair
 - [ ] ADFGVX
 - [ ] Transposition

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
    1. [x] Use GA to crack the key, measure time used
    2. [x] Use brute-force to crack the key, stop when excess 10 seconds.
    3. [ ] Use traditional cryptanalysis to crack the key, stop when excess 1.5 times time used by GA
 3. [ ] Combination of two to three algorithms
    1. [ ] Use GA to crack the key, measure time used
    2. [ ] Use traditional cryptanalysis to crack the key, stop when excess 1.5 times time used by GA
 4. [ ] Adjust test size to find the breaking point of GA

## Demo Video
- ga-image-crypto.mp4
on
[ipfs](https://gateway.ipfs.io/ipfs/QmWZBSrYkn9B2npAacRpJssdW6pkDbd5Mazpr4nuvtUL2s),
[zeronet-1](https://zero.acelewis.com/#1uPLoaDwKzP6MCGoVzw48r4pxawRBdmQc/data/users/1DLBhCccN3MbM5YmzNS2UfBzDkEZkGZtVg/1514136304-ga-image-crypto.mp4),
[zeronet-2](https://zero.acelewis.com/#149EPBN4hQRpwj5TcoPETepnoXHCUXzaCN/data/users/1DLBhCccN3MbM5YmzNS2UfBzDkEZkGZtVg/1514137192-ga-image-crypto.mp4),
[onedrive](https://1drv.ms/v/s!Aiv60SsDASnNhLcKQwHn1f_0A9vEFQ)
- ga-text.mp4
on
[ipfs](https://gateway.ipfs.io/ipfs/QmdQwzfPBiB25eoaUxjmxo7ANbsAiKkJkmVdabBnaguxXw),
[zeronet](https://zero.acelewis.com/#149EPBN4hQRpwj5TcoPETepnoXHCUXzaCN/data/users/1DLBhCccN3MbM5YmzNS2UfBzDkEZkGZtVg/1514137525-ga-text.mp4),
[onedrive](https://1drv.ms/v/s!Aiv60SsDASnNhLcItEvLY9gPp_QDIA)
- ga-text.mkv
on
[ipfs](https://gateway.ipfs.io/ipfs/QmeVhWx8U81XgnXkVktfSuByefpov6fYFGvUwW7L1nsLbA),
[onedrive](https://1drv.ms/u/s!Aiv60SsDASnNhLcJVDSelnVg1WdRng)
