package arcadia.com.confucius.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Ink and a red seal: nearly black ground, warm paper for text, and one vermilion for the word and the reference.
val Ink = Color(0xFF121013)
val Deep = Color(0xFF1C191C)
val Paper = Color(0xFFEDE3CF)
val Seal = Color(0xFFE25A41)
val Muted = Color(0xFFAEA596)
val Ash = Color(0xFF8C847A)

private val scheme = darkColorScheme(
    primary = Seal, onPrimary = Ink, secondary = Ash, onSecondary = Ink,
    background = Ink, onBackground = Paper, surface = Deep, onSurface = Paper,
    surfaceVariant = Color(0xFF2A2528), onSurfaceVariant = Muted,
    outline = Color(0xFF6E655F), outlineVariant = Color(0xFF3A3336),
    // Cards, the bar and dialogs read these; left unset they fall back to Material's grey. Highest must
    // differ from surfaceVariant, or a card takes onSurfaceVariant (Muted) for its text.
    surfaceContainerLowest = Ink, surfaceContainerLow = Color(0xFF181518), surfaceContainer = Deep,
    surfaceContainerHigh = Color(0xFF221E21), surfaceContainerHighest = Color(0xFF272225),
    surfaceBright = Color(0xFF332D30), surfaceDim = Ink,
    secondaryContainer = Color(0xFF4A2621), onSecondaryContainer = Paper,
)

private val type = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 31.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontSize = 26.sp, lineHeight = 33.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.Serif, fontSize = 22.sp, lineHeight = 29.sp),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 27.sp),
    bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 23.sp),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Medium),
)

@Composable
fun ConfuciusTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, typography = type, content = content)
