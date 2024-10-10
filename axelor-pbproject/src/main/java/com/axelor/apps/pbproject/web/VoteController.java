package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.VoteService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;


public class VoteController {

    private final VoteService voteService;

    @Inject
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // Метод для голосования "за"
    public void countVoteUp(ActionRequest request, ActionResponse response) {
        Long topicId = (Long) request.getContext().get("id"); // Извлечение ID топика
        Long userId = (Long) request.getUser().getId(); // Получаем ID пользователя из запроса

        try {
            voteService.incrementVote(topicId, userId); // Увеличение голоса через сервис
            response.setReload(true); // Обновляем интерфейс
        } catch (Exception e) {
            response.setError("Error while voting up: " + e.getMessage());
        }
    }

    // Метод для голосования "против"
    public void countVoteDown(ActionRequest request, ActionResponse response) {
        Long topicId = (Long) request.getContext().get("id"); // Извлечение ID топика
        Long userId = (Long) request.getUser().getId(); // Получаем ID пользователя из запроса

        try {
            voteService.decrementVote(topicId, userId); // Уменьшение голоса через сервис
            response.setReload(true); // Обновляем интерфейс
        } catch (Exception e) {
            response.setError("Error while voting down: " + e.getMessage());
        }
    }

}
