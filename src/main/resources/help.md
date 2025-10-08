# 📚 Liste des Effets de Potion et Enchantements

## 🧪 **Effets de Potion (PotionEffectType)**
Liste complète des effets disponibles pour les épées custom dans Minecraft 1.12.

### Effets Positifs
| Nom dans Config | Description | Utilisation Recommandée |
|----------------|-------------|------------------------|
| `SPEED` | Vitesse de déplacement augmentée | SELF - Mobilité |
| `INCREASE_DAMAGE` | Force - Augmente les dégâts | SELF - Attaque |
| `HEAL` | Soigne instantanément | SELF - Régénération |
| `JUMP` | Sauts plus hauts | SELF - Mobilité |
| `REGENERATION` | Régénération de vie | SELF - Survie |
| `DAMAGE_RESISTANCE` | Résistance aux dégâts | SELF - Défense |
| `FIRE_RESISTANCE` | Résistance au feu | SELF - Protection |
| `WATER_BREATHING` | Respiration sous l'eau | SELF - Exploration |
| `INVISIBILITY` | Invisibilité | SELF - Furtivité |
| `NIGHT_VISION` | Vision nocturne | SELF - Exploration |
| `HEALTH_BOOST` | Augmente la santé max | SELF - Survie |
| `ABSORPTION` | Coeurs d'absorption | SELF - Protection |
| `SATURATION` | Satiété instantanée | SELF - Survie |
| `LUCK` | Chance augmentée | SELF - Looting |
| `FAST_DIGGING` | Vitesse de minage | SELF - Utilité |

### Effets Négatifs (pour ENEMY)
| Nom dans Config | Description | Utilisation Recommandée |
|----------------|-------------|------------------------|
| `SLOW` | Ralentissement | ENEMY - Contrôle |
| `SLOW_DIGGING` | Fatigue de minage | ENEMY - Débuff |
| `HARM` | Dégâts instantanés | ENEMY - Dégâts |
| `CONFUSION` | Nausée | ENEMY - Désoriente |
| `BLINDNESS` | Cécité | ENEMY - Vision |
| `HUNGER` | Faim | ENEMY - Débuff |
| `WEAKNESS` | Faiblesse | ENEMY - Réduit dégâts |
| `POISON` | Poison | ENEMY - DoT |
| `WITHER` | Effet Wither | ENEMY - DoT |
| `LEVITATION` | Lévitation | ENEMY - Contrôle |
| `UNLUCK` | Malchance | ENEMY - Débuff |

### Exemples d'Utilisation
```yaml
# Épée vampire
#potion-effect:
#  type: HEAL
#  target: SELF
#  duration: 1
#  amplifier: 1
#  chance: 25

# Épée empoisonnée
#potion-effect:
#  type: POISON
#  target: ENEMY
 # duration: 100
#  amplifier: 2
#  chance: 50

# Épée de confusion
#potion-effect:
#  type: CONFUSION
#  target: ENEMY
#  duration: 200
#  amplifier: 1
#  chance: 100
```

---

## ⚔️ **Enchantements (Enchantment)**
Liste complète des enchantements disponibles pour les items custom.

### Enchantements d'Arme
| Nom dans Config | Description | Niveau Max | Compatible |
|----------------|-------------|-----------|-----------|
| `DAMAGE_ALL` | Tranchant (Sharpness) | 10+ | Épées |
| `DAMAGE_UNDEAD` | Châtiment (Smite) | 5 | Épées |
| `DAMAGE_ARTHROPODS` | Fléau des arthropodes | 5 | Épées |
| `KNOCKBACK` | Recul | 5+ | Épées |
| `FIRE_ASPECT` | Aura de feu | 2 | Épées |
| `LOOT_BONUS_MOBS` | Butin (Looting) | 3 | Épées |
| `SWEEPING_EDGE` | Affilage (1.11+) | 3 | Épées |

### Enchantements d'Armure
| Nom dans Config | Description | Niveau Max | Compatible |
|----------------|-------------|-----------|-----------|
| `PROTECTION_ENVIRONMENTAL` | Protection | 4 | Armures |
| `PROTECTION_FIRE` | Protection feu | 4 | Armures |
| `PROTECTION_FALL` | Protection chute | 4 | Bottes |
| `PROTECTION_EXPLOSIONS` | Protection explosion | 4 | Armures |
| `PROTECTION_PROJECTILE` | Protection projectile | 4 | Armures |
| `OXYGEN` | Respiration | 3 | Casque |
| `WATER_WORKER` | Affinité aquatique | 1 | Casque |
| `THORNS` | Épines | 3 | Armures |
| `DEPTH_STRIDER` | Agilité aquatique | 3 | Bottes |
| `FROST_WALKER` | Semelles givrantes | 2 | Bottes |

### Enchantements d'Outils
| Nom dans Config | Description | Niveau Max | Compatible |
|----------------|-------------|-----------|-----------|
| `DIG_SPEED` | Efficacité (Efficiency) | 5 | Outils |
| `SILK_TOUCH` | Toucher de soie | 1 | Outils |
| `LOOT_BONUS_BLOCKS` | Fortune | 3 | Outils |

### Enchantements d'Arc
| Nom dans Config | Description | Niveau Max | Compatible |
|----------------|-------------|-----------|-----------|
| `ARROW_DAMAGE` | Puissance (Power) | 5 | Arc |
| `ARROW_KNOCKBACK` | Frappe (Punch) | 2 | Arc |
| `ARROW_FIRE` | Flamme | 1 | Arc |
| `ARROW_INFINITE` | Infinité | 1 | Arc |

### Enchantements Universels
| Nom dans Config | Description | Niveau Max | Compatible |
|----------------|-------------|-----------|-----------|
| `DURABILITY` | Solidité (Unbreaking) | 10+ | Tous items |
| `MENDING` | Raccommodage (1.9+) | 1 | Tous items |
| `VANISHING_CURSE` | Malédiction disparition | 1 | Tous items |
| `BINDING_CURSE` | Malédiction liaison | 1 | Armures |

### Exemples d'Utilisation
```yaml
# Épée ultra puissante
#enchantments:
  - "DAMAGE_ALL:15"
  - "FIRE_ASPECT:2"
  - "KNOCKBACK:5"
  - "DURABILITY:10"
  - "LOOT_BONUS_MOBS:3"

# Pioche légendaire
#enchantments:
  - "DIG_SPEED:10"
  - "LOOT_BONUS_BLOCKS:5"
  - "DURABILITY:10"

# Arc surpuissant
#enchantments:
  - "ARROW_DAMAGE:10"
  - "ARROW_FIRE:1"
  - "ARROW_INFINITE:1"
  - "DURABILITY:5"
```

---

## 🎛️ **Configuration Debug**

Pour activer/désactiver les messages de debug dans le chat :

```yaml
settings:
  debug: false  # true = affiche les messages, false = silencieux
```

**Avec debug: true** :
```
§7[Debug] Effet activé sur vous-même: FIRE_RESISTANCE
§7[Debug] Effet appliqué à l'ennemi: POISON
```

**Avec debug: false** :
```
(aucun message)
```

---

## 📋 **Notes Importantes**

### Amplificateur (amplifier)
- `amplifier: 1` = Niveau I
- `amplifier: 2` = Niveau II
- etc.

### Durée (duration)
- En **ticks** (20 ticks = 1 seconde)
- `duration: 100` = 5 secondes
- `duration: 200` = 10 secondes
- `duration: 600` = 30 secondes

### Chance (chance)
- En **pourcentage** (0-100)
- `chance: 100` = Toujours
- `chance: 50` = 50% de chance
- `chance: 25` = 25% de chance

### Target (target)
- `SELF` = S'applique au joueur qui frappe
- `ENEMY` = S'applique à l'entité frappée

### Niveaux d'Enchantements
- Les niveaux normaux vont de 1 à 5
- Vous pouvez mettre **n'importe quel niveau** (ex: 10, 50, 100)
- `addUnsafeEnchantment()` permet de dépasser les limites vanilla

---

## 🔗 **Liens Utiles**

- **Documentation Bukkit** : https://hub.spigotmc.org/javadocs/bukkit/
- **Liste PotionEffectType** : https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html
- **Liste Enchantment** : https://helpch.at/docs/1.8/index.html?org/bukkit/enchantments/Enchantment.html

---

## ✨ **Exemples Créatifs**

### Épée du Vampire
```yaml
vampire_sword:
  name: "&4Épée Vampire"
  material: DIAMOND_SWORD
  enchantments:
    - "DAMAGE_ALL:8"
    - "DURABILITY:10"
  potion-effect:
    type: HEAL
    target: SELF
    duration: 1
    amplifier: 1
    chance: 30  # 30% de se soigner par hit
```

### Épée Berserker
```yaml
berserker_sword:
  name: "&cÉpée Berserker"
  material: GOLD_SWORD
  enchantments:
    - "DAMAGE_ALL:12"
    - "KNOCKBACK:3"
  potion-effect:
    type: INCREASE_DAMAGE
    target: SELF
    duration: 100
    amplifier: 2
    chance: 100  # Toujours actif 5 secondes
```

### Épée de Paralysie
```yaml
paralysis_sword:
  name: "&9Épée de Paralysie"
  material: IRON_SWORD
  enchantments:
    - "DAMAGE_ALL:6"
  potion-effect:
    type: SLOW
    target: ENEMY
    duration: 200
    amplifier: 5
    chance: 75  # 75% de ralentir énormément
```

### Épée du Chaos
```yaml
chaos_sword:
  name: "&5Épée du Chaos"
  material: DIAMOND_SWORD
  enchantments:
    - "DAMAGE_ALL:20"
    - "FIRE_ASPECT:2"
    - "KNOCKBACK:10"
  potion-effect:
    type: CONFUSION
    target: ENEMY
    duration: 300
    amplifier: 1
    chance: 100  # Désoriente toujours
```