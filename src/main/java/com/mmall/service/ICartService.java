package com.mmall.service;

import com.mmall.common.ServiceResponse;
import com.mmall.vo.CartVo;

public interface ICartService {

    ServiceResponse<CartVo> addCart(Integer userId, Integer productId, Integer count);

    ServiceResponse<CartVo> list(Integer userId);

    ServiceResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServiceResponse<CartVo> delete(Integer userId, String  productIds);

    ServiceResponse<CartVo> checkedOrUncheckedProduct(Integer userId, Integer productId, Integer checked);

    ServiceResponse<Integer> getCartProductCount(Integer userId);
}
