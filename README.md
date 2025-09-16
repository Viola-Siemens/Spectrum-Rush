# Product Requirements Document (PRD): Spectrum Rush

**Version:** 0.0.1+1.20.1

**Date:** 2025/09/15

**Author:** Liu Dongyu, Fuli_kk, ...

**Status:**

- [x] Draft
- [ ] In Review
- [ ] Approved

**Issue Tracker:** [https://github.com/Viola-Siemens/Spectrum-Rush/issues](https://github.com/Viola-Siemens/Spectrum-Rush/issues)

---

### **1. Overview & Vision**

This document outlines the requirements for the development of the **Spectrum Rush** mod for Minecraft. It aims to create a fast-paced, competitive mini-game centered around shearing colored sheep, testing players' speed, memory, and coordination.

**1.1. Problem Statement**
Minecraft lacks built-in, structured mini-games that are quick to set up and offer a clear, objective-based competitive experience. While existing mods add complexity or new mobs, there is a gap for a simple, engaging, and visually distinct game that can be enjoyed in short sessions with friends.

**1.2. Vision Statement**
To create a vibrant and chaotic mini-game mod where players race against time and each other to collect the right colored wool, transforming the peaceful act of shearing into a thrilling competition.

**1.3. Goals & Success Metrics**

- **Player Adoption:** 2,500 downloads on CurseForge & Modrinth within the first month.
- **Community Sentiment:** Positive initial reception with an average rating of 4.5/5 on mod platforms.
- **Content Creator Adoption:** Feature in at least 1 YouTube or Bilibili video from a top-50 Minecraft mini-game or modded content creator within the first two weeks.
- **Server Adoption:** Be included in the mini-game rotation of at least 5 public modded Minecraft servers.

### **2. User Personas & Stories**

**2.1. Persona 1: The Competitive Chris**

- **Playstyle:** Loves PvP and any game with a clear winner. Enjoys leaderboards and optimizing strategies for victory.
- **Goals:** To win. To have the highest score and be recognized as the best.
- **Frustrations:** Mini-games that are too luck-based or have unclear rules. Long setup times.

**2.2. Persona 2: The Casual Chloe**

- **Playstyle:** Prefers cooperative or light-hearted competitive play. Enjoys fun visuals and simple, accessible mechanics.
- **Goals:** To have a good time with friends without high pressure. To enjoy the colorful and chaotic atmosphere.
- **Frustrations:** Complex games with steep learning curves. Getting eliminated early and having to wait.

**2.3. User Stories**

- **As** Chris, **I want** a clear and prominent UI displaying the target color and my current score **so that I can** quickly strategize and track my performance against others.
- **As** Chris, **I want** the game to start automatically once all players are ready **so that I can** minimize downtime between rounds.
- **As** Chloe, **I want** the target color to be communicated through both a text message and a prominent visual icon (e.g., a colored wool block on the HUD) **so that I can** easily remember what I'm looking for.
- **As** Chloe, **I want** sheep to be easy to find and not de-spawn **so that I can** always participate throughout the entire round.
- **As** a server admin, **I want** to be able to configure the number of sheep, round duration, and color set **so that I can** tailor the game experience for my community.

### **3. Competitive Analysis (Mod Landscape)**

Nope.

### **4. Core Features & Modules**

![](docs/Product%20Architecture%20Diagram.png)

#### 4.1. Game Setup & Logic

- **Description:** The core system that initializes and manages the mini-game session.
- **Features:**
  - **World Generation:** On world creation or via a command, the mod randomly places 100 sheep in the world.
  - **Color Assignment:** Sheep are assigned one of 10 colors (e.g., White, Orange, Magenta, Light Blue, Yellow, Lime, Pink, Gray, Light Gray, Cyan). Each color has exactly 10 sheep.
  - **Game Trigger:** The game is started by a player (likely an OP) using a command (e.g., `/spectrumrush start`).
  - **Scoring System:** The mod tracks wool counts per player per color. Only wool collected *after* the corresponding color is requested counts towards that color's score.

#### 4.2. Gameplay Loop

- **Description:** The real-time loop that drives the player experience.
- **Features:**
  - **Instruction Period:** Every X seconds (e.g., 60 seconds), all players receive a clear instruction (title/chat message + HUD icon) to collect a specific color of wool (e.g., "COLLECT: YELLOW WOOL!").
  - **Collection Phase:** Players must find and shear sheep of the requested color before the next instruction is given.
  - **Wool Validation:** Wool items in the player's inventory are automatically counted for the currently active color. Shearing a sheep of the wrong color grants no points for the current round.
  - **Round End:** After a predetermined number of rounds or total time, the game ends.
  - **Victory Condition:** The player with the most wool collected for the *final requested color* is declared the winner. (Alternative: Total wool across all colors. The defined condition adds a strategic "final sprint" element).

#### 4.3. Player Feedback

- **Description:** Provides critical information to the player.
- **Features:**
  - **HUD Elements:**
    - Large, clear icon of the currently requested wool color.
    - Timer counting down to the next instruction.
    - Current player's score for the *active* color.
  - **Chat Messages:** Announcements for game start, instruction changes, and final scores.
  - **Scoreboard:** An optional side-scoreboard tracking all players' scores for the active color or total collected.

#### 4.4. Configuration

- **Description:** Allows server admins to customize the experience.
- **Features:** (Accessible via config file)
  - `numberOfSheeps`: Total sheep to spawn (default: 100).
  - `colorsUsed`: Array of dye colors to use (default: 10 colors).
  - `roundDuration`: Time between instructions in seconds (default: 60).
  - `totalRounds`: Number of instructions before game ends (default: 10).

### **5. Task Priority**

<table>
    <tr>
        <th>Module</th>
        <th>Sub-Module</th>
        <th>Priority</th>
        <th>Status</th>
    </tr>
    <tr>
        <td rowspan="4">Game Setup &amp; Logic</td>
        <td>World Generation</td>
        <td><span style="color:red;font-weight:900">P0</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Color Assignment</td>
        <td><span style="color:red;font-weight:900">P0</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Game Trigger</td>
        <td><span style="color:deepskyblue">P2</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Scoring System</td>
        <td><span style="color:orchid;font-weight:600">P1</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td rowspan="5">Gameplay Loop</td>
        <td>Instruction Period</td>
        <td><span style="color:red;font-weight:900">P0</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Collection Phase</td>
        <td><span style="color:red;font-weight:900">P0</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Wool Validation</td>
        <td><span style="color:red;font-weight:900">P0</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Round End</td>
        <td><span style="color:deepskyblue">P2</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td>Victory Condition</td>
        <td><span style="color:deepskyblue">P2</span></td>
        <td><input type="checkbox"/></td>
    </tr>
    <tr>
        <td rowspan="3">Player Feedback</td>
        <td>HUD Elements</td>
        <td>P3</td>
        <td><input type="checkbox"/></td>
    </tr>
    <tr>
        <td>Chat Messages</td>
        <td>P3</td>
        <td><input type="checkbox"/></td>
    </tr>
    <tr>
        <td>Scoreboard</td>
        <td><span style="color:deepskyblue">P2</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
    <tr>
        <td rowspan="3">Configuration</td>
        <td>Configuration</td>
        <td><span style="color:deepskyblue">P2</span></td>
        <td><input type="checkbox" checked/></td>
    </tr>
</table>




### **6. Technical Architecture**

- **Target Minecraft Version:** 1.20.1
- **Mod Loaders:** Forge.
- **Java Version:** JDK 17
- **Key Dependencies:** Nope.
