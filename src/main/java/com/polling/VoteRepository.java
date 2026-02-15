package com.polling;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByPollIdAndIpAddress(Long pollId, String ipAddress);
    boolean existsByPollIdAndToken(Long pollId, String token);

}
