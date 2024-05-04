package com.example.seoulclutureapp.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
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
                        events=(homeUiState as EventUiState.Success).todaysEvent,
                        contentPadding=it,
                        onEventClicked= { navigationToEventDetail(it) },
                        modifier=modifier.fillMaxSize())
                }
                is EventUiState.Error -> ErrorScreen(viewModel::getEvents,modifier = modifier.fillMaxSize())
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EventsScreen(events: List<Event>,
                 contentPadding: PaddingValues,
                 onEventClicked: (Int) -> Unit,
                 modifier: Modifier = Modifier){
    if(events.isEmpty()){
        LoadingScreen(Modifier.fillMaxSize())
    }else{
        val scrollState = rememberScrollState()
        val pagerState = rememberPagerState(0) { events.size }


        Column(modifier = modifier
            .padding(contentPadding)
            .verticalScroll(scrollState)) {
            HorizontalPager(state = pagerState,
                modifier = modifier
                    .padding(horizontal = 10.dp),
            ) { page ->
                // Our page content
                EventCard(event=events[page],
                    currentPage=page,
                    totalPage=events.size,
                    modifier=Modifier.clickable { onEventClicked(page) })
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