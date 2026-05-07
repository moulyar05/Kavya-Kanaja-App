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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle

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
data class Poet(

    val name: String,

    val bio: String,

    val works: String,

    val awardYear: String,

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
                mutableStateOf(loadPoemsFromJson(context))
            }

            val navController = rememberNavController()

            Scaffold(

                bottomBar = {

                    NavigationBar(
                        containerColor = Color(0xFFF5E6CA)
                    ) {

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("home") },
                            icon = {
                                Icon(Icons.Default.Home, null)
                            },
                            label = {
                                Text("Home")
                            }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("library") },
                            icon = {
                                Icon(Icons.Default.List, null)
                            },
                            label = {
                                Text("Library")
                            }
                        )

                        NavigationBarItem(
                            selected = false,
                            onClick = { navController.navigate("poets") },
                            icon = {
                                Icon(Icons.Default.Person, null)
                            },
                            label = {
                                Text("Poets")
                            }
                        )
                    }
                }

            ) { padding ->

                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.padding(padding)
                ) {

                    composable("home") {
                        HomeScreen(tts, poems)
                    }

                    composable("library") {
                        LibraryScreen(
                            poems = poems,

                            onToggleFavorite = { index ->

                                poems = poems.toMutableList().apply {

                                    this[index] =
                                        this[index].copy(
                                            isFavorite =
                                                !this[index].isFavorite
                                        )
                                }
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
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2)
                    )
                )
            )
            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),

            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {

            Image(
                painter = painterResource(id = R.drawable.kavya_logo),
                contentDescription = "Kavya Kanaja Logo",

                modifier = Modifier
                    .size(90.dp),

                contentScale = ContentScale.Fit
            )
        }

        Text(
            text = "Poem of the Day",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4E342E)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Discover the beauty of Kannada literature",
            fontSize = 16.sp,
            color = Color(0xFF8D6E63)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(

            modifier = Modifier.fillMaxWidth(),

            shape = RoundedCornerShape(32.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFFBF5)
            ),

            elevation = CardDefaults.cardElevation(14.dp)
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
                        fontSize = 24.sp,
                        lineHeight = 38.sp,
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
                        fontSize = 18.sp
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row {

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

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = {
                    showMeaning = !showMeaning
                }
            ) {
                Text("Bhavartha")
            }
        }

        if (showMeaning) {

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF8E1)
                )
            ) {

                Text(
                    text = poem.bhavartha,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            }
        }

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
            fontSize = 30.sp,
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
                modifier = Modifier.padding(18.dp),
                fontSize = 18.sp,
                lineHeight = 32.sp
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

//            Column {
//
//                poem.difficultWords.forEach { (word, meaning) ->
//
//                    OutlinedButton(
//
//                        onClick = {
//
//                            selectedMeaning = meaning
//                            showDialog = true
//                        },
//
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 6.dp)
//
//                    ) {
//
//                        Text(
//                            text = word,
//                            fontSize = 18.sp
//                        )
//                    }
//                }
//            }

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
                            text = poet.bio,
                            fontSize = 17.sp,
                            lineHeight = 28.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Famous Works:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = poet.works,
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
                        text = poet.bio,
                        fontSize = 18.sp,
                        lineHeight = 32.sp,
                        color = Color.DarkGray
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
                        text = poet.works,
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
            "Kuppali Venkatappa Puttappa, popularly known as Kuvempu, was one of the greatest Kannada poets and writers. He promoted universal human values through literature.",
            "Ramayana Darshanam, Malegalalli Madumagalu",
            "1967",
            R.drawable.kuvempu
        ),

        Poet(
            "D. R. Bendre",
            "Dattatreya Ramachandra Bendre was a famous Kannada poet known for deep philosophical poetry and emotional expressions.",
            "Naaku Tanti, Gari",
            "1973",
            R.drawable.bendre
        ),

        Poet(
            "K. Shivaram Karanth",
            "Shivaram Karanth was a novelist, thinker and environmental activist who contributed immensely to Kannada literature.",
            "Mookajjiya Kanasugalu",
            "1977",
            R.drawable.karanth
        ),

        Poet(
            "Masti Venkatesha Iyengar",
            "Masti was among the pioneers of Kannada short stories and literature.",
            "Chikkaveera Rajendra",
            "1983",
            R.drawable.masti
        ),

        Poet(
            "V. K. Gokak",
            "Vinayaka Krishna Gokak was a major Kannada writer and scholar.",
            "Bharatha Sindhu Rashmi",
            "1990",
            R.drawable.gokak
        ),

        Poet(
            "U. R. Ananthamurthy",
            "Ananthamurthy was a major voice in modern Kannada fiction.",
            "Samskara",
            "1994",
            R.drawable.ananthamurthy
        ),

        Poet(
            "Girish Karnad",
            "Girish Karnad was internationally known for modern Indian plays rooted in mythology and history.",
            "Tughlaq, Hayavadana",
            "1998",
            R.drawable.karnad
        ),

        Poet(
            "Chandrashekhara Kambara",
            "Kambara is famous for blending folklore with modern literature.",
            "Jokumaraswamy",
            "2010",
            R.drawable.kambara
        )
    )
}