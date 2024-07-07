import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePicker
import com.mohamedrejeb.calf.ui.timepicker.AdaptiveTimePickerState
import com.mohamedrejeb.calf.ui.timepicker.rememberAdaptiveTimePickerState
import kotlinx.datetime.*
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun App() {

    val startState = rememberAdaptiveTimePickerState(11, 30)
    val endState = rememberAdaptiveTimePickerState(13, 30)

    var hr by remember { mutableStateOf(0L) }
    var min by remember { mutableStateOf(0) }

    updateTimeDifference(startState, endState) {
        val endInstant = endState.toInstant() ?: return@updateTimeDifference
        val startInstant = startState.toInstant() ?: return@updateTimeDifference
        (endInstant - startInstant).toComponents { hours, minutes, _, _ ->
            hr = hours
            min = minutes
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTimePicker(state = startState)

        VerticalLine()

        TimeDifferenceUI(hr, min)

        VerticalLine()

        OutlinedTimePicker(state = endState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedTimePicker(
    state: AdaptiveTimePickerState,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
    ) {
        AdaptiveTimePicker(
            modifier = Modifier.padding(16.dp),
            state = state
        )
    }
}

@Composable
fun VerticalLine(modifier: Modifier = Modifier) {
    VerticalDivider(
        modifier = modifier.height(64.dp),
        thickness = 4.dp,
        color = MaterialTheme.colors.primary.copy(.5f)
    )
}

@Composable
fun updateTimeDifference(key1: AdaptiveTimePickerState, key2: AdaptiveTimePickerState, onChange: () -> Unit) {
    LaunchedEffect(key1.hour, key1.minute) {
        onChange()
    }
    LaunchedEffect(key2.hour, key2.minute) {
        onChange()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeDifferenceUI(
    hr: Long,
    min: Int,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedContent(targetState = hr.toInt(), transitionSpec = customIntTransitionSpec()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = it.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily.SansSerif, fontSize = 32.sp)
                )
            }
            Text(text = "h", style = MaterialTheme.typography.caption)
            AnimatedContent(targetState = min, transitionSpec = customIntTransitionSpec()) {
                Text(
                    modifier = Modifier.padding(4.dp),
                    text = it.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.h6.copy(fontFamily = FontFamily.SansSerif, fontSize = 32.sp)
                )
            }
            Text(text = "m", style = MaterialTheme.typography.caption)
        }
    }
}

@Preview
@Composable
fun TimeDifferenceUIPreview() {
    TimeDifferenceUI(2, 30)
}

@ExperimentalAnimationApi
fun customIntTransitionSpec(): AnimatedContentTransitionScope<Int>.() -> ContentTransform = {
    if (targetState > initialState) {
        (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
    } else {
        (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
    }.using(
        SizeTransform(clip = false)
    )
}

fun AdaptiveTimePickerState.toInstant() = instantFromLocalTime(hour, minute)

fun instantFromLocalTime(hr: Int, min: Int) = localDateTimeNow().let {
    if (hr < 24 && min < 60) {
        LocalDateTime(it.year, it.monthNumber, it.dayOfMonth, hr, min, 0).toInstant(TimeZone.currentSystemDefault())
    } else {
        null
    }
}


fun timeNow() = timeFormat().format(localDateTimeNow().time)

fun localDateTimeNow() = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

@OptIn(FormatStringsInDatetimeFormats::class)
fun timeFormat() = LocalTime.Format { byUnicodePattern("HH:mm") }


@Composable
@Preview
fun PreviewTimeDifferenceCalculator() {
    MaterialTheme {
        App()
    }
}
