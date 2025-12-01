package com.example.beautyapp.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beautyapp.data.Product
import com.example.beautyapp.data.Note  //new - import Note from data package instead of defining it here
import com.example.beautyapp.ui.components.ProductCard
import com.example.beautyapp.viewmodel.MainViewModel  //new - import MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

//new - REMOVED the local Note data class - now using the one from data package

@Composable
fun ProfileScreen(
    userName: String,
    likedProducts: List<Product>,
    likedProductIds: Set<Int>,
    onToggleLike: (Int) -> Unit,
    onAddToCart: (Int) -> Unit,
    onProductClick: (Product) -> Unit,
    onLogout: () -> Unit,
    viewModel: MainViewModel  //new - ADD THIS PARAMETER
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Favorites", "Notes")

    val notes by viewModel.notes.collectAsState(initial = emptyList())  //new - get notes from ViewModel database, not local state
    var showAddNoteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // Profile Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF472B6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileStat(
                        count = likedProducts.size.toString(),
                        label = "Favorites"
                    )
                    ProfileStat(
                        count = notes.size.toString(),
                        label = "Notes"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF472B6)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout")
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFFF472B6)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> FavoritesSection(
                    likedProducts = likedProducts,
                    likedProductIds = likedProductIds,
                    onToggleLike = onToggleLike,
                    onAddToCart = onAddToCart,
                    onProductClick = onProductClick
                )
                1 -> NotesSection(
                    notes = notes,
                    onAddNote = { showAddNoteDialog = true },
                    onDeleteNote = { noteId, imagePath ->  //new - now takes noteId AND imagePath
                        viewModel.deleteNote(noteId, imagePath)  //new - call ViewModel to delete from database
                    },
                    context = context
                )
            }
        }
    }

    if (showAddNoteDialog) {
        AddNoteDialog(
            context = context,
            onDismiss = { showAddNoteDialog = false },
            onSave = { title, content, imagePath ->
                viewModel.addNote(title, content, imagePath)  //new - call ViewModel to save to database
                showAddNoteDialog = false
            }
        )
    }
}

@Composable
fun FavoritesSection(
    likedProducts: List<Product>,
    likedProductIds: Set<Int>,
    onToggleLike: (Int) -> Unit,
    onAddToCart: (Int) -> Unit,
    onProductClick: (Product) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "My Favorites (${likedProducts.size})",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (likedProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "💝", fontSize = 64.sp)
                    Text(
                        text = "No favorites yet",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Start exploring and save your favorite products!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(likedProducts.withIndex().toList()) { (index, product) ->
                    ProductCard(
                        product = product,
                        isLiked = likedProductIds.contains(product.id),
                        onToggleLike = onToggleLike,
                        onAddToCart = onAddToCart,
                        onClick = { onProductClick(product) },
                        colorIndex = index
                    )
                }
            }
        }
    }
}

@Composable
fun NotesSection(
    notes: List<Note>,
    onAddNote: () -> Unit,
    onDeleteNote: (String, String?) -> Unit,  //new - now takes noteId AND imagePath
    context: Context
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Notes (${notes.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "📝", fontSize = 64.sp)
                        Text(
                            text = "No notes yet",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Tap the + button to create your first note!",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(
                            note = note,
                            onDelete = { onDeleteNote(note.id, note.imagePath) },  //new - pass both id and imagePath
                            context = context
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFFF472B6)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Note",
                tint = Color.White
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onDelete: () -> Unit,
    context: Context
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            note.imagePath?.let { path ->
                val imageFile = File(path)
                if (imageFile.exists()) {
                    val bitmap = remember(path) {
                        BitmapFactory.decodeFile(path)
                    }
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Note image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Text(
                text = note.content,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dateFormat.format(Date(note.timestamp)),
                fontSize = 12.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun AddNoteDialog(
    context: Context,
    onDismiss: () -> Unit,
    onSave: (String, String, String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var savedImagePath by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            savedImagePath = saveImageToSandbox(context, it)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Note",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF472B6),
                        focusedLabelColor = Color(0xFFF472B6)
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        if (it.length <= 150) content = it
                    },
                    label = { Text("Content (${content.length}/150)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFF472B6),
                        focusedLabelColor = Color(0xFFF472B6)
                    )
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF472B6).copy(alpha = 0.1f),
                            contentColor = Color(0xFFF472B6)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Add Image",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri != null) "Change Image" else "Add Image")
                    }

                    savedImagePath?.let { path ->
                        val bitmap = remember(path) {
                            BitmapFactory.decodeFile(path)
                        }
                        bitmap?.let {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = {
                                        File(path).delete()
                                        selectedImageUri = null
                                        savedImagePath = null
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(32.dp)
                                        .background(
                                            Color.Black.copy(alpha = 0.5f),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove image",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content, savedImagePath)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF472B6)
                ),
                enabled = title.isNotBlank() && content.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                savedImagePath?.let { File(it).delete() }
                onDismiss()
            }) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

fun saveImageToSandbox(context: Context, uri: Uri): String? {
    return try {
        val filename = "note_image_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, filename)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            }
        }

        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ProfileStat(
    count: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF472B6)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}