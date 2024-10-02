/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2024 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.pbproject.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.businesssupport.db.repo.ProjectTaskBusinessSupportRepository;
import com.axelor.apps.pbproject.db.repo.PetaProjectTaskProjectRepository;
import com.axelor.apps.pbproject.service.*;
import com.axelor.apps.pbproject.service.impl.*;

public class PbpProjectModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(UserPbpProjectService.class).to(UserPbpProjectServiceImpl.class);
    bind(WorkTimeService.class).to(WorkTimeServiceImpl.class);
    bind(ReadableTimeService.class).to(ReadableTimeServiceImpl.class);
    bind(DutyService.class).to(DutyServiceImpl.class);
    bind(UserActivityService.class).to(UserActivityServiceImpl.class);
    bind(ProjectTaskBusinessSupportRepository.class).to(PetaProjectTaskProjectRepository.class);
    bind(FaceIdService.class).to(FaceIdServiceImpl.class);
  }
}
