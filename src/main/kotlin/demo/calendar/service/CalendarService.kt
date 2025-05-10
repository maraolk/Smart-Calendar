package demo.calendar.service

import demo.calendar.dto.*
import demo.calendar.entity.CalendarEntity
import demo.calendar.entity.UserEntity
import demo.calendar.entity.UserToCalendarEntity
import demo.calendar.exception.*
import demo.calendar.repository.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CalendarService(
    private val userToCalendarRepository: UserToCalendarRepository,
    private val calendarRepository: CalendarRepository,
    private val eventRepository: EventRepository,
    private val tokenRepository: TokenRepository,
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun createCalendar(token: String, request:CreateCalendarRequest): CalendarResponse {
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        if(calendarRepository.findByTeg(request.teg) != null){
            throw InvalidTegException("Calendar with such teg already exists")
        }
        val calendar = CalendarEntity(
                calendar_name = request.calendarName,
                is_public = request.isPublic,
                description = request.description,
                teg = request.teg,
                active = true
            )
        calendarRepository.save(calendar)
        userToCalendarRepository.save(UserToCalendarEntity(user=user, calendar= calendarRepository.findByTeg(request.teg)!!, access_type = "ADMINISTRATOR"))
        return CalendarResponse(
            calendarName=request.calendarName,
            isPublic=request.isPublic,
            teg=request.teg,
            active=true,
            description = request.description,
        )
    }

    fun manageCalendar(token: String, request: ManageCalendarRequest): CalendarResponse {
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        if(calendarRepository.findByTeg(request.teg) == null){
            throw InvalidTegException("Calendar with such teg does not exists")
        }
        if(!userToCalendarRepository.findByUser(user)!!.calendar.active){
            throw NotActiveCalendarException("This calendar is not active")
        }
        val calendar = calendarRepository.findByTeg(request.teg)!!
        if(userToCalendarRepository.findByUser(user) == null || userToCalendarRepository.findByUser(user)!!.access_type == "DELETED"){
            if(!calendar.is_public){
                throw PrivateCalendarException("This calendar is private, you can't interact with it.")
            }
            throw LimitedAccessRightsException("You do not have access rights to change this calendar, you can only view it.")
        }
        if(userToCalendarRepository.findByUser(user)!!.access_type == "VIEWER" || userToCalendarRepository.findByUser(user)!!.access_type == "ORGANIZER"){
            throw LimitedAccessRightsException("You do not have access rights to change this calendar.")
        }
        val newCalendar = CalendarEntity(
            id = calendar.id,
            calendar_name = request.calendarName,
            is_public = request.isPublic,
            active = request.active,
            teg = request.teg,
            description = request.description)
        calendarRepository.save(newCalendar)
        return CalendarResponse(calendarName = request.calendarName,
            isPublic = request.isPublic,
            active = request.active,
            teg = request.teg,
            description = request.description)
    }
    fun manageUser(token: String, request: ManageUsersRequest){
        TODO()
    }

    fun deleteCalendar(token:String, request: DeleteCalendarRequest){
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        if(calendarRepository.findByTeg(request.calendarTeg) == null){
            throw InvalidTegException("Calendar with such teg does not exists")
        }
        val calendar = calendarRepository.findByTeg(request.calendarTeg)!!
        if(!userToCalendarRepository.findByUser(user)!!.calendar.active){
            throw NotActiveCalendarException("This calendar is not active")
        }
        if(userToCalendarRepository.findByUser(user) == null || userToCalendarRepository.findByUser(user)!!.access_type == "DELETED"){
            if(!calendar.is_public){
                throw PrivateCalendarException("This calendar is private, you can't interact with it.")
            }
            throw LimitedAccessRightsException("You do not have access rights to delete this calendar, you can only view it.")
        }
        if(userToCalendarRepository.findByUser(user)!!.access_type == "VIEWER" || userToCalendarRepository.findByUser(user)!!.access_type == "ORGANIZER" || userToCalendarRepository.findByUser(user)!!.access_type == "MODERATOR"){
            throw LimitedAccessRightsException("You do not have access rights to delete this calendar.")
        }
        val newCalendar = CalendarEntity(
            id=calendar.id,
            calendar_name = calendar.calendar_name,
            is_public = calendar.is_public,
            active=false,
            teg = calendar.teg,
            description = calendar.description,
            )
        calendarRepository.save(newCalendar)
    }
}