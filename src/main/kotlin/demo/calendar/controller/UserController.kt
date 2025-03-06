package demo.calendar.controller

import demo.calendar.dto.AuthorizeRequest
import demo.calendar.service.UserService
import demo.calendar.dto.SingUpRequest
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserController(
    val userService: UserService
) {
    @PostMapping("/register")
    fun regUser(@RequestBody request: SingUpRequest) = userService.registerUser(request)

    @PostMapping("/authorize")
    fun authUser(@RequestBody request: AuthorizeRequest) = userService.authUser(request)

    @PutMapping("/update")
    fun updUser(@RequestBody request: SingUpRequest, @RequestParam("token") token: String) = userService.updateUser(request, token)
}