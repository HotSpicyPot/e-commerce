package com.xy.ecommerce.service.impl;

import com.xy.ecommerce.common.Response;
import com.xy.ecommerce.dao.CategoryMapper;
import com.xy.ecommerce.entity.Category;
import com.xy.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Response addCategory(String categoryName, Integer parentId){
        if (StringUtils.isEmpty(categoryName.trim()) || parentId==null){
            return Response.createByErrorMessage("invalid parameter for a category");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);

        int count=categoryMapper.insert(category);
        if (count>0){
            return Response.createBySuccessMessage("added a category successfully");
        }

        return Response.createByErrorMessage("failed to add a category");
    }

    @Override
    public Response updateCategoryName(Integer categoryId, String categoryName){
        if (StringUtils.isEmpty(categoryName.trim()) || categoryId==null){
            return Response.createByErrorMessage("invalid parameter for a category");
        }
        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int count=categoryMapper.updateByPrimaryKeySelective(category);
        if (count>0){
            return Response.createBySuccessMessage("updated the category name successfully");
        }

        return Response.createByErrorMessage("failed to update the category name");
    }

    @Override
    public Response<List<Category>> getParallelChildrenCategory(int parentId){
        List<Category> categorieList=categoryMapper.selectParallelChildreCategoryByParentId(parentId);
        if (CollectionUtils.isEmpty(categorieList))
            return Response.createByErrorMessage("categories not found");
        return Response.createBySuccess(categorieList);
    }

        @Override
    public Response getCategoryWithChildren(int id){
        Set<Category> categorySet=new HashSet<>();
        findChildrenCategory(categorySet, id);
        return Response.createBySuccess(categorySet);
    }

    private void findChildrenCategory(Set<Category> categorySet, int id){
        Category category=categoryMapper.selectByPrimaryKey(id);
        if (category!=null){
            categorySet.add(category);
        }
        // recursively search children nodes
        List<Category> categoryList=categoryMapper.selectParallelChildreCategoryByParentId(id);
        for (Category child:categoryList){
            findChildrenCategory(categorySet, child.getId());
        }
    }
}
