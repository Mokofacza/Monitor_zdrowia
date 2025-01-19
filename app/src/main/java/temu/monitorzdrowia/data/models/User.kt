package temu.monitorzdrowia.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Period

// Ten plik jest odpowiedzialny za opisanie użytkownika w bazie danych.
// To właśnie tu definiujemy, jakie informacje zapisujemy o użytkowniku.

@Entity(tableName = "users") // Mówi Roomowi, że ta klasa to tabela w bazie danych
data class User(
    val name: String, // Imię użytkownika
    val subname: String, // Nazwisko użytkownika
    val birthDate: LocalDate, // Data urodzenia użytkownika
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0 // Unikalny identyfikator użytkownika, generowany automatycznie
) {
    // Wyliczanie wieku użytkownika na podstawie daty urodzenia
    val age: Int
        get() {
            val currentDate = LocalDate.now()
            return Period.between(birthDate, currentDate).years
        }
}