package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServiceResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProducManageService {
    ServiceResponse<String> addProduct(Product product);

    ServiceResponse<String> updateProduct(Product product);

    ServiceResponse<String> setSaleStatus(Integer productId, Integer status);

    ServiceResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServiceResponse getProductList(Integer pageNum, Integer pageSize);

    ServiceResponse<PageInfo> searchProduct(String productName, Integer prodcutId, Integer pageNum, Integer pageSize);

    ServiceResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServiceResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
