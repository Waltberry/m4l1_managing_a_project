import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

//
//  Woofhansa
//  main.kt
//
//  Created by Harrison Kong @ Coursera
//  for the course "Write a Mini Reservation App in Kotlin"
//

var seatStatus        = Array(36) { "O" }
var passengers        = arrayOfNulls<String>(36)
var captain           : String? = null
var firstOfficer      : String? = null
var attendants        = ArrayList<String>()

fun main() {

    var option: String? = " "

    while (option?.toUpperCase() != "X") {

        showMainMenu()

        option = readLine() ?: " "

        when(option) {
            "1"      -> subMenuBookASeat()
            "2"      -> subMenuAddCrewMember()
            "3"      -> subMenuManifest()
            "4"      -> subMenuCancelReservation()
            "5"      -> subMenuResetFlight()
            "X", "x" -> println(">>> Have a wanderful day! Bark! <<<")
            else     -> println(">>> Sorry, not a valid option. Wag! <<<")
        }
    }
}

fun getIndexFromSeatDesignator(seat: String) : Int? {

    // this function takes a seat designator such as "1C" or "6A" and
    // returns an index in the linear array
    // e.g. "1A" ->  0
    //      "2C" ->  6
    //      "9D" -> 35
    // returns null if the seat designator is not valid

    if (!isValidSeatDesignator(seat)) { return null }

    val row = seat.substring(0, 1)
    val col = seat.substring(1, 2).toUpperCase()

    var colValue = 0

    when(col) {
        "A" -> colValue = 0
        "B" -> colValue = 1
        "C" -> colValue = 2
        "D" -> colValue = 3
    }

    return (row.toInt() - 1) * 4 + colValue
}

fun getSeatDesignatorFromIndex(index: Int) : String? {

    // this function takes a linear array index from 0 to 35 and
    // returns the corresponding seat designator
    // e.g. 0 -> "1A"
    //      6 -> "2C"
    //     35 -> "9D"
    // returns null if the index is out of bound

    if (index !in 0..35) { return null }

    val rowValue = (index / 4) + 1
    val colValue = index % 4

    var colChar = "?"

    when(colValue) {
        0 -> colChar = "A"
        1 -> colChar = "B"
        2 -> colChar = "C"
        3 -> colChar = "D"
    }

    return rowValue.toString() + colChar
}

fun getPassengerAt(index: Int) : String? {

    // this function gets the name of the passenger at a seat
    // returns null if the seat is not valid
    // also returns null if the seat is not taken

    if (index !in 0..35) { return null }
    return passengers[index]
}

fun getPassengerAt(seat: String) : String? {

    // this function gets the name of the passenger at a seat
    // returns null if the seat is not valid
    // also returns null if the seat is not taken

    val index = getIndexFromSeatDesignator(seat) ?: return null
    return passengers[index]
}

fun isSeatAvailable(index: Int) : Boolean {

    if (index !in 0..35) { return false }

    return seatStatus[index] == "O"
}

fun isSeatAvailable(seat: String) : Boolean {

    val index = getIndexFromSeatDesignator(seat) ?: return false

    return seatStatus[index] == "O"
}

fun isValidSeatDesignator(seat: String) : Boolean {

    // check to see if the seat argument is valid
    // it must be 2 characters long
    // the first character must be either any number from 1 to 9
    // the second character must be either A, a, B, b, C or c. D or d
    // returns true if valid
    // returns false if invalid

    if (seat.length != 2) { return false }

    val row = seat.substring(0, 1)
    val col = seat.substring(1, 2).toLowerCase()

    return (col in "a".."d") && (row in "1".."9")
}

fun releaseSeat(seat: String) {

    // marks the seat in the seat status array with an O
    // sets the passenger name for the seat to null

    if (isSeatAvailable(seat)) { return }

    val index = getIndexFromSeatDesignator(seat) ?: return

    seatStatus[index] = "O"
    passengers[index] = null
}

fun reserveSeat(seat: String, name: String) {

    // marks the seat in the seat status array with an X
    // sets the passenger name for the seat to name

    if (!isSeatAvailable(seat)) { return }

    val index = getIndexFromSeatDesignator(seat) ?: return

    seatStatus[index] = "X"
    passengers[index] = name
}

fun printCrew() {

    println("** Flight Crew **")
    println(captain ?: "<No captain assigned>")
    println(firstOfficer ?: "<No first officer assigned>")

    if (attendants.count() == 0) {
        println("<No flight attendants assigned>")
    }
    else {
        attendants.forEach { println(it) }
    }
    println()
}

fun printSeatChart() {

    val rowAsString = Array(9) { "" }

    // make the seat rows from the seat Status array (note
    // the space in the middle)
    // example: "O O   O X"

    for (row in 0..8) {
        for (col in 0..3) {
            rowAsString[row] += seatStatus[row * 4 + col] + " "
            if (col == 1) { rowAsString[row] += "  "}
        }
    }

    println()
    println("            /  Front  \\")
    println("           /           \\")
    println("           | A B   C D |")
    println("           |           |")
    println("        1  | " + rowAsString[0] + "|")
    println("        2  | " + rowAsString[1] + "|")
    println("        3  | " + rowAsString[2] + "|")
    println("-----------|           |-----------")
    println("        4  | " + rowAsString[3] + "|")
    println(" WING   5  | " + rowAsString[4] + "|      WING ")
    println("---v--- 6 -| " + rowAsString[5] + "|-------v---")
    println("           |           |")
    println("        7  | " + rowAsString[6] + "|")
    println("        8  | " + rowAsString[7] + "|")
    println("       -9 -| " + rowAsString[8] + "|----")
    println("   ----    |           |    ----")
    println("-----------|   Back    |-----------")
    println("            \\    |    /")
    println("             \\   |   /")
    println("              \\  |  /")
    println("                 |")
    println(" O = available")
    println(" X = reserved")
    println()
}

fun showMainMenu() {
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println("*        Welcome to Woofhansa          *")
    println("*                                      *")
    println("* Because all dogs deserve first class *")
    println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
    println()

    // How can we make this java call safer?
    var date = Calendar.getInstance().time
    //var date : Date? = Calendar.getInstance().time
    println("It is now $date")

    println()
    println("**** MAIN MENU ****")
    println()
    println("1 = Book a seat")
    println("2 = Add crew member")
    println("3 = Manifest")
    println("4 = Cancel a reservation")
    println("5 = Reset Flight")
    println("X = Exit")
    println()
    println("What would you like to do?")
    println("Please enter an option:")
}

fun subMenuAddCrewMember() {

    println("*** Add Crew Member ***")

    var subMenuDone = false

    do {

        println("1. Captain")
        println("2. First Officer")
        println("3. Flight Attendant")
        println("X. Back to Main Menu")
        println()
        println("Please select the role to add:")

        var role : String?

        var roleLoopDone = false

        do {
            role = readLine() ?: ""

            when (role) {

                "1"      -> {
                    var captainName : String?

                    do {
                        println("Please enter the captain's name (min. 2 characters):")
                        captainName = readLine()
                    } while (captainName == null || captainName.length < 2)

                    captain = captainName asRole "Captain"
                    printCrew()
                    roleLoopDone = true
                }
                "2"      -> {
                    var firstOfficerName : String?
                    do {
                        println("Please enter the first officer's name (min. 2 characters):")
                        firstOfficerName = readLine()
                    } while (firstOfficerName == null || firstOfficerName.length < 2)

                    firstOfficer = firstOfficerName asRole "First Officer"
                    printCrew()
                    roleLoopDone = true
                }
                "3"      -> {
                    var attendantName : String?
                    do {
                        println("Please enter the flight attendant's name (min. 2 characters):")
                        attendantName = readLine()
                    } while (attendantName == null || attendantName.length < 2)

                    attendants.add(attendantName asRole "Flight Attendant")
                    printCrew()
                    roleLoopDone = true
                }
                "X", "x" -> { roleLoopDone = true
                    subMenuDone = true }
                else     -> println(">>> Invalid crew role. Please try again. <<<")

            }

        } while (!roleLoopDone)

    } while (!subMenuDone)

}

fun subMenuBookASeat() {
    println("*** Book a Seat ***")

    var subMenuDone = false

    do {

        printSeatChart()

        println("Please enter a seat you would like: (e.g. 1A, 5C, etc.)")
        println("Enter X to return to the main menu")

        var seat : String

        var reservationLoopDone = false

        do {
            seat = readLine() ?: ""

            if (seat.toUpperCase() == "X") {
                reservationLoopDone = true
                subMenuDone = true
            }
            else {
                if (isValidSeatDesignator(seat)) {
                    if (isSeatAvailable(seat)) {

                        // get passenger's name and store it

                        var passengerName : String

                        do {
                            println("Please enter passenger's name:")

                            passengerName = readLine() ?: ""

                            if (passengerName.length < 2) {
                                println(">>> Name must be at least 2 characters long. Please try again. <<<")
                            }

                        } while (passengerName.length < 2)

                        reserveSeat(seat, passengerName)
                        reservationLoopDone = true
                        println(">>> Reservation completed successfully! <<<")

                    } else {
                        println(">>> That seat is not available, please pick another seat. <<<")
                    }
                } else {
                    println(">>> That is not a valid seat, please try again. <<<")
                }
            }

        } while (!reservationLoopDone)

    } while (!subMenuDone)
}

fun subMenuCancelReservation() {
    println("Cancel Reservation")

    var subMenuDone = false

    do {

        printSeatChart()

        println("Please enter a seat you would like to cancel: (e.g. 1A, 5C, etc.)")
        println("Enter X to return to the main menu")

        var seat : String

        var cancellationLoopDone = false

        do {
            seat = readLine() ?: ""

            if (seat.toUpperCase() == "X") {
                cancellationLoopDone = true
                subMenuDone = true
            }
            else {
                if (isValidSeatDesignator(seat)) {
                    if (!isSeatAvailable(seat)) {
                        val passenger = getPassengerAt(seat) ?: "<unknown passenger>"
                        println("Cancel reservation for : " + seat.toUpperCase() + " " + passenger + "? (Y/N)")

                        val reply = readLine() ?: "N"

                        printSeatChart()

                        if (reply.toUpperCase() == "Y") {
                            releaseSeat(seat)

                            println(">>> Reservation canceled successfully! <<<")
                        }
                        else {
                            println(">>> Cancellation aborted! <<<")
                        }

                        cancellationLoopDone = true

                    } else {
                        println(">>> That seat is not reserved, please pick another seat. <<<")
                    }
                } else {
                    println(">>> That is not a valid seat, please try again. <<<")
                }
            }

        } while (!cancellationLoopDone)

    } while (!subMenuDone)

}

fun subMenuManifest() {

    println("**** Manifest ****")
    println()

    val totalSeats = 36
    var seatsBooked = 0

    // list the crew

    printCrew()

    // list the passenger

    println("** Passengers **")

//    for (index in 0 until totalSeats) {
//        if (!isSeatAvailable(index)) {
//            val seatDesc: String = getSeatDesignatorFromIndex(index) + " " + getPassengerAt(index)
//            println(seatDesc)
//            seatsBooked ++
//        }
//    }

    // Higher order functions

    // chain first two higher order functions together and convert passengerName as a sequence

    var lines = passengers.asSequence()
        .mapIndexed { index, it -> getSeatDesignatorFromIndex(index) + " " + it}
        .filterIndexed { index, it -> !isSeatAvailable(index) }

    // take passenger's names and add seat number

    //var lines = passengers.mapIndexed { index, it -> getSeatDesignatorFromIndex(index) + " " + it}

    // get rid of the empty seats

    //lines = lines.filterIndexed { index, it -> !isSeatAvailable(index) }

    // print all lines

    lines.forEach { println(it) }

    println()

    // count seats booked

    seatsBooked = lines.count()

    if (seatsBooked == 0 ) {
        println("<No passengers have been booked on this flight.>")
        println()
    }

    println("Booked: $seatsBooked")
    println("Available: " + (totalSeats - seatsBooked))
    println("Capacity: $totalSeats")

    val percentage = (seatsBooked.toDouble() / totalSeats.toDouble()) * 100.0

    println("Percent full: " + BigDecimal(percentage).setScale(1, RoundingMode.HALF_EVEN) + "%")

    println()
    println("Press ENTER to return to the main menu.")
    readLine()
}

fun subMenuResetFlight() {

    println("*** Reset Flight ***")

    println("Are you sure you want to reset this flight? (Y/N)")

    val reply = readLine() ?: "N"

    if (reply.toUpperCase() != "Y") {
        println(">>> Reset operation canceled. <<<")
        return
    }

    captain = null
    firstOfficer = null
    attendants.clear()

    for (index in 0..35) {
        seatStatus[index] = "O"
        passengers[index] = null
    }

    printSeatChart()
    printCrew()

    println(">>> Flight reset!! <<<")
}

infix fun String.asRole(role: String) : String {
    return role + " " + this
}