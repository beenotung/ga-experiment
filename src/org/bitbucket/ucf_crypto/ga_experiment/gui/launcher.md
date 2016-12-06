# Launcher (GUI)

## Demo Description
Show substitution on image encryption.

 - each pixel use 3 byte for RGB
 - each byte is mapped to another byte during encryption
 
Therefore, the gene is 256 byte as reversed mapping table.

## Keyboard Control
| Key | Meaning |
|---|---|
| Esc | Quit |
| ] | Increase population |
| [ | Decrease population |
| E | Encrypt (Ready to start GA) |
| P | Play / Pause the GA (auto mode |
| R | Reset GA |
| M | + P Mutate GA |
| N | - P Mutate GA |
| K | + A Mutate GA |
| J | - A Mutate GA |
| S | Step GA (a complete round) |
| C | Compare Mode |
| G | GA Mode |

## Compare Mode
 - left hand side
   - show source image
 - right hand side
   - show encrypted image

## GA Mode
 - show the genes in defending fitness order
   (best at left top)

## Progress
 - Display for the modes
   - [x] Compare Mode
   - [x] GA Mode
 - Connect GUI to GA
   - [x] Step
   - [x] Display population
   - [ ] Trigger extra mutation
   - [x] Change population size
