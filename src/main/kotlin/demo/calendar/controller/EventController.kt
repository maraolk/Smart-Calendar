//package demo.calendar.controller
//
//import demo.calendar.dto.*
//import demo.calendar.service.EventService
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/api/v1/event")
//class EventController(
//    val eventService: EventService
//) {
//    @PostMapping("/createEvent")
//    fun createEvent(@RequestParam("token") token : String, @RequestBody request: CreateEventRequest) = eventService.createEvent(token, request)
//
//    @PutMapping("/manageEvent")
//    fun manageEvent(@RequestParam("token") token: String, @RequestBody request: ManageEventRequest) = eventService.manageEvent(token, request)
//
//    @PutMapping("/deleteCalendar")
//    fun deleteEvent(@RequestParam("token") token : String, @RequestBody request: DeleteEventRequest) = eventService.deleteEvent(token, request)
//
//    @PostMapping("/reactEvent")
//    fun react(@RequestParam("token") token : String, @RequestBody request: ReactEventRequest) = eventService.reactEvent(token, request)
//}