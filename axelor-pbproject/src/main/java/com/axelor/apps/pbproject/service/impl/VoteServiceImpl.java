package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.pbproject.db.ForumTopic;
import com.axelor.apps.pbproject.db.ForumVote;
import com.axelor.apps.pbproject.db.repo.ForumTopicRepository;
import com.axelor.apps.pbproject.db.repo.ForumVoteRepository;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.apps.pbproject.service.VoteService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class VoteServiceImpl implements VoteService {

    private final ForumTopicRepository forumTopicRepository;
    private final ForumVoteRepository forumVoteRepository;
    private final UserRepository userRepository;

    @Inject
    public VoteServiceImpl(ForumTopicRepository forumTopicRepository, ForumVoteRepository forumVoteRepository, UserRepository userRepository) {
        this.forumTopicRepository = forumTopicRepository;
        this.forumVoteRepository = forumVoteRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void decrementVote(Long topicId, Long userId) throws Exception {
        ForumTopic topic = forumTopicRepository.find(topicId);
        User user = userRepository.find(userId);

        ForumVote existingVote = findVote(topic, user);

        if (existingVote != null) {
            handleExistingVote(existingVote, topic, false);
        } else {
            createNewVote(topic, user, false);
        }
        forumTopicRepository.save(topic);
    }

    @Override
    @Transactional
    public void incrementVote(Long topicId, Long userId) throws Exception {
        ForumTopic topic = forumTopicRepository.find(topicId);
        User user = userRepository.find(userId);

        ForumVote existingVote = findVote(topic, user);

        if (existingVote != null) {
            handleExistingVote(existingVote, topic, true);
        } else {
            createNewVote(topic, user, true);
        }
        forumTopicRepository.save(topic);
    }

    private ForumVote findVote(ForumTopic topic, User user) {
        return forumVoteRepository.all()
                .filter("self.forumTopic = ?1 AND self.author = ?2", topic, user)
                .fetchOne();
    }

    private void handleExistingVote(ForumVote existingVote, ForumTopic topic, boolean isUpvote) {
        if (existingVote.getIsUpvote() == isUpvote) {
            // Пользователь уже голосовал так же
            return;
        }

        existingVote.setIsUpvote(isUpvote);
        forumVoteRepository.save(existingVote);

        if (isUpvote) {
            topic.setUpvoteCount(topic.getUpvoteCount() + 1);
            topic.setDownvoteCount(topic.getDownvoteCount() - 1);
        } else {
            topic.setUpvoteCount(topic.getUpvoteCount() - 1);
            topic.setDownvoteCount(topic.getDownvoteCount() + 1);
        }
    }

    private void createNewVote(ForumTopic topic, User user, boolean isUpvote) {
        ForumVote newVote = new ForumVote();
        newVote.setForumTopic(topic);
        newVote.setAuthor(user);
        newVote.setIsUpvote(isUpvote);
        forumVoteRepository.save(newVote);

        if (isUpvote) {
            topic.setUpvoteCount(topic.getUpvoteCount() + 1);
        } else {
            topic.setDownvoteCount(topic.getDownvoteCount() + 1);
        }
    }
}
