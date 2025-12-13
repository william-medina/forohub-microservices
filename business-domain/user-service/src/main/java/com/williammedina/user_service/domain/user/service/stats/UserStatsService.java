package com.williammedina.user_service.domain.user.service.stats;

import com.williammedina.user_service.domain.user.dto.UserStatsDTO;

public interface UserStatsService {

    UserStatsDTO getUserStats(Long userId);

}
