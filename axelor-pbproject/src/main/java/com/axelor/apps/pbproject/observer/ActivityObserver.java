package com.axelor.apps.pbproject.observer;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.event.Observes;
import com.axelor.events.PostAction;
import com.axelor.events.RequestEvent;
import com.axelor.inject.Beans;
import java.time.LocalDateTime;
import javax.inject.Named;

public class ActivityObserver {

  void onUserAction(@Observes @Named(RequestEvent.SAVE) PostAction event) {
    User user = AuthUtils.getUser(); // Получаем текущего пользователя
    if (user != null) {
      user.setLastLoginDateTime(LocalDateTime.now());
      Beans.get(UserRepository.class).save(user);
    }
  }
}
