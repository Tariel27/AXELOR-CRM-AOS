package com.axelor.apps.pbproject.service.impl;

import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.pbproject.db.Dish;
import com.axelor.apps.pbproject.db.DishMenu;
import com.axelor.apps.pbproject.db.Lunch;
import com.axelor.apps.pbproject.db.LunchConfig;
import com.axelor.apps.pbproject.db.repo.DishMenuRepository;
import com.axelor.apps.pbproject.db.repo.DishRepository;
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
    private final DishRepository dishRepository;

    @Inject
    public LunchServiceImpl(LunchRepository lunchRepository, DishRepository dishRepository) {
        this.lunchRepository = lunchRepository;
        this.dishRepository = dishRepository;
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

        Dish[] dishes = {dishMenu.getFirstDish(), dishMenu.getSecondDish(), dishMenu.getThirdDish()};
        String[] portions = {"1", "1.5"};

        for (String portion : portions) {
            for (Dish dish : dishes) {
                if (dish != null) {
                    links
                            .append("https://pytest2.brisklyminds.com/crm/ws/lunch/order?l=")
                            .append(dish.getId()).append("&p=").append(portion)
                            .append("\t").append(portion)
                            .append(" ").append(dish.getName()).append("\t");
                }
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
    public void orderLunch(Long dishId, String portion) {
        if (Objects.isNull(dishId) || Objects.isNull(portion))
            throw new IllegalArgumentException("Query params cannot be null");

        Dish dish = dishRepository.find(dishId);
        if (Objects.isNull(dish)) throw new IllegalArgumentException("Dish not found for ID: " + dishId);

        if (!portion.matches("\\d+(\\.\\d+)?"))
            throw new IllegalArgumentException("Invalid portion format. Portion must be a number.");

        Lunch lunch = new Lunch();
        lunch.setDish(dish);
        lunch.setPortion(portion);

        lunchRepository.save(lunch);
    }

    @Override
    @Transactional
    public void autoOrder() {
        LunchConfig lunchConfig = Beans.get(LunchConfigRepository.class).all().fetchOne();
        if (Objects.isNull(lunchConfig)) return;
        DishMenu todayDishMenu = Beans.get(DishMenuRepository.class).getTodayDishMenu();
        if (Objects.isNull(todayDishMenu)) return;

        String defaultPortion = "1.5";
        for (User user : lunchConfig.getAutoOrderUser()) {
            addLunchForDish(user, todayDishMenu.getFirstDish(), defaultPortion, lunchConfig.getAutoFirst());
            addLunchForDish(user, todayDishMenu.getSecondDish(), defaultPortion, lunchConfig.getAutoSecond());
            addLunchForDish(user, todayDishMenu.getThirdDish(), defaultPortion, lunchConfig.getAutoThird());
        }
    }

    private void addLunchForDish(User user, Dish dish, String portion, boolean shouldAdd) {
        if (shouldAdd) {
            Lunch lunch = new Lunch();
            lunch.setDish(dish);
            lunch.setPortion(portion);
            lunch.setUserComment(Objects.nonNull(user.getName()) || user.getName().isEmpty() ? user.getCode() : user.getName());
            lunchRepository.save(lunch);
        }
    }
}
