package com.polling;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/polls")
@RequiredArgsConstructor
@CrossOrigin
public class PollController {

    private final PollService pollService;

    @PostMapping
    public Poll createPoll(@RequestBody Poll poll) {
        return pollService.createPoll(poll);
    }

    @GetMapping("/{id}")
    public Poll getPoll(@PathVariable Long id) {
        return pollService.getPoll(id);
    }
    
    @GetMapping
    public List<Poll> getAllPolls() {
        return pollService.getAllPolls();
    }

    @PostMapping("/{pollId}/vote/{optionId}")
    public Poll vote(@PathVariable Long pollId,
                     @PathVariable Long optionId,
                     HttpServletRequest request) {
        return pollService.vote(pollId, optionId, request);
    }
}
