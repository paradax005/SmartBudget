# SmartBudget Premium Design System 2026
## Ultra-Modern Fintech Visual Language

---

## 🎨 Design Philosophy

**"Calm, confident, premium, slightly futuristic"**

SmartBudget 2026 embodies the aesthetic of holding a black Amex for your personal finances. Every pixel is intentional, every interaction is delightful, every animation is purposeful. This is not just a finance app—it's a premium financial companion.

---

## 🌈 Color System

### Primary Brand Palette

**Deep Indigo–Violet Gradient** (Primary Actions, Hero Elements)
- Start: `#4F46E5` (Indigo-600)
- Mid: `#7C3AED` (Violet-600)  
- End: `#C084FC` (Fuchsia-400)
- Usage: Total Balance card, FAB, primary buttons, active states

**Emerald Gradient** (Income, Success, Positive Values)
- Start: `#10B981` (Emerald-500)
- End: `#34D399` (Emerald-400)
- Usage: Income transactions, positive net amounts, success states

**Crimson Gradient** (Expenses, Warnings, Negative Values)
- Start: `#EF4444` (Red-500)
- End: `#F87171` (Red-400)
- Usage: Expense transactions, exceeded budgets, error states

**Electric Cyan** (Interactive Accents, Highlights)
- Color: `#06B6D4` (Cyan-500)
- Glow: `rgba(6, 182, 212, 0.4)` with 8dp blur
- Usage: Links, secondary actions, focus states, shimmer effects

### Background & Surface

**Dark Mode (Primary)**
- Background: `#000000` (Pure Black - OLED optimized)
- Surface: `rgba(255, 255, 255, 0.05)` with `backdrop-filter: blur(20px)`
- Border: `rgba(255, 255, 255, 0.08)` (1px ultra-thin)

**Light Mode**
- Background: `#FFFFFF` (Pure White)
- Surface: `rgba(0, 0, 0, 0.03)` with `backdrop-filter: blur(20px)`
- Border: `rgba(0, 0, 0, 0.08)` (1px ultra-thin)

### Semantic Colors

- **Text Primary**: `#FFFFFF` (dark) / `#000000` (light)
- **Text Secondary**: `rgba(255, 255, 255, 0.6)` (dark) / `rgba(0, 0, 0, 0.6)` (light)
- **Text Tertiary**: `rgba(255, 255, 255, 0.4)` (dark) / `rgba(0, 0, 0, 0.4)` (light)
- **Divider**: `rgba(255, 255, 255, 0.05)` (dark) / `rgba(0, 0, 0, 0.05)` (light)

---

## ✍️ Typography

### Font Family
- **Primary**: Google Sans Variable (Android) / SF Pro Variable (iOS)
- **Monospaced Numbers**: JetBrains Mono / SF Mono for amounts

### Type Scale

**Display (Hero Numbers)**
- Size: 48–56sp
- Weight: 800–900 (Variable)
- Line Height: 1.1
- Letter Spacing: -0.02em
- Usage: Total Balance, large amounts

**Headline**
- Size: 32sp
- Weight: 700
- Line Height: 1.2
- Usage: Screen titles, major sections

**Title**
- Size: 24sp
- Weight: 600
- Line Height: 1.3
- Usage: Card titles, section headers

**Body Large**
- Size: 18sp
- Weight: 500
- Line Height: 1.5
- Usage: Important body text

**Body**
- Size: 16sp
- Weight: 400–500
- Line Height: 1.5
- Usage: Standard body text, descriptions

**Body Small**
- Size: 14sp
- Weight: 400
- Line Height: 1.4
- Usage: Secondary information, captions

**Label**
- Size: 12sp
- Weight: 600
- Line Height: 1.3
- Letter Spacing: 0.01em
- Usage: Labels, badges, metadata

### Amount Typography Rules
- Always use monospaced font for perfect alignment
- Medium–Bold weight (500–700)
- Negative letter spacing (-0.03em) for tight numbers
- Color: Semantic (green/red) or primary gradient

---

## 📐 Spacing System

**Base Unit**: 4dp

- **XS**: 4dp (tight spacing)
- **S**: 8dp (element spacing)
- **M**: 16dp (card padding, section spacing)
- **L**: 24dp (major section spacing)
- **XL**: 32dp (screen-level spacing)
- **XXL**: 48dp (hero section spacing)

**Screen Padding**: 20dp (generous negative space)

---

## 🎭 Component Library

### Cards

**Glassmorphic Card**
- Background: Frosted glass effect (backdrop-filter: blur(20px), opacity 0.7–0.8)
- Border: 1px solid with 0.08 opacity
- Border Radius: 24dp
- Elevation: Subtle inner shadow (inset 0 1px 2px rgba(0,0,0,0.1))
- Padding: 20dp
- Margin: 8dp between cards

**Hero Card (Total Balance)**
- Same as glassmorphic card
- Gradient overlay: Primary brand gradient (45° angle)
- Shimmer animation: Subtle aurora effect moving across gradient
- Height: 180dp minimum
- Special: Slight scale animation on appear (0.95 → 1.0)

### Buttons

**Primary Button (Filled)**
- Background: Primary brand gradient
- Height: 56dp
- Border Radius: 16dp
- Typography: Body Large, Weight 600
- Padding: 20dp horizontal
- Shadow: 0 8px 24px rgba(79, 70, 229, 0.3)
- States:
  - Normal: Full opacity
  - Pressed: Scale 0.95, opacity 0.9
  - Disabled: Opacity 0.3, no gradient

**Secondary Button (Outlined)**
- Background: Transparent
- Border: 1.5px solid, primary color, 0.6 opacity
- Height: 56dp
- Border Radius: 16dp
- Typography: Body Large, Weight 600
- States: Same as primary

**Text Button**
- Background: Transparent
- Typography: Body, Weight 600, primary color
- Padding: 12dp horizontal
- States: Opacity change on press

**FAB (Floating Action Button)**
- Size: 72dp × 72dp
- Background: Primary brand gradient
- Border Radius: 20dp (slightly less than full circle for modern look)
- Icon: 32dp, white
- Shadow: 0 12px 32px rgba(79, 70, 229, 0.4)
- Pulsing Glow: Subtle scale animation (1.0 → 1.05) when actionable
- Position: 24dp from edges

### Chips

**Filter/Choice Chip**
- Shape: Pill (fully rounded)
- Height: 40dp
- Padding: 16dp horizontal
- Typography: Body Small, Weight 500
- Background: Transparent with border (1.5px, 0.3 opacity)
- Selected State:
  - Background: Primary gradient with 0.2 opacity
  - Border: Primary color, full opacity
  - Glow: 0 0 12px primary color at 0.4 opacity
- Border Radius: 20dp (fully rounded)

### Input Fields

**Text Input (Material 3 Style)**
- Background: Frosted glass surface
- Border: 1px, 0.08 opacity, rounded 16dp
- Height: 64dp
- Padding: 20dp horizontal, 16dp vertical
- Typography: Body, Weight 400
- Focus State:
  - Border: 2px, primary color, full opacity
  - Glow: 0 0 8px primary color at 0.3 opacity
- Error State: Border and glow in crimson gradient

### Progress Bars

**Budget Progress Bar**
- Height: 12dp
- Border Radius: 6dp (rounded caps)
- Background: Surface color with 0.2 opacity
- Fill: Multi-color gradient
  - 0–75%: Emerald gradient
  - 75–100%: Yellow (#FBBF24 → #FCD34D)
  - 100%+: Crimson gradient
- Animation: Smooth fill animation on load (300ms spring)

### Charts

**Pie Chart (3D Raised)**
- Depth Shadow: Multiple layers for 3D effect
  - Layer 1: 0 4px 8px rgba(0,0,0,0.2)
  - Layer 2: 0 8px 16px rgba(0,0,0,0.15)
- Interactive: Scale to 1.05 on touch, highlight with glow
- Colors: Category colors with 0.9 opacity
- Animation: Staggered entry (each slice appears with 50ms delay)

### Bottom Sheets

**Modal Bottom Sheet**
- Background: Frosted glass with blur(30px)
- Border Radius: 32dp top corners
- Grabber: 40dp × 4dp, rounded, 0.3 opacity, centered at top
- Backdrop: Dark overlay (rgba(0,0,0,0.5)) with blur
- Animation: Spring physics (stiffness 300, damping 30)

### Segmented Control

**Glass Segmented Control**
- Background: Frosted glass surface
- Border: 1px, 0.08 opacity
- Border Radius: 16dp
- Indicator: Liquid-like moving background (primary gradient, 0.3 opacity)
- Animation: Smooth spring transition (200ms)
- Height: 48dp

---

## 🎬 Animation & Interaction

### Spring Physics
- **Stiffness**: 300
- **Damping**: 30
- **Mass**: 1.0
- Applied to: Screen transitions, sheet animations, card appearances

### Shared Element Transitions
- Total Balance Card → Account Detail
- Category Pie Slice → Category Detail
- Transaction Item → Transaction Detail
- Duration: 400ms spring

### Micro-interactions

**Tap Feedback**
- Scale: 0.95 → 1.0 (100ms)
- Opacity: 1.0 → 0.8 → 1.0 (100ms)
- Haptic: Light impact

**Swipe Actions**
- Threshold: 25% of width
- Animation: Spring physics
- Haptic: Medium impact on completion

**Pull-to-Refresh**
- Indicator: Gradient shimmer spinner
- Animation: Rotate with scale pulse
- Haptic: Light impact on refresh

### Loading States

**Skeleton Shimmer**
- Background: Surface color
- Shimmer: Primary gradient moving across (2s duration, infinite)
- Border Radius: Matches target component
- Pulse: Subtle opacity animation (0.6 → 1.0)

---

## 📱 Screen Specifications

### 1. Splash Screen

**Layout**
- Full screen, centered content
- Background: Pure black (dark) / Pure white (light)

**Elements**
- App Logo: 120dp × 120dp, centered
- App Name: Display typography, weight 900, centered below logo
- Tagline (optional): Body Small, 0.6 opacity, below name

**Animation**
- Logo: Scale in (0.8 → 1.0) with spring, 300ms
- Fade out: 200ms when navigating

**Duration**: 1–2 seconds

---

### 2. Login Screen

**Layout**
- Centered vertical layout
- Padding: 32dp horizontal, 48dp vertical
- Background: Pure black/white

**Elements**
- Logo: 80dp × 80dp, centered top, 48dp margin bottom
- App Name: Headline, centered, 24dp margin bottom
- Email Input: Full width, 16dp margin bottom
- Password Input: Full width, 16dp margin bottom
- Login Button: Full width, 24dp margin top
- Register Link: Text button, centered, 16dp margin top

**States**
- Loading: Button shows shimmer spinner
- Error: Snackbar with crimson gradient background, frosted glass

---

### 3. Register Screen

**Layout**
- Same as Login Screen
- Scrollable (NestedScrollView)

**Elements**
- Same header as Login
- Full Name Input
- Email Input
- Password Input
- Confirm Password Input (optional)
- Register Button
- Login Link

**Validation**
- Real-time: Border glow changes to crimson on error
- Success: Border glow changes to emerald

---

### 4. Dashboard Screen

**Layout**
- Scrollable vertical layout
- Padding: 20dp horizontal
- Background: Pure black/white

**Header Section**
- Month/Year Selector: Glass chip style, 16dp margin bottom
- Toolbar: Minimal, menu icon only

**Hero Card: Total Balance**
- Full width, 180dp height
- Primary gradient background with shimmer
- Typography: Display (56sp), weight 900
- Label: "Total Balance" (Body Small, 0.6 opacity)
- Margin: 0dp top, 24dp bottom

**Quick Stats Cards (Horizontal Scroll)**
- 4 cards: Income, Expenses, Net, Budget Status
- Card size: 160dp × 120dp
- Glassmorphic style
- Horizontal scroll, 8dp spacing
- Margin: 16dp bottom

**Budget Alerts Section (Conditional)**
- Glass card with crimson gradient accent border
- Full width
- RecyclerView of alert items
- Margin: 16dp bottom
- Animation: Slide in from top when alerts exist

**Expenses by Category Chart**
- Glass card, 320dp height
- 3D pie chart inside
- "View All Categories" link (text button, top right)
- Margin: 16dp bottom

**Recent Transactions**
- Section header: Title typography, "View All" link
- RecyclerView of transaction items
- Each item: Glass card, 80dp height
- Margin: 24dp bottom

**FAB**
- Position: Bottom right, 24dp from edges
- Pulsing glow animation

---

### 5. Transactions List Screen

**Layout**
- App bar with filter icon
- Summary bar (sticky): Glass card, 80dp height
- RecyclerView with transactions

**Summary Bar**
- 3 columns: Income (emerald), Expenses (crimson), Net (primary gradient)
- Glass card style
- Typography: Title for amounts

**Filter Bottom Sheet**
- Full-width modal
- Date range pickers
- Type chips (All, Income, Expense)
- Category selector
- Account selector
- Apply/Clear buttons

**Transaction Items**
- Glass card, 96dp height
- Category icon: 48dp circle with gradient background
- Amount: Title typography, semantic color
- Date: Body Small, 0.6 opacity
- Swipe actions: Delete (crimson), Edit (primary)

---

### 6. Add/Edit Transaction Screen

**Layout**
- Scrollable form
- Padding: 20dp

**Transaction Type Selector**
- Glass segmented control
- Two options: Income (emerald accent), Expense (crimson accent)
- Full width, 48dp height

**Amount Input**
- Large, prominent
- Typography: Display (48sp) when focused
- Currency symbol: Body Large, 0.6 opacity

**Account Balance Preview**
- Glass card, appears when account selected
- Shows current balance and projected balance
- Gradient border (emerald if income, crimson if expense)

**Form Fields**
- Account selector: Glass dropdown
- Category selector: Glass dropdown (filtered by type)
- Description: Multi-line input
- Date: Date picker with glass style

**Save Button**
- Full width, primary gradient
- Disabled state: 0.3 opacity until valid

---

### 7. Accounts List Screen

**Layout**
- App bar with title
- Total balance card (optional, glass style)
- RecyclerView with account cards

**Account Cards**
- Glass card, 120dp height
- Account icon: 56dp circle, gradient background
- Account name: Title typography
- Account type: Body Small, 0.6 opacity
- Balance: Display typography (32sp), primary gradient
- Swipe to delete

---

### 8. Add/Edit Account Screen

**Layout**
- Scrollable form
- Same style as Add Transaction

**Form Fields**
- Account name
- Account type: Glass segmented control (Bank, Cash, Credit Card, Savings)
- Initial balance
- Currency selector

---

### 9. Categories List Screen

**Layout**
- App bar with title
- Filter chips: All, Income, Expense (glass style)
- RecyclerView with category cards

**Category Cards**
- Glass card, 96dp height
- Category icon: 64dp circle, emoji or custom icon
- Category name: Title typography
- Type badge: Pill shape, emerald or crimson gradient
- Swipe to delete (with warning if in use)

---

### 10. Add/Edit Category Screen

**Layout**
- Scrollable form

**Form Fields**
- Category name
- Type selector: Glass segmented control
- Icon picker: Full-screen modal with emoji grid
- Color picker: Full-screen modal with gradient palette

**Icon Picker Modal**
- Full screen, frosted glass backdrop
- Grid of emoji icons (8 columns)
- Search bar at top
- Selected: Scale 1.2 with glow

**Color Picker Modal**
- Full screen, frosted glass backdrop
- Gradient palette grid
- Custom hex input
- Preview card showing selected color

---

### 11. Budgets List Screen

**Layout**
- App bar with month/year selector
- Budget status summary card (glass style)
- RecyclerView with budget cards

**Budget Status Summary**
- Glass card
- Total budgeted, total spent, exceeded count
- Gradient progress indicator

**Budget Cards**
- Glass card, 140dp height
- Category icon and name
- Progress bar: Multi-color gradient (12dp height)
- Amounts: Title typography
- Status badge: Pill shape (On Track / Warning / Exceeded)
- Percentage: Body Small, semantic color

---

### 12. Add/Edit Budget Screen

**Layout**
- Scrollable form

**Form Fields**
- Category selector: Glass dropdown (expense categories only)
- Budget amount: Large input
- Month picker: Glass style
- Year picker: Glass style
- Current spending preview: Glass card (optional)

---

### 13. Statistics / Analytics Screen

**Layout**
- Scrollable vertical layout
- Month/year selector at top

**Summary Cards**
- 4 glass cards in 2×2 grid
- Total Income, Total Expenses, Net, Transaction Count
- Gradient accents

**Charts**
- Income by Category: 3D pie chart
- Expenses by Category: 3D pie chart
- Monthly Trend: Line chart with gradient fill
- Category Comparison: Bar chart with gradient bars

**Category Breakdown List**
- Glass cards for each category
- Amount, count, percentage
- Interactive: Tap to filter transactions

---

## 🧭 Navigation

### Bottom Navigation Bar

**Style**
- Background: Frosted glass with blur(20px)
- Height: 80dp (generous for thumb reach)
- Border: 1px top, 0.08 opacity
- Icons: 28dp, 0.6 opacity (inactive), 1.0 opacity (active)
- Labels: Label typography, 0.6 opacity (inactive), 1.0 opacity (active)
- Active indicator: Primary gradient dot (8dp) above icon

**Items** (5 max)
1. Overview (Dashboard)
2. Transactions
3. Budgets
4. Accounts
5. More (Settings/Menu)

### Side Drawer / Profile Sheet

**Style**
- Full-height sheet from left
- Frosted glass background
- Width: 280dp
- Header: User profile (optional)
- Items: Categories, Settings, Logout
- Animation: Slide in with spring physics

### FAB Placement

- **Dashboard**: Bottom right, 24dp from edges
- **Transactions**: Bottom right, 24dp from edges
- **Other screens**: No FAB (use bottom nav or drawer)

---

## 🎯 Interaction Patterns

### Haptic Feedback

- **Light Impact**: Tap on cards, buttons, chips
- **Medium Impact**: Swipe actions, form submission
- **Heavy Impact**: Delete confirmations, critical actions
- **Success**: Light + Medium (double tap pattern)
- **Error**: Medium + Heavy (double tap pattern)

### Gestures

- **Swipe Left**: Delete (with confirmation)
- **Swipe Right**: Edit (optional)
- **Long Press**: Context menu
- **Pull Down**: Refresh
- **Swipe Up**: Dismiss bottom sheet

### Loading States

- **Initial Load**: Skeleton shimmer with gradient
- **Refresh**: Pull-to-refresh with gradient spinner
- **Form Submit**: Button shows shimmer spinner, form dims to 0.7 opacity
- **Empty State**: Illustration with gradient accent, helpful message

---

## 🌓 Dark Mode Implementation

### Primary Mode
- Dark mode is the default and primary experience
- Optimized for OLED displays (true black #000000)
- All gradients maintain vibrancy
- Text contrast ratios: WCAG AAA (7:1 minimum)

### Light Mode
- Pure white background (#FFFFFF)
- Same component styles, inverted colors
- Gradients remain vibrant
- Reduced opacity for glass effects (0.5–0.6)

### System Integration
- Respects system theme preference
- Smooth transition animation (300ms) when switching
- All components adapt automatically

---

## 📐 Responsive Considerations

### Phone Sizes
- Small (360dp): Compact spacing, smaller cards
- Medium (411dp): Standard spacing
- Large (480dp+): Generous spacing, larger cards

### Tablet (Future)
- Two-column layouts for lists
- Side-by-side detail views
- Larger cards and charts

---

## 🎨 Asset Specifications

### Icons
- Format: SVG (vector) preferred, PNG @2x/@3x fallback
- Size: 24dp standard, 32dp for FAB, 48dp for hero icons
- Style: Outlined, 2dp stroke weight
- Color: Inherit from theme

### Illustrations
- Style: Minimal, geometric, gradient accents
- Format: SVG
- Usage: Empty states, onboarding

### Gradients
- All gradients use 45° angle (diagonal)
- Smooth color stops (no banding)
- Export as 9-patch or CSS linear-gradient

---

## ✅ Quality Checklist

- [ ] All screens use pure black/white backgrounds
- [ ] All cards use glassmorphic effect (blur + opacity)
- [ ] All gradients use primary brand colors
- [ ] All typography uses variable font weights
- [ ] All amounts use monospaced font
- [ ] All interactions have haptic feedback
- [ ] All animations use spring physics
- [ ] All loading states use gradient shimmer
- [ ] All error states use crimson gradient
- [ ] All success states use emerald gradient
- [ ] Dark mode is primary and OLED-optimized
- [ ] Contrast ratios meet WCAG AAA
- [ ] Touch targets are minimum 48dp
- [ ] All screens are accessible (screen reader support)

---

## 🚀 Implementation Notes

### Android
- Use `Material3` theme with custom color system
- Implement `BackdropFilter` for glass effects (API 31+)
- Use `MotionLayout` for complex animations
- Implement haptic feedback with `HapticFeedbackConstants`

### Performance
- Lazy load charts and heavy components
- Cache gradient bitmaps
- Use `RecyclerView` with view holders
- Optimize blur effects (consider RenderScript or native)

### Accessibility
- All interactive elements have content descriptions
- Support font scaling (up to 200%)
- High contrast mode support
- Screen reader announcements for dynamic content

---

**This design system represents the pinnacle of 2025–2026 fintech mobile design. Every detail is intentional, every interaction is delightful, every pixel is premium.**

