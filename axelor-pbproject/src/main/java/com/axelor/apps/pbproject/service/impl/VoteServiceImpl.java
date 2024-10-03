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

        // Проверяем, голосовал ли пользователь за этот топик
        ForumVote existingVote = forumVoteRepository.all()
                .filter("self.forumTopic = ?1 AND self.author = ?2", topic, user)
                .fetchOne();

        if (existingVote != null) {
            if (!existingVote.getIsUpvote()) {
                // Просто возвращаем, если пользователь уже поставил дизлайк, и ничего не делаем
                return;
            } else {
                // Пользователь ранее поставил лайк, меняем на дизлайк
                existingVote.setIsUpvote(false);
                forumVoteRepository.save(existingVote);

                topic.setUpvoteCount(topic.getUpvoteCount() - 1);
                topic.setDownvoteCount(topic.getDownvoteCount() + 1);
            }
        } else {
            ForumVote newVote = new ForumVote();
            newVote.setForumTopic(topic);
            newVote.setAuthor(user);
            newVote.setIsUpvote(false);
            forumVoteRepository.save(newVote);

            topic.setDownvoteCount(topic.getDownvoteCount() + 1);
        }

        topic.setVoteCount(topic.getUpvoteCount() - topic.getDownvoteCount());
        forumTopicRepository.save(topic);
    }


    @Override
    @Transactional
    public void incrementVote(Long topicId, Long userId) throws Exception {
        ForumTopic topic = forumTopicRepository.find(topicId);
        User user = userRepository.find(userId);

        ForumVote existingVote = forumVoteRepository.all()
                .filter("self.forumTopic = ?1 AND self.author = ?2", topic, user)
                .fetchOne();

        if (existingVote != null) {
            if (existingVote.getIsUpvote()) {
                // Просто возвращаем, если пользователь уже поставил лайк, и ничего не делаем
                return;
            } else {
                existingVote.setIsUpvote(true);
                forumVoteRepository.save(existingVote);

                topic.setUpvoteCount(topic.getUpvoteCount() + 1);
                topic.setDownvoteCount(topic.getDownvoteCount() - 1);
            }
        } else {
            ForumVote newVote = new ForumVote();
            newVote.setForumTopic(topic);
            newVote.setAuthor(user);
            newVote.setIsUpvote(true);
            forumVoteRepository.save(newVote);

            topic.setUpvoteCount(topic.getUpvoteCount() + 1);
        }

        topic.setVoteCount(topic.getUpvoteCount() - topic.getDownvoteCount());
        forumTopicRepository.save(topic);
    }



    @Override
    @Transactional
    public void incrementViewCount(Long topicId) throws Exception {
        ForumTopic topic = forumTopicRepository.find(topicId);
        if (topic != null) {
            int currentViews = topic.getViewCount() != null ? topic.getViewCount() : 0;
            topic.setViewCount(currentViews + 1);
            forumTopicRepository.save(topic);
        } else {
            throw new Exception("Forum topic not found");
        }
    }
}
