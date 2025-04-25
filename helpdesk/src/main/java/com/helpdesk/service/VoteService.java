package com.helpdesk.service;

import com.helpdesk.dto.VoteDto;

public interface VoteService {
    VoteDto votePost(Long postId, boolean upvote, String username);
    VoteDto voteComment(Long commentId, boolean upvote, String username);
    VoteDto getPostVotes(Long postId, String username);
    VoteDto getCommentVotes(Long commentId, String username);
}