package demo.calendar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SmartCalendarApplication

fun main(args: Array<String>) {
    runApplication<SmartCalendarApplication>(*args)
}
