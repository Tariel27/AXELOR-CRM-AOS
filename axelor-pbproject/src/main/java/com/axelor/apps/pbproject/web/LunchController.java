package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.db.DishMenu;
import com.axelor.apps.pbproject.db.repo.LunchRepository;
import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.db.JpaSupport;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Objects;

import com.axelor.apps.pbproject.db.repo.DishMenuRepository;
import com.axelor.meta.CallMethod;


public class LunchController extends JpaSupport {

    @Inject
    private UserRepository userRepository;

    @Inject
    private LunchRepository lunchRepository;

    @Inject
    private DishMenuRepository dishMenuRepository;

    @Inject
    private LunchService lunchService;

    public void forceAutoOrder(ActionRequest request, ActionResponse response) {
        lunchService.autoOrder();
    }

    public void setLinks(ActionRequest request, ActionResponse response) {
        DishMenu todayDishMenu = dishMenuRepository.getTodayDishMenu();
        if (Objects.isNull(todayDishMenu)) {
            response.setInfo("Today menu is null");
            return;
        }

        response.setValue("copyLinks", lunchService.getLinks(todayDishMenu));
    }

    public void setLunch(ActionRequest request, ActionResponse response) {
        response.setValue("copyLunch", lunchService.getLunch());
    }

    public void reportLunch(ActionRequest request, ActionResponse response) {
        long totalUsers = userRepository.all().count();

        Query query = getEntityManager().createQuery("SELECT COUNT(DISTINCT l.partnerName) FROM Lunch l WHERE l.dateCreate >= CURRENT_DATE");
        long usersWithOrder = (long) query.getSingleResult();

        long usersWithoutOrder = totalUsers - usersWithOrder;

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalUsers);
        data.put("ordered", usersWithOrder);
        data.put("notOrdered", usersWithoutOrder);

        response.setData(Lists.newArrayList(data));
    }

    public void reportPortion(ActionRequest request, ActionResponse response) {
        Query queryOnePortion = getEntityManager().createQuery("SELECT COUNT(l) FROM Lunch l WHERE l.portion = '1' AND l.dateCreate >= CURRENT_DATE");
        long onePortionCount = (long) queryOnePortion.getSingleResult();

        Query queryOneAndHalfPortion = getEntityManager().createQuery("SELECT COUNT(l) FROM Lunch l WHERE l.portion = '1.5' AND l.dateCreate >= CURRENT_DATE");
        long oneAndHalfPortionCount = (long) queryOneAndHalfPortion.getSingleResult();

        Map<String, Object> data = new HashMap<>();
        data.put("onePortionCount", onePortionCount);
        data.put("oneAndHalfPortionCount", oneAndHalfPortionCount);

        response.setData(Lists.newArrayList(data));
    }

    public void reportDishCountForPortion(ActionRequest request, ActionResponse response) {
        Query query = getEntityManager().createNativeQuery(
                "SELECT d.name AS dishName, COUNT(l.id) AS orderCount " +
                        "FROM pbproject_lunch l " +
                        "JOIN pbproject_dish d ON l.dish_name = d.id " +
                        "WHERE l.date_create >= CURRENT_DATE AND l.portion = '1' " +
                        "GROUP BY d.name " +
                        "ORDER BY orderCount DESC"
        );
        List<Object[]> resultList = query.getResultList();

        Map<String, Object> data = new HashMap<>();

        if (resultList.size() > 0) {
            Object[] firstDish = resultList.get(0);
            data.put("firstDish", firstDish[0]);
            data.put("firstDishCount", firstDish[1]);
        }

        if (resultList.size() > 1) {
            Object[] secondDish = resultList.get(1);
            data.put("secondDish", secondDish[0]);
            data.put("secondDishCount", secondDish[1]);
        }

        if (resultList.size() > 2) {
            Object[] thirdDish = resultList.get(2);
            data.put("thirdDish", thirdDish[0]);
            data.put("thirdDishCount", thirdDish[1]);
        }

        if (resultList.size() > 3) {
            Object[] fourthDish = resultList.get(3);
            data.put("fourthDish", fourthDish[0]);
            data.put("fourthDishCount", fourthDish[1]);
        }

        if (resultList.size() > 4) {
            Object[] fifthDish = resultList.get(4);
            data.put("fifthDish", fifthDish[0]);
            data.put("fifthDishCount", fifthDish[1]);
        }

        response.setData(Lists.newArrayList(data));
    }

    public void reportDishCountForHalf(ActionRequest request, ActionResponse response) {
        Query query = getEntityManager().createNativeQuery(
                "SELECT d.name AS dishName, COUNT(l.id) AS orderCount " +
                        "FROM pbproject_lunch l " +
                        "JOIN pbproject_dish d ON l.dish_name = d.id " +
                        "WHERE l.date_create >= CURRENT_DATE AND l.portion = '1.5' " +
                        "GROUP BY d.name " +
                        "ORDER BY orderCount DESC"
        );
        List<Object[]> resultList = query.getResultList();

        Map<String, Object> data = new HashMap<>();

        if (resultList.size() > 0) {
            Object[] firstDish = resultList.get(0);
            data.put("firstDish", firstDish[0]);
            data.put("firstDishCount", firstDish[1]);
        }

        if (resultList.size() > 1) {
            Object[] secondDish = resultList.get(1);
            data.put("secondDish", secondDish[0]);
            data.put("secondDishCount", secondDish[1]);
        }

        if (resultList.size() > 2) {
            Object[] thirdDish = resultList.get(2);
            data.put("thirdDish", thirdDish[0]);
            data.put("thirdDishCount", thirdDish[1]);
        }

        if (resultList.size() > 3) {
            Object[] fourthDish = resultList.get(3);
            data.put("fourthDish", fourthDish[0]);
            data.put("fourthDishCount", fourthDish[1]);
        }

        if (resultList.size() > 4) {
            Object[] fifthDish = resultList.get(4);
            data.put("fifthDish", fifthDish[0]);
            data.put("fifthDishCount", fifthDish[1]);
        }

        response.setData(Lists.newArrayList(data));
    }

    public void searchDishOnDuckDuckGo(ActionRequest request, ActionResponse response) {
        Object dishNameObject = request.getContext().get("dishName");

        if (dishNameObject != null) {
            String dishName = dishNameObject.toString();

            String cleanDishName = dishName.replaceAll(".*name=([^}]+)}", "$1").trim();

            String duckDuckGoSearchUrl = "https://duckduckgo.com/?q=" + cleanDishName.replace(" ", "+");

            Map<String, Object> data = new HashMap<>();
            data.put("addUrlForGoogle", duckDuckGoSearchUrl);
            response.setValues(data);
        } else {
            response.setAlert("Please select a dish name before searching.");
        }
    }

    @CallMethod
    public String showO() {
        return "𖣠";
    }

}
