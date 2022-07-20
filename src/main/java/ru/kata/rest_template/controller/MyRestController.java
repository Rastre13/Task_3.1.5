package ru.kata.rest_template.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.kata.rest_template.model.User;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MyRestController {

    private RestTemplate restTemplate;
    private final String URL = "http://94.198.50.185:7081/api/users";
    private List<String> cookies;


    public MyRestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    private void main(){
        ResponseEntity<String> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, null, String.class);
        cookies = responseEntity.getHeaders().get("Set-Cookie");
        User[] users = restTemplate.getForObject(URL, User[].class);
        if (users != null) {
            for (User u : users) {
                System.out.println("Id: " + u.getId() + ", Name: " + u.getName() + ", Last Name: " + u.getLastName() + ", Age: " + u.getAge());
            }
        }

        User user = new User(3L, "James", "Brown", (byte) 26);
        ResponseEntity<String> responseEntityAddUser = saveUser(user, getHeaders());
        user.setName("Thomas");
        user.setLastName("Shelby");
        ResponseEntity<String> responseEntityUpdateUser = editUser(user, getHeaders());
        ResponseEntity<String> responseEntityDeleteUser = deleteUserByID(3L, getHeaders());

        System.out.println(responseEntityAddUser.getBody() + responseEntityUpdateUser.getBody() + responseEntityDeleteUser.getBody());
    }

    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", cookies.stream().collect(Collectors.joining(";")));
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    public ResponseEntity<String> saveUser(User user, HttpHeaders httpHeaders) {
        HttpEntity<User> httpEntity = new HttpEntity<>(user, httpHeaders);
        return restTemplate.exchange(URL, HttpMethod.POST,httpEntity, String.class);
    }

    public ResponseEntity<String> editUser(User user, HttpHeaders httpHeaders) {
        HttpEntity<User> httpEntity = new HttpEntity<>(user, httpHeaders);
        return restTemplate.exchange(URL, HttpMethod.PUT, httpEntity, String.class);
    }

    public ResponseEntity<String> deleteUserByID(long id, HttpHeaders httpHeaders) {
        HttpEntity<User> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(URL + "/" + id, HttpMethod.DELETE, httpEntity, String.class);
    }
}
