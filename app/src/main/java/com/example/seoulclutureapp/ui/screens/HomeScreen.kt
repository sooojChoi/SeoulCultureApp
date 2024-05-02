package com.example.seoulclutureapp.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.ui.theme.SeoulClutureAppTheme
import com.example.seoulclutureapp.ui.theme.gray80

@Composable
fun HomeScreen(
    eventUiState: EventUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    when (eventUiState) {
        is EventUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is EventUiState.Success ->
            EventsScreen(eventUiState.events, contentPadding, modifier.fillMaxSize())
        is EventUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun EventsScreen(events: List<Event>, contentPadding: PaddingValues,modifier: Modifier = Modifier){
    LazyColumn(
        contentPadding = contentPadding
    ){
        items(events){
            event->
            EventCard(event)
        }
    }
}

@Composable
fun EventCard(event: Event, modifier:Modifier = Modifier){
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
            Text(event.title, fontSize = 18.sp, fontStyle = FontStyle.Italic)
            Text(event.place, fontSize=15.sp)

        }


    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
//    Image(
//        modifier = modifier.size(200.dp),
//        painter = painterResource(R.drawable.loading_img),
//        contentDescription = stringResource(R.string.loading)
//    )
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
        HomeScreen(retryAction = {},
            eventUiState = EventUiState.Loading)
    }
}