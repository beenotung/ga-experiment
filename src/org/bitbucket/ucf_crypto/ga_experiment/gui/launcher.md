# Launcher (GUI)

## Demo Description
Show substitution on image encryption.

 - each pixel use 3 byte for RGB
 - each byte is mapped to another byte during encryption
 
Therefore, the gene is 256 byte as reversed mapping table.

## Keyboard Control
|impl | key | meaning |
|---|---|---|
| [x] | Esc | Quit |
| [x] | ] | Increase population |
| [x] | [ | Decrease population |
| [x] | E | Encryp (Ready to start GA) |
| [x] | P | Play / Pause the GA (auto mode |
| [x] | R | Reset GA |
| [x] | M | + P Mutate GA |
| [x] | N | - P Mutate GA |
| [x] | K | + A Mutate GA |
| [x] | J | - A Mutate GA |
| [x] | S | Step GA (a complete round) |
| [x] | C | Compare Mode |
| [x] | G | GA Mode |

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
   - [ ] Compare Mode
   - [ ] GA Mode
 - Connect GUI to GA
   - [ ] Step
   - [ ] Display population
   - [ ] Trigger extra mutation
   - [ ] Change population size
