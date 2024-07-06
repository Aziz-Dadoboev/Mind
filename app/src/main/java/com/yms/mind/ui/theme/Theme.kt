package com.yms.mind.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Light Mode Preview"
//)
//@Preview(
//    showBackground = false,
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "Dark Mode Preview"
//)
//
//@Composable
//fun AppThemePreview(
//    @PreviewParameter(BoolParameterProvider::class) useDarkTheme: Boolean
//) {
//    AppTheme(useDarkTheme = useDarkTheme) {
//        Surface {
//            Text(
//                text = "Preview Text",
//                style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(16.dp)
//            )
//        }
//    }
//}

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
//
//class BoolParameterProvider : PreviewParameterProvider<Boolean> {
//    override val values: Sequence<Boolean> = sequenceOf(true, false)
//}

//
//@Preview(
//    showBackground = true,
//    name = "Light Theme Colors Preview",
//    uiMode = Configuration.UI_MODE_TYPE_NORMAL
//)
//@Composable
//fun LightThemePreview(
//    @PreviewParameter(BoolParameterProvider::class) useDarkTheme: Boolean
//) {
//    AppTheme(useDarkTheme = false) {
//        Surface {
//
//            Row(modifier = Modifier.background(LightColors.background)) {
//                Box(
//                    modifier = Modifier
//                        .background(LightColors.primary)
//                        .size(100.dp)
//                )  {
//                    Text(text = "Color [Light] Blue / Primary")
//                }
//                Box(
//                    modifier = Modifier
//                        .background(LightColors.surface)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Light] White / Surface")
//                }
//                Box(
//                    modifier = Modifier
//                        .background(LightColors.secondary)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Light] Brown / Secondary")
//                }
//                Box(
//                    modifier = Modifier
//                        .background(LightColors.error)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Dark] Red / error")
//                }
//            }
//        }
//    }
//}
//
//@Preview(
//    showBackground = true,
//    name = "Dark Theme Colors Preview",
//    uiMode = Configuration.UI_MODE_TYPE_NORMAL
//)
//@Composable
//fun DarkThemePreview(
//    @PreviewParameter(BoolParameterProvider::class) useDarkTheme: Boolean
//) {
//    AppTheme(useDarkTheme = true) {
//        Surface {
//            Row(modifier = Modifier.background(DarkColors.background)) {
//                Box(
//                    modifier = Modifier
//                        .background(DarkColors.primary)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Dark] Blue / primary")
//                }
//                Box(
//                    modifier = Modifier
//                        .background(DarkColors.surface)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Dark] Black / surface")
//                }
//                Box(
//                    modifier = Modifier
//                        .background(DarkColors.secondary)
//                        .size(100.dp)
//                ) {
//                    Text(text = "Color [Dark] Brown / secondary")
//                }
//                Box(
//                        modifier = Modifier
//                            .background(DarkColors.error)
//                            .size(100.dp)
//                        ) {
//                    Text(text = "Color [Dark] Red / error")
//                }
//            }
//        }
//    }
//}