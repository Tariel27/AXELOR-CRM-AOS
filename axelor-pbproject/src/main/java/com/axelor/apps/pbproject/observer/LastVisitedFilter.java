package com.axelor.apps.pbproject.observer;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.inject.Beans;
import com.google.inject.persist.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

@WebFilter("/ds/*")
public class LastVisitedFilter implements Filter {
  @Override
  @Transactional
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    User user = AuthUtils.getUser();

    user.setLastLoginDateTime(LocalDateTime.now());
    Beans.get(UserRepository.class).save(user);

    chain.doFilter(request, response);
  }
}
