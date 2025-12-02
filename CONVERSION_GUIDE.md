# React/TypeScript to Kotlin/Jetpack Compose Conversion Guide

This document shows how your React/TypeScript code was converted to Kotlin with Jetpack Compose.

## 🔄 Core Concept Mappings

### State Management

**React (TypeScript):**
```typescript
const [products, setProducts] = useState<Product[]>([]);
const [likedProducts, setLikedProducts] = useState<Set<number>>(new Set());
const [activeTab, setActiveTab] = useState('home');
```

**Kotlin (Compose):**
```kotlin
data class AppState(
    val products: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(),
    val activeTab: String = "home"
)

private val _state = MutableStateFlow(AppState())
val state: StateFlow<AppState> = _state.asStateFlow()
```

### API Calls

**React (TypeScript):**
```typescript
const fetchProducts = async () => {
  const response = await fetch('https://makeup-api.herokuapp.com/api/v1/products.json');
  const data = await response.json();
  setProducts(data);
};
```

**Kotlin (Compose):**
```kotlin
fun fetchProducts() {
    viewModelScope.launch {
        try {
            val products = MakeupApi.service.getProducts()
            _state.value = _state.value.copy(products = products)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```

## 📦 Component Conversions

### 1. Product Card

**React/TypeScript (ProductCard.tsx):**
```typescript
export function ProductCard({ product, isLiked, onToggleLike, colorIndex, onAddToCart }: ProductCardProps) {
  const [isAdded, setIsAdded] = useState(false);
  
  return (
    <div className="flex flex-col">
      <div className={`${bgColor} rounded-2xl p-3 relative`}>
        {/* Heart Icon - Top Left */}
        <button onClick={() => onToggleLike(product.id)}>
          <Heart className={isLiked ? 'fill-red-500' : 'text-gray-400'} />
        </button>
        
        {/* Plus Icon - Top Right */}
        <button onClick={handleAddToCart}>
          <Plus className="w-4 h-4" />
        </button>
        
        <img src={product.image_link} alt={product.name} />
      </div>
      <p>{product.name}</p>
    </div>
  );
}
```

**Kotlin/Compose (ProductCard.kt):**
```kotlin
@Composable
fun ProductCard(
    product: Product,
    isLiked: Boolean,
    onToggleLike: (Int) -> Unit,
    colorIndex: Int,
    onAddToCart: (Int) -> Unit
) {
    var isAdded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            colors = CardDefaults.cardColors(containerColor = bgColor)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Heart Icon - Top Left
                IconButton(
                    onClick = { onToggleLike(product.id) },
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                
                // Plus Icon - Top Right
                IconButton(
                    onClick = { onAddToCart(product.id) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = Icons.Default.Add)
                }
                
                AsyncImage(model = product.imageLink, contentDescription = product.name)
            }
        }
        Text(text = product.name ?: "")
    }
}
```

### 2. Bottom Navigation

**React/TypeScript (BottomNav.tsx):**
```typescript
export function BottomNav({ activeTab, onTabChange, cartCount }: BottomNavProps) {
  const navItems = [
    { id: 'home', icon: Home, label: 'Home' },
    { id: 'scan', icon: Camera, label: 'AI Scan', isCenter: true },
    { id: 'cart', icon: ShoppingCart, label: 'Cart' },
  ];

  return (
    <nav className="fixed bottom-0">
      {navItems.map((item) => (
        <button key={item.id} onClick={() => onTabChange(item.id)}>
          <Icon className={isActive ? 'text-pink-500' : 'text-gray-400'} />
          {item.id === 'cart' && cartCount > 0 && (
            <span className="badge">{cartCount}</span>
          )}
        </button>
      ))}
    </nav>
  );
}
```

**Kotlin/Compose (BottomNavBar.kt):**
```kotlin
@Composable
fun BottomNavBar(
    activeTab: String,
    onTabChange: (String) -> Unit,
    cartCount: Int
) {
    val navItems = listOf(
        NavItem("home", Icons.Default.Home, "Home"),
        NavItem("scan", Icons.Default.CameraAlt, "AI Scan", isCenter = true),
        NavItem("cart", Icons.Default.ShoppingCart, "Cart")
    )
    
    Surface(shadowElevation = 8.dp) {
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            navItems.forEach { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = item.icon,
                        tint = if (activeTab == item.id) Color.Pink else Color.Gray
                    )
                    if (item.id == "cart" && cartCount > 0) {
                        Badge { Text(text = cartCount.toString()) }
                    }
                }
            }
        }
    }
}
```

### 3. Home Screen

**React/TypeScript (HomeScreen.tsx):**
```typescript
export function HomeScreen({ onViewProducts, featuredProducts, onAddToCart, loading }: HomeScreenProps) {
  return (
    <div className="flex-1 relative">
      <img src="face-image.jpg" className="w-full h-full" />
      
      {/* AR Overlay */}
      <svg className="absolute inset-0">
        {/* SVG paths for face mesh */}
      </svg>
      
      <div className="absolute bottom-0">
        <p>Special for you</p>
        
        <div className="flex gap-3">
          {featuredProducts.map((product) => (
            <div key={product.id}>
              <img src={product.image_link} />
              <button onClick={() => onAddToCart(product.id)}>
                <Plus />
              </button>
            </div>
          ))}
        </div>
        
        <button onClick={onViewProducts}>View all products</button>
      </div>
    </div>
  );
}
```

**Kotlin/Compose (HomeScreen.kt):**
```kotlin
@Composable
fun HomeScreen(
    onViewProducts: () -> Unit,
    featuredProducts: List<Product>,
    onAddToCart: (Int) -> Unit,
    loading: Boolean
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = "face-image.jpg",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        // AR Overlay (simplified)
        Box(modifier = Modifier.fillMaxSize().background(gradient))
        
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
        ) {
            Text(text = "Special for you", color = Color.White)
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(featuredProducts) { product ->
                    Card {
                        AsyncImage(model = product.imageLink)
                        FloatingActionButton(onClick = { onAddToCart(product.id) }) {
                            Icon(imageVector = Icons.Default.Add)
                        }
                    }
                }
            }
            
            Button(onClick = onViewProducts) {
                Text(text = "View all products")
            }
        }
    }
}
```

## 🎨 Styling Conversions

### Tailwind CSS to Compose Modifiers

| React/Tailwind | Kotlin/Compose |
|----------------|----------------|
| `className="flex flex-col"` | `Column(modifier = Modifier)` |
| `className="flex flex-row"` | `Row(modifier = Modifier)` |
| `className="w-full h-full"` | `Modifier.fillMaxSize()` |
| `className="p-4"` | `Modifier.padding(16.dp)` |
| `className="rounded-2xl"` | `shape = RoundedCornerShape(16.dp)` |
| `className="bg-pink-400"` | `backgroundColor = Color(0xFFF472B6)` |
| `className="text-white"` | `color = Color.White` |
| `className="gap-3"` | `Arrangement.spacedBy(12.dp)` |

### Color Conversions

| Tailwind | Hex | Compose |
|----------|-----|---------|
| `pink-400` | `#F472B6` | `Color(0xFFF472B6)` |
| `pink-500` | `#EC4899` | `Color(0xFFEC4899)` |
| `red-500` | `#EF4444` | `Color(0xFFEF4444)` |
| `emerald-500` | `#10B981` | `Color(0xFF10B981)` |
| `gray-400` | `#9CA3AF` | `Color(0xFF9CA3AF)` |

## 📱 Key Differences

### 1. Image Loading

**React:**
- Uses `<img>` tag with `src` attribute
- `onError` handler for fallbacks

**Compose:**
- Uses Coil's `AsyncImage` composable
- Built-in placeholder and error handling

### 2. Lists/Grids

**React:**
- `.map()` to iterate arrays
- Manual grid with CSS Grid

**Compose:**
- `LazyRow`, `LazyColumn` for scrollable lists
- `LazyVerticalGrid` for grids
- `items()` function to render list items

### 3. Icons

**React:**
- lucide-react library
- `<Heart />`, `<Plus />` components

**Compose:**
- Material Icons built-in
- `Icons.Default.Favorite`, `Icons.Default.Add`

### 4. Effects

**React:**
```typescript
useEffect(() => {
  fetchProducts();
}, []);
```

**Compose:**
```kotlin
LaunchedEffect(Unit) {
    fetchProducts()
}
// Or in ViewModel init block
```

## 🔧 File Structure Comparison

### React/TypeScript
```
src/
├── App.tsx
├── components/
│   ├── ProductCard.tsx
│   ├── BottomNav.tsx
│   └── HomeScreen.tsx
└── styles/
    └── globals.css
```

### Kotlin/Compose
```
app/src/main/java/com/example/beautyapp/
├── MainActivity.kt
├── data/
│   └── Product.kt
├── network/
│   └── MakeupApiService.kt
├── viewmodel/
│   └── MainViewModel.kt
└── ui/
    ├── components/
    ├── screens/
    └── theme/
```

## ✅ Benefits of Kotlin/Compose

1. **Type Safety**: Stronger type system with compile-time checks
2. **No CSS**: Styling is done in Kotlin code
3. **State Management**: Built-in with StateFlow and ViewModel
4. **Native Performance**: Runs natively on Android
5. **Less Boilerplate**: Composables are simpler than React components
6. **Hot Reload**: Compose Preview for instant UI updates

## 🎓 Learning Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Compose Samples](https://github.com/android/compose-samples)
- [State in Compose](https://developer.android.com/jetpack/compose/state)
- [Navigation in Compose](https://developer.android.com/jetpack/compose/navigation)

---

**Your React app is now fully converted to Kotlin with Jetpack Compose! 🎉**
