package com.vladimirpandurov.springSecurity01B.resource;

import com.vladimirpandurov.springSecurity01B.domain.HttpResponse;
import com.vladimirpandurov.springSecurity01B.domain.User;
import com.vladimirpandurov.springSecurity01B.domain.UserPrincipal;
import com.vladimirpandurov.springSecurity01B.dto.UserDTO;
import com.vladimirpandurov.springSecurity01B.dtomapper.UserDTOMapper;
import com.vladimirpandurov.springSecurity01B.exception.ApiException;
import com.vladimirpandurov.springSecurity01B.form.LoginForm;
import com.vladimirpandurov.springSecurity01B.provider.TokenProvider;
import com.vladimirpandurov.springSecurity01B.service.RoleService;
import com.vladimirpandurov.springSecurity01B.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

import static com.vladimirpandurov.springSecurity01B.utils.ExceptionUtils.processError;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;


@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @PostMapping("/login")
    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
        Authentication authentication = authenticate(loginForm.getEmail(), loginForm.getPassword());
        UserDTO user = getAuthenticatedUser(authentication);
        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
        UserDTO userDTO = this.userService.createUser(user);
        return ResponseEntity.created(getUri(userDTO.getId())).body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userDTO))
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .message("User created")
                        .build()
        );
    }
    //for reset password when user is not loged in
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Email sent. Please check your email to reset your password.")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }
    //for reset password when user is not loged in
    @GetMapping("/verify/password/{key}")
    public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key) {
        UserDTO user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", user))
                .message("Please enter a new password")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build());
    }
    //for reset password when user is not logged in
    @PostMapping("/resetpassword/{key}/{password}/{confirmPassword}")
    public ResponseEntity<HttpResponse> resetPasswordWithUrl(@PathVariable("key") String key, @PathVariable("password") String password,
                                                          @PathVariable("confirmPassword") String confirmPassword){
        userService.renewPassword(key, password, confirmPassword);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("Password reset successfully")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse> profile(Authentication authentication){
        UserDTO user = userService.getUserByEmail(authentication.getName());
        log.info(authentication.getName());
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(Map.of("user", user))
                .message("Profile Retrieved")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build()
        );
    }

    @GetMapping("/verify/code/{email}/{code}")
    public ResponseEntity<HttpResponse> verifyCode(@PathVariable("email") String email, @PathVariable("code") String code) {
        UserDTO user = userService.verifyCode(email, code);
        return sendResponse(user);
    }

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> handleError(HttpServletRequest request){
        return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .reason("An error occurred " + request.getMethod())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .build());
    }

    private URI getUri(Long userId) {
        return URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/get/" + userId).toUriString());
    }

    private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(user)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(user))))
                        .message("Login Success")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private UserPrincipal getUserPrincipal(UserDTO user) {
        return new UserPrincipal(UserDTOMapper.toUser(userService.getUserByEmail(user.getEmail())), this.roleService.getRoleByUserId(user.getId()));
    }

    private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
        this.userService.sendVerificationCode(user);
        return ResponseEntity.ok().body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user))
                        .message("Verification code sent")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
        );
    }

    private Authentication authenticate(String email, String password) {
        try{
            Authentication authentication = authenticationManager.authenticate(unauthenticated(email, password));
            return authentication;
        }catch (Exception exception){
            processError(request, response, exception);
            throw new ApiException(exception.getMessage());
        }

    }

    private UserDTO getAuthenticatedUser(Authentication authentication){
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

}
