package com.noteit.features.notes.ui.screens


import android.Manifest
import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noteit.R
import com.noteit.data.model.NotesResponse
import com.noteit.ui.theme.Background
import com.noteit.ui.theme.BoxColor
import com.noteit.ui.theme.Content
import com.noteit.utils.VoiceToTextParser
import com.noteit.utils.VoiceToTextParserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen() {
    var search by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var des by remember { mutableStateOf("") }
    var isAddDialog by remember { mutableStateOf(false) }
    val voiceToTextParser by lazy {
        VoiceToTextParser(app = Application())
    }
    var canRecord by remember {
        mutableStateOf(false)
    }
    val recordAudioLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted->
            canRecord = isGranted
        })
    LaunchedEffect(key1 = recordAudioLauncher) {
        recordAudioLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }
    val state by voiceToTextParser.state.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    isAddDialog = true
                },
                Modifier.background(color = BoxColor) 
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Item",
                    tint = Color.White
                )
            }
        }
    )  { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        ) {
            SearchBar(search = search, onValueChange = {
                search = it
            }, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 50.dp))
        }

    }
    
    if(isAddDialog) {
        ShowDialogBox(title = title, description = des, state = state, voiceToTextParser = voiceToTextParser, onTitleChange = {title = it}, onDesChange = {des = it}, onClose = {
            isAddDialog = it
        }) {
            
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    search:String,
    modifier:Modifier = Modifier,
    onValueChange:(String)->Unit
) {
    TextField(value = search, onValueChange = onValueChange,
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.textFieldColors(Content, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = BoxColor)
        },
        trailingIcon = {
            if(search.isNotEmpty())
                IconButton(onClick = {
                    onValueChange("")
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "")
                }
        },
        placeholder = {
            Text(text = "Type something...", style = TextStyle(
                color = Color.Black.copy(0.5f)
            ))
        }
    )
}

@Composable
fun NotesEachRow(
    notesResponse: NotesResponse,
    modifier: Modifier = Modifier,
    onDelete: ()->Unit
) {
    Box( modifier = Modifier
        .fillMaxWidth()
        .background(
            color = Content, shape = RoundedCornerShape(8.dp)
        )) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row( modifier = Modifier.fillMaxWidth()) {
                Text(text = notesResponse.title, style = TextStyle(
                    color = Color.Black,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600
                ), modifier = Modifier.weight(0.7f))
                IconButton(onClick = {}, modifier = Modifier
                    .weight(0.3f)
                    .align(CenterVertically)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "", tint = BoxColor)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = notesResponse.description, style = TextStyle(
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 14.sp
            ))
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = notesResponse.updatedAt.split("T")[0], style = TextStyle(
                color = Color.Black.copy(alpha = 0.3f),
                fontSize = 10.sp
            ))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShowDialogBox(
    title:String,
    description:String,
    state: VoiceToTextParserState,
    voiceToTextParser: VoiceToTextParser,
    onTitleChange:(String)->Unit,
    onDesChange:(String)->Unit,
    onClose:(Boolean)->Unit,
    onClick:()->Unit,
) {
    AlertDialog(onDismissRequest = {},
        {
            Button(onClick = { onClick() }, modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BoxColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(15.dp)
            ) {
                Text(text = "Save")
            };
            FloatingActionButton(onClick = {
                if (state.isSpeaking) {
                    voiceToTextParser.stopListening()
                } else {
                    voiceToTextParser.startListening()
                }
            }) {
                AnimatedContent(targetState = state.isSpeaking) { isSpeaking->
                    if(isSpeaking) {
                        Icon(imageVector = Icons.Rounded.Stop, contentDescription = "")
                    } else {
                        Icon(imageVector = Icons.Rounded.Mic, contentDescription = "")
                    }
                }
            }
        },
    shape = RoundedCornerShape(16.dp),
        containerColor = Background,
        title = {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { onClose(false) }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = BoxColor)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                AppTextField(text = title, placeholder = stringResource(R.string.title), modifier = Modifier.height(15.dp), onValueChange = onTitleChange)
                Spacer(modifier = Modifier.height(15.dp))
                AppTextField(text = description, placeholder = stringResource(R.string.desc), modifier = Modifier.height(300.dp), onValueChange = onDesChange)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    text:String,
    placeholder:String,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(value = text, onValueChange = onValueChange, modifier = modifier.fillMaxWidth(),
    colors = TextFieldDefaults.textFieldColors(
        containerColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent
    ),
        placeholder = {
            Text(text = placeholder, color = Color.Black.copy(alpha = 0.4f))
        }
    )
}