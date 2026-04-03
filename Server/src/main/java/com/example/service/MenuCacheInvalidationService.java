package com.example.service;

import java.util.Collection;

public interface MenuCacheInvalidationService {
    void invalidateDishCategoriesAfterCommit(Collection<Long> categoryIds, String reason);

    void invalidateSetmealCategoriesAfterCommit(Collection<Long> categoryIds, String reason);

    void invalidateMenuCachesNowAndDelayed(Collection<Long> dishCategoryIds, Collection<Long> setmealCategoryIds, String reason);
}
