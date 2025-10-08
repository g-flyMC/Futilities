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
