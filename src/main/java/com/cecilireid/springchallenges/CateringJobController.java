package com.cecilireid.springchallenges;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("cateringJobs")
public class CateringJobController {
    private static final String IMAGE_API = "https://foodish-api.herokuapp.com";
    private final CateringJobRepository cateringJobRepository;


    WebClient client;

    public CateringJobController(CateringJobRepository cateringJobRepository, WebClient.Builder webClientBuilder) {
        this.cateringJobRepository = cateringJobRepository;
        this.client = WebClient.create(IMAGE_API);
    }

    @GetMapping
    @ResponseBody
    public List<CateringJob> getCateringJobs() {
        return cateringJobRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CateringJob getCateringJobById(@PathVariable Long id) {
        if (cateringJobRepository.existsById(id)) {
            return cateringJobRepository.findById(id).get();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status")
    public List<CateringJob> getCateringJobsByStatus(@RequestParam(value = "status") Status status) {
        List<CateringJob> cateringJobs = cateringJobRepository.findByStatus(status);
        return cateringJobs;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CateringJob createCateringJob(@RequestBody CateringJob job) {
        CateringJob cateringJobCreated = this.cateringJobRepository.save(job);
        return cateringJobCreated;
    }

    @PutMapping(path = "/{id}")
    public CateringJob updateCateringJob(@RequestBody CateringJob cateringJob,@PathVariable Long id) {
        if(this.cateringJobRepository.existsById(id)){
            cateringJob.setId(id);
            return this.cateringJobRepository.save(cateringJob);
        }else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping(path = "/{id}")
    public CateringJob patchCateringJob(@PathVariable Long id,@RequestBody JsonNode json) {
        Optional<CateringJob> optionalCateringJob = this.cateringJobRepository.findById(id);
        if(optionalCateringJob.isPresent()){
            CateringJob job = optionalCateringJob.get();
            JsonNode menu = json.get("menu");
            JsonNode nroGuest = json.get("noOfGuests");
            if(menu != null && nroGuest != null){
                job.setMenu(menu.asText());
                job.setNoOfGuests(nroGuest.asInt());
                return this.cateringJobRepository.save(job);
            }else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            }
        }else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(path = "/image")
    public Mono<String> getSurpriseImage() {
        Mono<String> response = this.client.get().uri("/api").retrieve().bodyToMono(String.class);
        return response;
    }

//    @ExceptionHandler(HttpClientErrorException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    String handleClientException(){
//        return "Not Found";
//    }
}
