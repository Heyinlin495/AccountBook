# 📒 个人记账本 (AccountBook)

一款简洁实用的 Android 个人记账应用，帮助你轻松记录日常收入与支出，掌握财务状况。

## ✨ 功能特性

### 📝 记账管理
- **添加记录** — 通过底部弹窗快速录入收入/支出
- **编辑记录** — 点击记录即可修改
- **删除记录** — 长按记录确认删除
- 每条记录包含：类型（收入/支出）、分类、金额、备注、时间

### 🏷️ 预设分类

| 支出分类 | 收入分类 |
|---------|---------|
| 🍜 餐饮 | 💰 工资 |
| 🚌 交通 | 💼 兼职 |
| 🛒 购物 | 📈 理财 |
| 🎮 娱乐 | 🧧 红包 |
| 🏠 住房 | 📦 其他 |
| 🏥 医疗 | |
| 📚 教育 | |
| 📦 其他 | |

每个分类拥有独立配色，视觉区分清晰。

### 📋 账单列表
- 按日期倒序展示所有记录
- **搜索过滤** — 支持按分类名称或备注内容实时搜索
- **月份筛选** — 通过日期选择器筛选特定月份
- **DiffUtil** — 高效列表更新，支持局部刷新

### 📊 统计分析
- **月度汇总** — 当月总收入、总支出、结余一目了然
- **饼图** — 支出分类占比分析，带百分比标签
- **折线图** — 收入 vs 支出月度趋势对比
- 支持前后月份切换导航

## 📱 界面预览

- **Material Design 3** 风格界面
- **深色模式** 自动适配
- **Edge-to-edge** 全屏沉浸式显示
- 收入绿色标识（+），支出红色标识（-）

## 🏗️ 技术架构

### 架构模式：MVVM + Repository

```
┌─────────────────────────────────────────┐
│                  UI Layer                │
│  ┌──────────┐  ┌──────────────────────┐ │
│  │  Bills   │  │    Statistics        │ │
│  │ Fragment  │  │    Fragment          │ │
│  └────┬─────┘  └──────────┬───────────┘ │
│       │                    │             │
│       │    ┌───────────┐   │             │
│       └────│ ViewModel │───┘             │
│            └─────┬─────┘                 │
├──────────────────┼───────────────────────┤
│            Data Layer                    │
│       ┌──────────┴──────────┐            │
│       │     Repository      │            │
│       └──────────┬──────────┘            │
│       ┌──────────┴──────────┐            │
│       │    Room DAO          │            │
│       └──────────┬──────────┘            │
│       ┌──────────┴──────────┐            │
│       │  SQLite Database     │            │
│       └─────────────────────┘            │
└─────────────────────────────────────────┘
```

### 项目结构

```
app/src/main/java/edu/guigu/accountbook/
├── MainActivity.kt                  # 主 Activity（TabLayout + ViewPager2）
├── data/
│   ├── model/Record.kt              # Room 实体类
│   ├── dao/RecordDao.kt             # 数据访问对象
│   ├── database/AppDatabase.kt      # Room 数据库单例
│   └── repository/RecordRepository.kt # 数据仓库
├── ui/
│   ├── adapter/
│   │   ├── MainPagerAdapter.kt      # ViewPager2 适配器
│   │   └── RecordAdapter.kt         # 记录列表适配器（DiffUtil）
│   ├── component/ChartMarkerView.kt # 图表标记视图
│   ├── dialog/AddEditRecordDialog.kt # 添加/编辑弹窗
│   ├── fragment/
│   │   ├── BillsFragment.kt         # 账单列表页
│   │   └── StatisticsFragment.kt    # 统计分析页
│   └── viewmodel/RecordViewModel.kt # ViewModel
└── util/DateUtils.kt                # 日期工具类
```

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Kotlin** | 2.2.10 | 开发语言 |
| **Room** | 2.7.1 | 本地数据库 ORM |
| **LiveData** | 2.8.7 | 可观察数据持有者 |
| **ViewModel** | 2.8.7 | 生命周期感知的 UI 数据管理 |
| **ViewPager2** | 1.1.0 | 可滑动标签页 |
| **MPAndroidChart** | 3.1.0 | 饼图 & 折线图 |
| **Material Design 3** | 1.13.0 | UI 组件库 |
| **ViewBinding** | — | 类型安全的视图绑定 |
| **Fragment KTX** | 1.8.6 | Fragment Result API |
| **KSP** | 2.3.2 | Room 注解处理 |

## 🔧 构建环境

| 配置项 | 值 |
|--------|-----|
| compileSdk | 36 |
| minSdk | 30 (Android 11) |
| targetSdk | 36 |
| Gradle | 9.4.1 |
| AGP | 9.2.1 |
| JVM Target | 11 |

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog (2023.1) 或更高版本
- JDK 11+
- Android SDK 36

### 构建步骤

```bash
# 1. 克隆项目
git clone https://github.com/Ajisuanji666/Android-MyApplication.git
cd Android-MyApplication

# 2. 使用 Android Studio 打开项目，或命令行构建
./gradlew assembleDebug

# 3. 安装到设备
./gradlew installDebug
```

## 📦 依赖说明

- **纯本地应用** — 无需网络权限，所有数据存储在本地 SQLite 数据库
- **JitPack 仓库** — MPAndroidChart 通过 JitPack 引入

## 📄 License

本项目仅供学习交流使用。

---

> 💡 如果这个项目对你有帮助，欢迎 ⭐ Star 支持！
