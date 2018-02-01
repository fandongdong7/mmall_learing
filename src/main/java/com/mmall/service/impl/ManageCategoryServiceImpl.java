package com.mmall.service.impl;

import com.mmall.common.ServiceResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.IManageCategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ManageCategoryServiceImpl implements IManageCategoryService {
    private Logger logger = LoggerFactory.getLogger(ManageCategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServiceResponse addCategory(String categoryName, Integer parentId) {
        if (StringUtils.isBlank(categoryName) || parentId == null) {
            return ServiceResponse.createByErrorMessage("分类参数异常");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);

        int resutltDB = categoryMapper.insert(category);
        if (resutltDB > 0) {
            return ServiceResponse.createBySuccessMessage("添加分类成功");
        }
        return ServiceResponse.createBySuccessMessage("添加分类失败");
    }

    @Override
    public ServiceResponse updateCategory(String categoryName, Integer categoryId) {
        if (StringUtils.isBlank(categoryName) || categoryId == null) {
            return ServiceResponse.createByErrorMessage("更新分类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int reusltDB = categoryMapper.updateByPrimaryKeySelective(category);
        if (reusltDB > 0) {
            return ServiceResponse.createBySuccessMessage("修改分类名称成功");
        }
        return ServiceResponse.createByErrorMessage("修改分类名称失败");
    }

    @Override
    public ServiceResponse<List<Category>> getChildrenCategory(Integer categoryId) {
        if (categoryId == null) {
            return ServiceResponse.createByErrorMessage("查询参数传递异常");
        }
        List<Category> childrenCategory = categoryMapper.getChildrenCategory(categoryId);
        if (CollectionUtils.isEmpty(childrenCategory)) {
            logger.info("未找到当前分类的子类");
        }
        return ServiceResponse.createBySuccess(childrenCategory);
    }

    public ServiceResponse<List<Integer>> getDeepChildre(Integer categoryId) {
        if (categoryId == null) {
            return ServiceResponse.createByErrorMessage("查询参数异常");
        }
        Set<Category> categorySet = new HashSet<>();
        List<Integer> integerList = new ArrayList<>();
         findChildreCategory(categorySet, categoryId);
        for (Category category : categorySet) {
            integerList.add(category.getId());
        }
        return ServiceResponse.createBySuccess(integerList);
    }

    private Set<Category> findChildreCategory(Set<Category> categorySet, Integer categoryId) {
        //查询自己的节点
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }
        //查询子节点放置到集合中
        List<Category> childrenCategory = categoryMapper.getChildrenCategory(categoryId);
        for (Category categoryChildren : childrenCategory) {
            findChildreCategory(categorySet, categoryChildren.getId());
        }
        return categorySet;
    }
}
