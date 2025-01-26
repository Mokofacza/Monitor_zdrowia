package temu.monitorzdrowia.ui.build

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import temu.monitorzdrowia.data.models.User
import java.io.ByteArrayOutputStream

@Composable
fun ProfileContent(
    user: User,
    age: Int?,
    onPickPhoto: (ByteArray) -> Unit,
    onEditField: (ProfileField) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity


    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    val compressedBytes = compressImage(bytes)
                    Log.d("ProfileContent", "Photo selected and compressed")
                    onPickPhoto(compressedBytes)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Nie udało się załadować zdjęcia.", Toast.LENGTH_SHORT).show()
                Log.e("ProfileContent", "Error loading image: ${e.message}", e)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("ProfileContent", "Uprawnienie do odczytu zdjęć przyznane")
            Toast.makeText(context, "Uprawnienie przyznane", Toast.LENGTH_SHORT).show()
            pickImageLauncher.launch("image/*")
        } else {
            Log.d("ProfileContent", "Uprawnienie do odczytu zdjęć odrzucone")
            Toast.makeText(context, "Uprawnienie odrzucone", Toast.LENGTH_SHORT).show()
            val permissionToRequest = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionToRequest)) {
                showSettingsDialog = true
            }
        }
    }


    fun hasReadImagesPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }


    fun requestReadImagesPermission() {
        val permissionToRequest = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        permissionLauncher.launch(permissionToRequest)
    }

    // obsługa wyboru zdjęcia
    val onClickProfileImage: () -> Unit = {
        if (hasReadImagesPermission()) {
            pickImageLauncher.launch("image/*")
        } else {
            val permissionToRequest = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permissionToRequest)) {
                showRationale = true
            } else {
                // zapytanie odnośnie uprawnień aplikacji
                requestReadImagesPermission()
            }
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Uprawnienie do odczytu zdjęć") },
            text = { Text("Aplikacja potrzebuje dostępu do Twoich zdjęć, aby umożliwić ustawienie zdjęcia profilowego.") },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    requestReadImagesPermission()
                }) {
                    Text("Zezwól")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    // Udostępnianie uprawnień dla aplikacji
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Uprawnienie odrzucone") },
            text = { Text("Aplikacja nie ma dostępu do Twoich zdjęć. Możesz włączyć uprawnienie w ustawieniach aplikacji.") },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    // Open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Otwórz ustawienia")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }

    // UI aplikacji
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profil Użytkownika",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Profile Image/Icon
            val userPhoto = user.photo
            if (userPhoto != null) {
                val bitmap = remember(userPhoto) {
                    BitmapFactory.decodeByteArray(userPhoto, 0, userPhoto.size)
                }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Zdjęcie profilowe",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape) // Circular image
                            .clickable { onClickProfileImage() }
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // Optional border
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Brak zdjęcia",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { onClickProfileImage() }
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape) // Optional border
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Data rows
            DataRow(
                label = "Imię:",
                value = user.name,
                onEditClick = { onEditField(ProfileField.Name) }
            )
            DataRow(
                label = "Nazwisko:",
                value = user.subname,
                onEditClick = { onEditField(ProfileField.Subname) }
            )
            DataRow(
                label = "Data ur.:",
                value = user.birthDate.toString(),
                onEditClick = { onEditField(ProfileField.BirthDate) }
            )

            // wiek – automatyczny
            age?.let {
                DataRow(
                    label = "Wiek:",
                    value = "$it lat"
                )
            }

            DataRow(
                label = "Adres:",
                value = user.address ?: "-",
                onEditClick = { onEditField(ProfileField.Address) }
            )
            DataRow(
                label = "Płeć:",
                value = user.sex ?: "-",
                onEditClick = { onEditField(ProfileField.Sex) }
            )
            DataRow(
                label = "Aglomeracja:",
                value = user.citySize ?: "-",
                onEditClick = { onEditField(ProfileField.CitySize) }
            )

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}

/**
 * Function to compress image.
 */
fun compressImage(bytes: ByteArray): ByteArray {
    val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)
    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
    return outputStream.toByteArray()
}
