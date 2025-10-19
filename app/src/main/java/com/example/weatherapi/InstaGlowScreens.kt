package com.example.weatherapi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.navigation.NavController


@Composable
fun InstaGlowApp(){
    val navController = rememberNavController()
    NavHost(navController, startDestination = "splash"){
        composable ("splash"){  SplashScreen(navController) }
        composable("login"){ LoginScreen(navController) }
    }
}


/*/
SplashScreen()- is the first Screen users will use when they open the app.
The user will see the app's logo for 5 seconds, and after that, will be navigated
to the login screen
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(Unit) {
        delay(5000) // wait 5 seconds
        navController.navigate("login"){
            popUpTo("splash") {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center

    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "InstaGlow Logo",
            modifier= Modifier.size(100.dp)

        )
    }

}

/*/
LoginScreen()- This is the second screen users will see after splash screen
 */
@Composable
fun LoginScreen(navController: NavHostController){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier=Modifier.fillMaxSize(),
        color = Color(0xFFFFF0F5)
    ){
        Column(
            modifier= Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
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
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Gmail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Login Screen, just has filler text for now, I will add login
            //functionalities via firebase

            Button(
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD87093)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("SIGN IN", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Do you not have an account yet? REGISTER",
                fontSize = 12.sp,
                color = Color(0xFFD87093),
                modifier = Modifier.clickable {  }
            )



        }
    }
}