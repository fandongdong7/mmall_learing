package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServiceResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    @Override
    public ServiceResponse<CartVo> addCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServiceResponse.createByErrorMessage("参数异常");
        }
        //通过用户id和商品id查询购物车信息
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
//        如果没有购物商品信息则创建一个新的购物车项存入购物车中
        if (cart == null) {
            //说明购物车中没有该商品，创建购物车
            Cart cartItem = new Cart();
            cartItem.setUserId(userId);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setQuantity(count);
            int resultDB = cartMapper.insert(cartItem);
            if (resultDB == 0) {
                return ServiceResponse.createByErrorMessage("添加购物车失败");
            }
        } else {
//        如果有购物车信息则在将商品数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            //update DB
            int resultDB = cartMapper.updateByPrimaryKeySelective(cart);
            if (resultDB == 0) {
                return ServiceResponse.createByErrorMessage("updateCart fail");
            }
        }
//        返回购物车信息
        return ServiceResponse.createBySuccess(getCartVoLimit(userId));
    }

    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

    @Override
    public ServiceResponse<CartVo> list(Integer userId) {
        if (userId == null) {
            ServiceResponse.createByErrorMessage("param exception");
        }
        CartVo cartVoLimit = getCartVoLimit(userId);
        return ServiceResponse.createBySuccess(cartVoLimit);
    }

    @Override
    public ServiceResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //first check Cart is exist
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        int resultDB = cartMapper.updateByPrimaryKeySelective(cart);
        if (resultDB == 0) {
            return ServiceResponse.createByErrorMessage("update DB fail");
        }
        return list(userId);
    }

    @Override
    public ServiceResponse<CartVo> delete(Integer userId, String productIds) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (productList == null) {
            return ServiceResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        int resultDB = cartMapper.deleteByUserIdProductIds(userId, productList);
        if (resultDB == 0) {
            return ServiceResponse.createByErrorMessage("delete DB fail");
        }
        return list(userId);
    }

    @Override
    public ServiceResponse<CartVo> checkedOrUncheckedProduct(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId,productId, checked);
        return list(userId);
    }

    @Override
    public ServiceResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServiceResponse.createBySuccess(0);
        }
        int resultDB = cartMapper.selectCartProductCount(userId);
        return ServiceResponse.createBySuccess(resultDB);
    }
}
