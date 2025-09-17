- # 产品需求文档 (PRD)：Spectrum Rush

  **版本：** 0.0.1+1.20.1

  **日期：** 2025年9月15日

  **作者：** 刘冬煜、Fuli_kk、...

  **状态：**

  - [x] 草稿
  - [x] 审核中
  - [x] 已批准

  **问题跟踪器：** [https://github.com/Viola-Siemens/Spectrum-Rush/issues](https://github.com/Viola-Siemens/Spectrum-Rush/issues)

  ---

  ### **1. 概述与愿景**

  本文档概述了 **Spectrum Rush** 模组的开发需求。该模组旨在打造一款快节奏、竞技性十足的小游戏，以剪羊毛为核心，考验玩家的速度、记忆力和协调性。

  **1.1.问题陈述**
  Minecraft 缺乏内置的、结构化的迷你游戏，这些迷你游戏需要快速设置，并提供清晰的、基于目标的竞争体验。虽然现有的模组增加了复杂性或新生物，但缺少一款简单、引人入胜且视觉效果独特的游戏，适合与朋友在短时间内畅玩。

  **1.2. 愿景陈述**
  创建一个充满活力且充满混乱的迷你游戏模组，让玩家与时间赛跑，互相争夺收集正确颜色的羊毛，将平静的剪羊毛活动变成一场惊心动魄的比赛。

  **1.3. 目标与成功指标**

  - **玩家接受度**：第一个月在 CurseForge 和 Modrinth 上的下载量达到 2,500 次。
  - **社区情绪**：**初始反响积极，在模组平台上的平均评分为 4.5/5。
  - **内容创作者参与度**：在前两周内，至少有1个来自 Minecraft 排名前 50 的小游戏或模组内容创作者的 YouTube 或哔哩哔哩视频。
  - **服务器参与度**：至少有5个 Minecraft 公共模组服务器的小游戏轮换。

  ### **2. 用户角色与故事**

  **2.1. Persona 1：好胜的Chris**

  - **游戏风格**：喜欢PvP以及任何胜负分明的游戏。喜欢排行榜并优化策略以赢得胜利。
  - **目标**：赢得胜利。获得最高分并被公认为最强者。
  - **挫败感**：过于依赖运气或规则不明确的小游戏。设置时间过长。

  **2.2. Persona 2：休闲的Chloe**

  - **游戏风格**：喜欢合作或轻松的竞技游戏。喜欢有趣的视觉效果和简单易上手的机制。
  - **目标**：与好友畅享轻松愉快的游戏时光，无需承受太大压力。享受丰富多彩、精彩纷呈的氛围。
  - **挫败感**：游戏复杂，学习难度高。早早出局，不得不等待。

  **2.3. 用户故事**

  - **作为** Chris，**我希望**有一个清晰醒目的用户界面，能够显示目标颜色和我当前的分数，**这样我就可以**快速制定策略并追踪我与其他玩家的表现。
  - **作为** Chris，**我希望**游戏能够在所有玩家准备就绪后自动开始，**这样我就可以**最大限度地减少回合之间的停机时间。
  - **作为** Chloe，**我希望**目标颜色能够通过文字信息和醒目的视觉图标（例如，HUD 上的彩色羊毛块）来传达，**这样我就可以**轻松记住自己要寻找的内容。
  - **作为** Chloe，**我希望**羊容易找到，并且不会消失，**这样我就能**在整个回合中始终参与游戏。
  - **作为**服务器管理员，**我希望**能够配置羊的数量、回合时长和颜色设置，**这样我就能**为我的社区定制游戏体验。

  ### **3. 竞品分析（模组优势）**

  无。

  ### **4. 核心功能和模块**

  ![](docs/Product%20Architecture%20Diagram.png)

  #### 4.1. 游戏设置和逻辑

  - **描述**：初始化和管理迷你游戏会话的核心系统。
  - **功能**：
  - **世界生成**：在创建世界或通过命令创建世界时，模组会随机在世界中放置 100 只羊。
  - **颜色分配**：绵羊会被分配10种颜色中的一种（例如：白色、橙色、洋红色、浅蓝色、黄色、黄绿色、粉红色、灰色、浅灰色、青色）。每种颜色对应10只绵羊。
  - **游戏触发器**：游戏由玩家（可能是原版玩家）使用命令（例如：/spectrumrush start）开始。
  - **计分系统**：本模组会记录每位玩家每种颜色的羊毛数量。只有在指定颜色后收集的羊毛才会计入该颜色的分数。

  #### 4.2. 游戏循环

  - **描述**：驱动玩家体验的实时循环。
  - **特色**：
  - **指令阶段**：每隔 X 秒（例如 60 秒），所有玩家都会收到一条清晰的指令（标题/聊天消息 + HUD 图标），指示玩家收集特定颜色的羊毛（例如，“收集：黄色羊毛！”）。
  - **收集阶段**：玩家必须在收到下一个指令之前找到并剪下指定颜色的羊毛。
  - **羊毛验证**：玩家物品栏中的羊毛物品将自动计入当前活动颜色。剪下错误颜色的羊毛不会在当前回合获得任何分数。
  - **回合结束**：在预定回合数或总时间后，游戏结束。
  - **胜利条件**：收集到*最终指定颜色*羊毛最多的玩家获胜。
  - （替代方案：所有颜色羊毛总数。定义的条件添加了一个战略性的“最后冲刺”元素）。

    #### 4.3. 玩家反馈

    - **描述**：向玩家提供关键信息。
    - **功能**：
    - **HUD 元素**：
    - 当前请求羊毛颜色的大而清晰的图标。
    - 计时器倒计时到下一个指令。
    - 当前玩家*活动*颜色的得分。
    - **聊天消息**：游戏开始、指令更改和最终得分的公告。
    - **记分牌**：可选的侧记分牌，用于跟踪所有玩家活动颜色或收集到的总分数。

    #### 4.4. 配置

    - **描述**：允许服务器管理员自定义体验。
    - **功能**：（可通过配置文件访问）
    - `numberOfSheeps`：待生成的羊总数（默认值：100）。
    - `colorsUsed`：要使用的染料颜色数组（默认值：10 种颜色）。
    - `roundDuration`：指令间隔时间（以秒为单位）（默认值：60）。
    - `totalRounds`：游戏结束前的指令数（默认值：10）。

    ### **5. 任务优先级**

    <table>
    <tr>
    <th>模块</th>
    <th>子模块</th>
    <th>优先级</th>
    <th>状态</th>
    </tr>
    <tr>
    <td rowspan="4">游戏设置与逻辑</td>
    <td>世界生成</td>
    <td><span style="color:red;font-weight:900">P0</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>颜色分配</td>
    <td><span style="color:red;font-weight:900">P0</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>游戏触发器</td>
    <td><span style="color:deepskyblue">P2</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>评分系统</td>
    <td><span style="color:orchid;font-weight:600">P1</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td rowspan="5">游戏循环</td>
    <td>指令阶段</td>
    <td><span style="color:red;font-weight:900">P0</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>收集阶段</td>
    <td><span style="color:red;font-weight:900">P0</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>羊毛验证</td>
    <td><span style="color:red;font-weight:900">P0</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>回合结束</td>
    <td><span style="color:deepskyblue">P2</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>胜利条件</td>
    <td><span style="color:deepskyblue">P2</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td rowspan="3">玩家反馈</td>
    <td>HUD元素</td>
    <td>P3</td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>聊天消息</td>
    <td>P3</td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td>记分牌</td>
    <td><span style="color:deepskyblue">P2</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    <tr>
    <td rowspan="3">配置</td>
    <td>配置</td>
    <td><span style="color:deepskyblue">P2</span></td>
    <td><input type="checkbox"/></td>
    </tr>
    </table>

    ### **6. 技术架构**

    - **目标 Minecraft 版本**：1.20.1
    - **Mod 加载器**：Forge。
    - **Java 版本**：JDK 17
    - **关键依赖项**：无。
