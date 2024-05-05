package com.example.seoulclutureapp.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.seoulclutureapp.ui.theme.errorLight
import com.example.seoulclutureapp.ui.theme.gray80
import kotlinx.coroutines.launch


object EventDetailDestination : NavigationDestination {
    override val route = "event_detail"
    override val titleRes = R.string.event_detail_title
    const val eventIdArg = "eventId"
    val routeWithArgs = "$route/{$eventIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EventDetailScreen(
      navigateBack: () -> Unit,
      modifier: Modifier = Modifier,
      viewModel: EventDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState:EventDetailsUiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            EventTopAppBar(
                scrollBehavior=scrollBehavior,
                title = stringResource(EventDetailDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        modifier = modifier
    ){
        if(viewModel.eventId != null && uiState.todayEvent != null){
            EventDetailBody(event= uiState.todayEvent!!,
                contentPadding = it,
                isLike = uiState.isLike,
                onLikeClicked = {
                    coroutineScope.launch {
                        viewModel.updateEventLike()
                    }
                })
        }else{
            DetailErrorScreen(it, Modifier.fillMaxSize())
        }

    }
}

@Composable
fun EventDetailBody(event:Event,
                    contentPadding: PaddingValues = PaddingValues(0.dp),
                    isLike:Boolean,
                    onLikeClicked: ()->Unit,
                    modifier: Modifier = Modifier){
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))

    Column(modifier = modifier
        .verticalScroll(scrollState)
        .padding(contentPadding)
        .padding(12.dp)) {
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
                .fillMaxWidth()
                .padding(10.dp)
        )
        Column(modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 12.dp)
            .fillMaxWidth()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(
                    event.classification,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
            Text(
                text = event.title, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 5.dp)
            )
            Text(
                "장소: ${event.place}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                "기간: ${event.date}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(top = 2.dp)
            )
            if(event.orgName!=null){
                Text(
                    "기관명: ${event.orgName}", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if(event.useTarget!=null){
                Text(
                    "이용대상: ${event.useTarget}", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if(event.useFee!=null && event.useFee!=""){
                Text(
                    "이용요금: ${event.useFee}", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }else if(event.useFee=="" && event.isFree!=null){
                Text(
                    "이용요금: ${event.isFree}", fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row( modifier = Modifier.align(Alignment.End)
                .padding(top=20.dp),
                horizontalArrangement = Arrangement.Center) {
                if(event.link!=null) {
                    IconButton(
                        onClick = {  context.startActivity(intent) },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            tint = Color.Black,
                            contentDescription = stringResource(R.string.favorite_button)
                        )
                    }
                }
                if(isLike){
                    IconButton(
                        onClick = onLikeClicked,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            tint = errorLight,
                            contentDescription = stringResource(R.string.favorite_button)
                        )
                    }
                }else{
                    IconButton(
                        onClick = onLikeClicked,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            tint = errorLight,
                            contentDescription = stringResource(R.string.favorite_button)
                        )
                    }
                }

            }

        }
    }
}
@Composable
fun DetailErrorScreen(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EventDetailPreview(){
    SeoulClutureAppTheme {
        EventDetailBody(event=Event("클래식","강동구","김성녀의 뮤지컬모노드라마 [벽 속의 요정]",
            "2024-10-31~2024-11-10","강동문화회관","기관1",
            "7세 이상 관람 가능(2017년생부터 관람 가능)","R석 60,000원 / S석 50,000원 / A석 35,000원","링크1",
            "https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=7dd997739af14eb1953d269dc96d2f4a&thumb=Y","2024-01-02",
            "202-01-02","2024-01-02",
            "longitude","latitude","free"),
            isLike=false,
            onLikeClicked = {},
            modifier = Modifier.fillMaxSize())
    }
}