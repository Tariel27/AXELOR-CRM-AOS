package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.pbproject.db.Dish;
import com.axelor.apps.pbproject.db.DishMenu;
import com.axelor.apps.pbproject.db.Lunch;
import com.axelor.apps.pbproject.db.LunchConfig;
import com.axelor.apps.pbproject.db.repo.DishMenuRepository;
import com.axelor.apps.pbproject.db.repo.LunchConfigRepository;
import com.axelor.apps.pbproject.db.repo.LunchRepository;
import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.auth.db.User;
import com.axelor.db.JPA;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class LunchServiceImpl implements LunchService {
    private final LunchRepository lunchRepository;
    private final DishMenuRepository dishMenuRepository;

    @Inject
    public LunchServiceImpl(LunchRepository lunchRepository, DishMenuRepository dishMenuRepository) {
        this.lunchRepository = lunchRepository;
        this.dishMenuRepository = dishMenuRepository;
    }

    @Override
    public List<Lunch> getLunchYesterday() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        return lunchRepository.all()
                .filter("self.dateCreate = :yesterday")
                .bind("yesterday", yesterday)
                .fetch();
    }

    @Override
    public String getLinks(DishMenu dishMenu) {
        StringBuilder links = new StringBuilder();

        String[] portions = {"1", "1.5"};

        for (String portion : portions) {
            for (int i = 0; i < 3; i++) {
                int position = i + 1;
                links.append("https://pytest2.brisklyminds.com/crm/ws/lunch/order?d=")
                        .append(position).append("&p=").append(portion).append("\n");
            }
        }

        return links.toString();
    }

    @Override
    public String getLunch() {
        try {
            Query query = JPA.em().createNativeQuery(
                    "SELECT pd.name, lunch.portion, COUNT(lunch.dish) AS dish_count\n" +
                            "FROM pbproject_lunch lunch\n" +
                            "LEFT JOIN pbproject_dish pd ON pd.id = lunch.dish\n" +
                            "WHERE DATE(lunch.created_on) = DATE(NOW())\n" +
                            "GROUP BY pd.name, lunch.portion\n" +
                            "ORDER BY pd.name;");

            List<Object[]> results = (List<Object[]>) query.getResultList();

            StringBuilder resultText = new StringBuilder();
            for (Object[] row : results) {
                String dishName = (String) row[0];
                String portion = (String) row[1];
                BigInteger dishCount = (BigInteger) row[2];

                resultText.append("(Название: ").append(dishName)
                        .append(", Порция: ").append(portion)
                        .append(", Количество: ").append(dishCount).append(")")
                        .append("\t\n");
            }

            return resultText.toString();
        } catch (Exception exception) {
            TraceBackService.trace(exception);
            return null;
        }
    }

    @Override
    @Transactional(rollbackOn = {RuntimeException.class, NullPointerException.class, IllegalArgumentException.class})
    public void orderLunch(Integer dishPosition, String portion) {
        if (Objects.isNull(dishPosition) || Objects.isNull(portion))
            throw new IllegalArgumentException("Query params cannot be null");

        DishMenu todayDishMenu = dishMenuRepository.getTodayDishMenu();
        if (Objects.isNull(todayDishMenu)) throw new RuntimeException("На сегодня меню не составлено");
        Dish dish = getDish(dishPosition, todayDishMenu);

        if (!portion.matches("\\d+(\\.\\d+)?"))
            throw new IllegalArgumentException("Invalid portion format. Portion must be a number.");

        Lunch lunch = new Lunch();
        lunch.setDish(dish);
        lunch.setPortion(portion);

        lunchRepository.save(lunch);
    }

    private Dish getDish(Integer position, DishMenu dishMenu) {
        switch (position) {
            case 1:
                return dishMenu.getFirstDish();
            case 2:
                return dishMenu.getSecondDish();
            case 3:
                return dishMenu.getThirdDish();
            default:
                throw new IllegalArgumentException("Dish not found for position: " + position);
        }
    }

    @Override
    @Transactional
    public void autoOrder() {
        LunchConfig lunchConfig = Beans.get(LunchConfigRepository.class).all().fetchOne();
        if (Objects.isNull(lunchConfig)) return;
        DishMenu todayDishMenu = dishMenuRepository.getTodayDishMenu();
        if (Objects.isNull(todayDishMenu)) return;

        if (Objects.isNull(lunchConfig.getAutoOrderUsers()) || lunchConfig.getAutoOrderUsers().isEmpty()) return;

        String defaultPortion = "1.5";
        lunchConfig.getAutoOrderUsers().forEach(user -> {
            String username = "Не указано";
            if (Objects.nonNull(user.getLunchUser()) && Objects.nonNull(user.getLunchUser().getName())) {
                username = user.getLunchUser().getName();
            }
            addLunchForDish(username, todayDishMenu.getFirstDish(), defaultPortion, user.getAutoFirst());
            addLunchForDish(username, todayDishMenu.getSecondDish(), defaultPortion, user.getAutoSecond());
            addLunchForDish(username, todayDishMenu.getThirdDish(), defaultPortion, user.getAutoThird());
        });
    }

    private void addLunchForDish(String comment, Dish dish, String portion, boolean shouldAdd) {
        if (shouldAdd) {
            Lunch lunch = new Lunch();
            lunch.setDish(dish);
            lunch.setPortion(portion);
            lunch.setUserComment(comment);
            lunchRepository.save(lunch);
        }
    }
}
