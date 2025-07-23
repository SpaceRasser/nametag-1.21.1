# PlayerTagsMod

A lightweight NeoForge mod for Minecraft 1.21.1 that lets players set custom name‑prefix tags in‑game—no server restarts required. Tags can be colored, bolded, and even gradient‑shaded for maximum flair.

---

## 📦 Features

* **Custom prefix tags** per player, saved between sessions
* **Color tags**: `<red>`, `<dark_red>`, `<blue>`, `<aqua>`, `<gold>`, `<yellow>`, etc.
* **Bold text**: `<bold>…</bold>`
* **Hex‑gradient**: `<gradient:#RRGGBB:#RRGGBB>…</gradient>`
* Tags are applied via teams, so they show up next to your name in chat and over your head
* Works in single‑player (with cheats enabled) or on any NeoForge server

---

## ⚙️ Installation

1. Drop the `playertagsmod-<version>.jar` into your `mods/` folder.
2. Launch Minecraft with NeoForge MDK for **1.21.1**.
3. (Single‑player) Make sure **Allow Cheats** is ON in your world options.

---

## 🎮 Usage

### Basic commands

```txt
/tag <text…>         — set your prefix to “<text…>”  
/tag reset           — clear your tag  
```

You can mix plain text and any of the special tags below.

---

### Supported Tags

#### Named Colors

Wrap text in `<colorName>…</colorName>` to color it. Available names:

```
black, dark_blue, dark_green, dark_aqua,
dark_red, dark_purple, gold, gray, dark_gray,
blue, green, aqua, red, light_purple, yellow, white
```

**Example:**

```
/tag <dark_red>Danger!</dark_red> <aqua>Safe</aqua>
```

#### Bold

Make text bold:

```
/tag <bold>Important</bold>
```

#### Hex Gradient

Fade from one hex color to another over the length of your text:

```
/tag <gradient:#FF0000:#0000FF>Hot→Cold</gradient>
```

---

### Putting It All Together

You can combine bold and gradient for maximum effect:

```txt
/tag <gradient:#00FF00:#0000FF>Hello <bold>World</bold>!</gradient>
```

* “Hello ” fades from green to blue
* “World” is **bold** and part of the same gradient

---

## 💾 Persistence

Once you set a tag, it’s saved per‑player. When you rejoin or reload the world, your tag is re‑applied automatically.

---

## 🛡️ Permissions

* By default, only operators (OPs) or worlds with **cheats enabled** can use `/tag`.

---

## 📜 License

This mod is released under the MIT License. See [LICENSE.md](LICENSE.md) for details.

---

Enjoy your new custom name tags! 🎨
