package com.polling;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final OptionRepository optionRepository;
    private final VoteRepository voteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Poll createPoll(Poll poll) {

        poll.getOptions().forEach(option -> {
            option.setPoll(poll);

            // 🔥 ensure voteCount is never null
            if (option.getVoteCount() == null) {
                option.setVoteCount(0);
            }
        });

        return pollRepository.save(poll);
    }


    public Poll getPoll(Long id) {
        return pollRepository.findById(id).orElseThrow();
    }

    public Poll vote(Long pollId, Long optionId, HttpServletRequest request) {

        String token = request.getHeader("X-User-Token");

        if (token == null || token.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User token is required"
            );
        }

        if (voteRepository.existsByPollIdAndToken(pollId, token)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You have already voted in this poll"
            );
        }

        Poll poll = pollRepository.findById(pollId).orElseThrow();
        Option option = optionRepository.findById(optionId).orElseThrow();

        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setOption(option);
        vote.setToken(token);

        voteRepository.save(vote);

        messagingTemplate.convertAndSend(
                "/topic/poll/" + pollId,
                poll
        );

        return poll;
    }
    public List<Poll> getAllPolls() {
        return pollRepository.findAll();
    }
}
