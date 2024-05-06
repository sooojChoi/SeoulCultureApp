package com.example.seoulclutureapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.seoulclutureapp.ui.AppViewModelProvider
import com.example.seoulclutureapp.ui.EventTopAppBar
import com.example.seoulclutureapp.ui.navigation.NavigationDestination
import com.example.seoulclutureapp.ui.theme.SeoulClutureAppTheme

object MyEventDestination : NavigationDestination {
    override val route = "my_event"
    override val titleRes = R.string.my_event_title
    override val icon = Icons.Filled.Favorite
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyEventScreen(
    navigationToEventDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyEventViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState:MyEventUiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { EventTopAppBar(scrollBehavior = scrollBehavior,
            title= stringResource(R.string.my_event_title),
            canNavigateBack = false
        ) }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            MyEventsScreen(events = uiState.events,
                onEventClicked = navigationToEventDetail,
                modifier = Modifier.padding(it)
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyEventsScreen(events: List<Event>,
                 onEventClicked: (Int) -> Unit,
                 modifier: Modifier = Modifier){
    if(events.isEmpty()){
        MyEventEmptyScreen(modifier.fillMaxSize())
    }else{
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(items=events){
                EventCardWithNoImg(it,
                    Modifier.clickable { onEventClicked(events.indexOf(it)) })
            }
        }
    }
}
@Composable
fun EventCardWithNoImg(event: Event, modifier:Modifier = Modifier){
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp),
        colors= CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
       // border = BorderStroke(1.dp, color=MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            Row {
                Column(modifier = Modifier
                    .padding(top = 2.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            event.classification,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(text=event.title, fontSize = 15.sp, fontWeight = FontWeight.Bold,
                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top=4.dp))
                    Text("장소: ${event.place}", fontSize=13.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top=2.dp))
                    Text("기간: ${event.date}", fontSize=13.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top=2.dp))

                }

            }


        }


    }
}
@Preview(showBackground = true)
@Composable
fun EventCardWithNoImgPreview(){
    SeoulClutureAppTheme {
        EventCardWithNoImg(event = Event("클래식","강동구","김성녀의 뮤지컬모노드라마 [벽 속의 요정]",
            "2024-10-31~2024-11-10","강동문화회관","기관1",
            "7세 이상 관람 가능(2017년생부터 관람 가능)","R석 60,000원 / S석 50,000원 / A석 35,000원","링크1",
            "https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=7dd997739af14eb1953d269dc96d2f4a&thumb=Y","2024-01-02",
            "202-01-02","2024-01-02",
            "longitude","latitude","free"),)
    }
}

@Composable
fun MyEventEmptyScreen(modifier:Modifier = Modifier){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_campaign), contentDescription = ""
        )
        Text(text = stringResource(R.string.my_event_empty),
            color = Color.Gray,
            modifier = Modifier.padding(16.dp))

    }
}