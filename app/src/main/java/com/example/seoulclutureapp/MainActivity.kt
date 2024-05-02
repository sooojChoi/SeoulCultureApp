package com.example.seoulclutureapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoulclutureapp.ui.EventApp
import com.example.seoulclutureapp.ui.theme.SeoulClutureAppTheme
import com.example.seoulclutureapp.ui.theme.gray80

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeoulClutureAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EventApp()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PageContainer(modifier: Modifier = Modifier){

    val pagerState = rememberPagerState(0)
    HorizontalPager(pageCount=5, state = pagerState,
        modifier = modifier,

           ) { page ->
        // Our page content
//        Text(
//            text = "Page: $page",
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(100.dp)
//        )
        CardItem(
            Modifier
                .fillMaxWidth()
                .height(150.dp),
            page, 5)
    }
}

@Composable
fun CardItem(modifier: Modifier = Modifier, currentPage: Int = 1, totalPage:Int=1){

    Card(modifier = modifier
        .fillMaxWidth()
        .padding(10.dp),
        colors= CardDefaults.cardColors(containerColor = Color(red = 243, green=202, blue=82)),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp)
                ) {
            Text("제목입니다. ", fontSize = 18.sp, fontStyle = FontStyle.Italic)
            Text("내용입니다. 내용이 참 많습니다. ", fontSize=15.sp)

            Row (modifier = Modifier
                .align(Alignment.End)
                .fillMaxHeight()){
                Card(
                    modifier = Modifier.align(Alignment.Bottom),
                    colors = CardDefaults.cardColors(containerColor = gray80)
                ) {
                    Text(
                        "${currentPage + 1} / $totalPage",
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SeoulClutureAppTheme {
        PageContainer()
    }
}