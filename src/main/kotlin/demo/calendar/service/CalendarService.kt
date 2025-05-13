package demo.calendar.service

import demo.calendar.dto.*
import demo.calendar.entity.CalendarEntity
import demo.calendar.entity.UserToCalendarEntity
import demo.calendar.exception.*
import demo.calendar.repository.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class CalendarService(
    private val userToCalendarRepository: UserToCalendarRepository,
    private val calendarRepository: CalendarRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
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

    @Transactional
    fun manageCalendar(token: String, request: ManageCalendarRequest): CalendarResponse {
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        val calendar = calendarRepository.findByTeg(request.teg)
            ?: throw InvalidTegException("Calendar with such teg does not exists")
        if (!calendar.active){
            throw NotActiveCalendarException("This calendar is not active")
        }
        val accessType = userToCalendarRepository.findByUserAndCalendar(user, calendar)?.access_type
        if (accessType == null || accessType == "DELETED"){
            if(!calendar.is_public){
                throw PrivateCalendarException("This calendar is private, you can't interact with it.")
            }
            throw LimitedAccessRightsException("You do not have access rights to manage this calendar, you can only view it.")
        }
        if (accessType == "VIEWER" || accessType == "ORGANIZER"){
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

    @Transactional
    fun manageUsers(token: String, request: ManageUsersRequest){
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        val calendar = calendarRepository.findByTeg(request.teg) ?: throw InvalidTegException("Calendar with such teg does not exists")
        if (!calendar.active){
            throw NotActiveCalendarException("This calendar is not active")
        }
        val accessType = userToCalendarRepository.findByUserAndCalendar(user, calendar)?.access_type
        if (accessType == null || accessType == "DELETED"){
            if(!calendar.is_public){
                throw PrivateCalendarException("This calendar is private, you can't interact with it.")
            }
            throw LimitedAccessRightsException("You do not have access rights to manage this calendars users, you can only view it.")
        }
        val newUser = userRepository.findByTg(request.userTg) ?: throw UserNotFoundException("User with this tg not found")
        val oldAccessType = userToCalendarRepository.findByUserAndCalendar(newUser, calendar)
        if (accessType == "VIEWER") throw LimitedAccessRightsException("You do not have access rights to manage this calendars users")
        else if (accessType == "ORGANIZER" && request.accessType != "VIEWER" && request.accessType != "DELETED") throw LimitedAccessRightsException("You do not have access rights to change users access type if it's higher then yours")
        else if (accessType == "MODERATOR" && (request.accessType == "MODERATOR" || request.accessType == "ADMINISTRATOR")) throw LimitedAccessRightsException("You do not have access rights to change users access type if it's higher then yours")
        if (oldAccessType != null) {
            if (accessType == "ORGANIZER" && oldAccessType.access_type != "VIEWER" && oldAccessType.access_type != "DELETED") throw LimitedAccessRightsException("You do not have access rights to change access type of user with higher access the you")
            else if (accessType == "MODERATOR" && (oldAccessType.access_type == "MODERATOR" || oldAccessType.access_type == "ADMINISTRATOR")) throw LimitedAccessRightsException("You do not have access rights to change access type of user with higher access the you")
            else if (accessType == "ADMINISTRATOR" && oldAccessType.access_type == accessType) throw LimitedAccessRightsException("You do not have access rights to change access type of other administrator")
            userToCalendarRepository.save(UserToCalendarEntity(oldAccessType.id, newUser, calendar, request.accessType))
        }
        else {
            userToCalendarRepository.save(UserToCalendarEntity(user = newUser, calendar = calendar, access_type = request.accessType))
        }

    }

    @Transactional
    fun deleteCalendar(token:String, request: DeleteCalendarRequest){
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        val calendar = calendarRepository.findByTeg(request.teg)
            ?: throw InvalidTegException("Calendar with such teg does not exists")
        if (!calendar.active){
            throw NotActiveCalendarException("This calendar is not active")
        }
        val accessType = userToCalendarRepository.findByUserAndCalendar(user, calendar)?.access_type
        if (accessType == null || accessType == "DELETED"){
            if(!calendar.is_public){
                throw PrivateCalendarException("This calendar is private, you can't interact with it.")
            }
            throw LimitedAccessRightsException("You do not have access rights to manage this calendar, you can only view it.")
        }
        if (accessType == "VIEWER" || accessType == "ORGANIZER" || accessType == "MODERATOR"){
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

    fun getCalendars(token: String, request: GetCalendarsRequest): List<CalendarResponse> {
        val tEntity = tokenRepository.findByToken(token)
        userService.tokenIsValid(tEntity)
        val user = tEntity!!.user
        val sort = Sort.by(request.sortBy ?: "calendarName")
        val pageable = PageRequest.of(request.page, request.size, sort)
        when (request.type) {
            "PUBLIC" -> return calendarRepository.findByIsPublic(true, pageable).content.map {it.toCalendar()}
            "ALLOWED" -> {
                val tmp = userToCalendarRepository.findByUser(user, pageable).content.filter { it.access_type != "DELETED" }
                return tmp.map {it.calendar.toCalendar()}
            }
            "OWN" -> {
                val tmp = userToCalendarRepository.findByUser(user, pageable).content.filter { it.access_type == "ADMINISTRATOR" }
                return tmp.map {it.calendar.toCalendar()}
            }
            else -> throw BadRequestException("Bad request: unexpected type")
        }
    }
}