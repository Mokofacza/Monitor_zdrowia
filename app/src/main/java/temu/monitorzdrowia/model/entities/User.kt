package temu.monitorzdrowia.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate


// Ten plik jest odpowiedzialny za opisanie użytkownika w bazie danych.
// To właśnie tu definiujemy, jakie informacje zapisujemy o użytkowniku.

@Entity // Mówi Roomowi, że ta klasa to tabela w bazie danych
data class User(
    val name: String, // Imię użytkownika
    val subname: String, // Nazwisko użytkownika
    val birthDate: LocalDate?, // Data urodzenia użytkownika
    val sex: String? = null,        // Płeć
    val address: String? = null,    // Adres
    val citySize: String? = null,   // Wielkość aglomeracji
    val photo: ByteArray? = null,//  zdjęcie
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 // Unikalny identyfikator użytkownika, generowany automatycznie
)