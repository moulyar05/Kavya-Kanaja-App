package com.example.kavya_kanaja_app

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import java.util.*
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
import android.content.Context
import androidx.compose.foundation.BorderStroke

data class Poem(

    val title: String,

    val poet: String,

    val originalText: String,

    val bhavartha: String,

    val difficultWords: Map<String, String>,

    var isFavorite: Boolean = false
)

fun loadPoemsFromJson(context: android.content.Context): List<Poem> {

    val json =
        context.assets.open("poems.json")
            .bufferedReader()
            .use { it.readText() }

    val type =
        object : com.google.gson.reflect.TypeToken<List<Poem>>() {}.type

    return com.google.gson.Gson().fromJson(json, type)
}

fun saveFavorites(
    context: Context,
    poems: List<Poem>
) {

    val prefs =
        context.getSharedPreferences(
            "favorites",
            Context.MODE_PRIVATE
        )

    val favoriteTitles =
        poems.filter { it.isFavorite }
            .map { it.title }
            .toSet()

    prefs.edit()
        .putStringSet(
            "favorite_poems",
            favoriteTitles
        )
        .apply()
}

fun loadFavorites(
    context: Context,
    poems: List<Poem>
): List<Poem> {

    val prefs =
        context.getSharedPreferences(
            "favorites",
            Context.MODE_PRIVATE
        )

    val favoriteTitles =
        prefs.getStringSet(
            "favorite_poems",
            emptySet()
        ) ?: emptySet()

    return poems.map {

        it.copy(
            isFavorite =
                favoriteTitles.contains(it.title)
        )
    }
}

data class Poet(

    val name: String,

    val shortIntro: String,

    val biography: String,

    val famousWorks: String,

    val awardYear: String,

    val birthYear: String,

    val deathYear: String,

    val contribution: String,

    val writingStyle: String,

    val imageRes: Int
)

class MainActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this) {
            tts.language = Locale("kn", "IN")
        }

        setContent {

            val context = LocalContext.current

            var poems by remember {
                mutableStateOf(

                    loadFavorites(
                        context,
                        loadPoemsFromJson(context)
                    )
                )
            }

            val navController = rememberNavController()

            var userName by remember {
                mutableStateOf("")
            }
            Scaffold(

                bottomBar = {

                    NavigationBar(

                        modifier = Modifier
                            .padding(14.dp)
                            .clip(RoundedCornerShape(28.dp)),

                        containerColor = Color(0xFFFFFCF8),

                        tonalElevation = 12.dp
                    ){

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("home") },
                            icon = {
                                Icon(Icons.Default.Home, null)
                            },
                            label = {
                                Text("Home")
                            },
                            colors = NavigationBarItemDefaults.colors(

                                selectedIconColor = Color(0xFF6D4C41),

                                selectedTextColor = Color(0xFF6D4C41),

                                indicatorColor = Color(0xFFFFE0B2),

                                unselectedIconColor = Color.Gray,

                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("library") },
                            icon = {
                                Icon(Icons.Default.List, null)
                            },
                            label = {
                                Text("Library")
                            },
                            colors = NavigationBarItemDefaults.colors(

                                selectedIconColor = Color(0xFF6D4C41),

                                selectedTextColor = Color(0xFF6D4C41),

                                indicatorColor = Color(0xFFFFE0B2),

                                unselectedIconColor = Color.Gray,

                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("poets") },
                            icon = {
                                Icon(Icons.Default.Person, null)
                            },
                            label = {
                                Text("Poet's Corner")
                            },
                            colors = NavigationBarItemDefaults.colors(

                                selectedIconColor = Color(0xFF6D4C41),

                                selectedTextColor = Color(0xFF6D4C41),

                                indicatorColor = Color(0xFFFFE0B2),

                                unselectedIconColor = Color.Gray,

                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("profile") },

                            icon = {
                                Icon(Icons.Default.AccountCircle, null)
                            },

                            label = {
                                Text("Profile")
                            },
                            colors = NavigationBarItemDefaults.colors(

                                selectedIconColor = Color(0xFF6D4C41),

                                selectedTextColor = Color(0xFF6D4C41),

                                indicatorColor = Color(0xFFFFE0B2),

                                unselectedIconColor = Color.Gray,

                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }

            ) { padding ->

                NavHost(
                    navController = navController,
                    startDestination = "splash",
                    modifier = Modifier.padding(padding)
                ) {
                    composable("splash") {

                        SplashScreen {

                            navController.navigate("home") {

                                popUpTo("splash") {
                                    inclusive = true
                                }
                            }
                        }
                    }


                    composable("home") {
                        HomeScreen(tts, poems)
                    }

                    composable("library") {
                        LibraryScreen(
                            poems = poems,

                            onToggleFavorite = { index ->

                                val updatedPoems =
                                    poems.toMutableList().apply {

                                        this[index] =
                                            this[index].copy(
                                                isFavorite =
                                                    !this[index].isFavorite
                                            )
                                    }

                                poems = updatedPoems

                                saveFavorites(
                                    context,
                                    updatedPoems
                                )
                            },

                            navController = navController
                        )
                    }

                    composable("poets") {
                        PoetScreen(navController)
                    }

                    composable("poetDetail/{name}") { backStackEntry ->

                        val name =
                            backStackEntry.arguments?.getString("name")

                        val poet =
                            getPoets().first { it.name == name }

                        PoetDetailScreen(poet)
                    }

                    composable("detail/{index}") { backStackEntry ->

                        val index =
                            backStackEntry.arguments
                                ?.getString("index")
                                ?.toIntOrNull() ?: 0

                        PoemDetailScreen(
                            poem = poems[index],
                            tts = tts
                        )
                    }
                    composable("profile") {

                        ProfileScreen(
                            poems,
                            navController
                        )
                    }
                    composable("favorites") {

                        FavoritesScreen(
                            poems = poems,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.kavya_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(50.dp)
        )
    }
}

fun getPoemOfTheDay(poems: List<Poem>): Poem {

    val calendar = Calendar.getInstance()
    val day = calendar.get(Calendar.DAY_OF_YEAR)

    return poems[day % poems.size]
}

@Composable
fun HomeScreen(
    tts: TextToSpeech,
    poems: List<Poem>
) {
    val poem = getPoemOfTheDay(poems)

    var showMeaning by remember {
        mutableStateOf(false)
    }
    var selectedWord by remember {
        mutableStateOf("")
    }

    var selectedMeaning by remember {
        mutableStateOf("")
    }

    var showMeaningDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Brush.verticalGradient(

                    colors = listOf(

                        Color(0xFFFFF8F1),

                        Color(0xFFFFE8D6),

                        Color(0xFFFFD7BA)
                    )
                )
            )
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.kavya_logo),
            contentDescription = "Kavya Kanaja Logo",

            modifier = Modifier
                .padding(top = 10.dp)
                .height(140.dp),

            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "🌸 Poem of the Day",

            fontSize = 32.sp,

            fontWeight = FontWeight.Bold,

            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Experience the soul of Kannada literature",
            fontSize = 16.sp,
            color = Color(0xFF8D6E63),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(

            modifier = Modifier
                .fillMaxWidth(),

            shape = RoundedCornerShape(34.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFCFA)
            ),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 18.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = poem.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D4037)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "✍ ${poem.poet}",
                    fontSize = 16.sp,
                    color = Color(0xFF8D6E63)
                )



                Spacer(modifier = Modifier.height(18.dp))

                ClickableText(

                    text = buildAnnotatedString {

                        poem.originalText.split(" ").forEach { word ->

                            val cleanWord =
                                word.replace(",", "")
                                    .replace(".", "")

                            if (poem.difficultWords.containsKey(cleanWord)) {

                                pushStringAnnotation(
                                    tag = "WORD",
                                    annotation = cleanWord
                                )

                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFD84315),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {

                                    append("$word ")
                                }

                                pop()

                            } else {

                                append("$word ")
                            }
                        }
                    },

                    style = TextStyle(
                        fontSize = 26.sp,
                        lineHeight = 46.sp,
                        letterSpacing = 0.4.sp,
                        color = Color(0xFF3E2723)
                    ),

                    onClick = { offset ->

                        val annotations =
                            buildAnnotatedString {

                                poem.originalText.split(" ").forEach { word ->

                                    val cleanWord =
                                        word.replace(",", "")
                                            .replace(".", "")

                                    if (poem.difficultWords.containsKey(cleanWord)) {

                                        pushStringAnnotation(
                                            tag = "WORD",
                                            annotation = cleanWord
                                        )

                                        append("$word ")

                                        pop()

                                    } else {

                                        append("$word ")
                                    }
                                }

                            }.getStringAnnotations(
                                tag = "WORD",
                                start = offset,
                                end = offset
                            )

                        annotations.firstOrNull()?.let {

                            selectedWord = it.item

                            selectedMeaning =
                                poem.difficultWords[it.item]
                                    ?: ""

                            showMeaningDialog = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "- ${poem.poet}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

            }
        }


        Spacer(modifier = Modifier.height(20.dp))

        Row {

            Button(

                shape = RoundedCornerShape(16.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6D4C41)
                ),

                onClick = {
                    tts.speak(
                        poem.originalText,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            ) {
                Text("🔊 Listen")
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(

                shape = RoundedCornerShape(18.dp),

                border = BorderStroke(
                    1.dp,
                    Color(0xFF6D4C41)
                ),

                onClick = {
                    showMeaning = !showMeaning
                }
            ) {
                Text("Bhavartha")
            }
        }
        if (showMeaning) {

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color(0xFFE0CFC2)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bhavartha",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF5D4037)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = poem.bhavartha,
                fontSize = 18.sp,
                lineHeight = 30.sp,
                color = Color(0xFF4E342E)
            )
        }
    }
    if (showMeaningDialog) {

        AlertDialog(

            onDismissRequest = {
                showMeaningDialog = false
            },

            confirmButton = {

                TextButton(
                    onClick = {
                        showMeaningDialog = false
                    }
                ) {
                    Text("Close")
                }
            },

            title = {
                Text(
                    text = selectedWord,
                    fontWeight = FontWeight.Bold
                )
            },

            text = {
                Text(selectedMeaning)
            }
        )
    }
}

@Composable
fun LibraryScreen(
    poems: List<Poem>,
    onToggleFavorite: (Int) -> Unit,
    navController: NavController
){

    var search by remember {
        mutableStateOf("")
    }

    val filtered = poems.filter {

        it.originalText.contains(search, true) ||
                it.poet.contains(search, true)
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF6EC))
            .padding(14.dp)

    ) {

        OutlinedTextField(
            value = search,
            onValueChange = {
                search = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search poems or poets...")
            },
            leadingIcon = {
                Icon(Icons.Default.Search, null)
            },
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn {

            itemsIndexed(filtered) { _, poem ->

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {

                            val realIndex = poems.indexOf(poem)

                            navController.navigate("detail/$realIndex")
                        },

                    shape = RoundedCornerShape(22.dp),

                    elevation = CardDefaults.cardElevation(10.dp)

                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Row(
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = poem.poet,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            IconButton(
                                onClick = {

                                    val realIndex =
                                        poems.indexOf(poem)

                                    onToggleFavorite(realIndex)
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (poem.isFavorite)
                                            Icons.Default.Favorite
                                        else
                                            Icons.Default.FavoriteBorder,

                                    contentDescription = null,

                                    tint =
                                        if (poem.isFavorite)
                                            Color.Red
                                        else
                                            Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = poem.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )

                        Spacer(modifier = Modifier.height(6.dp))


                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = poem.originalText,
                            fontSize = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 30.sp,
                            color = Color.DarkGray
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Tap to read more...",
                            fontSize = 14.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PoemDetailScreen(
    poem: Poem,
    tts: TextToSpeech
) {
    var selectedWord by remember {
        mutableStateOf("")
    }

    var selectedMeaning by remember {
        mutableStateOf("")
    }

    var showMeaning by remember {
        mutableStateOf(false)
    }

    var showMeaningDialog by remember {
        mutableStateOf(false)
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFF8F0))
            .padding(18.dp)

    ) {

        Text(
            text = poem.title,
            fontSize = 38.sp,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "✍ ${poem.poet}",
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp)

        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                ClickableText(

                    text = buildAnnotatedString {

                        poem.originalText.split(" ").forEach { word ->

                            val cleanWord =
                                word.replace(",", "")
                                    .replace(".", "")

                            if (poem.difficultWords.containsKey(cleanWord)) {

                                pushStringAnnotation(
                                    tag = "WORD",
                                    annotation = cleanWord
                                )

                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFD84315),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {

                                    append("$word ")
                                }

                                pop()

                            } else {

                                append("$word ")
                            }
                        }
                    },

                    style = TextStyle(
                        fontSize = 24.sp,
                        lineHeight = 40.sp,
                        color = Color.Black
                    ),

                    onClick = { offset ->

                        val annotations =
                            buildAnnotatedString {

                                poem.originalText.split(" ").forEach { word ->

                                    val cleanWord =
                                        word.replace(",", "")
                                            .replace(".", "")

                                    if (poem.difficultWords.containsKey(cleanWord)) {

                                        pushStringAnnotation(
                                            tag = "WORD",
                                            annotation = cleanWord
                                        )

                                        append("$word ")

                                        pop()

                                    } else {

                                        append("$word ")
                                    }
                                }
                            }.getStringAnnotations(
                                tag = "WORD",
                                start = offset,
                                end = offset
                            )

                        annotations.firstOrNull()?.let {

                            selectedWord = it.item

                            selectedMeaning =
                                poem.difficultWords[it.item]
                                    ?: ""

                            showMeaningDialog = true
                        }
                    }
                )
                if (showMeaningDialog) {

                    AlertDialog(

                        onDismissRequest = {
                            showMeaningDialog = false
                        },

                        confirmButton = {

                            Button(
                                onClick = {
                                    showMeaningDialog = false
                                }
                            ) {

                                Text("Close")
                            }
                        },

                        title = {

                            Text(
                                text = selectedWord,
                                fontWeight = FontWeight.Bold
                            )
                        },

                        text = {

                            Text(
                                text = selectedMeaning,
                                fontSize = 18.sp,
                                lineHeight = 28.sp
                            )
                        }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "🌸 Bhavartha",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5D4037)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0)
            ),

            shape = RoundedCornerShape(20.dp)
        ) {

            Text(
                text = poem.bhavartha,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                lineHeight = 30.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                tts.speak(
                    poem.originalText,
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    null
                )
            }
        ) {

            Text("🔊 Listen")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Spacer(modifier = Modifier.height(14.dp))

        Column {

            var selectedMeaning by remember {
                mutableStateOf("")
            }

            var showDialog by remember {
                mutableStateOf(false)
            }

            if (showDialog) {

                AlertDialog(

                    onDismissRequest = {
                        showDialog = false
                    },

                    confirmButton = {

                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {

                            Text("Close")
                        }
                    },

                    title = {

                        Text("Meaning")
                    },

                    text = {

                        Text(
                            text = selectedMeaning,
                            fontSize = 18.sp
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PoetScreen(navController: NavController){

    val poets = getPoets()

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(16.dp)

    ) {
        item {

            Column(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {

                Text(

                    text = "🏆 Jnanpith Awardees",

                    fontSize = 30.sp,

                    fontWeight = FontWeight.Bold,

                    color = Color(0xFF4E342E)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(

                    text =
                        "The Jnanpith Award is India's highest literary honor presented to writers for their exceptional contribution to literature. Karnataka is home to many legendary Kannada poets and authors whose works continue to inspire generations.",

                    fontSize = 16.sp,

                    lineHeight = 28.sp,

                    color = Color(0xFF6D4C41)
                )

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        itemsIndexed(poets) { _, poet ->

            Card(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable {

                        navController.navigate("poetDetail/${poet.name}")
                    },

                shape = RoundedCornerShape(24.dp),

                elevation = CardDefaults.cardElevation(12.dp)

            ) {

                Column {

                    Image(
                        painter = painterResource(id = poet.imageRes),
                        contentDescription = poet.name,

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 24.dp,
                                    topEnd = 24.dp
                                )
                            ),

                        contentScale = ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = poet.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4E342E)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = poet.shortIntro,
                            fontSize = 17.sp,
                            lineHeight = 28.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = " Birth - Death",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${poet.birthYear} - ${poet.deathYear}",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "🏆 Jnanpith Award",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Awarded in ${poet.awardYear}",
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "🌟 Contribution to Kannada Literature",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5D4037)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = poet.contribution,
                            fontSize = 18.sp,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))



                        Text(
                            text = poet.writingStyle,
                            fontSize = 18.sp,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Famous Works:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = poet.famousWorks,
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PoetDetailScreen(poet: Poet) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFFF8F0))
            .padding(20.dp)

    ) {

        Card(

            shape = RoundedCornerShape(28.dp),

            elevation = CardDefaults.cardElevation(12.dp)

        ) {

            Column {

                Image(
                    painter = painterResource(id = poet.imageRes),
                    contentDescription = poet.name,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),

                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        text = poet.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "🏆 Jnanpith Award Winner",
                        fontSize = 18.sp,
                        color = Color(0xFF6D4C41),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Biography",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = poet.biography,
                        fontSize = 18.sp,
                        lineHeight = 32.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Birth & Death",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "${poet.birthYear} - ${poet.deathYear}",
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🏆 Jnanpith Award",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Awarded in ${poet.awardYear}",
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "🌟 Contribution",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = poet.contribution,
                        fontSize = 18.sp,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "✍ Writing Style",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = poet.writingStyle,
                        fontSize = 18.sp,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "📚 Famous Works",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5D4037)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = poet.famousWorks,
                        fontSize = 18.sp,
                        lineHeight = 30.sp
                    )
                }
            }
        }
    }
}

fun getPoets(): List<Poet> {

    return listOf(
        Poet(
            "Kuvempu",

            "Rashtrakavi of Karnataka",

            "Kuppali Venkatappa Puttappa, popularly known as Kuvempu, was one of the greatest Kannada poets and writers. He promoted universal human values and transformed Kannada literature with philosophical depth.",

            "Ramayana Darshanam, Malegalalli Madumagalu",

            "1967",

            "1904",

            "1994",

            "Kuvempu introduced the concept of Vishwamanava (Universal Humanism) and elevated Kannada literature globally.",

            "Philosophical, spiritual, nature-centered and humanistic writing.",

            R.drawable.kuvempu
        ),

        Poet(
            "D. R. Bendre",

            "Ambikatanayadatta",

            "Dattatreya Ramachandra Bendre was among the greatest Kannada poets known for lyrical beauty and deep philosophy.",

            "Naaku Tanti, Gari",

            "1973",

            "1896",

            "1981",

            "Bendre enriched Kannada poetry with emotional intensity and symbolism.",

            "Mystical, lyrical and philosophical poetry.",

            R.drawable.bendre
        ),

        Poet(
            "K. Shivaram Karanth",

            "Novelist and environmentalist",

            "Karanth was a novelist, thinker, Yakshagana artist and environmental activist.",

            "Mookajjiya Kanasugalu",

            "1977",

            "1902",

            "1997",

            "He contributed immensely to literature, theatre, culture and environmental awareness.",

            "Social realism blended with cultural themes.",

            R.drawable.karanth
        ),

        Poet(
            "Masti Venkatesha Iyengar",

            "Father of Kannada short stories",

            "Masti was one of the pioneers of Kannada short story literature.",

            "Chikkaveera Rajendra",

            "1983",

            "1891",

            "1986",

            "He shaped modern Kannada short story writing.",

            "Simple, elegant and socially reflective writing.",

            R.drawable.masti
        ),

        Poet(
            "V. K. Gokak",

            "Scholar and poet",

            "Vinayaka Krishna Gokak was a scholar, poet and leader of the Gokak movement.",

            "Bharatha Sindhu Rashmi",

            "1990",

            "1909",

            "1992",

            "He played a major role in promoting Kannada language and literature.",

            "Epic poetry with philosophical depth.",

            R.drawable.gokak
        ),

        Poet(
            "U. R. Ananthamurthy",

            "Modern Kannada novelist",

            "Ananthamurthy was one of the strongest voices in Navya Kannada literature.",

            "Samskara",

            "1994",

            "1932",

            "2014",

            "He questioned social traditions and explored morality through literature.",

            "Modernist and socially critical writing.",

            R.drawable.ananthamurthy
        ),

        Poet(
            "Girish Karnad",

            "Playwright and actor",

            "Girish Karnad was internationally acclaimed for modern Indian plays rooted in mythology and history.",

            "Tughlaq, Hayavadana",

            "1998",

            "1938",

            "2019",

            "He modernized Indian theatre and brought Kannada drama to global recognition.",

            "Historical, symbolic and mythological storytelling.",

            R.drawable.karnad
        ),

        Poet(
            "Chandrashekhara Kambara",

            "Folklore-based writer",

            "Kambara is known for blending Kannada folklore with modern literary expression.",

            "Jokumaraswamy",

            "2010",

            "1937",

            "Present",

            "He preserved folk traditions through literature and theatre.",

            "Folklore-rich dramatic and poetic style.",

            R.drawable.kambara
        )
    )
}


//        Poet(
//            "Kuvempu",
//            "Kuppali Venkatappa Puttappa, popularly known as Kuvempu, was one of the greatest Kannada poets and writers. He promoted universal human values through literature.",
//            "Ramayana Darshanam, Malegalalli Madumagalu",
//            "1967",
//            R.drawable.kuvempu
//        ),
//
//        Poet(
//            "D. R. Bendre",
//            "Dattatreya Ramachandra Bendre was a famous Kannada poet known for deep philosophical poetry and emotional expressions.",
//            "Naaku Tanti, Gari",
//            "1973",
//            R.drawable.bendre
//        ),
//
//        Poet(
//            "K. Shivaram Karanth",
//            "Shivaram Karanth was a novelist, thinker and environmental activist who contributed immensely to Kannada literature.",
//            "Mookajjiya Kanasugalu",
//            "1977",
//            R.drawable.karanth
//        ),
//
//        Poet(
//            "Masti Venkatesha Iyengar",
//            "Masti was among the pioneers of Kannada short stories and literature.",
//            "Chikkaveera Rajendra",
//            "1983",
//            R.drawable.masti
//        ),
//
//        Poet(
//            "V. K. Gokak",
//            "Vinayaka Krishna Gokak was a major Kannada writer and scholar.",
//            "Bharatha Sindhu Rashmi",
//            "1990",
//            R.drawable.gokak
//        ),
//
//        Poet(
//            "U. R. Ananthamurthy",
//            "Ananthamurthy was a major voice in modern Kannada fiction.",
//            "Samskara",
//            "1994",
//            R.drawable.ananthamurthy
//        ),
//
//        Poet(
//            "Girish Karnad",
//            "Girish Karnad was internationally known for modern Indian plays rooted in mythology and history.",
//            "Tughlaq, Hayavadana",
//            "1998",
//            R.drawable.karnad
//        ),
//
//        Poet(
//            "Chandrashekhara Kambara",
//            "Kambara is famous for blending folklore with modern literature.",
//            "Jokumaraswamy",
//            "2010",
//            R.drawable.kambara
//        )
//    )
//}

//val poets = listOf(
//
//    Poet(
//        "Kuvempu",
//
//        "Rashtrakavi of Karnataka",
//
//        "Kuppali Venkatappa Puttappa, popularly known as Kuvempu, was one of the greatest Kannada poets and writers. He promoted universal human values and transformed Kannada literature with philosophical depth.",
//
//        "Ramayana Darshanam, Malegalalli Madumagalu",
//
//        "1967",
//
//        "1904",
//
//        "1994",
//
//        "Kuvempu introduced the concept of Vishwamanava (Universal Humanism) and elevated Kannada literature globally.",
//
//        "Philosophical, spiritual, nature-centered and humanistic writing.",
//
//        R.drawable.kuvempu
//    ),
//
//    Poet(
//        "D. R. Bendre",
//
//        "Ambikatanayadatta",
//
//        "Dattatreya Ramachandra Bendre was among the greatest Kannada poets known for lyrical beauty and deep philosophy.",
//
//        "Naaku Tanti, Gari",
//
//        "1973",
//
//        "1896",
//
//        "1981",
//
//        "Bendre enriched Kannada poetry with emotional intensity and symbolism.",
//
//        "Mystical, lyrical and philosophical poetry.",
//
//        R.drawable.bendre
//    ),
//
//    Poet(
//        "K. Shivaram Karanth",
//
//        "Novelist and environmentalist",
//
//        "Karanth was a novelist, thinker, Yakshagana artist and environmental activist.",
//
//        "Mookajjiya Kanasugalu",
//
//        "1977",
//
//        "1902",
//
//        "1997",
//
//        "He contributed immensely to literature, theatre, culture and environmental awareness.",
//
//        "Social realism blended with cultural themes.",
//
//        R.drawable.karanth
//    ),
//
//    Poet(
//        "Masti Venkatesha Iyengar",
//
//        "Father of Kannada short stories",
//
//        "Masti was one of the pioneers of Kannada short story literature.",
//
//        "Chikkaveera Rajendra",
//
//        "1983",
//
//        "1891",
//
//        "1986",
//
//        "He shaped modern Kannada short story writing.",
//
//        "Simple, elegant and socially reflective writing.",
//
//        R.drawable.masti
//    ),
//
//    Poet(
//        "V. K. Gokak",
//
//        "Scholar and poet",
//
//        "Vinayaka Krishna Gokak was a scholar, poet and leader of the Gokak movement.",
//
//        "Bharatha Sindhu Rashmi",
//
//        "1990",
//
//        "1909",
//
//        "1992",
//
//        "He played a major role in promoting Kannada language and literature.",
//
//        "Epic poetry with philosophical depth.",
//
//        R.drawable.gokak
//    ),
//
//    Poet(
//        "U. R. Ananthamurthy",
//
//        "Modern Kannada novelist",
//
//        "Ananthamurthy was one of the strongest voices in Navya Kannada literature.",
//
//        "Samskara",
//
//        "1994",
//
//        "1932",
//
//        "2014",
//
//        "He questioned social traditions and explored morality through literature.",
//
//        "Modernist and socially critical writing.",
//
//        R.drawable.ananthamurthy
//    ),
//
//    Poet(
//        "Girish Karnad",
//
//        "Playwright and actor",
//
//        "Girish Karnad was internationally acclaimed for modern Indian plays rooted in mythology and history.",
//
//        "Tughlaq, Hayavadana",
//
//        "1998",
//
//        "1938",
//
//        "2019",
//
//        "He modernized Indian theatre and brought Kannada drama to global recognition.",
//
//        "Historical, symbolic and mythological storytelling.",
//
//        R.drawable.karnad
//    ),
//
//    Poet(
//        "Chandrashekhara Kambara",
//
//        "Folklore-based writer",
//
//        "Kambara is known for blending Kannada folklore with modern literary expression.",
//
//        "Jokumaraswamy",
//
//        "2010",
//
//        "1937",
//
//        "Present",
//
//        "He preserved folk traditions through literature and theatre.",
//
//        "Folklore-rich dramatic and poetic style.",
//
//        R.drawable.kambara
//    )
//)



@Composable
fun ProfileScreen(
    poems: List<Poem>,
    navController: NavController
) {

    val favoriteCount =
        poems.count { it.isFavorite }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(

                    colors = listOf(

                        Color(0xFFFFF8F1),

                        Color(0xFFFFE8D6),

                        Color(0xFFFFD7BA)
                    )
                )
            )
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(id = R.drawable.kavya_logo),
            contentDescription = null,

            modifier = Modifier
                .size(120.dp),

            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kavya Reader",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Kannada Literature Explorer",
            fontSize = 16.sp,
            color = Color(0xFF8D6E63)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                    navController.navigate("favorites")
                },

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp)

        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "❤️ Favorite Poems",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "$favoriteCount poems saved",
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tap to view favorites",
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp)

        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "📚 Poems Collection",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${poems.size} Kannada poems available",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(24.dp),

            elevation = CardDefaults.cardElevation(10.dp)

        ) {

            Column(
                modifier = Modifier.padding(22.dp)
            ) {

                Text(
                    text = "ℹ About App",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text =
                        "Kavya Kanaja is a Kannada literature learning app designed to help users understand difficult poems through Bhavartha, audio reading, and word meanings.",

                    fontSize = 17.sp,
                    lineHeight = 30.sp
                )
            }
        }
    }
}

@Composable
fun FavoritesScreen(
    poems: List<Poem>,
    navController: NavController
) {

    val favorites =
        poems.filter { it.isFavorite }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F0))
            .padding(16.dp)

    ) {

        Text(
            text = "❤️ Favorite Poems",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (favorites.isEmpty()) {

            Text(
                text = "No favorite poems yet",
                fontSize = 18.sp
            )

        } else {

            LazyColumn {

                itemsIndexed(favorites) { _, poem ->

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .clickable {

                                val realIndex =
                                    poems.indexOf(poem)

                                navController.navigate(
                                    "detail/$realIndex"
                                )
                            },

                        shape = RoundedCornerShape(24.dp),

                        elevation =
                            CardDefaults.cardElevation(10.dp)

                    ) {

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Text(
                                text = poem.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = poem.poet,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = poem.originalText,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun SplashScreen(
    onFinish: () -> Unit
) {

    LaunchedEffect(Unit) {

        delay(2200)

        onFinish()
    }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(

                Brush.verticalGradient(

                    colors = listOf(

                        Color(0xFFFFF8F1),

                        Color(0xFFFFE8D6),

                        Color(0xFFFFD7BA)
                    )
                )
            ),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Image(
            painter = painterResource(id = R.drawable.ka_logo),
            contentDescription = null,

            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Kavya Kanaja",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Preserving the beauty of Kannada poetry",
            fontSize = 18.sp,
            color = Color(0xFF6D4C41)
        )
    }
}