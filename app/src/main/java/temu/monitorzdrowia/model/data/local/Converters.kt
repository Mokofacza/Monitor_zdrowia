package temu.monitorzdrowia.model.data.local

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


object Converters {

    // Formatter do konwersji LocalDateTime na String i odwrotnie
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Konwerter z LocalDateTime na String
    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.format(formatter)
    }

    // Konwerter ze String na LocalDateTime
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(it, formatter) // Parsuje sformatowany String na LocalDateTime
        }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()  // LocalDate zapisany jako "YYYY-MM-DD"
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }
}
