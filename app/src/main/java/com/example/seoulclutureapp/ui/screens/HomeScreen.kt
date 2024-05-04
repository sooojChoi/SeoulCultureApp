package com.example.seoulclutureapp.ui.screens

import android.content.res.Resources.Theme
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seoulclutureapp.CardItem
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.ui.theme.SeoulClutureAppTheme
import com.example.seoulclutureapp.ui.theme.gray80
import com.example.seoulclutureapp.ui.theme.yellowCard
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    eventUiState: EventUiState,
    todaysEvent:List<Event> = listOf(),
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    var pagerEvent: List<Event> by remember { mutableStateOf(listOf()) }
    when (eventUiState) {
        is EventUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is EventUiState.Success -> {
            LaunchedEffect(todaysEvent){

                coroutineScope {
                    launch { pagerEvent = todays10Events(todaysEvent, eventUiState.events) }
                }
            }
            EventsScreen(pagerEvent, contentPadding, modifier.fillMaxSize())

        }
        is EventUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun todays10Events(todayEvents: List<Event>, totalEvent: List<Event>): List<Event>{
    val list = ArrayList<Event>()
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val todayDate = sdf.parse(LocalDate.now().toString())

    if(todayEvents.isNotEmpty()){
            // 오늘이 '시작 날짜'인 행사를 10개 담는다.
            for(event in todayEvents){
                val startDate = sdf.parse(event.startDate.substring(0, 10))

                if(todayDate.compareTo(startDate)==0){
                    list.add(event)
                    if(list.size==10){
                        break
                    }
                }
            }
            if(list.size<10){
                // 만약 10개가 되지 않았다면, 이전에 담았던 것 외에 오늘을 포함하는 행사를 최대 10개까지 담는다.
                for(event in todayEvents){
                    val startDate = sdf.parse(event.startDate.substring(0, 10))

                    if(todayDate.compareTo(startDate)!=0){
                        list.add(event)
                        if(list.size==10){
                            break
                        }
                    }
                }
            }


    }
    // 오늘의 날짜를 포함하는 행사가 하나도 없다면, 오늘을 포함하지 않는 행사라도 보여준다.
    // 단, 미래에 열리는 행사일 것.
    else{
            if(list.size==0){
                for(event in totalEvent){
                    val startDate = sdf.parse(event.startDate.substring(0, 10))

                    if(todayDate < startDate){
                        list.add(event)
                        if(list.size==10){
                            break
                        }
                    }
                }
            }
    }

    return list
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(events: List<Event>, contentPadding: PaddingValues,modifier: Modifier = Modifier){
    if(events.isEmpty()){
        LoadingScreen(Modifier.fillMaxSize())
    }else{
        val scrollState = rememberScrollState()
        val pagerState = rememberPagerState(0)
        Column(modifier = modifier
            .padding(contentPadding)
            .verticalScroll(scrollState)) {
            HorizontalPager(
                pageCount = events.size, state = pagerState,
                modifier = modifier
                    .padding(horizontal = 10.dp),
            ) { page ->
                // Our page content
                EventCard(events[page], page, events.size)
            }
        }
    }

}

@Composable
fun EventCard(event: Event, currentPage:Int, totalPage:Int, modifier:Modifier = Modifier){
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(10.dp)
        .aspectRatio(2f),
        colors= CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(event.imgSrc)
                        .crossfade(true)
                        .build(),
                    error = painterResource(R.drawable.ic_connection_error),
                    placeholder = painterResource(R.drawable.ic_downloading),
                    contentDescription = stringResource(R.string.event_photo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .aspectRatio(3f / 4f)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
                Column(modifier = Modifier.weight(2f).padding(start=10.dp,top=2.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Text(
                            event.classification,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(text=event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top=2.dp))
                    Text(event.place, fontSize=13.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top=2.dp))
                    Text(event.date, fontSize=13.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Row (modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxHeight()){
                        Text(
                            "${currentPage + 1} / $totalPage",
                            fontSize = 12.sp,
                            fontWeight=FontWeight.W500,
                            modifier = Modifier.align(Alignment.Bottom),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

            }


        }


    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(modifier=modifier,
        contentAlignment = Alignment.Center
        ) {

        CircularProgressIndicator(
            modifier = modifier
                .aspectRatio(1f)
                .padding(120.dp),
            color = MaterialTheme.colorScheme.secondary,
            strokeWidth = 10.dp,
        )

    }
}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenPreview(){
    SeoulClutureAppTheme{
        EventCard(event = Event("클래식","강동구","김성녀의 뮤지컬모노드라마 [벽 속의 요정]",
            "2024-10-31~2024-11-10","강동문화회관","기관1",
            "7세 이상 관람 가능(2017년생부터 관람 가능)","R석 60,000원 / S석 50,000원 / A석 35,000원","링크1",
            "https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=7dd997739af14eb1953d269dc96d2f4a&thumb=Y","2024-01-02",
            "202-01-02","2024-01-02",
            "longitude","latitude","free"),
            currentPage = 1,
            totalPage=10)
    }
}