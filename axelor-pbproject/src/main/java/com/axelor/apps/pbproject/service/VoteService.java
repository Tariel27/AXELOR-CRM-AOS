package com.axelor.apps.pbproject.service;


public interface VoteService {
    void incrementVote(Long topicId, Long userId) throws Exception; // Обновляем сигнатуру
    void decrementVote(Long topicId, Long userId) throws Exception; // Обновляем сигнатуру
}