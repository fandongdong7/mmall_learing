package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface IManageCategoryService {
    ServiceResponse addCategory(String categoryName, Integer parentId);

    ServiceResponse updateCategory(String categoryName, Integer categoryId);

    ServiceResponse<List<Category>> getChildrenCategory(Integer categoryId);

    ServiceResponse<List<Integer>> getDeepChildre(Integer categoryId);
}
