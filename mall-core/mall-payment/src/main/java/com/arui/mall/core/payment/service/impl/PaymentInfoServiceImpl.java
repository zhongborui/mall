package com.arui.mall.core.payment.service.impl;

import com.arui.mall.core.payment.mapper.PaymentInfoMapper;
import com.arui.mall.core.payment.service.PaymentInfoService;
import com.arui.mall.model.pojo.entity.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 支付信息表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-11-11
 */
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {

}
