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
| [ ] | ] | Increase population |
| [ ] | [ | Decrease population |
| [x] | E | Encryp (Ready to start GA) |
| [ ] | P | Play / Pause the GA (auto mode |
| [ ] | R | Reset GA |
| [ ] | M | Mutate GA |
| [-] | S | Step GA (a complete round) |
| [ ] | C | Compare Mode |
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
