package com.example.todolist.data.entities

import android.graphics.Color
import com.example.todolist.R

data class Category (
    var id: Long,
    var name: String,
    var color: Int,
    var icon: Int
) {

    companion object {
        const val TABLE_NAME = "Category"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_TITLE = "name"
        const val COLUMN_NAME_COLOR = "color"
        const val COLUMN_NAME_ICON = "icon"
        val COLUMN_NAMES = arrayOf(
            COLUMN_NAME_ID,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_COLOR,
            COLUMN_NAME_ICON
        )

        val colors = arrayOf(
            Color.parseColor("#fe453a"),
            Color.parseColor("#ff9e0b"),
            Color.parseColor("#ffd50b"),
            Color.parseColor("#2fd05b"),
            Color.parseColor("#79c3fe"),
            Color.parseColor("#0b84fd"),
            Color.parseColor("#5d5be5"),
            Color.parseColor("#fe4f79"),
            Color.parseColor("#d57ff4"),
            Color.parseColor("#c8a576"),
            Color.parseColor("#757e86"),
            Color.parseColor("#ebb5ae")
        )

        val icons = arrayOf(
            // MISCELÁNEO
            R.drawable.category_icon_tasks,   // Tareas
            R.drawable.category_icon_flag,    // Bandera
            R.drawable.category_icon_shopping_cart, // Carro
            R.drawable.category_icon_medicine,    // Medicamento
            R.drawable.category_icon_restaurant,  // Restaurante
            R.drawable.category_icon_pram,    // Cochecito (Pram)

            // PROFESIONES
            R.drawable.category_icon_schooling, // Enseñanza
            R.drawable.category_icon_scissors, // Manualidades
            R.drawable.category_icon_construction, // Construcción
            R.drawable.category_icon_design, // Diseño
            R.drawable.category_icon_architecture, // Arquitectura
            R.drawable.category_icon_vaccines,    // Medicina

            // ECONOMÍA
            R.drawable.category_icon_gift,    // Regalo
            R.drawable.category_icon_wallet,    // Cartera
            R.drawable.category_icon_creditcard,  // Tarjeta de credito
            R.drawable.category_icon_money,   // Dinero
            R.drawable.category_icon_dollar,   // Dollar
            R.drawable.category_icon_bitcoin,   // Bitcoin


            // TRANSPORTE
            R.drawable.category_icon_bike,   // Bicicleta
            R.drawable.category_icon_car,    // Coche
            R.drawable.category_icon_bus,    // Autobús
            R.drawable.category_icon_train,  // Tren
            R.drawable.category_icon_airplane, // Avión
            R.drawable.category_icon_boat,   // Barco

            // ACTIVIDADES
            R.drawable.category_icon_wine,    // Vino
            R.drawable.category_icon_cake,    // Pastel
            R.drawable.category_icon_umbrella, // Sombrilla
            R.drawable.category_icon_run,     // Correr
            R.drawable.category_icon_workout, // Ejercicio
            R.drawable.category_icon_meditation, // Meditación

            // NATURALEZA
            R.drawable.category_icon_flower,  // Flor
            R.drawable.category_icon_forest,  // Bosque
            R.drawable.category_icon_fire,    // Fuego
            R.drawable.category_icon_ice,     // Hielo
            R.drawable.category_icon_day,     // Día
            R.drawable.category_icon_night,   // Noche

            // TECNOLOGÍA
            R.drawable.category_icon_computer, // Computadora
            R.drawable.category_icon_phone,    // Teléfono
            R.drawable.category_icon_gamepad,  // Mando
            R.drawable.category_icon_music,    // Música
            R.drawable.category_icon_headphones, // Cascos
            R.drawable.category_icon_lightbulb, // Bombilla

            // DEPORTES
            R.drawable.category_icon_sports_soccer,   // Fubol
            R.drawable.category_icon_sports_rugby,   // Rugby
            R.drawable.category_icon_sports_tennis,   // Tenis
            R.drawable.category_icon_sports_basketball,   // Basket
            R.drawable.category_icon_sports_baseball,   // Baseball
            R.drawable.category_icon_sports_volleyball,   // Volley

            R.drawable.category_icon_sports_swimming,   // Natación
            R.drawable.category_icon_sports_rowing,   // Remo
            R.drawable.category_icon_sports_boxing,   // Boxeo
            R.drawable.category_icon_sports_skateboarding,   // Skate
            R.drawable.category_icon_sports_snowboarding,   // Snow
            R.drawable.category_icon_sports_motorsports,   // Motor

            // EDIFICIOS
            R.drawable.category_icon_house,   // Casa
            R.drawable.category_icon_building, // Bloque
            R.drawable.category_icon_company, // Compañía
            R.drawable.category_icon_factory, // Fábrica
            R.drawable.category_icon_store, // Tienda
            R.drawable.category_icon_cottage, // Cabaña

            R.drawable.category_icon_castle,  // Castillo
            R.drawable.category_icon_church,  // Iglesia
            R.drawable.category_icon_mosque,  // Mezquita
            R.drawable.category_icon_synagogue,  // Sinagoga
            R.drawable.category_icon_temple_hindu,  // Templo hindu
            R.drawable.category_icon_temple_buddhist,  // Templo budista

            // SÍMBOLOS
            R.drawable.category_icon_circle,  // Circulo
            R.drawable.category_icon_triangle,  // Triangulo
            R.drawable.category_icon_square,  // Cuadrado
            R.drawable.category_icon_pentagon,  // Pentágono
            R.drawable.category_icon_heart,  // Corazón
            R.drawable.category_icon_star,  // Estrella
        )
    }

    override fun equals(other: Any?): Boolean{
        if(other is Category){
            return id == other.id
        }
        return false;
    }
}