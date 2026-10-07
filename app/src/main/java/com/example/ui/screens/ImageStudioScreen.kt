package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MyraCardBg
import com.example.ui.theme.MyraCardBorder
import com.example.ui.theme.MyraCyan
import com.example.ui.theme.MyraDarkBg
import com.example.ui.theme.MyraPink
import com.example.ui.theme.MyraPurple
import com.example.ui.theme.MyraPurpleLight
import com.example.ui.theme.MyraTextHint
import com.example.ui.theme.MyraTextPrimary
import com.example.ui.theme.MyraTextSecondary

data class ImagePromptIdea(
    val title: String,
    val hindiTitle: String,
    val prompt: String,
    val iconEmoji: String
)

@Composable
fun ImageStudioScreen(
    onPromptSelect: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = listOf(
        ImagePromptIdea(
            title = "Lord Shiva / Mahadev",
            hindiTitle = "दिव्य महादेव",
            prompt = "महादेव भगवान शिव का ध्यानस्थ रूप, हिमालय की वादियों में, त्रिशूल और डमरू के साथ सुंदर विवरण",
            iconEmoji = "🕉️"
        ),
        ImagePromptIdea(
            title = "Futuristic AI Robot",
            hindiTitle = "भविष्य का रोबोट",
            prompt = "MYRA AI जैसा प्यारा और चमकीला रोबोट, नियॉन लाइट और नीली आँखों के साथ",
            iconEmoji = "🤖"
        ),
        ImagePromptIdea(
            title = "Indian Festive Heritage",
            hindiTitle = "भारतीय धरोहर व उत्सव",
            prompt = "दीयों की रोशनी से जगमगाता भारतीय मंदिर और सुंदर रंगोली",
            iconEmoji = "🪔"
        ),
        ImagePromptIdea(
            title = "Cosmic Galaxy",
            hindiTitle = "ब्रह्मांड व सितारे",
            prompt = "तारों से भरा आकाश, नेबुला और ग्रहों का खूबसूरत दृश्य",
            iconEmoji = "🌌"
        ),
        ImagePromptIdea(
            title = "Peaceful Nature",
            hindiTitle = "शांत प्रकृति",
            prompt = "हरी-भरी वादियों में बहता झरना और उगता हुआ सूरज",
            iconEmoji = "🏞️"
        ),
        ImagePromptIdea(
            title = "Cyberpunk City",
            hindiTitle = "साइबरपंक शहर",
            prompt = "रात के समय नियॉन रोशनी से चमकती हुई आधुनिक शहर की इमारतें",
            iconEmoji = "🏙️"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MyraDarkBg)
    ) {
        // Top App Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("image_studio_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "MYRA AI Image Studio",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Header Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(MyraPurple, MyraPink)
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "क्रिएटिव इमेज आइडियाज़",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "MYRA से अपनी पसंद की तस्वीरें, वॉलपेपर या आर्ट बनाने का तरीका जानें। किसी भी विषय पर क्लिक करें:",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(prompts) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MyraCardBg
                    ),
                    modifier = Modifier
                        .border(1.dp, MyraCardBorder, RoundedCornerShape(16.dp))
                        .clickable { onPromptSelect(item.prompt) }
                        .testTag("image_prompt_card_${item.title}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = item.iconEmoji,
                            fontSize = 32.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.hindiTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MyraTextPrimary
                        )

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            color = MyraCyan
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.prompt,
                            fontSize = 11.sp,
                            color = MyraTextSecondary,
                            maxLines = 3,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}
