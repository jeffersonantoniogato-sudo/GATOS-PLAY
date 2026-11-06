package com.gatostv.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

private val Navy = Color(0xFF080D1B)
private val Panel = Color(0xFF121B31)
private val Purple = Color(0xFF8B5CF6)
private val Cyan = Color(0xFF22D3EE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme(
                primary = Purple, secondary = Cyan, background = Navy, surface = Panel
            )) {
                GatosApp(
                    onLogin = { server, user, pass, onResult ->
                        lifecycleScope.launch {
                            runCatching { XtreamClient(server, user, pass).authenticate() }
                                .onSuccess { onResult(Result.success(it)) }
                                .onFailure { onResult(Result.failure(it)) }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GatosApp(onLogin: (String, String, String, (Result<AccountInfo>) -> Unit) -> Unit) {
    var server by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<String?>(null) }
    var account by remember { mutableStateOf<AccountInfo?>(null) }

    Row(Modifier.fillMaxSize().background(Navy)) {
        Column(
            Modifier.width(230.dp).fillMaxHeight().background(Panel).padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("GATOS", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                Text("TV", color = Cyan, fontSize = 28.sp, fontWeight = FontWeight.Black)
                HorizontalDivider(color = Color.DarkGray)
                Text("REPRODUTOR IPTV", color = Color.LightGray, fontSize = 11.sp)
            }
            Text("Versão 1.0", color = Color.Gray, fontSize = 12.sp)
        }
        Column(
            Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(22.dp)) {
                Column(Modifier.widthIn(max = 560.dp).padding(28.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if (account == null) "Conectar sua lista" else "Conectado", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Entre com os dados Xtream Codes fornecidos pelo seu serviço.", color = Color.LightGray)
                    OutlinedTextField(server, { server = it }, label = { Text("Servidor (ex.: https://servidor.com:8080)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(username, { username = it }, label = { Text("Usuário") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(password, { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
                    Button(
                        onClick = {
                            busy = true
                            status = null
                            onLogin(server.trim(), username.trim(), password) { result ->
                                busy = false
                                result.onSuccess {
                                    account = it
                                    status = "Conexão validada. Próxima etapa: carregar categorias e canais."
                                }.onFailure {
                                    status = it.message ?: "Não foi possível conectar. Confira os dados e tente novamente."
                                }
                            }
                        },
                        enabled = !busy && server.isNotBlank() && username.isNotBlank() && password.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) { Text(if (busy) "Conectando..." else "CONECTAR") }
                    status?.let { Text(it, color = if (account != null) Cyan else Color(0xFFFFB4AB)) }
                    account?.let {
                        Text("Usuário: ${it.username}", color = Color.White)
                        Text("Status: ${it.status}", color = Color.LightGray)
                        Text("Validade: ${it.expiration ?: "Não informada"}", color = Color.LightGray)
                    }
                }
            }
        }
    }
}
