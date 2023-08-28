package com.example.listaarticulos

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.listaarticulos.db.AppDatabase
import com.example.listaarticulos.db.Articulo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val articuloDao = AppDatabase.getInstance(this@MainActivity).articuloDao()
            val itemCount = articuloDao.contar()
            if (itemCount < 1) {
                articuloDao.insertar(Articulo(0, "Manzanas", false))
                articuloDao.insertar(Articulo(0, "Leche", false))
                articuloDao.insertar(Articulo(0, "Pan", false))
            }
        }

        setContent {
            ListaArticulosUI()
        }
    }
}

@Composable
fun ListaArticulosUI() {
    val context = LocalContext.current
    val (articulos, setArticulos) = remember { mutableStateOf(emptyList<Articulo>()) }

    LaunchedEffect(articulos) {
        withContext(Dispatchers.IO) {
            val dao = AppDatabase.getInstance(context).articuloDao()
            setArticulos(dao.findAll())
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(articulos) { articulo ->
            ArticuloItemUI(articulo) {
                setArticulos(emptyList<Articulo>())
            }
        }

        // Agregar un elemento para crear un nuevo artículo
        item {
            CrearArticuloUI(context) {
                setArticulos(emptyList<Articulo>())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearArticuloUI(context: Context, onArticuloCreated: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var nuevoArticuloText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        TextField(
            value = nuevoArticuloText,
            onValueChange = { nuevoArticuloText = it },
            modifier = Modifier.weight(2f),
            label = { Text("Nuevo Artículo") }
        )

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(context).articuloDao()
                    val nuevoArticulo = Articulo(0, nuevoArticuloText, false)
                    dao.insertar(nuevoArticulo)
                    nuevoArticuloText = ""
                    onArticuloCreated()
                }
            }
        ) {
            Text("Crear Artículo")
        }
    }
}

@Composable
fun ArticuloItemUI(articulo: Articulo, onSave: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        if (articulo.comprado) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Artículo comprado",
                modifier = Modifier.clickable {
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(context).articuloDao()
                        articulo.comprado = false
                        dao.actualizar(articulo)
                        onSave()
                    }
                }
            )
        } else {
            Icon(
                Icons.Default.Face,
                contentDescription = "Artículo por comprar",
                modifier = Modifier.clickable {
                    coroutineScope.launch(Dispatchers.IO) {
                        val dao = AppDatabase.getInstance(context).articuloDao()
                        articulo.comprado = true
                        dao.actualizar(articulo)
                        onSave()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = articulo.nombre,
            modifier = Modifier.weight(2f)
        )
        Icon(
            Icons.Default.Delete,
            contentDescription = "Eliminar artículo",
            modifier = Modifier.clickable {
                coroutineScope.launch(Dispatchers.IO) {
                    val dao = AppDatabase.getInstance(context).articuloDao()
                    dao.eliminar(articulo)
                    onSave()
                }
            }
        )
    }
}
