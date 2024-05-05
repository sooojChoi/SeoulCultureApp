package com.example.seoulclutureapp.ui.screens

import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.ui.navigation.NavigationDestination
import com.example.seoulclutureapp.ui.AppViewModelProvider
import com.example.seoulclutureapp.ui.EventTopAppBar
import com.example.seoulclutureapp.ui.theme.SeoulClutureAppTheme

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
    override val icon = Icons.Filled.Home
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navigationToEventDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val homeUiState:EventUiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { EventTopAppBar(scrollBehavior = scrollBehavior,
            title= stringResource(R.string.app_name),
            canNavigateBack = false
            ) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            when (homeUiState) {
                is EventUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is EventUiState.Success -> {
                    EventsScreen(
                        todaysEvents=(homeUiState as EventUiState.Success).todaysEvent,
                        totalEvents = (homeUiState as EventUiState.Success).events,
                        contentPadding=it,
                        onEventClicked= { navigationToEventDetail(it) },
                        modifier=modifier.fillMaxSize())
                }
                is EventUiState.Error -> ErrorScreen(viewModel::getEvents,modifier = modifier.fillMaxSize())
            }
        }
    }

}

sealed class Classification(title:String){
    object Concert: Classification("공연")
    object Experience: Classification("체험")
    object Exhibition: Classification("전시")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(todaysEvents: List<Event>,
                 totalEvents:List<Event>,
                 contentPadding: PaddingValues,
                 onEventClicked: (Int) -> Unit,
                 modifier: Modifier = Modifier){
    var classification:Classification by remember { mutableStateOf(Classification.Concert) }

    if(todaysEvents.isEmpty()){
        LoadingScreen(Modifier.fillMaxSize())
    }else{
        val scrollState = rememberScrollState()
        val pagerState = rememberPagerState(0) {
            if(todaysEvents.size < 10){
                todaysEvents.size
            }else{
                10
            }
        }


        Column(modifier = modifier
            .padding(contentPadding)
            .verticalScroll(scrollState)) {
            Text("오늘의 행사",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 14.dp))
            HorizontalPager(state = pagerState,
                modifier = modifier
                    .padding(horizontal = 10.dp),
            ) { page ->
                // Our page content
                EventCardWithPage(event=todaysEvents[page],
                    currentPage=page,
                    totalPage=todaysEvents.size,
                    modifier=Modifier.clickable { onEventClicked(page) })
            }
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly){
                NavigateCard(title = "공연",
                    isClicked = classification is Classification.Concert,
                    modifier = Modifier.clickable {
                    classification = Classification.Concert
                })
                NavigateCard(title = "체험",
                    isClicked = classification is Classification.Experience,
                    modifier = Modifier.clickable {
                    classification = Classification.Experience
                })
                NavigateCard(title = "전시",
                    isClicked = classification is Classification.Exhibition,
                    modifier = Modifier.clickable {
                    classification = Classification.Exhibition
                })
            }
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                .height(8000.dp)
                .padding(horizontal = 15.dp, vertical = 10.dp)){
                items(totalEvents.filter {
                    when(classification){
                        is Classification.Concert -> {
                            it.classification.contains("뮤지컬")||
                            it.classification.contains("클래식")||
                            it.classification.contains("국악")||
                            it.classification.contains("연극")||
                            it.classification.contains("무용")||
                            it.classification.contains("독주")
                        }
                        is Classification.Experience -> {
                            it.classification.contains("체험")||
                            it.classification.contains("교육")||
                            it.classification.contains("축제")
                        }
                        is Classification.Exhibition -> {
                            it.classification.contains("전시")||
                            it.classification.contains("미술")||
                            it.classification.contains("문화")||
                            it.classification.contains("영화")
                        }

                    }
                }){
                    EventCardWithNoImg(event = it)
                }
            }



        }
    }

}

@Composable
fun NavigateCard(title:String,
                 isClicked:Boolean,
                 modifier: Modifier = Modifier){
    Card(modifier = modifier
        .padding(horizontal = 10.dp),
        colors= CardDefaults.cardColors(containerColor = if(isClicked){
            MaterialTheme.colorScheme.primaryContainer
        }else{
            MaterialTheme.colorScheme.primary
        }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
    ){
        Text(
            text = title,
            color = if(isClicked){
                MaterialTheme.colorScheme.onPrimaryContainer
            }else{
                MaterialTheme.colorScheme.background
            },
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 30.dp)
        )
    }
}

@Composable
fun EventCardWithPage(event: Event, currentPage:Int, totalPage:Int, modifier:Modifier = Modifier){
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
                Column(modifier = Modifier
                    .weight(2f)
                    .padding(start = 10.dp, top = 2.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        Text(
                            event.classification,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            fontWeight = FontWeight.Bold,
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
        EventCardWithPage(event = Event("클래식","강동구","김성녀의 뮤지컬모노드라마 [벽 속의 요정]",
            "2024-10-31~2024-11-10","강동문화회관","기관1",
            "7세 이상 관람 가능(2017년생부터 관람 가능)","R석 60,000원 / S석 50,000원 / A석 35,000원","링크1",
            "https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=7dd997739af14eb1953d269dc96d2f4a&thumb=Y","2024-01-02",
            "202-01-02","2024-01-02",
            "longitude","latitude","free"),
            currentPage = 1,
            totalPage=10)
    }
}