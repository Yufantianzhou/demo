package com.example.demo.service.http;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class ActixAuthService {
    private final WebClient webClient;
    private final LoginRequest loginRequest;
    private String token = "";
    private String role = "";

    public ActixAuthService(WebClient webClient, LoginRequest loginRequest) {
        this.webClient = webClient;
        this.loginRequest = loginRequest;
    }

    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return webClient.post()
                .uri("/login")  // 发送到登录的 URI
                .header("Content-Type", "application/json")
                .bodyValue(loginRequest)  // 包含用户名和密码的请求体
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .retry(3)
                .timeout(Duration.ofSeconds(2));  // 假设返回的 JWT token 是字符串
    }

    @PostConstruct
    public void init() {
        log.info("login and fetch token");
        LoginResponse response = login(loginRequest).block();
        if (response != null) {
            this.token = response.claims().token();
            this.role = response.claims().role();
        } else {
            log.error("login failed");
        }
        log.info("token: {},  role: {}", token, role);
    }

    public String getToken() {
        return token;
    }

//    public Mono<String> getAccountInfo(String authToken) {
//        return webClient.get()
//                .uri("/account")
//                .header("Authorization", "Bearer " + authToken)
//                .retrieve()
//                .bodyToMono(String.class);
//    }
}
