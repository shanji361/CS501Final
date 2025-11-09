import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.weatherapi.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay

/*/
SplashScreen()- is the first Screen users will use when they open the app.
The user will see the app's logo for 5 seconds, and after that, will be navigated
to the login screen
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(5000) // wait 5 seconds
        navController.navigate("login") {
            popUpTo("splash") {
                inclusive = true
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "InstaGlow Logo",
            modifier = Modifier.size(100.dp)

        )
    }

}

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit = {}, // update callback, and pass in string for usrname as an argument
    onRegisterClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    // google sign in setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // Launcher to handle result from Google Sign-In intent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener(activity) { signInTask ->
                        if (signInTask.isSuccessful) {
                            val user = auth.currentUser
                            //displayName is the user's name, we save in this variable, and pass in argument in toast msg
                            val displayName = user?.displayName ?: "User"
                            Toast.makeText(context, "Welcome $displayName", Toast.LENGTH_LONG)
                                .show()
                            onLoginSuccess(displayName)
                        } else {
                            Toast.makeText(context, "Sign-in failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    //  facebook sign in setup
    val callbackManager = remember { CallbackManager.Factory.create() }

    fun signInWithFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(activity, listOf("email", "public_profile"))

        LoginManager.getInstance()
            .registerCallback(
                callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        val credential = FacebookAuthProvider
                            .getCredential(result.accessToken.token)
                        auth.signInWithCredential(credential)
                            .addOnSuccessListener {
                                val name = it.user?.displayName ?: "User"
                                Toast.makeText(context, "Welcome $name", Toast.LENGTH_SHORT).show()
                                onLoginSuccess(name)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Login failed: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                    override fun onCancel() {
                        Toast.makeText(context, "Facebook login cancelled", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onError(error: FacebookException) {
                        Toast.makeText(
                            context,
                            "Facebook error: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }

    fun signInWithEmail() {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val displayName = user?.displayName ?: email
                        Toast.makeText(context, "Welcome $displayName", Toast.LENGTH_LONG).show()
                        onLoginSuccess(displayName)
                    } else {
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }


    // UI
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF0F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "InstaGlow Logo",
                modifier = Modifier.size(80.dp)

            )
            Text(
                text = "InstaGlow",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Cursive,
                color = Color(0xFFD87093),
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFD87093),
                    unfocusedIndicatorColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, description)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFD87093),
                    unfocusedIndicatorColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // sign in button
            Button(
                onClick = { signInWithEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD87093)
                )
            ) {
                Text("SIGN IN", color = Color.White, fontSize = 18.sp)
            }


            // log in with section
            Spacer(modifier = Modifier.height(48.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text(
                    text = "Or log in with",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            // social icon button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //google icon
                SocialLoginIcon(
                    iconId = R.drawable.google_logo,
                    contentDescription = "Sign in with Google",
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                )
                Spacer(modifier = Modifier.width(24.dp))
                // facebook login icon
                SocialLoginIcon(
                    iconId = R.drawable.facebook_logo,
                    contentDescription = "Continue with Facebook",
                    onClick = { signInWithFacebook() },
                    iconSize = 46.dp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            // register txt
            ClickableText(
                text = buildAnnotatedString {
                    append("Do not have an account yet? ")
                    withStyle(style = SpanStyle(color = Color(0xFFD87093), fontWeight = FontWeight.Bold)) {
                        append("REGISTER")
                    }
                },
                onClick = {
                    // For simplicity, any click on the text will trigger the registration action
                    onRegisterClick()
                },
                style = LocalTextStyle.current.copy(
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
            )
        }
    }
    DisposableEffect(Unit) {
        val callback = { requestCode: Int, resultCode: Int, data: Intent? ->
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
        onDispose {  }
    }
}
// reusable composable for the social login icons
@Composable
fun SocialLoginIcon(
    iconId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: Dp = 28.dp
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
        )
    }
}
